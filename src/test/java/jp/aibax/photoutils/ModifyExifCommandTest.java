package jp.aibax.photoutils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jp.aibax.image.Exif;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Calendar.DATE;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ModifyExifCommandTest
{
    private static final DateFormat DEBUG_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

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

    @Test
    public void testFileExists()
    {
        Path path = Paths.get("./testdata/sample.jpg");
        assertTrue(path.toFile().exists());
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
     * 指定したDate オブジェクトの指定したフィールドの値を変更します
     */
    private Date _setDateField(Date original, int field, int amount)
    {
        if (original == null)
        {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(original);
        calendar.set(field, amount);

        return calendar.getTime();
    }

    /**
     * 指定したDate オブジェクトの指定したフィールドの値を加算します
     */
    private Date _addDateField(Date original, int field, int amount)
    {
        if (original == null)
        {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(original);
        calendar.add(field, amount);

        return calendar.getTime();
    }

    /**
     * DateTimeOriginal, SubSecTimeOriginal タグを削除するテスト
     */
    @Test
    public void testRemoveDateTimeOriginal()
    {
        Path original = Paths.get("./testdata/sample.jpg");

        ModifyExifCommand command = new ModifyExifCommand();

        try
        {
            // 変更前
            Exif before = Exif.decode(original);
            Date dateBefore = before.getDateTimeOriginal();
            Integer subSecTimeBefore = before.getSubSecTimeOriginal();

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            Date dateTimeOriginal = null;
            Integer subSecTimeOriginal = null;

            // 変更処理（テスト対象メソッド）
            command.set(testfile, dateTimeOriginal, subSecTimeOriginal, false);

            // 変更後
            Exif modified = Exif.decode(testfile);
            Date dateModified = modified.getDateTimeOriginal();
            Integer subSecTimeModified = modified.getSubSecTimeOriginal();

            // 結果の検証
            assertNotEquals(dateBefore, dateModified);
            assertNull(dateModified);
            assertNull(subSecTimeModified);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * DateTimeOriginal タグを更新するテスト
     * SubSecTimeOriginalは指定しない => タグを削除
     */
    @Test
    public void testModifyDateTimeOriginal_SubSecTimeOriginalIsNotDefined()
    {
        Path original = Paths.get("./testdata/sample.jpg");

        ModifyExifCommand command = new ModifyExifCommand();

        try
        {
            // 変更前
            Exif before = Exif.decode(original);
            Date dateBefore = before.getDateTimeOriginal();
            Integer subSecTimeBefore = before.getSubSecTimeOriginal();

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            Date now = _setDateField(new Date(), MILLISECOND, 0);

            Date dateTimeOriginal = now;
            Integer subSecTimeOriginal = null;  // SubSecTimeOriginalは指定しない => タグを削除

            // 変更処理（テスト対象メソッド）
            command.set(testfile, dateTimeOriginal, subSecTimeOriginal, false);

            // 変更後
            Exif modified = Exif.decode(testfile);
            Date dateModified = modified.getDateTimeOriginal();
            Integer subSecTimeModified = modified.getSubSecTimeOriginal();

            // 結果の検証
            assertNotEquals(dateBefore, dateModified);
            assertEquals(now, dateModified);
            assertNull(subSecTimeModified);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * DateTimeOriginal タグを更新するテスト
     * SubSecTimeOriginalに0を指定
     */
    @Test
    public void testModifyDateTimeOriginal_SubSecTimeOriginalIsZero()
    {
        Path original = Paths.get("./testdata/sample.jpg");

        ModifyExifCommand command = new ModifyExifCommand();

        try
        {
            // 変更前
            Exif before = Exif.decode(original);
            Date dateBefore = before.getDateTimeOriginal();
            Integer subSecTimeBefore = before.getSubSecTimeOriginal();

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            Date now = _setDateField(new Date(), MILLISECOND, 0);

            Date dateTimeOriginal = now;
            Integer subSecTimeOriginal = 0;  // SubSecTimeOriginalに0を指定

            // 変更処理（テスト対象メソッド）
            command.set(testfile, dateTimeOriginal, subSecTimeOriginal, false);

            // 変更後
            Exif modified = Exif.decode(testfile);
            Date dateModified = modified.getDateTimeOriginal();
            Integer subSecTimeModified = modified.getSubSecTimeOriginal();

            // 結果の検証
            assertNotEquals(dateBefore, dateModified);
            assertEquals(now, dateModified);
            assertNotNull(subSecTimeModified);
            assertEquals(0, subSecTimeModified.intValue());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * DateTimeOriginal タグを更新するテスト
     * SubSecTimeOriginalに123を指定
     */
    @Test
    public void testModifyDateTimeOriginal()
    {
        Path original = Paths.get("./testdata/sample.jpg");

        ModifyExifCommand command = new ModifyExifCommand();

        try
        {
            // 変更前
            Exif before = Exif.decode(original);
            Date dateBefore = before.getDateTimeOriginal();
            Integer subSecTimeBefore = before.getSubSecTimeOriginal();

            // テストデータの準備
            Path testfile = _prepareTestFile(original);
            testfiles.add(testfile);

            // 使用するデータ
            Date now = _setDateField(new Date(), MILLISECOND, 0);

            Date dateTimeOriginal = now;
            Integer subSecTimeOriginal = 123;  // SubSecTimeOriginalに123を指定

            // 変更処理（テスト対象メソッド）
            command.set(testfile, dateTimeOriginal, subSecTimeOriginal, false);

            // 変更後
            Exif modified = Exif.decode(testfile);
            Date dateModified = modified.getDateTimeOriginal();
            Integer subSecTimeModified = modified.getSubSecTimeOriginal();

            // 使用するデータ
            Date dateExpected = _setDateField(now, MILLISECOND, 123);

            // 結果の検証
            assertNotEquals(dateBefore, dateModified);
            assertEquals(dateExpected, dateModified);
            assertNotNull(subSecTimeModified);
            assertEquals(123, subSecTimeModified.intValue());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddDays()
    {
        Path original = Paths.get("./testdata/sample.jpg");

        ModifyExifCommand command = new ModifyExifCommand();

        try
        {
            Date originalDate = Exif.decode(original).getDateTimeOriginal();

            {
                int addDays = 1;
                Date expectDate = _addDateField(originalDate, DATE, addDays);

                Path testfile = _prepareTestFile(original);
                testfiles.add(testfile);

                command.adjust(testfile, addDays, 0, 0, 0, false);
                Date modifiedDate = Exif.decode(testfile).getDateTimeOriginal();

                assertNotEquals(originalDate, modifiedDate);
                assertEquals(expectDate, modifiedDate);
            }

            {
                int addDays = -2;
                Date expectDate = _addDateField(originalDate, DATE, addDays);

                Path testfile = _prepareTestFile(original);
                testfiles.add(testfile);

                command.adjust(testfile, addDays, 0, 0, 0, false);
                Date modifiedDate = Exif.decode(testfile).getDateTimeOriginal();

                assertNotEquals(originalDate, modifiedDate);
                assertEquals(expectDate, modifiedDate);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddHours()
    {
        Path original = Paths.get("./testdata/sample.jpg");

        ModifyExifCommand command = new ModifyExifCommand();

        try
        {
            Date originalDate = Exif.decode(original).getDateTimeOriginal();

            {
                int addHour = 1;
                Date expectDate = _addDateField(originalDate, HOUR, addHour);

                Path testfile = _prepareTestFile(original);
                testfiles.add(testfile);

                command.adjust(testfile, 0, addHour, 0, 0, false);
                Date modifiedDate = Exif.decode(testfile).getDateTimeOriginal();

                assertNotEquals(originalDate, modifiedDate);
                assertEquals(expectDate, modifiedDate);
            }

            {
                int addHour = -2;
                Date expectDate = _addDateField(originalDate, HOUR, addHour);

                Path testfile = _prepareTestFile(original);
                testfiles.add(testfile);

                command.adjust(testfile, 0, addHour, 0, 0, false);
                Date modifiedDate = Exif.decode(testfile).getDateTimeOriginal();

                assertNotEquals(originalDate, modifiedDate);
                assertEquals(expectDate, modifiedDate);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddMinutes()
    {
        Path original = Paths.get("./testdata/sample.jpg");

        ModifyExifCommand command = new ModifyExifCommand();

        try
        {
            Date originalDate = Exif.decode(original).getDateTimeOriginal();

            {
                int addMinutes = 1;
                Date expectDate = _addDateField(originalDate, MINUTE, addMinutes);

                Path testfile = _prepareTestFile(original);
                testfiles.add(testfile);

                command.adjust(testfile, 0, 0, addMinutes, 0, false);
                Date modifiedDate = Exif.decode(testfile).getDateTimeOriginal();

                assertNotEquals(originalDate, modifiedDate);
                assertEquals(expectDate, modifiedDate);
            }

            {
                int addMinutes = -2;
                Date expectDate = _addDateField(originalDate, MINUTE, addMinutes);

                Path testfile = _prepareTestFile(original);
                testfiles.add(testfile);

                command.adjust(testfile, 0, 0, addMinutes, 0, false);
                Date modifiedDate = Exif.decode(testfile).getDateTimeOriginal();

                assertNotEquals(originalDate, modifiedDate);
                assertEquals(expectDate, modifiedDate);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testAddSeconds()
    {
        Path original = Paths.get("./testdata/sample.jpg");

        ModifyExifCommand command = new ModifyExifCommand();

        try
        {
            Date originalDate = Exif.decode(original).getDateTimeOriginal();

            {
                int addSeconds = 1;
                Date expectDate = _addDateField(originalDate, SECOND, addSeconds);

                Path testfile = _prepareTestFile(original);
                testfiles.add(testfile);

                command.adjust(testfile, 0, 0, 0, addSeconds, false);
                Date modifiedDate = Exif.decode(testfile).getDateTimeOriginal();

                assertNotEquals(originalDate, modifiedDate);
                assertEquals(expectDate, modifiedDate);
            }

            {
                int addSeconds = -2;
                Date expectDate = _addDateField(originalDate, SECOND, addSeconds);

                Path testfile = _prepareTestFile(original);
                testfiles.add(testfile);

                command.adjust(testfile, 0, 0, 0, addSeconds, false);
                Date modifiedDate = Exif.decode(testfile).getDateTimeOriginal();

                assertNotEquals(originalDate, modifiedDate);
                assertEquals(expectDate, modifiedDate);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
