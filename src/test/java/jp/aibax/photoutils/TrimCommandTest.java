package jp.aibax.photoutils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.aibax.image.ImageUtils;

import static java.awt.Color.GREEN;
import static java.awt.Color.WHITE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TrimCommandTest
{
    private List<Path> testfiles = new ArrayList<>();

    @Before
    public void setUp()
    {
        testfiles.clear();
    }

    @After
    public void tearDown()
    {
        testfiles.forEach(testfile -> {
            try
            {
                System.out.println("Delete file => " + testfile);
                Files.deleteIfExists(testfile);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    /**
     * テスト用画像データをコピーして、ユニットテストで使用する画像ファイルのパスを返します
     */
    private Path _prepareTestFile(Path original) throws IOException
    {
        if (original == null)
        {
            return null;
        }

        Path testfile = Files.createTempFile(original.toRealPath().getParent(), ".", "");
        Files.copy(original, testfile, REPLACE_EXISTING);

        return testfile;
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

                assertEquals(width, 1440);
                assertEquals(height, 1080);
                assertEquals(trimmedImage.getRGB(0, 0), GREEN.getRGB());
                assertEquals(trimmedImage.getRGB(width - 1, height - 1), GREEN.getRGB());
                assertEquals(trimmedImage.getRGB(width / 2, height / 2), WHITE.getRGB());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * 長辺の長さを指定して画像をリサイズするテスト（横長の画像）
     */
    @Test
    public void testTrim_Landscape()
    {
        Path original = Paths.get("./testdata/1920x1080.png");
        assertTrue(Files.exists(original));

        TrimCommand command = new TrimCommand();

        try
        {
            // 変更前
            BufferedImage originalImage = ImageUtils.readImage(original);

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            float aspectRatio = (float)4 / (float)3; // 1920x1080 16:9 → 4:3 (1.33)

            // 変更処理（テスト対象メソッド）
            command.trim(testfile, aspectRatio, false);

            // 変更後
            BufferedImage trimmedImage = ImageUtils.readImage(testfile);
            int width = trimmedImage.getWidth();
            int height = trimmedImage.getHeight();

            // 結果の検証
            assertNotNull(originalImage);
            assertEquals(1920, originalImage.getWidth());
            assertEquals(1080, originalImage.getHeight());

            assertNotNull(trimmedImage);
            assertEquals(1440, width);
            assertEquals(1080, height);
            assertEquals(GREEN.getRGB(), trimmedImage.getRGB(0, 0));
            assertEquals(GREEN.getRGB(), trimmedImage.getRGB(width - 1, height - 1));
            assertEquals(WHITE.getRGB(), trimmedImage.getRGB(width / 2, height / 2));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * 長辺の長さを指定して画像をリサイズするテスト（縦長の画像）
     */
    @Test
    public void testResizeMax_Portrait()
    {
        Path original = Paths.get("./testdata/1440x1080.png");
        assertTrue(Files.exists(original));

        ResizeCommand command = new ResizeCommand();

        try
        {
            // 変更前
            BufferedImage originalImage = ImageUtils.readImage(original);

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            int max = 640;

            // 変更処理（テスト対象メソッド）
            command.resize(testfile, max, false);

            // 変更後
            BufferedImage resizedImage = ImageUtils.readImage(testfile);

            // 結果の検証
            assertNotNull(originalImage);
            assertEquals(1440, originalImage.getWidth());
            assertEquals(1080, originalImage.getHeight());
            assertNotNull(resizedImage);
            assertEquals(640, resizedImage.getWidth());
            assertEquals(480, resizedImage.getHeight());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * 長辺の長さを指定して画像をリサイズするテスト（正方形の画像）
     */
    @Test
    public void testResizeMax_Square()
    {
        Path original = Paths.get("./testdata/1080x1080.png");
        assertTrue(Files.exists(original));

        ResizeCommand command = new ResizeCommand();

        try
        {
            // 変更前
            BufferedImage originalImage = ImageUtils.readImage(original);

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            int max = 640;

            // 変更処理（テスト対象メソッド）
            command.resize(testfile, max, false);

            // 変更後
            BufferedImage resizedImage = ImageUtils.readImage(testfile);

            // 結果の検証
            assertNotNull(originalImage);
            assertEquals(1080, originalImage.getWidth());
            assertEquals(1080, originalImage.getHeight());
            assertNotNull(resizedImage);
            assertEquals(640, resizedImage.getWidth());
            assertEquals(640, resizedImage.getHeight());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * 幅と高さを指定して画像をリサイズするテスト
     * 16:9 → 4:3
     */
    @Test
    public void testResize_1920x1080_640x480()
    {
        Path original = Paths.get("./testdata/1920x1080.png");
        assertTrue(Files.exists(original));

        ResizeCommand command = new ResizeCommand();

        try
        {
            // 変更前
            BufferedImage originalImage = ImageUtils.readImage(original);

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            int width = 640;
            int height = 480;

            // 変更処理（テスト対象メソッド）
            command.resize(testfile, width, height, false);

            // 変更後
            BufferedImage resizedImage = ImageUtils.readImage(testfile);

            // 結果の検証
            assertNotNull(originalImage);
            assertEquals(1920, originalImage.getWidth());
            assertEquals(1080, originalImage.getHeight());
            assertNotNull(resizedImage);
            assertEquals(640, resizedImage.getWidth());
            assertEquals(480, resizedImage.getHeight());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * 幅と高さを指定して画像をリサイズするテスト
     * 9:16 → 4:3
     */
    @Test
    public void testResize_1080x1920_640x480()
    {
        Path original = Paths.get("./testdata/1080x1920.png");
        assertTrue(Files.exists(original));

        ResizeCommand command = new ResizeCommand();

        try
        {
            // 変更前
            BufferedImage originalImage = ImageUtils.readImage(original);

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            int width = 640;
            int height = 480;

            // 変更処理（テスト対象メソッド）
            command.resize(testfile, width, height, false);

            // 変更後
            BufferedImage resizedImage = ImageUtils.readImage(testfile);

            // 結果の検証
            assertNotNull(originalImage);
            assertEquals(1080, originalImage.getWidth());
            assertEquals(1920, originalImage.getHeight());
            assertNotNull(resizedImage);
            assertEquals(640, resizedImage.getWidth());
            assertEquals(480, resizedImage.getHeight());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * 幅のみを指定して画像をリサイズするテスト
     * 16:9 → 16:9
     */
    @Test
    public void testResize_1920x1080_640xAuto()
    {
        Path original = Paths.get("./testdata/1920x1080.png");
        assertTrue(Files.exists(original));

        ResizeCommand command = new ResizeCommand();

        try
        {
            // 変更前
            BufferedImage originalImage = ImageUtils.readImage(original);

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            int width = 640;
            int height = 0;

            // 変更処理（テスト対象メソッド）
            command.resize(testfile, width, height, false);

            // 変更後
            BufferedImage resizedImage = ImageUtils.readImage(testfile);

            // 結果の検証
            assertNotNull(originalImage);
            assertEquals(1920, originalImage.getWidth());
            assertEquals(1080, originalImage.getHeight());
            assertNotNull(resizedImage);
            assertEquals(640, resizedImage.getWidth());
            assertEquals(360, resizedImage.getHeight());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * 幅のみを指定して画像をリサイズするテスト
     * 9:16 → 9:16
     */
    @Test
    public void testResize_1080x1920_540xAuto()
    {
        Path original = Paths.get("./testdata/1080x1920.png");
        assertTrue(Files.exists(original));

        ResizeCommand command = new ResizeCommand();

        try
        {
            // 変更前
            BufferedImage originalImage = ImageUtils.readImage(original);

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            int width = 540;
            int height = 0;

            // 変更処理（テスト対象メソッド）
            command.resize(testfile, width, height, false);

            // 変更後
            BufferedImage resizedImage = ImageUtils.readImage(testfile);

            // 結果の検証
            assertNotNull(originalImage);
            assertEquals(1080, originalImage.getWidth());
            assertEquals(1920, originalImage.getHeight());
            assertNotNull(resizedImage);
            assertEquals(540, resizedImage.getWidth());
            assertEquals(960, resizedImage.getHeight());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * 高さのみを指定して画像をリサイズするテスト
     * 16:9 → 16:9
     */
    @Test
    public void testResize_1920x1080_Autox270()
    {
        Path original = Paths.get("./testdata/1920x1080.png");
        assertTrue(Files.exists(original));

        ResizeCommand command = new ResizeCommand();

        try
        {
            // 変更前
            BufferedImage originalImage = ImageUtils.readImage(original);

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            int width = 0;
            int height = 270;

            // 変更処理（テスト対象メソッド）
            command.resize(testfile, width, height, false);

            // 変更後
            BufferedImage resizedImage = ImageUtils.readImage(testfile);

            // 結果の検証
            assertNotNull(originalImage);
            assertEquals(1920, originalImage.getWidth());
            assertEquals(1080, originalImage.getHeight());
            assertNotNull(resizedImage);
            assertEquals(480, resizedImage.getWidth());
            assertEquals(270, resizedImage.getHeight());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * 高さのみを指定して画像をリサイズするテスト
     * 9:16 → 9:16
     */
    @Test
    public void testResize_1080x1920_Autox480()
    {
        Path original = Paths.get("./testdata/1080x1920.png");
        assertTrue(Files.exists(original));

        ResizeCommand command = new ResizeCommand();

        try
        {
            // 変更前
            BufferedImage originalImage = ImageUtils.readImage(original);

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            int width = 0;
            int height = 480;

            // 変更処理（テスト対象メソッド）
            command.resize(testfile, width, height, false);

            // 変更後
            BufferedImage resizedImage = ImageUtils.readImage(testfile);

            // 結果の検証
            assertNotNull(originalImage);
            assertEquals(1080, originalImage.getWidth());
            assertEquals(1920, originalImage.getHeight());
            assertNotNull(resizedImage);
            assertEquals(270, resizedImage.getWidth());
            assertEquals(480, resizedImage.getHeight());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
