/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package per.sunmes.kfat.sys;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.management.MemoryUsage;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLOutputFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import per.sunmes.cppe.io.PlistUtil;
import per.sunmes.cppe.io.data.MPlist;
import per.sunmes.kfat.data.AnimationInfo;
import per.sunmes.kfat.data.AnimationProjectInfo;
import per.sunmes.kfat.data.FrameInfo;
import per.sunmes.kfat.data.ImagePartInfo;

/**
 *
 * @author Administrator
 */
public class FileUtil {

    public static Object readObjectFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println(String.format("FileUtil::readObjectFormFile(%s)\n FileNotExists!", filePath));
            return null;
        }
        ObjectInputStream ois = null;
        Object result = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            result = ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return result;
    }

    public static boolean writeObjectToFile(Serializable obj, String filePath) {
        if (obj == null) {
            System.out.println("FileUtil::writeObjectToFile. obj is null.");
            return false;
        }
        ObjectOutputStream oos = null;
        File outFile = new File(filePath);
        if (!outFile.exists()) {
            String dirPath = outFile.getAbsolutePath();
            int p = dirPath.lastIndexOf('\\');
            if (p < 0) {
                p = dirPath.lastIndexOf('/');
            }
            if (p < 0) {

            } else {
                dirPath = dirPath.substring(0, p);
                new File(dirPath).mkdirs();
            }
        }

        try {
            oos = new ObjectOutputStream(new FileOutputStream(outFile));
            oos.writeObject(obj);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ex) {
                    Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }

    public static boolean copyResourceFileToProjectDirectory(File src) {
        AnimationProjectInfo project = SysData.instance().getProject();
        if (project != null) {
            if (project.projectDirectory != null) {
                String copyFilePath;
                if (project.projectDirectory.endsWith("\\") || project.projectDirectory.endsWith("/")) {
                    copyFilePath = String.format("%sres/%s", project.projectDirectory, src.getName());
                } else {
                    copyFilePath = String.format("%s/res/%s", project.projectDirectory, src.getName());
                }
                BufferedInputStream bis = null;
                BufferedOutputStream bos = null;

                try {
                    bis = new BufferedInputStream(new FileInputStream(src));
                    bos = new BufferedOutputStream(new FileOutputStream(copyFilePath, false));

                    byte[] buffer = new byte[1024 * 1024];
                    int lenI;
                    while ((lenI = bis.read(buffer)) > 0) {
                        bos.write(buffer, 0, lenI);
                    }

                    return true;
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException ex) {
                        }
                    }
                    if (bos != null) {
                        try {
                            bos.close();
                        } catch (IOException ex) {
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取res文件夹下的图片文件的图片对象.
     *
     * @param name
     * @return
     */
    public static BufferedImage getImageOfImageName(String name) {
        AnimationProjectInfo project = SysData.instance().getProject();
        BufferedImage img;
        try {
            File imgFile = new File(project.getImageFilePath(name));
            if (Runtime.getRuntime().freeMemory() <= imgFile.length()) {
                JOptionPane.showMessageDialog(null, "内存不足!无法继续载入图片.");
                return null;
            }
            img = ImageIO.read(imgFile);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, String.format("载入图片文件\n[%s]\n出错!", project.getImageFilePath(name)));
            return null;
        }
        return img;
    }

    public static void exportProject() {
        AnimationProjectInfo project = SysData.instance().getProject();
        if (project == null) {
            return;
        }

        Document document = new Document();
        Element rootElement = new Element("kfa");
        document.addContent(rootElement);

        rootElement.setAttribute("name", project.name);

        for (AnimationInfo ai : project.animations) {
            Element aiElement = new Element("animation");
            aiElement.setAttribute("name", ai.name);
            rootElement.addContent(aiElement);
            for (FrameInfo fi : ai.frames) {
                Element fiElement = new Element("frame");
                fiElement.setAttribute("id", String.valueOf(fi.id));
                aiElement.addContent(fiElement);

                for (ImagePartInfo ipi : fi.parts) {
                    Element ipiElement = new Element("part");
                    ipiElement.setAttribute("img", ipi.imageName);
                    ipiElement.setAttribute("x", String.valueOf(ipi.x));
                    ipiElement.setAttribute("y", String.valueOf(ipi.y));
                    ipiElement.setAttribute("flipx", String.valueOf(ipi.flipX));
                    ipiElement.setAttribute("flipy", String.valueOf(ipi.flipY));
                    fiElement.addContent(ipiElement);
                }

                for (Rectangle rect : fi.rectangles) {
                    Element rectElement = new Element("rectangle");
                    rectElement.setAttribute("x", String.valueOf(rect.x));
                    rectElement.setAttribute("y", String.valueOf(rect.y));
                    rectElement.setAttribute("width", String.valueOf(rect.width));
                    rectElement.setAttribute("height", String.valueOf(rect.height));
                    fiElement.addContent(rectElement);
                }
            }
        }

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        String outFile = String.format("%s/out/%s.xml", project.projectDirectory, project.name);
        try {
            outputter.output(document, new FileOutputStream(outFile));
            JOptionPane.showMessageDialog(null, String.format("项目[%s],导出完成!", outFile));
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
