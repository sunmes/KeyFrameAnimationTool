package per.sunmes.cppe.io.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plist file format rootElement plist[version] frames { frame offset rotated
 * sourceColorRect sourceSize } metadata { format realTextureFileName size
 * smartupdate textureFileName }
 *
 */
public class MPlist {

    public class Frame {

        public String name;
        public int[] frame = new int[4];
        public int[] offset = new int[2];
        public boolean rotated = false;
        public int[] sourceColorRect = new int[4];
        public int[] sourceSize = new int[2];

        @Override
        public String toString() {
            return name;
        }

        public String toStringInfos() {
            return String.format(
                    "Frame{"
                    + "\r\n  name[%s],"
                    + "\r\n  frame[(%d,%d),(%d,%d)],"
                    + "\r\n  offset[%d,%d],"
                    + "\r\n  rotated[%s],"
                    + "\r\n  sourceColorRect[(%d,%d),(%d,%d)],"
                    + "\r\n  sourceSize[%d,%d]"
                    + "\r\n}", name, frame[0], frame[1], frame[2], frame[3],
                    offset[0], offset[1],
                    rotated,
                    sourceColorRect[0], sourceColorRect[1],
                    sourceColorRect[2], sourceColorRect[3],
                    sourceSize[0], sourceSize[1]
            );
        }
    }

    public class Metadata {

        public int format;
        public String realTextureFileName;
        public int[] size = new int[2];
        public String smartupdate;
        public String textureFileName;

        @Override
        public String toString() {
            return String.format(
                    "Metadata{"
                    + "\r\n  format[%d],"
                    + "\r\n  realTextureFileName[%s],"
                    + "\r\n  size[%d,%d],"
                    + "\r\n  smartupdate[%s],"
                    + "\r\n  textureFileName[%s]"
                    + "\r\n}", format,
                    realTextureFileName,
                    size[0], size[1],
                    smartupdate,
                    textureFileName
            );
        }
    }

    public String version;
    public List<Frame> frames;
    public Metadata metadata;

    public Map<String, Frame> imageFrames = new HashMap<>();

    public static int[] getPoints(String string) {
        int[] result;
        int p = 0, e = 0;
        if (string.charAt(1) == '{') {
            result = new int[4];
            p = string.indexOf(',', 2);
            result[0] = Integer.parseInt(string.substring(2, p++));
            e = string.indexOf('}', p);
            result[1] = Integer.parseInt(string.substring(p, e));
            p = e + 3;
            e = string.indexOf(',', p);
            result[2] = Integer.parseInt(string.substring(p, e++));
            p = string.indexOf('}', e);
            result[3] = Integer.parseInt(string.substring(e, p));
        } else {
            result = new int[2];
            p = string.indexOf(',', 1);
            result[0] = Integer.parseInt(string.substring(1, p++));
            e = string.indexOf('}', p);
            result[1] = Integer.parseInt(string.substring(p, e));
        }
        return result;
    }

}
