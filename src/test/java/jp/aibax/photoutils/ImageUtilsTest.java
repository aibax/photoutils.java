package jp.aibax.photoutils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import jp.aibax.image.ImageUtils;

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
}
