package jp.aibax.photoutils;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.commons.imaging.util.IoUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import jp.aibax.image.ImageUtils;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class TrimCommand
{
    private static final String VERSION = "trimimg version 1.0.0-SNAPSHOT";

    @Option(name = "-?", aliases = "--help", help = true, usage = "このメッセージを表示")
    private Boolean usageFlag;

    @Option(name = "-v", aliases = "--version", usage = "バージョンを表示")
    private Boolean versionFlag;

    @Option(name = "-r", aliases = "--aspect-ratio", usage = "アスペクト比（ 1:1=1.0 / 4:3=1.33 / 3:4=0.75 / 16:9=1.78 )")
    private String aspectRatio;

    @Option(name = "--dry-run", usage = "実際に実行せずに実行結果を表示")
    private Boolean dryrunFlag;

    @Argument(metaVar = "arguments...", handler = StringArrayOptionHandler.class)
    private String[] arguments;

    public static void main(String[] args)
    {
        TrimCommand command = new TrimCommand();

        CmdLineParser parser = new CmdLineParser(command);

        try
        {
            parser.parseArgument(args);
        }
        catch (CmdLineException e)
        {
            e.printStackTrace();
            return;
        }

        if (isTrue(command.versionFlag))
        {
            System.out.println(VERSION);
            return;
        }

        if (isTrue(command.usageFlag) || (command.validateArguments() == false))
        {
            System.out.println("Usage of resizeimg:");
            System.out.println("trimimg [OPTIONS] FILES...");
            parser.printUsage(System.out);
            return;
        }

        float aspectRatio = Float.parseFloat(command.aspectRatio);
        boolean dryrun = isTrue(command.dryrunFlag);

        Arrays.asList(command.arguments).forEach(argument -> {

            Path target = Paths.get(argument);

            try
            {
                command.trim(target, aspectRatio, dryrun);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        });
    }

    private boolean validateArguments()
    {
        if ((arguments == null) || (arguments.length == 0))
        {
            /* 処理対象のファイルの指定なし */
            return false;
        }

        if (isEmpty(aspectRatio))
        {
            return false;
        }

        try
        {
            if (Float.parseFloat(aspectRatio) <= 0)
            {
                return false;
            }
        }
        catch (NumberFormatException e)
        {
            return false;
        }

        return true;
    }

    public void trim(Path target, float aspectRatio, boolean dryrun) throws IOException
    {
        if (target == null)
        {
            return;
        }

        if (!Files.exists(target))
        {
            throw new FileNotFoundException();
        }

        if (aspectRatio <= 0)
        {
            throw new IllegalArgumentException("Invalid aspect ratio (" + aspectRatio + ")");
        }

        if (Files.isDirectory(target))
        {
            DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>()
            {
                @Override
                public boolean accept(Path entry) throws IOException
                {
                    return Pattern.matches("(?i).*\\.(jpg|jpeg)$", entry.getFileName().toString());
                }
            };

            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(target, filter))
            {
                for (Path file : directoryStream)
                {
                    trim(file, aspectRatio, dryrun);
                }
            }

            return;
        }

        Path _target = target.toRealPath();

        _trim(_target, aspectRatio, dryrun);
    }

    private void _trim(Path target, float aspectRatio, boolean dryrun) throws IOException
    {
        /*
         * トリミング
         */
        byte[] bytes = ImageUtils.trim(target, aspectRatio);

        BufferedImage original = ImageUtils.readImage(target);
        BufferedImage trimmed = ImageUtils.readImage(bytes);
        System.out
            .printf("[TRIM] %s : %dx%d => %dx%d\n", target.getFileName(), original.getWidth(), original.getHeight(),
                trimmed.getWidth(), trimmed.getHeight());

        /*
         * ファイル出力
         */
        if (!dryrun)
        {
            Path tmpfile = Files.createTempFile(target.getParent(), ".", "");
            IoUtils.writeToFile(bytes, tmpfile.toFile());
            Files.move(tmpfile, target, REPLACE_EXISTING);
        }
    }
}
