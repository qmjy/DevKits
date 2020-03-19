package cn.devkits.client.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class DKFileUtilTest {
    @Test
    public void testIsTextFile() {
        if (DKSystemUtil.isWindows()) {
            File file = new File("C:\\Windows\\System32\\drivers\\etc\\hosts");
            assertTrue(DKFileUtil.isTextFile(file));
        } else {
            assertTrue(true);
        }
    }

    @Test
    public void testIsImg() {
        String file = this.getClass().getResource("/logo.png").getFile().toString();
        assertTrue(DKFileUtil.isImg(new File(file)));
    }


    @Test
    public void testFormatBytes() {
        if (DKSystemUtil.isWindows()) {
            assertEquals("1 KB", DKFileUtil.formatBytes(1024));
        } else {
            assertEquals("1 KiB", DKFileUtil.formatBytes(1024));
        }
    }


}
