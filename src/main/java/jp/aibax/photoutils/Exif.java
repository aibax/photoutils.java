package jp.aibax.photoutils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.constants.TagInfo;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.sanselan.formats.tiff.constants.ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL;
import static org.apache.sanselan.formats.tiff.constants.TiffTagConstants.TIFF_TAG_MAKE;
import static org.apache.sanselan.formats.tiff.constants.TiffTagConstants.TIFF_TAG_MODEL;

public class Exif
{
    private static final DateFormat EXIF_DATE_FORMAT = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

    private Path file = null;

    private Date lastModified = null;

    private String make = null;

    private String model = null;

    private Date dateTimeOriginal = null;

    private Exif()
    {
    }

    public static Exif decode(Path file) throws IOException
    {
        if (file == null)
        {
            throw new IllegalArgumentException("Path is not defined.");
        }

        if (!Files.exists(file))
        {
            throw new FileNotFoundException();
        }

        if (Files.isDirectory(file))
        {
            throw new IllegalArgumentException("File is directory.");
        }

        try
        {
            return _decode(file);
        }
        catch (ImageReadException e)
        {
            throw new RuntimeException("Cannot read metadata. (" + file + ")", e);
        }
    }

    private static Exif _decode(Path file) throws ImageReadException, IOException
    {
        Exif exif = new Exif();
        exif.file = file;

        /* EXIFデータの取得 */
        IImageMetadata imageMetadata = Sanselan.getMetadata(file.toFile());

        if (imageMetadata == null)
        {
            // No Metadata
            return exif;
        }

        if (!(imageMetadata instanceof JpegImageMetadata))
        {
            // Unsupported Image Type
            return exif;
        }

        /* EXIFデータの解析 */
        JpegImageMetadata jpegImageMetadata = (JpegImageMetadata)imageMetadata;

        exif.make = readMetadataAsString(jpegImageMetadata, TIFF_TAG_MAKE);
        exif.model = readMetadataAsString(jpegImageMetadata, TIFF_TAG_MODEL);
        exif.dateTimeOriginal = readMetadataAsDate(jpegImageMetadata, EXIF_TAG_DATE_TIME_ORIGINAL);

        exif.lastModified = new Date(Files.getLastModifiedTime(file).toMillis());

        return exif;
    }

    private static String readMetadataAsString(final JpegImageMetadata jpegImageMetadata, final TagInfo tagInfo)
    {
        if (jpegImageMetadata == null)
        {
            return null;
        }

        if (tagInfo == null)
        {
            return null;
        }

        TiffField field = jpegImageMetadata.findEXIFValue(tagInfo);

        if (field == null)
        {
            return null;
        }

        try
        {
            String value = field.getStringValue();

            if (value != null)
            {
                value = value.trim();
            }

            return value;
        }
        catch (ImageReadException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Date readMetadataAsDate(final JpegImageMetadata jpegImageMetadata, final TagInfo tagInfo)
    {
        String stringValue = readMetadataAsString(jpegImageMetadata, tagInfo);

        if (isEmpty(stringValue))
        {
            return null;
        }

        try
        {
            return EXIF_DATE_FORMAT.parse(stringValue);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getMake()
    {
        return this.make;
    }

    public String getModel()
    {
        return this.model;
    }

    public Date getDateTimeOriginal()
    {
        return this.dateTimeOriginal;
    }

    public Date getLastModified()
    {
        return this.lastModified;
    }

    public String toString()
    {
        String nl = "\n";

        StringBuilder s = new StringBuilder();
        s.append("[").append(file.getFileName()).append("]").append(nl);
        s.append("Make: ").append(make).append(nl);
        s.append("Model: ").append(model).append(nl);
        s.append("DateTimeOriginal: ")
            .append((dateTimeOriginal != null) ? EXIF_DATE_FORMAT.format(dateTimeOriginal) : "").append(nl);
        s.append("LastModified: ").append((lastModified != null) ? EXIF_DATE_FORMAT.format(lastModified) : "")
            .append(nl);

        return s.toString();
    }
}
