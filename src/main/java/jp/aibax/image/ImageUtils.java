package jp.aibax.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import static jp.aibax.image.ImageFormat.BMP;
import static jp.aibax.image.ImageFormat.GIF;
import static jp.aibax.image.ImageFormat.JPEG;
import static jp.aibax.image.ImageFormat.PNG;

public class ImageUtils
{
    /**
     * ヘッダと画像のフォーマットの対応リスト
     */
    private static final Map<byte[], ImageFormat> HEADER = new LinkedHashMap<>();

    static
    {
        /* JPEG */
        HEADER.put(new byte[] { (byte)0xFF, (byte)0xD8 }, JPEG);

        /* PNG */
        HEADER.put(new byte[] { (byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A,
            (byte)0x0A }, PNG);

        /* GIF */
        HEADER.put(new byte[] { (byte)0x47, (byte)0x49, (byte)0x46, (byte)0x38, (byte)0x37, (byte)0x61 }, GIF);
        HEADER.put(new byte[] { (byte)0x47, (byte)0x49, (byte)0x46, (byte)0x38, (byte)0x39, (byte)0x61 }, GIF);

        /* BMP */
        HEADER.put(new byte[] { (byte)0x42, (byte)0x4D }, BMP);
    }

    /**
     * 指定されたファイルを全てバイト配列に読み込みます
     *
     * @param file 読み込むファイル
     * @return 読み込んだファイルのデータ
     * @throws IOException
     */
    private static byte[] _validateAndReadAllBytes(Path file) throws IOException
    {
        if (file == null)
        {
            throw new IllegalArgumentException("File is not defined");
        }

        if (!Files.exists(file))
        {
            throw new FileNotFoundException(file.getFileName().toString());
        }

        if (Files.isDirectory(file))
        {
            throw new IllegalArgumentException("File is directory (" + file.getFileName().toString() + ")");

        }

        return Files.readAllBytes(file);
    }

    /**
     * 画像を読み込みます
     *
     * @param file 読み込む画像ファイル
     * @return 読み込んだ画像
     * @throws IOException
     */
    public static BufferedImage readImage(Path file) throws IOException
    {
        return readImage(_validateAndReadAllBytes(file));
    }

    /**
     * 画像を読み込みます
     *
     * @param image 読み込む画像データ
     * @return 読み込んだ画像
     * @throws IOException
     */
    public static BufferedImage readImage(byte[] image) throws IOException
    {
        if (image == null)
        {
            throw new IllegalArgumentException("Image is not defined");
        }

        BufferedImage bufferedImage = null;

        try
        {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(image))
            {
                bufferedImage = ImageIO.read(inputStream);
            }
        }
        catch (IIOException e)
        {
            throw e;
        }

        return bufferedImage;
    }

    /**
     * 画像のフォーマットを返します
     *
     * @param file 読み込む画像ファイル
     * @return 画像のフォーマット（フォーマットが識別できない場合はnull）
     */
    public static ImageFormat getImageFormat(Path file) throws IOException
    {
        return getImageFormat(_validateAndReadAllBytes(file));
    }

    /**
     * 画像のフォーマットを返します
     *
     * @param image 読み込む画像データ
     * @return 画像のフォーマット（フォーマットが識別できない場合はnull）
     */
    public static ImageFormat getImageFormat(byte[] image)
    {
        if (image == null)
        {
            throw new IllegalArgumentException("Image is not defined");
        }

        try
        {
            for (byte[] header : HEADER.keySet())
            {
                /* may throws ArrayIndexOutOfBoundsException */
                if (Arrays.equals(Arrays.copyOfRange(image, 0, header.length), header))
                {
                    return HEADER.get(header);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            return null;
        }

        return null;
    }
}
