package jp.aibax.photoutils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import static java.util.Calendar.OCTOBER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExifRenameCommandTest
{
    @Test
    public void testFileExists()
    {
        Path path = Paths.get("./testdata/sample.jpg");
        assertTrue(path.toFile().exists());
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

            Date d20151029110857 = new GregorianCalendar(2015, OCTOBER, 29, 11, 8, 57).getTime();
            assertEquals(d20151029110857, exif.getDateTimeOriginal());
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
