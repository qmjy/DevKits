package cn.devkits.client.util;

import static org.junit.Assert.*;

import java.io.File;

import com.drew.metadata.Metadata;
import org.junit.Test;

public class DKFileUtilTest {

    @Test
    public void testGetMetadataOfFile() {
        String file = this.getClass().getResource("/logo.png").getFile().toString();
        Metadata metadata = DKFileUtil.getMetadataOfFile(new File(file));
        System.out.println();
    }

    @Test
    public void testIsTextFile() {
        if (DKSysUtil.isWindows()) {
            File file = new File("C:\\Windows\\System32\\drivers\\etc\\hosts");
            assertTrue(DKFileUtil.isTextFile(file));
        } else {
            assertTrue(true);
        }
    }

    @Test
    public void testIsImg() {
        String file = this.getClass().getResource("/logo.png").getFile().toString();
        assertTrue(DKFileUtil.isRealImg(new File(file)));
    }


    @Test
    public void testFormatBytes() {
        if (DKSysUtil.isWindows()) {
            assertEquals("1 KB", DKFileUtil.formatBytes(1024));
        } else {
            assertEquals("1 KiB", DKFileUtil.formatBytes(1024));
        }
    }


}
