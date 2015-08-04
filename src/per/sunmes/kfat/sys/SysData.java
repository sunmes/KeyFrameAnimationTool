/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package per.sunmes.kfat.sys;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import per.sunmes.cppe.io.PlistReader;
import per.sunmes.cppe.io.PlistUtil;
import per.sunmes.cppe.io.data.MPlist;
import per.sunmes.cppe.io.data.MPlist.Frame;
import per.sunmes.kfat.data.AnimationInfo;
import per.sunmes.kfat.data.AnimationProjectInfo;
import per.sunmes.kfat.data.FrameInfo;
import per.sunmes.kfat.data.ImagePartInfo;
import per.sunmes.kfat.ui.EditorPanel;
import per.sunmes.kfat.ui.ImagePartPropertiesPanel;
import per.sunmes.kfat.ui.MainFrame;
import per.sunmes.kfat.ui.WorkPanel;

/**
 *
 * @author Administrator
 */
public class SysData {

    public final static String CONFIG_FILE = System.getProperty("user.dir") + "/config.obj";

    /**
     * 全局通用数据单例
     */
    private static SysData _instance;

    public static SysData instance() {
        if (_instance == null) {
            _instance = new SysData();
        }
        return _instance;
    }

    /**
     * 当前项目
     */
    private AnimationProjectInfo project;

    public AnimationProjectInfo getProject() {
        return project;
    }

    /**
     * 载入项目 清空所有其它数据,同时载入项目内图片资源
     *
     * @param project
     */
    public void setProject(AnimationProjectInfo project) {
        this.project = project;
        loadResImageBuffers();
        currentAnimation = null;
        currentFrame = null;
        currentPart = null;
        currentRectangle = null;
        if (ep != null) {
            ep.loadCurrentFrame();
        }

        config.lastProjectPath = project.projectDirectory;
        FileUtil.writeObjectToFile(config, CONFIG_FILE);
    }

    /**
     * 程序配置.
     */
    private ApplicationConfig config;

    public ApplicationConfig getApplicationConfig() {
        if (config == null) {
            File configFile = new File(SysData.CONFIG_FILE);
            if (configFile.exists()) {
                ApplicationConfig loadConfig = (ApplicationConfig) FileUtil.readObjectFromFile(SysData.CONFIG_FILE);
                this.config = loadConfig;
            } else {
                config = new ApplicationConfig();
            }
        }
        return config;
    }

    /**
     * 主窗口
     */
    public MainFrame mf;
    /**
     * 工作面板
     */
    public WorkPanel wp;
    /**
     * 编辑面板
     */
    public EditorPanel ep;

    /**
     * 当前动画
     */
    public AnimationInfo currentAnimation;
    /**
     * 当前帧
     */
    public FrameInfo currentFrame;
    /**
     * 当前部件
     */
    public ImagePartInfo currentPart;
    /**
     * 当前矩形
     */
    public Rectangle currentRectangle;

    /**
     * 部件资源面板
     */
    public ImagePartPropertiesPanel ippp;

    /**
     * 项目内图片资源缓存
     */
    public Map<String, BufferedImage> cachedImages = new HashMap<>();
    /**
     * Plist中的小图,对应Plist信息
     */
    public Map<String, MPlist> plistImages = new HashMap<>();
    /**
     * Plist文件名,Plist对象
     */
    public Map<String, MPlist> cachedPlists = new HashMap<>();

    public void newAnimation() {
        if (project == null) {
            return;
        }
        String newName = JOptionPane.showInputDialog(mf, "新增动画名称:", String.format("animation%02d", project.animations.size() + 1));
        for (AnimationInfo ai : project.animations) {
            if (ai.name.equalsIgnoreCase(newName)) {
                JOptionPane.showMessageDialog(mf, "该名称的动画已存在!");
                return;
            }
        }
        AnimationInfo ai = new AnimationInfo();
        ai.name = newName;
        FrameInfo f = new FrameInfo(0);
        ai.frames.add(f);
        project.animations.add(ai);

        currentAnimation = ai;
        currentFrame = f;
        currentPart = null;
        currentRectangle = null;
        wp.addAnimationInfo(ai);
        wp.addFrameInfo(f);
        wp.selectAnimation(ai);
        wp.selectFrame(f);
    }

