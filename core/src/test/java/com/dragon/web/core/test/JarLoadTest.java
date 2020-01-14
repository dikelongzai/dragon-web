package com.dragon.web.core.test;

import com.dragon.web.common.util.FileUtil;
import org.junit.Test;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-07 11:42
 */
public class JarLoadTest {
    public static String url = "G:\\new_emoss_jars\\emoss_system.jar";

    @Test
    public void testLoadJar() throws Exception {
        JarFile file = new JarFile(url);
        try {
            Enumeration enums = file.entries();
            while (enums.hasMoreElements()) {
                JarEntry entry = (JarEntry) enums.nextElement();
                String fn = "jar://" + entry.getName();

                System.out.println(fn);
            }
        } finally {
            file.close();
        }

    }
}
