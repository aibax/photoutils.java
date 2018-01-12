package jp.aibax.photoutils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

import org.junit.Test;

import jp.aibax.image.ImageUtils;

import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static jp.aibax.image.ImageFormat.BMP;
import static jp.aibax.image.ImageFormat.GIF;
import static jp.aibax.image.ImageFormat.JPEG;
import static jp.aibax.image.ImageFormat.PNG;

public class ImageUtilsTest
{
    @Test
    public void testJPEG()
    {
        Path file = Paths.get("./testdata/image.jpg");
        assertTrue(Files.exists(file));

        try
        {
            assertEquals(JPEG, ImageUtils.getImageFormat(file));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testJPEGCMYK()
    {
        Path file = Paths.get("./testdata/image_cmyk.jpg");
        assertTrue(Files.exists(file));

        try
        {
            assertEquals(JPEG, ImageUtils.getImageFormat(file));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testPNG()
    {
        Path file = Paths.get("./testdata/image.png");
        assertTrue(Files.exists(file));

        try
        {
            assertEquals(PNG, ImageUtils.getImageFormat(file));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testGIF()
    {
        Path file = Paths.get("./testdata/image.gif");
        assertTrue(Files.exists(file));

        try
        {
            assertEquals(GIF, ImageUtils.getImageFormat(file));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testBMP()
    {
        Path file = Paths.get("./testdata/image.bmp");
        assertTrue(Files.exists(file));

        try
        {
            assertEquals(BMP, ImageUtils.getImageFormat(file));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testReadImageAndSize_1080x1440()
    {
        Path file = Paths.get("./testdata/1080x1440.png");
        assertTrue(Files.exists(file));

        try
        {
            BufferedImage image = ImageUtils.readImage(file);

            assertNotNull(image);
            assertEquals(1080, image.getWidth());
            assertEquals(1440, image.getHeight());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testReadImageAndSize_1080x1920()
    {
        Path file = Paths.get("./testdata/1080x1920.png");
        assertTrue(Files.exists(file));

        try
        {
            BufferedImage image = ImageUtils.readImage(file);

            assertNotNull(image);
            assertEquals(1080, image.getWidth());
            assertEquals(1920, image.getHeight());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testReadImageAndSize_1440x1080()
    {
        Path file = Paths.get("./testdata/1440x1080.png");
        assertTrue(Files.exists(file));

        try
        {
            BufferedImage image = ImageUtils.readImage(file);

            assertNotNull(image);
            assertEquals(1440, image.getWidth());
            assertEquals(1080, image.getHeight());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testReadImageAndSize_1920x1080()
    {
        Path file = Paths.get("./testdata/1920x1080.png");
        assertTrue(Files.exists(file));

        try
        {
            BufferedImage image = ImageUtils.readImage(file);

            assertNotNull(image);
            assertEquals(1920, image.getWidth());
            assertEquals(1080, image.getHeight());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testResize_LongSide640_Landscape()
    {
        Path original = Paths.get("./testdata/1440x1080.png");
        assertTrue(Files.exists(original));

        try
        {
            byte[] resized = ImageUtils.resize(original, 640);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(resized))
            {
                BufferedImage resizedImage = ImageIO.read(inputStream);

                assertEquals(640, resizedImage.getWidth());
                assertEquals(480, resizedImage.getHeight());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testResize_LongSide640_Portrait()
    {
        Path original = Paths.get("./testdata/1080x1440.png");
        assertTrue(Files.exists(original));

        try
        {
            byte[] resized = ImageUtils.resize(original, 640);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(resized))
            {
                BufferedImage resizedImage = ImageIO.read(inputStream);

                assertEquals(480, resizedImage.getWidth());
                assertEquals(640, resizedImage.getHeight());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testResize_LongSide640_Square()
    {
        Path original = Paths.get("./testdata/1080x1080.png");
        assertTrue(Files.exists(original));

        try
        {
            byte[] resized = ImageUtils.resize(original, 640);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(resized))
            {
                BufferedImage resizedImage = ImageIO.read(inputStream);

                assertEquals(640, resizedImage.getWidth());
                assertEquals(640, resizedImage.getHeight());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testResize_640x480()
    {
        Path original = Paths.get("./testdata/dog.jpg");
        assertTrue(Files.exists(original));

        try
        {
            byte[] resized = ImageUtils.resize(original, 640, 480);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(resized))
            {
                BufferedImage resizedImage = ImageIO.read(inputStream);

                assertEquals(640, resizedImage.getWidth());
                assertEquals(480, resizedImage.getHeight());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testResize_640x480_CMYK()
    {
        Path original = Paths.get("./testdata/dog_cmyk.jpg");
        assertTrue(Files.exists(original));

        try
        {
            byte[] resized = ImageUtils.resize(original, 640, 480);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(resized))
            {
                BufferedImage resizedImage = ImageIO.read(inputStream);

                assertEquals(640, resizedImage.getWidth());
                assertEquals(480, resizedImage.getHeight());
            }

            fail("ImageIOがCMYKカラースペースのJPEGファイルをサポートしたらこのテストが失敗する => CMYKのJPEGの処理でImageIOを使用するように変更");
        }
        catch (IIOException e)
        {
            /* ImageIO is not supported CMYK color space. */
            System.out.println("[SKIP] ImageIO is not supported CMYK color space.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testResize_640xAuto()
    {
        Path original = Paths.get("./testdata/dog.jpg");
        assertTrue(Files.exists(original));

        try
        {
            byte[] resized = ImageUtils.resize(original, 640, 0);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(resized))
            {
                BufferedImage resizedImage = ImageIO.read(inputStream);

                assertEquals(640, resizedImage.getWidth());
                assertEquals(480, resizedImage.getHeight());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testResize_Autox480()
    {
        Path original = Paths.get("./testdata/dog.jpg");
        assertTrue(Files.exists(original));

        try
        {
            byte[] resized = ImageUtils.resize(original, 0, 480);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(resized))
            {
                BufferedImage resizedImage = ImageIO.read(inputStream);

                assertEquals(640, resizedImage.getWidth());
                assertEquals(480, resizedImage.getHeight());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testResize_100x100()
    {
        Path original = Paths.get("./testdata/dog.jpg");
        assertTrue(Files.exists(original));

        try
        {
            byte[] resized = ImageUtils.resize(original, 100, 100);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(resized))
            {
                BufferedImage resizedImage = ImageIO.read(inputStream);

                assertEquals(100, resizedImage.getWidth());
                assertEquals(100, resizedImage.getHeight());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testTrim_1920x1080_133()
    {
        Path original = Paths.get("./testdata/1920x1080.png");

        // 1920x1080 16:9 → 4:3 (1.33)
        float aspectRatio = (float)4 / (float)3;

        try
        {
            byte[] trimmed = ImageUtils.trim(original, aspectRatio);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(trimmed))
            {
                BufferedImage trimmedImage = ImageIO.read(inputStream);

                int width = trimmedImage.getWidth();
                int height = trimmedImage.getHeight();

                assertEquals(1440, width);
                assertEquals(1080, height);
                assertEquals(GREEN.getRGB(), trimmedImage.getRGB(0, 0));
                assertEquals(GREEN.getRGB(), trimmedImage.getRGB(width - 1, height - 1));
                assertEquals(WHITE.getRGB(), trimmedImage.getRGB(width / 2, height / 2));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testTrim_1440x1080_178()
    {
        Path original = Paths.get("./testdata/1440x1080.png");

        // 1440x1080 4:3 → 16:9 (1.78)
        float aspectRatio = (float)16 / (float)9;

        try
        {
            byte[] trimmed = ImageUtils.trim(original, aspectRatio);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(trimmed))
            {
                BufferedImage trimmedImage = ImageIO.read(inputStream);

                int width = trimmedImage.getWidth();
                int height = trimmedImage.getHeight();

                assertEquals(1440, width);
                assertEquals(810, height);
                assertEquals(BLUE.getRGB(), trimmedImage.getRGB(0, 0));
                assertEquals(BLUE.getRGB(), trimmedImage.getRGB(width - 1, height - 1));
                assertEquals(WHITE.getRGB(), trimmedImage.getRGB(width / 2, height / 2));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testTrim_1080x1920_133()
    {
        Path original = Paths.get("./testdata/1080x1920.png");

        // 1080x1920 9:16 → 4:3 (1.33)
        float aspectRatio = (float)4 / (float)3;

        try
        {
            byte[] trimmed = ImageUtils.trim(original, aspectRatio);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(trimmed))
            {
                BufferedImage trimmedImage = ImageIO.read(inputStream);

                int width = trimmedImage.getWidth();
                int height = trimmedImage.getHeight();

                assertEquals(1080, width);
                assertEquals(810, height);
                assertEquals(GREEN.getRGB(), trimmedImage.getRGB(0, 0));
                assertEquals(GREEN.getRGB(), trimmedImage.getRGB(width - 1, height - 1));
                assertEquals(WHITE.getRGB(), trimmedImage.getRGB(width / 2, height / 2));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testTrim_1080x1920_178()
    {
        Path original = Paths.get("./testdata/1080x1920.png");

        // 1080x1920 9:16 → 16:9 (1.78)
        float aspectRatio = (float)16 / (float)9;

        try
        {
            byte[] trimmed = ImageUtils.trim(original, aspectRatio);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(trimmed))
            {
                BufferedImage trimmedImage = ImageIO.read(inputStream);

                int width = trimmedImage.getWidth();
                int height = trimmedImage.getHeight();

                assertEquals(1080, width);
                assertEquals(608, height);
                assertEquals(BLUE.getRGB(), trimmedImage.getRGB(0, 0));
                assertEquals(BLUE.getRGB(), trimmedImage.getRGB(width - 1, height - 1));
                assertEquals(WHITE.getRGB(), trimmedImage.getRGB(width / 2, height / 2));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testTrim_1080x1440_133()
    {
        Path original = Paths.get("./testdata/1080x1440.png");

        // 1080x1440 3:4 → 4:3 (1.33)
        float aspectRatio = (float)4 / (float)3;

        try
        {
            byte[] trimmed = ImageUtils.trim(original, aspectRatio);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(trimmed))
            {
                BufferedImage trimmedImage = ImageIO.read(inputStream);

                int width = trimmedImage.getWidth();
                int height = trimmedImage.getHeight();

                assertEquals(1080, width);
                assertEquals(810, height);
                assertEquals(GREEN.getRGB(), trimmedImage.getRGB(0, 0));
                assertEquals(GREEN.getRGB(), trimmedImage.getRGB(width - 1, height - 1));
                assertEquals(WHITE.getRGB(), trimmedImage.getRGB(width / 2, height / 2));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testTrim_1080x1440_178()
    {
        Path original = Paths.get("./testdata/1080x1440.png");

        // 1080x1440 3:4 → 16:9 (1.78)
        float aspectRatio = (float)16 / (float)9;

        try
        {
            byte[] trimmed = ImageUtils.trim(original, aspectRatio);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(trimmed))
            {
                BufferedImage trimmedImage = ImageIO.read(inputStream);

                int width = trimmedImage.getWidth();
                int height = trimmedImage.getHeight();

                assertEquals(1080, width);
                assertEquals(608, height);
                assertEquals(BLUE.getRGB(), trimmedImage.getRGB(0, 0));
                assertEquals(BLUE.getRGB(), trimmedImage.getRGB(width - 1, height - 1));
                assertEquals(WHITE.getRGB(), trimmedImage.getRGB(width / 2, height / 2));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSquare_1920x1080()
    {
        Path original = Paths.get("./testdata/1920x1080.png");

        // 1920x1080 16:9 → 1:1
        try
        {
            byte[] squared = ImageUtils.square(original);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(squared))
            {
                BufferedImage squaredImage = ImageIO.read(inputStream);

                int width = squaredImage.getWidth();
                int height = squaredImage.getHeight();

                assertEquals(1080, width);
                assertEquals(1080, height);
                assertEquals(RED.getRGB(), squaredImage.getRGB(0, 0));
                assertEquals(RED.getRGB(), squaredImage.getRGB(width - 1, height - 1));
                assertEquals(WHITE.getRGB(), squaredImage.getRGB(width / 2, height / 2));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSquare_1440x1080()
    {
        Path original = Paths.get("./testdata/1440x1080.png");

        // 1440x1080 4:3 → 1:1
        try
        {
            byte[] squared = ImageUtils.square(original);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(squared))
            {
                BufferedImage squaredImage = ImageIO.read(inputStream);

                int width = squaredImage.getWidth();
                int height = squaredImage.getHeight();

                assertEquals(1080, width);
                assertEquals(1080, height);
                assertEquals(RED.getRGB(), squaredImage.getRGB(0, 0));
                assertEquals(RED.getRGB(), squaredImage.getRGB(width - 1, height - 1));
                assertEquals(WHITE.getRGB(), squaredImage.getRGB(width / 2, height / 2));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSquare_1080x1920()
    {
        Path original = Paths.get("./testdata/1080x1920.png");

        // 1080x1920 9:16 → 1:1
        try
        {
            byte[] squared = ImageUtils.square(original);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(squared))
            {
                BufferedImage squaredImage = ImageIO.read(inputStream);

                int width = squaredImage.getWidth();
                int height = squaredImage.getHeight();

                assertEquals(1080, width);
                assertEquals(1080, height);
                assertEquals(RED.getRGB(), squaredImage.getRGB(0, 0));
                assertEquals(RED.getRGB(), squaredImage.getRGB(width - 1, height - 1));
                assertEquals(WHITE.getRGB(), squaredImage.getRGB(width / 2, height / 2));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSquare_1080x1440()
    {
        Path original = Paths.get("./testdata/1080x1440.png");
        // 1080x1440 3:4 → 1:1

        try
        {
            byte[] squared = ImageUtils.square(original);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(squared))
            {
                BufferedImage squaredImage = ImageIO.read(inputStream);

                int width = squaredImage.getWidth();
                int height = squaredImage.getHeight();

                assertEquals(1080, width);
                assertEquals(1080, height);
                assertEquals(RED.getRGB(), squaredImage.getRGB(0, 0));
                assertEquals(RED.getRGB(), squaredImage.getRGB(width - 1, height - 1));
                assertEquals(WHITE.getRGB(), squaredImage.getRGB(width / 2, height / 2));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSquare_CMYK()
    {
        Path original = Paths.get("./testdata/dog_cmyk.jpg");

        try
        {
            byte[] squared = ImageUtils.square(original);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(squared))
            {
                BufferedImage squaredImage = ImageIO.read(inputStream);

                int width = squaredImage.getWidth();
                int height = squaredImage.getHeight();

                assertEquals(2448, width);
                assertEquals(2448, height);
            }

            fail("ImageIOがCMYKカラースペースのJPEGファイルをサポートしたらこのテストが失敗する => CMYKのJPEGの処理でImageIOを使用するように変更");
        }
        catch (IIOException e)
        {
            /* ImageIO is not supported CMYK color space. */
            System.out.println("[SKIP] ImageIO is not supported CMYK color space.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