    public void newFrame() {
        AnimationInfo ai = currentAnimation;
        if (ai == null) {
            return;
        }
        FrameInfo fi = new FrameInfo(ai.frames.size());
        ai.frames.add(fi);
        currentFrame = fi;
        currentPart = null;
        currentRectangle = null;
        wp.addFrameInfo(fi);
        wp.selectFrame(fi);
    }

    public void newPart(String imageName) {
        if (currentFrame == null) {
            return;
        }
        ImagePartInfo part = ep.addImage(imageName);
        if (part != null) {
            wp.addPartInfo(part);
            wp.selectPartInfo(part);
        } else {
            System.out.println("newPart Null!!");
        }
    }

    /**
     * 返回名称对应图片,如果为Plist小图,则生成单独的Plist小图
     *
     * @param name
     * @return
     */
    public BufferedImage getImageOfName(String name) {
        MPlist plist = getPlistOfImagePart(name);
        if (plist == null) {
            return cachedImages.get(name);
        }
        return PlistUtil.getImageFromPlist(name, plist);
    }

    public void deleteCurrentPart() {
        if (currentPart != null) {
            int index = currentFrame.parts.indexOf(currentPart);
            currentFrame.parts.remove(index);
            currentPart = null;
            wp.updatePartsAndRectangles();
            ep.loadCurrentFrame();
//            if (currentFrame.parts.size() > 0) {
//                if (index >= currentFrame.parts.size()) {
//                    wp.selectPartInfo(currentFrame.parts.get(index - 1));
//                } else {
//                    wp.selectPartInfo(currentFrame.parts.get(index));
//                }
//            } else {
//                wp.clearPart();
//                ep.loadCurrentFrame();
//            }
        }
    }

    public void deleteCurrentRectangle() {

    }

    public void deleteCurrentFrame() {
        if (currentFrame != null) {
            int index = currentAnimation.frames.indexOf(currentFrame);
            currentAnimation.frames.remove(index);
            currentFrame = null;
            currentPart = null;
            currentRectangle = null;
            for (int i = index; i < currentAnimation.frames.size(); i++) {
                currentAnimation.frames.get(i).id = i;
            }
            currentPart = null;
            currentRectangle = null;
            wp.updateFrame();
            ep.loadCurrentFrame();
//            if (currentAnimation.frames.size() > 0) {
//                if (index >= currentAnimation.frames.size()) {
//                    wp.selectFrame(currentAnimation.frames.get(index - 1));
//                } else {
//                    wp.selectFrame(currentAnimation.frames.get(index));
//                }
//            } else {
//                wp.clearPart();
//                wp.clearRectangle();
//            }
        }
    }

    public void copyCurrentFrame() {
        if (currentFrame != null) {
            int index = currentAnimation.frames.indexOf(currentFrame);
            FrameInfo newFi = currentFrame.copy();
            currentAnimation.frames.add(index + 1, newFi);
            //wp.addFrameInfo(newFi, index + 1);
            for (int i = index + 1; i < currentAnimation.frames.size(); i++) {
                currentAnimation.frames.get(i).id = i;
            }
//            currentFrame = newFi;
//            currentPart = null;
//            currentRectangle = null;
            wp.updateFrame();
            wp.selectFrame(newFi);
        }
    }

    public void upCurrentFrame() {
        if (currentFrame != null) {
            int index = currentAnimation.frames.indexOf(currentFrame);
            if (index > 0) {
                FrameInfo lastFrameInfo = currentAnimation.frames.get(index - 1);
                currentAnimation.frames.set(index - 1, currentFrame);
                currentFrame.id = index - 1;
                currentAnimation.frames.set(index, lastFrameInfo);
                lastFrameInfo.id = index;
                wp.updateFrame();
            }
        }
    }

    public void downCurrentFrame() {
        if (currentFrame != null) {
            int index = currentAnimation.frames.indexOf(currentFrame);
            if (index < currentAnimation.frames.size() - 1) {
                FrameInfo nextFrameInfo = currentAnimation.frames.get(index + 1);
                currentAnimation.frames.set(index + 1, currentFrame);
//                wp.setFrameIndex(currentFrame, index + 1);
                currentFrame.id = index + 1;
                currentAnimation.frames.set(index, nextFrameInfo);
//                wp.setFrameIndex(nextFrameInfo, index);
                nextFrameInfo.id = index;
                wp.updateFrame();
            }
        }
    }

