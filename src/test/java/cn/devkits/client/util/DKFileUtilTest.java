package cn.devkits.client.util;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.Test;

public class DKFileUtilTest {

    @Test
    public void testIsImg() {
        String file = this.getClass().getResource("/logo.png").getFile().toString();
        assertTrue(DKFileUtil.isImg(new File(file)));
    }

}
