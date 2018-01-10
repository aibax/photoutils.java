package jp.aibax.photoutils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.OCTOBER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExifTest
{
    @Test
    public void testFileExists()
    {
        Path path = Paths.get("./testdata/sample.jpg");
        assertTrue(path.toFile().exists());
    }

    private static Date d(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second)
    {
        return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second).getTime();
    }

    private static Date d(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, int millisecond)
    {
        Date date = d(year, month, dayOfMonth, hourOfDay, minute, second);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(MILLISECOND, millisecond);

        return calendar.getTime();
    }

    @Test
    public void testDecode()
    {
        Path path = Paths.get("./testdata/sample.jpg");

        try
        {
            Exif exif = Exif.decode(path);
            assertNotNull(exif);

            assertEquals("Apple", exif.getMake());
            assertEquals("iPhone 6", exif.getModel());

            Date d20151029110857789 = d(2015, OCTOBER, 29, 11, 8, 57, 789);
            assertEquals(d20151029110857789, exif.getDateTimeOriginal());

            Date d20151029110857 = d(2015, OCTOBER, 29, 11, 8, 57);
            assertEquals(d20151029110857, exif.getLastModified());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testToString()
    {
        Path path = Paths.get("./testdata/sample.jpg");

        try
        {
            Exif exif = Exif.decode(path);
            System.out.println(exif.toString());

            assertTrue(exif.toString().length() > 0);
            assertTrue(exif.toString().startsWith("[sample.jpg]"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
