package jp.aibax.image;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import jp.aibax.exception.UnsupportedImageFormatException;

import static java.awt.Image.SCALE_AREA_AVERAGING;
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

    /**
     * 画像を指定された長辺の長さにリサイズします
     *
     * @param file             リサイズする画像ファイル
     * @param lengthOfLongSide リサイズ後の長辺の長さ
     * @return リサイズされた画像
     * @throws UnsupportedImageFormatException 非対応の画像形式
     * @throws IOException
     */
    public static byte[] resize(Path file, int lengthOfLongSide) throws UnsupportedImageFormatException, IOException
    {
        return resize(_validateAndReadAllBytes(file), lengthOfLongSide);
    }

    /**
     * 画像を指定された長辺の長さにリサイズします
     *
     * @param image            リサイズする画像データ
     * @param lengthOfLongSide リサイズ後の長辺の長さ
     * @return リサイズされた画像
     * @throws UnsupportedImageFormatException 非対応の画像形式
     * @throws IOException
     */
    public static byte[] resize(byte[] image, int lengthOfLongSide) throws UnsupportedImageFormatException, IOException
    {
        if ((image == null) || (image.length == 0))
        {
            throw new IllegalArgumentException();
        }

        ImageFormat imageFormat = ImageUtils.getImageFormat(image);

        if (imageFormat == null)
        {
            throw new UnsupportedImageFormatException("Unsupported image format.");
        }

        BufferedImage sourceImage = readImage(image);

        float aspectRatio = (float)sourceImage.getWidth() / (float)sourceImage.getHeight();

        if (aspectRatio > 1)
        {
            /* 横長 */
            return resize(image, lengthOfLongSide, 0);
        }
        else
        {
            /* 縦長 */
            return resize(image, 0, lengthOfLongSide);
        }
    }

    /**
     * 画像を指定された幅と高さにリサイズします
     *
     * @param file   リサイズする画像ファイル
     * @param width  リサイズ後の幅（0の場合は縦横比を維持して自動計算）
     * @param height リサイズ後の高さ（0の場合は縦横比を維持して自動計算）
     * @return リサイズされた画像
     * @throws UnsupportedImageFormatException 非対応の画像形式
     * @throws IOException
     */
    public static byte[] resize(Path file, int width, int height) throws UnsupportedImageFormatException, IOException
    {
        return resize(_validateAndReadAllBytes(file), width, height);
    }

    /**
     * 画像を指定された幅と高さにリサイズします
     *
     * @param image  リサイズする画像データ
     * @param width  リサイズ後の幅（0の場合は縦横比を維持して自動計算）
     * @param height リサイズ後の高さ（0の場合は縦横比を維持して自動計算）
     * @return リサイズされた画像
     * @throws UnsupportedImageFormatException 非対応の画像形式
     * @throws IOException
     */
    public static byte[] resize(byte[] image, int width, int height) throws UnsupportedImageFormatException, IOException
    {
        if ((image == null) || (image.length == 0))
        {
            throw new IllegalArgumentException();
        }

        ImageFormat imageFormat = ImageUtils.getImageFormat(image);

        if (imageFormat == null)
        {
            throw new UnsupportedImageFormatException("Unsupported image format.");
        }

        BufferedImage sourceImage = readImage(image);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
        {
            width = (0 < width) ? width : -1;
            height = (0 < height) ? height : -1;

            Image scaledImage = sourceImage.getScaledInstance(width, height, SCALE_AREA_AVERAGING);

            width = scaledImage.getWidth(null);
            height = scaledImage.getHeight(null);

            BufferedImage resizedImage = new BufferedImage(width, height, sourceImage.getType());

            Graphics g = null;

            try
            {
                g = resizedImage.createGraphics();
                g.drawImage(scaledImage, 0, 0, width, height, null);
            }
            finally
            {
                if (g != null)
                {
                    g.dispose();
                }
            }

            ImageIO.write(resizedImage, imageFormat.getName(), outputStream);

            return outputStream.toByteArray();
        }
    }

    /**
     * 画像を指定されたアスペクト比（縦横比）でトリミングします
     *
     * @param file        トリミングする画像ファイル
     * @param aspectRatio アスペクト比（幅/高さ） 例) 1:1 = 1 / 4:3 = 1.33 / 16:9 = 1.78
     * @return リサイズされた画像
     * @throws UnsupportedImageFormatException 非対応の画像形式
     * @throws IOException
     */
    public static byte[] trim(Path file, float aspectRatio) throws UnsupportedImageFormatException, IOException
    {
        return trim(_validateAndReadAllBytes(file), aspectRatio);
    }

    /**
     * 画像を指定されたアスペクト比（縦横比）でトリミングします
     *
     * @param image       トリミングする画像のデータ
     * @param aspectRatio アスペクト比（幅/高さ） 例) 1:1 = 1 / 4:3 = 1.33 / 16:9 = 1.78
     * @return トリミングされた画像
     * @throws UnsupportedImageFormatException 非対応の画像形式
     * @throws IOException
     */
    public static byte[] trim(byte[] image, float aspectRatio) throws UnsupportedImageFormatException, IOException
    {
        if ((image == null) || (image.length == 0) || (aspectRatio < 0))
        {
            throw new IllegalArgumentException();
        }

        ImageFormat imageFormat = ImageUtils.getImageFormat(image);

        if (imageFormat == null)
        {
            throw new UnsupportedImageFormatException("Unsupported image format.");
        }

        BufferedImage sourceImage = readImage(image);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
        {
            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();
            int x = 0;
            int y = 0;

            float sourceAspectRatio = (float)width / (float)height;

            if (sourceAspectRatio == aspectRatio)
            {
                /* アスペクト比が一致 */
                return image;
            }

            if (sourceAspectRatio > aspectRatio)
            {
                /* 元画像のアスペクト比の方が大きい（元画像の方が横長） → 元画像の高さを基準に幅を計算 */

                /*
                 * [計算例]
                 * 元画像のサイズ 1920x1080 → アスペクト比 16:9 = 1.78
                 * トリミングするアスペクト比 4:3 = 1.33
                 * トリミング後の画像の幅 1080 x (4/3) = 1440
                 */

                width = Math.round(height * aspectRatio);

                x = Math.round((float)(sourceImage.getWidth() - width) / 2);
            }
            else
            {
                /* 元画像のアスペクト比の方が小さい（元画像の方が縦長） → 元画像の幅を基準に幅を計算 */

                /*
                 * [計算例]
                 * 元画像のサイズ 1440x1080 → アスペクト比 4:3 = 1.33
                 * トリミングするアスペクト比 16:9 = 1.78
                 * トリミング後の画像の高さ 1440 / (16/9) = 810
                 */

                height = Math.round(width / aspectRatio);

                y = Math.round((float)(sourceImage.getHeight() - height) / 2);
            }

            /* 画像のトリミング */
            BufferedImage squaredImage = sourceImage.getSubimage(x, y, width, height);

            ImageIO.write(squaredImage, imageFormat.getName(), outputStream);

            return outputStream.toByteArray();
        }
    }

    /**
     * 画像を正方形にトリミングします
     *
     * @param file トリミングする画像ファイル
     * @return トリミングされた画像
     * @throws UnsupportedImageFormatException 非対応の画像形式
     * @throws IOException
     */
    public static byte[] square(Path file) throws UnsupportedImageFormatException, IOException
    {
        return trim(file, 1);
    }

    /**
     * 画像を正方形にトリミングします
     *
     * @param image トリミングする画像のデータ
     * @return トリミングされた画像
     * @throws UnsupportedImageFormatException 非対応の画像形式
     * @throws IOException
     */
    public static byte[] square(byte[] image) throws UnsupportedImageFormatException, IOException
    {
        return trim(image, 1);
    }
}
