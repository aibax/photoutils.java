package jp.aibax.photoutils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Pattern;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ExifRenameCommand
{
    private static final String VERSION = "exifrename version 1.0.0-SNAPSHOT";

    private static final DateFormat DEFAULT_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    private enum TextCase
    {
        Uppercase, Lowercase
    }

    @Option(name = "-h", aliases = "--help", help = true, usage = "このメッセージを表示します")
    private Boolean usageFlag;

    @Option(name = "-v", aliases = "--version", usage = "バージョンを表示します")
    private Boolean versionFlag;

    @Option(name = "-c", aliases = "--counter-length", usage = "カウンタ部分の桁数")
    private int counterLength = 2;

    @Option(name = "-m", aliases = "--model", usage = "撮影したカメラの機種名をファイル名に追加する")
    private Boolean modelFlag;

    @Option(name = "-p", aliases = "--prefix", usage = "ファイル名の先頭に付与するプレフィックス")
    private String prefix;

    @Option(name = "-s", aliases = "--suffix", usage = "ファイル名の末尾に付与するサフィックス")
    private String suffix;

    @Option(name = "-e", aliases = "--lowercase-extension", usage = "拡張子を小文字に変換する")
    private Boolean lowercaseExtension;

    @Option(name = "-E", aliases = "--uppercase-extension", usage = "拡張子を大文字に変換する")
    private Boolean uppercaseExtension;

    @Option(name = "-d", aliases = "--dry-run", usage = "実際に実行せずに実行結果を表示する")
    private Boolean dryrunFlag;

    @Argument(metaVar = "arguments...", handler = StringArrayOptionHandler.class)
    private String[] arguments;

    public static void main(String[] args)
    {
        ExifRenameCommand command = new ExifRenameCommand();

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

        if (isTrue(command.usageFlag) || (command.arguments == null) || (command.arguments.length == 0))
        {
            System.out.println("Usage of exifrename:");
            System.out.println("exifrename [OPTIONS] FILES...");
            parser.printUsage(System.out);
            return;
        }

        int counterLength = command.counterLength;
        boolean addModel = isTrue(command.modelFlag);
        String prefix = command.prefix;
        String suffix = command.suffix;
        boolean lowercaseExtension = isTrue(command.lowercaseExtension);
        boolean uppercaseExtension = isTrue(command.uppercaseExtension);
        TextCase textCase = uppercaseExtension ? TextCase.Uppercase : lowercaseExtension ? TextCase.Lowercase : null;
        boolean dryrun = isTrue(command.dryrunFlag);

        Arrays.asList(command.arguments).forEach(argument -> {

            Path target = Paths.get(argument);

            try
            {
                command.rename(target, counterLength, addModel, prefix, suffix, textCase, dryrun);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        });
    }

    public void rename(Path target, int counterLength, boolean addModel, String prefix, String suffix,
        TextCase extension, boolean dryrun) throws IOException
    {
        if (target == null)
        {
            return;
        }

        if (!Files.exists(target))
        {
            throw new FileNotFoundException();
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
                    rename(file, counterLength, addModel, prefix, suffix, extension, dryrun);
                }
            }

            return;
        }

        int _counterLength = (counterLength > 0) ? counterLength : 0;
        String _prefix = (prefix != null) ? prefix : "";
        String _suffix = (suffix != null) ? suffix : "";

        _renameFile(target, _counterLength, addModel, _prefix, _suffix, extension, dryrun);
    }

    private void _renameFile(Path target, int counterLength, boolean addModel, String prefix, String suffix,
        TextCase extension, boolean dryrun) throws IOException
    {
        Exif exif = Exif.decode(target);

        /*
         * 撮影時刻
         */
        Date dateTimeOriginal = exif.getDateTimeOriginal();

        if (dateTimeOriginal == null)
        {
            // Exifから撮影時刻が取得できない場合 => ファイルの更新時刻を使用
            dateTimeOriginal = exif.getLastModified();
        }

        /* モデル名 */
        String model = null;

        if (addModel)
        {
            model = exif.getModel();
            model = model.replace(" ", "_");
        }

        /* 拡張子 */
        String ext = parseExtention(target);

        if (isNotEmpty(ext) && (extension != null))
        {
            switch (extension)
            {
                case Uppercase:
                    ext = ext.toUpperCase();
                    break;

                case Lowercase:
                    ext = ext.toLowerCase();
                    break;
            }
        }

        /* ファイル名の抜けを防ぐため一時的なファイル名にリネーム */
        String tmpname = String.valueOf(System.currentTimeMillis()) + ext;
        Path tmpfile = target.getParent().resolve(tmpname);

        if (!dryrun)
        {
            Files.move(target, tmpfile);
        }

        Path newfile = null;

        /* 連番 */
        NumberFormat counterFormat = NumberFormat.getIntegerInstance();
        counterFormat.setGroupingUsed(false);
        counterFormat.setMinimumIntegerDigits(counterLength);

        for (int i = 0; ; i++)
        {
            String newname = buildFilename(dateTimeOriginal, model, counterFormat.format(i), prefix, suffix, ext);

            newfile = target.getParent().resolve(newname);

            if (!Files.exists(newfile))
            {
                break;
            }
        }

        System.out.printf("[RENAME] %s => %s\n", target.getFileName(), newfile.getFileName());

        if (!dryrun)
        {
            Files.move(tmpfile, newfile);
        }
    }

    private String parseExtention(Path path)
    {
        if (path == null)
        {
            return null;
        }

        String filename = path.getFileName().toString();
        int index = filename.lastIndexOf(".");
        return (index > 0) ? filename.substring(index) : "";
    }

    private String buildFilename(Date timestamp, String model, String counter, String prefix, String suffix, String ext)
    {
        StringBuilder filename = new StringBuilder();

        if (isNotEmpty(prefix))
        {
            filename.append(prefix);
        }

        if (timestamp != null)
        {
            filename.append(DEFAULT_TIMESTAMP_FORMAT.format(timestamp));
        }

        if (isNotEmpty(model))
        {
            filename.append((filename.length() > 0) ? "_" : "");
            filename.append(model);
        }

        if (isNotEmpty(counter))
        {
            filename.append((filename.length() > 0) ? "_" : "");
            filename.append(counter);
        }

        if (isNotEmpty(suffix))
        {
            filename.append(suffix);
        }

        filename.append(ext);

        return filename.toString();
    }
}