package per.sunmes.cppe.io;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import per.sunmes.cppe.io.data.MPlist;
import per.sunmes.cppe.io.data.MPlist.Frame;

public class PlistPicExport {

    public static void export(String outdir, BufferedImage image, MPlist plist) {
        File outDir = new File(outdir);
        if (!outDir.exists() && !outDir.mkdirs()) {
             JOptionPane.showMessageDialog(null, "无法创建导出目录!", "出错了!", JOptionPane.ERROR_MESSAGE);
        }
        try {
            String type = plist.metadata.realTextureFileName.substring(plist.metadata.realTextureFileName.lastIndexOf('.') + 1);
            StringBuilder sb = new StringBuilder(outDir.getAbsolutePath());
            sb.append('/');
            if (image == null) {
                JOptionPane.showMessageDialog(null, "图片数据不存在!", "出错了!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (Frame f : plist.frames) {
                sb.delete(outdir.length() + 1, sb.length());
                BufferedImage cImg = new BufferedImage(f.sourceSize[0], f.sourceSize[1], BufferedImage.TYPE_4BYTE_ABGR);
                if (f.rotated) {
                    Graphics2D g = ((Graphics2D) cImg.getGraphics());
                    g.rotate(-Math.PI / 2, 0, cImg.getHeight());
                    g.drawImage(image, 0, cImg.getHeight(), cImg.getHeight(), cImg.getWidth() + cImg.getHeight(), f.frame[0], f.frame[1], f.frame[0] + f.frame[3], f.frame[1] + f.frame[2], null);

                } else {
                    cImg.getGraphics().drawImage(image, 0, 0, cImg.getWidth(), cImg.getHeight(), f.frame[0], f.frame[1], f.frame[2] + f.frame[0], f.frame[3] + f.frame[1], null);
                }

                sb.append(f.name);
                createParentDirs(sb.toString());
                boolean result = ImageIO.write(cImg, type, new File(sb.toString()));
                //System.out.println("Out File : " + sb.toString() + " //" + result);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("IO异常!\r\n%s",e), "出错了!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void createParentDirs(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        sb.delete(sb.lastIndexOf("/"), sb.length());
        File dir = new File(sb.toString());
        if (dir.exists() && dir.isDirectory()) {
            return;
        }
        //System.out.println("Create directories: " + sb.toString());
        if (!dir.mkdirs()) {
             JOptionPane.showMessageDialog(null, "无法创建导出目录!", "出错了!", JOptionPane.ERROR_MESSAGE);
        }
    }
}
