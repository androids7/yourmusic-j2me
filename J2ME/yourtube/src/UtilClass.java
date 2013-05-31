import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;

public class UtilClass extends Object {
    private static final String HEXSTR         = "0123456789ABCDEF";
    private static final char   HEX[]          = "0123456789ABCDEF".toCharArray();
    private static final String URL_UNRESERVED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~";
    private static final String URL_ENCODEDOK  = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~%";

    private static final String USER_AGENT = "YourTube";

    public static String URLDecode(String str) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '%' && i < str.length() - 2) {
                if (HEXSTR.indexOf(str.charAt(i + 1)) >= 0 &&
                    HEXSTR.indexOf(str.charAt(i + 2)) >= 0) {
                    buf.append((char)(((HEXSTR.indexOf(str.charAt(i + 1)) << 4) + HEXSTR.indexOf(str.charAt(i + 2))) & 0xff));
                    i = i + 2;
                } else {
                    buf.append(str.charAt(i));
                }
            } else {
                buf.append(str.charAt(i));
            }
        }

        return buf.toString();
    }

    public static String URLEncode(String str) {
        byte                  bytes[] = new byte[0];
        ByteArrayOutputStream stream = null;
        DataOutputStream      data_stream = null;
        StringBuffer          buf = new StringBuffer();

        try {
            stream      = new ByteArrayOutputStream();
            data_stream = new DataOutputStream(stream);

            data_stream.writeUTF(str);

            bytes = stream.toByteArray();
        } catch (Exception ex) {
            // Ignore
        } finally {
            if (data_stream != null) {
                try {
                    data_stream.close();
                } catch (Exception ex) {
                    // Ignore
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }

        for (int i = 2; i < bytes.length; i++) {
            if (URL_UNRESERVED.indexOf(bytes[i]) >= 0) {
                buf.append((char)bytes[i]);
            } else {
                buf.append('%').append(HEX[(bytes[i] >> 4) & 0x0f]).append(HEX[bytes[i] & 0x0f]);
            }
        }

        return buf.toString();
    }

    public static boolean URLEncodedOk(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (URL_ENCODEDOK.indexOf(str.charAt(i)) == -1) {
                return false;
            }
        }

        return true;
    }
    
    public static String DurationToString(int duration) {
        int d =  duration / 86400;
        int h = (duration % 86400) / 3600;
        int m = (duration % 3600)  / 60;
        int s =  duration % 60;

        if (d == 0) {
            return ZeroPad(h) + ":" + ZeroPad(m) + ":" + ZeroPad(s);
        } else {
            return String.valueOf(d) + "d " + ZeroPad(h) + ":" + ZeroPad(m) + ":" + ZeroPad(s);
        }
    }

    public static String ZeroPad(int num) {
        if (num < 10) {
            return new String("0") + String.valueOf(num);
        } else {
            return                   String.valueOf(num);
        }
    }

    public static String MakeValidFilename(String string) {
        String file_name = new String(string);

        file_name = file_name.replace('\\', '_');
        file_name = file_name.replace('/',  '_');
        file_name = file_name.replace(':',  '_');
        file_name = file_name.replace('*',  '_');
        file_name = file_name.replace('?',  '_');
        file_name = file_name.replace('"',  '_');
        file_name = file_name.replace('\'', '_');
        file_name = file_name.replace('>',  '_');
        file_name = file_name.replace('<',  '_');
        file_name = file_name.replace('|',  '_');

        return file_name;
    }

    public static String MakeFullFileURL(String file_name) {
        FileConnection file = null;

        try {
            file = (FileConnection)Connector.open("file:///" + SettingStorageClass.GetDestinationDisk() + "DATA/Videos");

            if (file.exists()) {
                return "file:///" + SettingStorageClass.GetDestinationDisk() + "DATA/Videos/" + file_name;
            }
        } catch (Exception ex) {
            // Ignore
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }

        file = null;

        try {
            file = (FileConnection)Connector.open("file:///" + SettingStorageClass.GetDestinationDisk() + "Videos");

            if (file.exists()) {
                return "file:///" + SettingStorageClass.GetDestinationDisk() + "Videos/" + file_name;
            }
        } catch (Exception ex) {
            // Ignore
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }

        file = null;

        try {
            file = (FileConnection)Connector.open("file:///" + SettingStorageClass.GetDestinationDisk() + "DATA/Videos");

            file.mkdir();

            return "file:///" + SettingStorageClass.GetDestinationDisk() + "DATA/Videos/" + file_name;
        } catch (Exception ex) {
            // Ignore
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }

        file = null;

        try {
            file = (FileConnection)Connector.open("file:///" + SettingStorageClass.GetDestinationDisk() + "Videos");

            file.mkdir();

            return "file:///" + SettingStorageClass.GetDestinationDisk() + "Videos/" + file_name;
        } catch (Exception ex) {
            // Ignore
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }

        return "";
    }

    public static boolean ValidateFileConnectionAPI() {
        if (System.getProperty("microedition.io.file.FileConnection.version") != null) {
            return true;
        } else {
            return false;
        }
    }

    public static Vector GetFileSystemRoots() {
        Vector roots = new Vector();

        try {
            Enumeration root_enum = FileSystemRegistry.listRoots();

            while (root_enum.hasMoreElements()) {
                roots.addElement(root_enum.nextElement());
            }
        } catch (Exception ex) {
            // Ignore
        }

        return roots;
    }

    public static String GetDefaultVideoDisk() {
        String path = System.getProperty("fileconn.dir.videos");

        if (path != null) {
            int pos = path.indexOf("file:///");

            if (pos != -1) {
                path = path.substring(pos + 8, path.length());
            }

            pos = path.indexOf("/");

            if (pos != -1) {
                path = path.substring(0, pos + 1);
            }

            return path;
        } else {
            return "";
        }
    }

    public static Image LoadImageFromURL(String url) {
        Image          result = null;
        HttpConnection connection = null;
        InputStream    stream = null;

        try {
            connection = (HttpConnection)Connector.open(url);

            connection.setRequestMethod(HttpConnection.GET);
            connection.setRequestProperty("User-Agent", USER_AGENT);

            stream = connection.openInputStream();

            result = Image.createImage(stream);
        } catch (Exception ex) {
            result = null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception ex) {
                    // Ignore
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception ex) {
                    // Ignore
                }
            }
        }

        return result;
    }

    public static Image ResizeImageToWidth(Image image, int width) {
        int src_width  = image.getWidth();
        int src_height = image.getHeight();
        int height     = width * src_height / src_width;

        Image result      = Image.createImage(width, height);
        Graphics graphics = result.getGraphics();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                graphics.setClip(x, y, 1, 1);

                int dx = x * src_width  / width;
                int dy = y * src_height / height;

                graphics.drawImage(image, x - dx, y - dy, Graphics.LEFT | Graphics.TOP);
            }
        }

        return Image.createImage(result);
    }
}