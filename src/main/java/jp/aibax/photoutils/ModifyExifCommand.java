package jp.aibax.photoutils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.IImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.SECOND;
import static org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL;
import static org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_SUB_SEC_TIME_ORIGINAL;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ModifyExifCommand
{
    private static final String VERSION = "modexif version 1.0.0-SNAPSHOT (using Apache Commons Imaging 1.0 RC7)";

    private static final DateFormat EXIF_DATE_FORMAT = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

    private static final DateFormat YYYYMMDDHHMMSS = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final DateFormat YYYYMMDDHHMMSSSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    @Option(name = "-?", aliases = "--help", help = true, usage = "このメッセージを表示します")
    private Boolean usageFlag;

    @Option(name = "-v", aliases = "--version", usage = "バージョンを表示します")
    private Boolean versionFlag;

    @Option(name = "-t", aliases = "--datetime", usage = "DateTimeOriginalタグに設定する時刻 ('yyyyMMddHHmmss' または 'yyyyMMddHHmmssSSS' 形式の文字列で指定)")
    private String datetime;

    @Option(name = "-d", aliases = "--add-days", usage = "DateTimeOriginalタグの時刻を指定された日数だけ補正します")
    private int addDays = 0;

    @Option(name = "-h", aliases = "--add-hours", usage = "DateTimeOriginalタグの時刻を指定された時間だけ補正します")
    private int addHours = 0;

    @Option(name = "-m", aliases = "--add-minutes", usage = "DateTimeOriginalタグの時刻を指定された分だけ補正します")
    private int addMinutes = 0;

    @Option(name = "-s", aliases = "--add-seconds", usage = "DateTimeOriginalタグの時刻を指定された秒だけ補正します")
    private int addSeconds = 0;

    @Option(name = "--dry-run", usage = "実際に実行せずに実行結果を表示する")
    private Boolean dryrunFlag;

    @Argument(metaVar = "arguments...", handler = StringArrayOptionHandler.class)
    private String[] arguments;

    public static void main(String[] args)
    {
        ModifyExifCommand command = new ModifyExifCommand();

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
            System.out.println("Usage of modexif:");
            System.out.println("modexif [OPTIONS] FILES...");
            parser.printUsage(System.out);
            return;
        }

        Date date = null;
        Integer millisecond = null;

        if (command.datetime != null)
        {
            try
            {
                if (command.datetime.length() == "yyyyMMddHHmmss".length())
                {
                    date = YYYYMMDDHHMMSS.parse(command.datetime);
                    millisecond = null;
                }

                if (command.datetime.length() == "yyyyMMddHHmmssSSS".length())
                {
                    date = YYYYMMDDHHMMSSSSS.parse(command.datetime);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    millisecond = calendar.get(MILLISECOND);
                }
            }
            catch (ParseException e)
            {
                date = null;
                millisecond = null;
            }

            if (date == null)
            {
                System.out.println("Usage of modexif:");
                System.out.println("modexif [OPTIONS] FILES...");
                parser.printUsage(System.out);
                return;
            }
        }

        Date dateTimeOriginal = date;
        Integer subSecTimeOriginal = millisecond;
        int addDays = command.addDays;
        int addHours = command.addHours;
        int addMinutes = command.addMinutes;
        int addSeconds = command.addSeconds;
        boolean dryrun = isTrue(command.dryrunFlag);

        Arrays.asList(command.arguments).forEach(argument -> {

            Path target = Paths.get(argument);

            try
            {
                if (dateTimeOriginal != null)
                {
                    command.set(target, dateTimeOriginal, subSecTimeOriginal, dryrun);
                }

                if ((addDays != 0) || (addHours != 0) || (addMinutes != 0) || (addSeconds != 0))
                {
                    command.adjust(target, addDays, addHours, addMinutes, addSeconds, dryrun);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        });
    }

    public void set(Path target, Date dateTimeOriginal, Integer subSecTimeOriginal, boolean dryrun)
        throws IOException, ImageWriteException, ImageReadException
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
                    set(file, dateTimeOriginal, subSecTimeOriginal, dryrun);
                }
            }

            return;
        }

        Path _target = target.toRealPath();

        _set(_target, dateTimeOriginal, subSecTimeOriginal, dryrun);
    }

    private void _set(Path target, Date dateTimeOriginal, Integer subSecTimeOriginal, boolean dryrun)
        throws IOException, ImageWriteException, ImageReadException
    {
        Exif exif = Exif.decode(target);

        /*
         * 撮影時刻
         */
        Date currentDateTimeOriginal = exif.getDateTimeOriginal();
        Integer currentSubSecTimeOriginal = exif.getSubSecTimeOriginal();

        System.out.printf("[SET] %s : %s%s => %s%s\n", target.getFileName(),
            (currentDateTimeOriginal != null) ? EXIF_DATE_FORMAT.format(currentDateTimeOriginal) : "Undefined",
            (currentSubSecTimeOriginal != null) ? " (" + currentSubSecTimeOriginal + ")" : "",
            (dateTimeOriginal != null) ? EXIF_DATE_FORMAT.format(dateTimeOriginal) : "Undefined",
            (subSecTimeOriginal != null) ? " (" + subSecTimeOriginal + ")" : "");

        /*
         * 時刻設定
         */
        if (!dryrun)
        {
            _updateMetadata(target, new ExifMetadata(EXIF_TAG_DATE_TIME_ORIGINAL, dateTimeOriginal),
                new ExifMetadata(EXIF_TAG_SUB_SEC_TIME_ORIGINAL, subSecTimeOriginal));
        }
    }

    public void adjust(Path target, int addDays, int addHours, int addMinutes, int addSeconds, boolean dryrun)
        throws ImageReadException, ImageWriteException, IOException
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
                    adjust(file, addDays, addHours, addMinutes, addSeconds, dryrun);
                }
            }

            return;
        }

        Path _target = target.toRealPath();

        int second = 1;
        int minute = 60 * second;
        int hour = 60 * minute;
        int day = 24 * hour;
        int _addSeconds = (addDays * day) + (addHours * hour) + (addMinutes * minute) + (addSeconds * second);

        _adjust(_target, _addSeconds, dryrun);
    }

    private void _adjust(Path target, int addSeconds, boolean dryrun)
        throws ImageReadException, ImageWriteException, IOException
    {
        Exif exif = Exif.decode(target);

        /*
         * 撮影時刻
         */
        Date dateTimeOriginal = exif.getDateTimeOriginal();

        if (dateTimeOriginal == null)
        {
            // Exifから撮影時刻が取得できない場合 => 処理終了
            return;
        }

        /*
         * 補正後時刻
         */
        Date dateTimeAdjusted = dateTimeOriginal;

        if (addSeconds != 0)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateTimeOriginal);
            cal.add(SECOND, addSeconds);
            dateTimeAdjusted = cal.getTime();
        }

        System.out.printf("[ADJUST] %s : %s => %s\n", target.getFileName(), EXIF_DATE_FORMAT.format(dateTimeOriginal),
            EXIF_DATE_FORMAT.format(dateTimeAdjusted));

        /*
         * 時刻補正
         */
        if (!dryrun && !dateTimeAdjusted.equals(dateTimeOriginal))
        {
            _updateMetadata(target, new ExifMetadata(EXIF_TAG_DATE_TIME_ORIGINAL, dateTimeAdjusted));
        }
    }

    class ExifMetadata
    {
        private TagInfo tagInfo;

        private Object value;

        public ExifMetadata(TagInfo tagInfo, Object value)
        {
            this.tagInfo = tagInfo;
            this.value = value;
        }

        public TagInfo getTagInfo()
        {
            return tagInfo;
        }

        public Object getValue()
        {
            return value;
        }
    }

    private void _updateMetadata(Path target, ExifMetadata... metadata)
        throws ImageReadException, ImageWriteException, IOException
    {
        if ((metadata == null) || (metadata.length == 0))
        {
            return;
        }

        /*
         * EXIFデータの取得
         */
        IImageMetadata imageMetadata = Imaging.getMetadata(target.toFile());

        if (imageMetadata == null)
        {
            return;
        }

        if (!(imageMetadata instanceof JpegImageMetadata))
        {
            return;
        }

        /*
         * EXIFデータの更新
         */
        TiffImageMetadata tiffImageMetadata = ((JpegImageMetadata)imageMetadata).getExif();
        TiffOutputSet outputSet = tiffImageMetadata.getOutputSet();
        TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();

        Arrays.asList(metadata).forEach(v -> {

            try
            {
                _updateExifMetadata(exifDirectory, v.getTagInfo(), v.getValue());
            }
            catch (ImageWriteException e)
            {
                e.printStackTrace();
            }

        });

        /*
         * EXIFデータを更新したファイルを保存
         */
        Path tmpfile = Files.createTempFile(target.getParent(), ".", "");

        try (OutputStream outputStream = Files.newOutputStream(tmpfile))
        {
            new ExifRewriter().updateExifMetadataLossless(target.toFile(), outputStream, outputSet);
        }

        Files.move(tmpfile, target, REPLACE_EXISTING);
    }

    private void _updateExifMetadata(TiffOutputDirectory directory, TagInfo tagInfo, Object value)
        throws ImageWriteException
    {
        if (directory == null)
        {
            return;
        }

        if (tagInfo == null)
        {
            return;
        }

        if (tagInfo instanceof TagInfoAscii)
        {
            __updateExifMetadata(directory, (TagInfoAscii)tagInfo, value);
        }
        else
        {
            //TODO: 必要に応じて別の型の処理を追加
            throw new UnsupportedOperationException(
                "Type '" + tagInfo.getClass().getSimpleName() + "' is not supported.");
        }
    }

    private void __updateExifMetadata(TiffOutputDirectory directory, TagInfoAscii tagInfo, Object value)
        throws ImageWriteException
    {
        if (directory.findField(tagInfo) != null)
        {
            directory.removeField(tagInfo);
        }

        if (value != null)
        {
            String stringValue = null;

            if (value instanceof String)
            {
                stringValue = (String)value;
            }
            else if (value instanceof Integer)
            {
                stringValue = String.valueOf(value);
            }
            else if (value instanceof Date)
            {
                stringValue = EXIF_DATE_FORMAT.format((Date)value);
            }
            else
            {
                //TODO: 必要に応じて別の型の処理を追加
                throw new UnsupportedOperationException(
                    "Type '" + value.getClass().getSimpleName() + "' is not supported.");
            }

            directory.add(tagInfo, stringValue);
        }
    }
}
