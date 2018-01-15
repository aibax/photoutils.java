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

public class ResizeCommand
{
    private static final String VERSION = "resizeimg version 1.0.0-SNAPSHOT";

    @Option(name = "-?", aliases = "--help", help = true, usage = "このメッセージを表示")
    private Boolean usageFlag;

    @Option(name = "-v", aliases = "--version", usage = "バージョンを表示")
    private Boolean versionFlag;

    @Option(name = "-m", aliases = "--max", usage = "長辺の長さ (px)")
    private int max = 0;

    @Option(name = "-w", aliases = "--width", usage = "リサイズ後の幅 (px)")
    private int width = 0;

    @Option(name = "-h", aliases = "--height", usage = "リサイズ後の高さ (px)")
    private int height = 0;

    @Option(name = "--dry-run", usage = "実際に実行せずに実行結果を表示")
    private Boolean dryrunFlag;

    @Argument(metaVar = "arguments...", handler = StringArrayOptionHandler.class)
    private String[] arguments;

    public static void main(String[] args)
    {
        ResizeCommand command = new ResizeCommand();

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
            System.out.println("resizeimg [OPTIONS] FILES...");
            parser.printUsage(System.out);
            return;
        }

        int max = command.max;
        int width = command.width;
        int height = command.height;
        boolean dryrun = isTrue(command.dryrunFlag);

        Arrays.asList(command.arguments).forEach(argument -> {

            Path target = Paths.get(argument);

            try
            {
                if ((width > 0) || (height > 0))
                {
                    command.resize(target, width, height, dryrun);
                    return;
                }

                if (max > 0)
                {
                    command.resize(target, max, dryrun);
                    return;
                }
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

        if ((max < 0) || (width < 0) || (height < 0))
        {
            /* リサイズ後のサイズがマイナス */
            return false;
        }

        if ((max == 0) && (width == 0) && (height == 0))
        {
            /* リサイズ後のサイズの指定なし */
            return false;
        }

        return true;
    }

    public void resize(Path target, int max, boolean dryrun) throws IOException
    {
        if (target == null)
        {
            return;
        }

        if (!Files.exists(target))
        {
            throw new FileNotFoundException();
        }

        if (max <= 0)
        {
            throw new IllegalArgumentException("Invalid image size (" + max + ")");
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
                    resize(file, max, dryrun);
                }
            }

            return;
        }

        Path _target = target.toRealPath();

        _resize(_target, max, dryrun);
    }

    private void _resize(Path target, int max, boolean dryrun) throws IOException
    {
        /*
         * リサイズ
         */
        byte[] bytes = ImageUtils.resize(target, max);

        BufferedImage original = ImageUtils.readImage(target);
        BufferedImage resized = ImageUtils.readImage(bytes);
        System.out.printf("[RESIZE] %s : %dx%d => %dx%d\n", target.getFileName(), original.getWidth(),
            original.getHeight(), resized.getWidth(), resized.getHeight());

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

    public void resize(Path target, int width, int height, boolean dryrun) throws IOException
    {
        if (target == null)
        {
            return;
        }

        if (!Files.exists(target))
        {
            throw new FileNotFoundException();
        }

        if (width < 0)
        {
            throw new IllegalArgumentException("Invalid image width (" + width + ")");
        }

        if (height < 0)
        {
            throw new IllegalArgumentException("Invalid image height (" + height + ")");
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
                    resize(file, width, height, dryrun);
                }
            }

            return;
        }

        Path _target = target.toRealPath();

        _resize(_target, width, height, dryrun);
    }

    private void _resize(Path target, int width, int height, boolean dryrun) throws IOException
    {
        /*
         * リサイズ
         */
        byte[] bytes = ImageUtils.resize(target, width, height);

        BufferedImage original = ImageUtils.readImage(target);
        BufferedImage resized = ImageUtils.readImage(bytes);
        System.out.printf("[RESIZE] %s : (%dx%d) => (%dx%d)\n", target.getFileName(), original.getWidth(),
            original.getHeight(), resized.getWidth(), resized.getHeight());

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
