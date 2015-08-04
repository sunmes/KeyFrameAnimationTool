/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package per.sunmes.cppe.io;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import per.sunmes.cppe.io.data.MPlist;
import per.sunmes.kfat.data.AnimationProjectInfo;
import per.sunmes.kfat.sys.SysData;

/**
 *
 * @author Administrator
 */
public class PlistUtil {

    /**
     * 根据Plist和小图名称,获取图像对象.
     *
     * @param imageName
     * @param plist
     * @return
     */
    public static BufferedImage getImageFromPlist(String imageName, MPlist plist) {
        BufferedImage img = null;
        if (plist == null) {
            return null;
        }
        BufferedImage srcImg = SysData.instance().cachedImages.get(plist.metadata.realTextureFileName);
//        try {
//            AnimationProjectInfo project = SysData.instance().getProject();
//            if (project != null) {
//                srcImg = ImageIO.read(new File(project.getImageFilePath(plist.metadata.realTextureFileName)));
//            } else {
//                srcImg = ImageIO.read(new File(plist.metadata.realTextureFileName));
//            }
//        } catch (IOException ex) {
//            JOptionPane.showMessageDialog(SysData.instance().mf, String.format("载入Plist原始图\n[%s]\n失败", plist.metadata.realTextureFileName));
//            Logger.getLogger(PlistUtil.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
        if (imageName == null || imageName.isEmpty()) {
            return srcImg;
        }

        for (MPlist.Frame f : plist.frames) {
            if (imageName.equals(f.name)) {
                img = new BufferedImage(f.sourceSize[0], f.sourceSize[1], BufferedImage.TYPE_4BYTE_ABGR);
                if (f.rotated) {
                    Graphics2D g = ((Graphics2D) img.getGraphics());
                    g.rotate(-Math.PI / 2, 0, img.getHeight());
                    g.drawImage(srcImg, 0, img.getHeight(), img.getHeight(), img.getWidth() + img.getHeight(), f.frame[0], f.frame[1], f.frame[0] + f.frame[3], f.frame[1] + f.frame[2], null);
                } else {
                    img.getGraphics().drawImage(srcImg, 0, 0, img.getWidth(), img.getHeight(), f.frame[0], f.frame[1], f.frame[2] + f.frame[0], f.frame[3] + f.frame[1], null);
                }
            }
        }

        return img;
    }

}