    public void copyCurrentPart() {
        if (currentPart != null) {
            ImagePartInfo ipi = currentPart.copy();
            currentFrame.parts.add(ipi);
            wp.selectPartInfo(ipi);
            wp.updatePartsAndRectangles();
        }
    }

    public void deleteCurrentAnimation() {
        if (currentAnimation != null) {
            int index = project.animations.indexOf(currentAnimation);
            project.animations.remove(index);
            currentAnimation = null;
            currentFrame = null;
            currentPart = null;
            wp.updateTreeList();
            wp.updateFrame();
//            if (project.animations.size() > 0) {
//                if (index >= project.animations.size()) {
//                    wp.selectAnimation(project.animations.get(index - 1));
//                } else {
//                    wp.selectAnimation(project.animations.get(index));
//                }
//
//            } else {
//                wp.updateTreeList();
//            }
        }
    }

    /**
     * 获取图片对应的Plist信息. 如果不是Plist的图片,则返回null
     *
     * @param imageName
     * @return
     */
    public MPlist getPlistOfImage(String imageName) {
        for (MPlist plist : cachedPlists.values()) {
            if (plist.metadata.realTextureFileName.equals(imageName)) {
                return plist;
            }
        }
        return null;
    }

    /**
     * 获取小图的Plist信息.不是Plist图则返回null.
     *
     * @param imageName
     * @return
     */
    public MPlist getPlistOfImagePart(String imageName) {
        if (plistImages.containsKey(imageName)) {
            return plistImages.get(imageName);
        }
        return null;
    }

    public BufferedImage getSrcImageOfName(String in) {
        return cachedImages.get(in);
    }

    public void addNewSrcImageResource(File imageFile) {
        try {
            BufferedImage imgBufferdImage = ImageIO.read(imageFile);
            if (imgBufferdImage == null) {
                JOptionPane.showMessageDialog(mf, "未找到图片资源!");
                return;
            }
            cachedImages.put(imageFile.getName(), imgBufferdImage);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mf, "图片载入出错!");
            Logger.getLogger(SysData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addPlistImageResource(String fileName, MPlist plist) {
        cachedPlists.put(fileName, plist);
        if (!cachedImages.containsKey(plist.metadata.realTextureFileName)) {
            BufferedImage imgBufferedImage = FileUtil.getImageOfImageName(plist.metadata.realTextureFileName);
            if (imgBufferedImage == null) {
                JOptionPane.showMessageDialog(mf, String.format("Plist文件[%s]\n所需的图片文件[%s]\n未找到!", fileName, plist.metadata.realTextureFileName));
            }
            cachedImages.put(plist.metadata.realTextureFileName, imgBufferedImage);
        }
        for (Frame frame : plist.frames) {
            plistImages.put(frame.name, plist);
        }
    }

    /**
     * 载入项目内包含的图片资源
     *
     */
    public void loadResImageBuffers() {
        cachedImages.clear();
        if (project == null) {
            return;
        }
        File resDir = new File(project.projectDirectory + "/res");
        if (!resDir.exists() || resDir.isFile()) {
            return;
        }
        File[] imageFiles = resDir.listFiles();

        for (File imgFile : imageFiles) {
            if (imgFile.getName().endsWith("plist")) {
                MPlist plist = PlistReader.readPlist(imgFile);
                cachedPlists.put(imgFile.getName(), plist);
                if (!cachedImages.containsKey(plist.metadata.realTextureFileName)) {
                    BufferedImage imgBufferedImage = FileUtil.getImageOfImageName(plist.metadata.realTextureFileName);
                    if (imgBufferedImage == null) {
                        JOptionPane.showMessageDialog(mf, String.format("Plist文件[%s]\n所需的图片文件[%s]\n未找到!", imgFile.getName(), plist.metadata.realTextureFileName));
                        continue;
                    }
                    cachedImages.put(plist.metadata.realTextureFileName, imgBufferedImage);
                }
                for (Frame frame : plist.frames) {
                    plistImages.put(frame.name, plist);
                }
            } else {
                if (!cachedImages.containsKey(imgFile.getName())) {
                    BufferedImage imgBufferedImage = FileUtil.getImageOfImageName(imgFile.getName());
                    if (imgBufferedImage == null) {
                        //JOptionPane.showMessageDialog(mf, String.format("", imgFile.getName(), plist.metadata.realTextureFileName));
                        continue;
                    }
                    cachedImages.put(imgFile.getName(), imgBufferedImage);
                }
            }
        }
    }
}
