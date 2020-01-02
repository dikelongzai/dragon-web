package com.dragon.web.common.util;

import com.dragon.web.common.security.Log;
import info.monitorenter.cpdetector.io.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-02 18:24
 */
public class FileUtil {
    private static final String ENCODING_UTF_8 = "UTF-8";
    private static Class resourceClass = FileUtil.class;

    public static void setResourceClass(Class resourceClass) {
        resourceClass = resourceClass;
    }

    public static Class getResourceClass() {
        return resourceClass;
    }

    public static String readString(String filename) {
        return readString(filename, "UTF-8");
    }

    public static String readString(String filename, String encoding) {
        InputStream is = null;
        try {
            is = getInputStream(filename);
            String readStream = readStream(is, encoding);
            return readStream;
        } catch (Exception ex) {
            Log.debug(ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ex) {
                    Log.debug(ex);
                }
            }

        }

        return null;
    }

    public static String readStream(InputStream is) {
        return readStream(is, "UTF-8");
    }

    public static String readStream(InputStream is, String encoding) {
        BufferedReader fis = null;

        try {
            if (is == null) {
                return null;
            } else {
                StringBuffer buffer = new StringBuffer();
                fis = new BufferedReader(new InputStreamReader(is, encoding));
                String s = fis.readLine();

                while (s != null) {
                    buffer.append(s);
                    s = fis.readLine();
                    if (s != null) {
                        buffer.append("\n");
                    }
                }

                return buffer.toString();
            }
        } catch (Exception exp) {
            Log.debug(exp);
            return null;
        }
    }

    public static void writeString(String filename, String content) {
        writeString(filename, "UTF-8", content);
    }

    public static void writeString(String filename, String encoding, String content) {
        BufferedWriter fis = null;

        try {
            fis = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), encoding));
            fis.write(content);
        } catch (Exception var12) {
            throw new RuntimeException(var12.getMessage(), var12);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception var11) {
                    ;
                }
            }

        }

    }

    public static Document readDocument(String filename) {
        return readDocument(filename, "UTF-8");
    }

    public static Document readDocument(String filename, String encoding) {
        try {
            String s = readString(filename, encoding);
            if (s != null) {
                return DocumentHelper.parseText(s);
            }
        } catch (Exception exp) {
            Log.debug(exp);
        }

        return null;
    }

    public static void writeDocument(String filename, Document document) {
        writeDocument(filename, document, "UTF-8");
    }

    public static void writeDocument(String filename, Document document, String encoding) {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(encoding);
        XMLWriter writer = null;
        FileOutputStream fos = null;

        try {
            File file = new File(filename);
            if (!file.exists()) {
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            writer = new XMLWriter(fos, format);
            writer.write(document);
        } catch (Exception var14) {
            throw new RuntimeException(var14.getMessage(), var14);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }

                if (fos != null) {
                    fos.close();
                }
            } catch (Exception var13) {
                ;
            }

        }

    }

    public static InputStream getInputStream(String filename) {
        try {
            InputStream res = null;
            if (StringUtil.isEmpty(filename)) {
                return null;
            } else {
                if (filename.startsWith("jar://")) {
                    filename = filename.substring(6);
                    res = resourceClass.getResourceAsStream("/" + filename);
                } else {
                    File f = new File(filename);
                    if (f.exists() && f.isFile()) {
                        res = new FileInputStream(f);
                    }
                }

                return res;
            }
        } catch (Exception exp) {
            Log.debug(exp);
            return null;
        }
    }

    public static String getLastFilename(String path) {
        int i = 0;
        if (path.lastIndexOf("\\") > 0) {
            i = path.lastIndexOf("\\");
        }
        int j = 0;
        if (path.lastIndexOf("/") > 0) {
            j = path.lastIndexOf("/");
        }

        return i <= 0 && j <= 0 ? path : path.substring(Math.max(i, j) + 1);
    }

    public static void newFile(File file) throws Exception {
        if (!file.exists()) {
            file.createNewFile();
        }

    }

    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {

                    for (int index = 0; index < files.length; ++index) {
                        File f = files[index];
                        deleteFile(f);
                    }
                }
            }

            file.delete();
        }

    }

    public static boolean renameFile(String soruce, String dest, boolean check) {
        try {
            File f0 = new File(soruce);
            File f1 = new File(dest);
            if (check && f1.exists()) {
                throw new RuntimeException(dest + "已存在!");
            } else if (f0.exists() && !f1.exists()) {
                f0.renameTo(f1);
                return true;
            } else {
                return false;
            }
        } catch (Exception var5) {
            throw new RuntimeException("重命名失败", var5);
        }
    }

    public static String getFileEncode(String path) {
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        detector.add(new ParsingDetector(false));
        detector.add(JChardetFacade.getInstance());
        detector.add(ASCIIDetector.getInstance());
        detector.add(UnicodeDetector.getInstance());
        Charset charset = null;
        String encode = "";
        File f = new File(path);

        try {
            charset = detector.detectCodepage(f.toURI().toURL());
            encode = charset.name();
            if ("Windows-1252".equalsIgnoreCase(encode)) {
                encode = "Unicode";
            } else if ("UTF_8".equalsIgnoreCase(encode)) {
                encode = "UTF-8";
            }
        } catch (Exception exp) {
            Log.warn(exp);
        }
        if (StringUtil.isEmpty(encode)) {
            try {
                InputStream ios = new FileInputStream(f);
                byte[] b = new byte[3];
                ios.read(b);
                ios.close();
                if (b[0] == -17 && b[1] == -69 && b[2] == -65) {
                    encode = "UTF-8";
                }
            } catch (Exception var7) {
                ;
            }
        }

        return encode;
    }
}
