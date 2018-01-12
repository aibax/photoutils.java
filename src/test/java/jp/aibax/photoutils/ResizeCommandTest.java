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

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ResizeCommandTest
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

    /**
     * 長辺の長さを指定して画像をリサイズするテスト（横長の画像）
     */
    @Test
    public void testResizeMax_Landscape()
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
