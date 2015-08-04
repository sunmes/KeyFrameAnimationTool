/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package per.sunmes.kfat.ui;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import per.sunmes.kfat.data.AnimationInfo;
import per.sunmes.kfat.data.AnimationProjectInfo;
import per.sunmes.kfat.data.FrameInfo;
import per.sunmes.kfat.data.ImagePartInfo;
import per.sunmes.kfat.sys.FileUtil;
import per.sunmes.kfat.sys.SysData;

/**
 *
 * @author Administrator
 */
public class WorkPanel extends javax.swing.JPanel {

    private final DefaultListModel<String> imageListModel = new DefaultListModel<>();
    private final DefaultComboBoxModel<AnimationInfo> animationComboBoxModel = new DefaultComboBoxModel<>();
    private final DefaultListModel<FrameInfo> frameListModel = new DefaultListModel<>();
    private final DefaultListModel<ImagePartInfo> partListModel = new DefaultListModel<>();
    private final DefaultListModel<Rectangle> rectangleListModel = new DefaultListModel<>();

    private final ImagePartPropertiesPanel ippp = new ImagePartPropertiesPanel();
    //private final ImageResourcePropertiesPanel irpp = new ImageResourcePropertiesPanel();

    /**
     * Creates new form WorkPanel
     */
    public WorkPanel() {
        GridLayout layout = new GridLayout(1, 1);
        setLayout(layout);
        initComponents();
        curstomInit();
    }

    public void frameSliderValueChanged() {
        AnimationInfo ai = SysData.instance().currentAnimation;
        FrameInfo fi = SysData.instance().currentFrame;
        if (ai != null) {
            if (fi != null) {
                int pI = ai.frames.indexOf(fi);
                int rI = frameSlider.getValue();
                if (rI >= ai.frames.size()) {
                    rI = ai.frames.size() - 1;
                }
                if (rI != pI) {
                    SysData.instance().currentFrame = ai.frames.get(rI);
                    updateFrame();
//                    updateCurrenAFInfo();
                }
            }
        }
    }

    private void curstomInit() {
        updateTreeList();

        GridLayout layout = new GridLayout(1, 1);
        editPanel.setLayout(layout);
        EditorPanel ep = new EditorPanel();
        editPanel.add(ep);
        SysData.instance().ep = ep;

        panelZone.setLayout(layout);
        panelZone.add(ippp);

        SysData.instance().ippp = ippp;
        //SysData.instance().irpp = irpp;

        frameSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                frameSliderValueChanged();
            }
        });

        animationComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setCurrentSelectedAnimationInfo();
            }
        });

        frameList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                setCurrentSelectedFrameInfo();
            }
        });

        partList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                setCurrentSelectedPartInfo();
            }
        });

        rectangleList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {

            }
        });

        animationComboBox.setModel(animationComboBoxModel);
        frameList.setModel(frameListModel);
        partList.setModel(partListModel);
        rectangleList.setModel(rectangleListModel);
    }

    public void setCurrentSelectedAnimationInfo() {
        AnimationInfo ai = (AnimationInfo) animationComboBox.getSelectedItem();
        if (ai == null) {
            frameListModel.clear();
            partListModel.clear();
            rectangleListModel.clear();
            return;
        }
        if (ai != SysData.instance().currentAnimation) {
            SysData.instance().currentAnimation = ai;
            SysData.instance().currentFrame = null;
            SysData.instance().currentPart = null;
            SysData.instance().currentRectangle = null;
            SysData.instance().ep.loadCurrentFrame();
        }

        updateFrame();
//        frameListModel.clear();
//        partListModel.clear();
//        rectangleListModel.clear();
//        for (FrameInfo fi : ai.frames) {
//            frameListModel.addElement(fi);
//        }
    }

    public void updateFrame() {
        frameListModel.clear();
        for (FrameInfo fi : SysData.instance().currentAnimation.frames) {
            frameListModel.addElement(fi);
        }
        if (SysData.instance().currentFrame != null) {
            if (frameList.getSelectedValue() != SysData.instance().currentFrame) {
                frameList.setSelectedValue(SysData.instance().currentFrame, true);
            }
            SysData.instance().ep.loadCurrentFrame();
            updatePartsAndRectangles();
            updateCurrenAFInfo();
        } else {
            partListModel.clear();
            rectangleListModel.clear();
        }
    }

    public void updatePartsAndRectangles() {
        partListModel.clear();
        rectangleListModel.clear();

        if (SysData.instance().currentFrame == null) {
            return;
        }

        for (int i = SysData.instance().currentFrame.parts.size() - 1; i >= 0; i--) {
            ImagePartInfo ipi = SysData.instance().currentFrame.parts.get(i);
            partListModel.addElement(ipi);
        }
        for (Rectangle r : SysData.instance().currentFrame.rectangles) {
            rectangleListModel.addElement(r);
        }
        if (SysData.instance().currentPart != null) {
            if (partList.getSelectedValue() != SysData.instance().currentPart) {
                partList.setSelectedValue(SysData.instance().currentPart, true);
            }
            setCurrentSelectedPartInfo();
        }

    }

    public void setCurrentSelectedFrameInfo() {
        FrameInfo fi = (FrameInfo) frameList.getSelectedValue();
        if (fi == null) {
            partListModel.clear();
            rectangleListModel.clear();
            return;
        }
        if (fi != SysData.instance().currentFrame) {
            SysData.instance().currentFrame = fi;
            SysData.instance().currentPart = null;
            SysData.instance().currentRectangle = null;
        }
        SysData.instance().ep.loadCurrentFrame();
        updatePartsAndRectangles();
        updateCurrenAFInfo();
//        for (ImagePartInfo ipi : fi.parts) {
//            partListModel.addElement(ipi);
//        }
//        for (Rectangle r : fi.rectangles) {
//            rectangleListModel.addElement(r);
//        }
//        SysData.instance().ep.loadCurrentFrame();
//        updateCurrenAFInfo();
    }

    public void setCurrentSelectedPartInfo() {
        ImagePartInfo ipi = (ImagePartInfo) partList.getSelectedValue();
        if (ipi == null) {
            return;
        }
        //SysData.instance().currentPart = ipi;
        SysData.instance().ep.setSelectedImage(ipi);
    }

    public void afterShow() {
        ((EditorPanel) editPanel.getComponents()[0]).resetOriginPoint();
        ((EditorPanel) editPanel.getComponents()[0]).repaint();
    }

    public void showEditImagePartInfo(ImagePartInfo p) {
        ippp.loadPartInfo(p);
    }

    public void showImageResourceInfo() {
        //panelZone.removeAll();

        //panelZone.add(irpp);
    }

    private void loadProjectInfo() {
        updateTreeList();
    }

    public void addImageNode(String name) {
        imageListModel.addElement(name);
    }

    public void addAnimationInfo(AnimationInfo ai) {
        animationComboBoxModel.addElement(ai);
        //animationComboBox.repaint();
        updateCurrenAFInfo();
    }

    public void addPartInfo(ImagePartInfo part) {
    }

    public void removeCurrentPartInfo() {
    }

    public void addFrameInfo(FrameInfo fi) {
        frameListModel.addElement(fi);
    }

    public void addFrameInfo(FrameInfo fi, int index) {
        frameListModel.add(index, fi);
    }

    public void setFrameIndex(FrameInfo fi, int index) {
        frameListModel.setElementAt(fi, index);
        frameList.repaint();
    }

    public void updateTreeList() {

        AnimationProjectInfo project = SysData.instance().getProject();

        if (project != null) {
            imageListModel.clear();
            clearAnimation();

//            for (String fileName : project.imageResources) {
//            }
            for (String imageName : SysData.instance().cachedImages.keySet()) {
                imageListModel.addElement(imageName);
            }
            imageResourceList.setModel(imageListModel);
            for (AnimationInfo ai : project.animations) {
                animationComboBoxModel.addElement(ai);

//                for (FrameInfo fi : ai.frames) {
//                    for (ImagePartInfo ipi : fi.parts) {
//                    }
//                    for (Rectangle rect : fi.rectangles) {
//                    }
//                }
            }
            setCurrentSelectedAnimationInfo();
        }
    }

    public void clearAnimation() {
        animationComboBoxModel.removeAllElements();
        clearFrame();
    }

    public void clearFrame() {
        frameListModel.clear();
        clearPart();
        clearRectangle();
    }

    public void clearPart() {
        partListModel.clear();
    }

    public void clearRectangle() {
        rectangleListModel.clear();
    }

    public void selectAnimation(AnimationInfo ai) {
        animationComboBox.setSelectedItem(ai);
        setCurrentSelectedAnimationInfo();
    }

    public void selectFrame(FrameInfo fi) {
        frameList.setSelectedValue(fi, true);
        setCurrentSelectedFrameInfo();
    }

    public void selectPartInfo(ImagePartInfo ipi) {
        partList.setSelectedValue(ipi, true);
        setCurrentSelectedPartInfo();
    }

    public void selectRectangle(Rectangle rect) {
        rectangleList.setSelectedValue(rect, true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        panelZone = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        projectNameLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        imageResourceList = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        addImageButton = new javax.swing.JButton();
        boxPanel = new javax.swing.JPanel();
        editZonePanel = new javax.swing.JPanel();
        editPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        animationComboBox = new javax.swing.JComboBox();
        newAnimationButton = new javax.swing.JButton();
        delAnimationButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        frameList = new javax.swing.JList();
        delFrameButton = new javax.swing.JButton();
        newFrameButton = new javax.swing.JButton();
        copyFrameButton = new javax.swing.JButton();
        upFrameButton = new javax.swing.JButton();
        downFrameButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        partList = new javax.swing.JList();
        copyPartButton = new javax.swing.JButton();
        delPartButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        rectangleList = new javax.swing.JList();
        createRectangleButton = new javax.swing.JButton();
        delRectangleButton = new javax.swing.JButton();
        framePanel = new javax.swing.JPanel();
        frameSlider = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        animationNameLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        frameIndexLabel = new javax.swing.JLabel();
        playLoopCheckBox = new javax.swing.JCheckBox();
        playToggleButton = new javax.swing.JToggleButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jSplitPane1.setDividerLocation(180);

        jSplitPane2.setDividerLocation(250);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        javax.swing.GroupLayout panelZoneLayout = new javax.swing.GroupLayout(panelZone);
        panelZone.setLayout(panelZoneLayout);
        panelZoneLayout.setHorizontalGroup(
            panelZoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 177, Short.MAX_VALUE)
        );
        panelZoneLayout.setVerticalGroup(
            panelZoneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 317, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(panelZone);

        jLabel4.setText("项目:");

        projectNameLabel.setText("ProjectName");

        imageResourceList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imageResourceListMouseClicked(evt);
            }
        });
        imageResourceList.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                imageResourceListMouseMoved(evt);
            }
        });
        imageResourceList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                imageResourceListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(imageResourceList);

        jLabel5.setText("图像资源列表:");

        addImageButton.setText("添加所选图像至画布");
        addImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addImageButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(projectNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel5)
                            .addComponent(addImageButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(projectNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addImageButton)
                .addContainerGap())
        );

        jSplitPane2.setLeftComponent(jPanel1);

        jSplitPane1.setLeftComponent(jSplitPane2);

        javax.swing.GroupLayout editPanelLayout = new javax.swing.GroupLayout(editPanel);
        editPanel.setLayout(editPanelLayout);
        editPanelLayout.setHorizontalGroup(
            editPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        editPanelLayout.setVerticalGroup(
            editPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel6.setText("动画:");

        newAnimationButton.setText("新增");
        newAnimationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAnimationButtonActionPerformed(evt);
            }
        });

        delAnimationButton.setText("删除");
        delAnimationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delAnimationButtonActionPerformed(evt);
            }
        });

        jLabel7.setText("帧:");

        jScrollPane2.setViewportView(frameList);

        delFrameButton.setText("删除");
        delFrameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delFrameButtonActionPerformed(evt);
            }
        });

        newFrameButton.setText("新增");
        newFrameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFrameButtonActionPerformed(evt);
            }
        });

        copyFrameButton.setText("复制");
        copyFrameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyFrameButtonActionPerformed(evt);
            }
        });

        upFrameButton.setText("上");
        upFrameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upFrameButtonActionPerformed(evt);
            }
        });

        downFrameButton.setText("下");
        downFrameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downFrameButtonActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(partList);

        copyPartButton.setText("复制");
        copyPartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyPartButtonActionPerformed(evt);
            }
        });

        delPartButton.setText("删除");
        delPartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delPartButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(copyPartButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delPartButton)
                        .addGap(0, 50, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(copyPartButton)
                    .addComponent(delPartButton))
                .addGap(0, 40, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("图片部件", jPanel3);

        jScrollPane4.setViewportView(rectangleList);

        createRectangleButton.setText("绘制新矩形");
        createRectangleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createRectangleButtonActionPerformed(evt);
            }
        });

        delRectangleButton.setText("删除选中矩形");
        delRectangleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delRectangleButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(createRectangleButton)
                            .addComponent(delRectangleButton))
                        .addGap(0, 65, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createRectangleButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(delRectangleButton)
                .addContainerGap())
        );

        jTabbedPane1.addTab("碰撞矩形", jPanel4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(animationComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(newAnimationButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(delAnimationButton))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(newFrameButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(delFrameButton))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(copyFrameButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(upFrameButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(downFrameButton)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(animationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newAnimationButton)
                    .addComponent(delAnimationButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(newFrameButton)
                    .addComponent(delFrameButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(copyFrameButton)
                    .addComponent(upFrameButton)
                    .addComponent(downFrameButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout editZonePanelLayout = new javax.swing.GroupLayout(editZonePanel);
        editZonePanel.setLayout(editZonePanelLayout);
        editZonePanelLayout.setHorizontalGroup(
            editZonePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editZonePanelLayout.createSequentialGroup()
                .addComponent(editPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        editZonePanelLayout.setVerticalGroup(
            editZonePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(editPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        frameSlider.setValue(0);

        jLabel1.setText("时间轴:");

        jLabel2.setText("动画:");

        animationNameLabel.setText("animationName");

        jLabel3.setText("当前帧:");

        frameIndexLabel.setText("0");

        playLoopCheckBox.setText("循环");
        playLoopCheckBox.setToolTipText("");
        playLoopCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playLoopCheckBoxActionPerformed(evt);
            }
        });

        playToggleButton.setText("播放");
        playToggleButton.setToolTipText("");
        playToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playToggleButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout framePanelLayout = new javax.swing.GroupLayout(framePanel);
        framePanel.setLayout(framePanelLayout);
        framePanelLayout.setHorizontalGroup(
            framePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(framePanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(framePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(framePanelLayout.createSequentialGroup()
                        .addComponent(frameSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(playLoopCheckBox))
                    .addGroup(framePanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(246, 246, 246)
                        .addComponent(playToggleButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(framePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(framePanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(animationNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(framePanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(frameIndexLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)))
                .addGap(19, 19, 19))
        );
        framePanelLayout.setVerticalGroup(
            framePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(framePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(framePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(frameIndexLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, framePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(framePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(animationNameLabel)
                    .addComponent(playToggleButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(framePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(playLoopCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(frameSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout boxPanelLayout = new javax.swing.GroupLayout(boxPanel);
        boxPanel.setLayout(boxPanelLayout);
        boxPanelLayout.setHorizontalGroup(
            boxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(editZonePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(framePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        boxPanelLayout.setVerticalGroup(
            boxPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(boxPanelLayout.createSequentialGroup()
                .addComponent(editZonePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(framePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setRightComponent(boxPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSplitPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

//    private TempWindow tmpWindow;
    private void imageResourceListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_imageResourceListValueChanged

    }//GEN-LAST:event_imageResourceListValueChanged

    private void addImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addImageButtonActionPerformed
        String imagePath = getSelectedImageValue();
        if (imagePath == null) {
            return;
        }
        SysData.instance().newPart(imagePath);
    }//GEN-LAST:event_addImageButtonActionPerformed

    private void imageResourceListMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageResourceListMouseMoved
//        if (tmpWindow != null) {
//            tmpWindow.dispose();
//            tmpWindow = null;
//        }
    }//GEN-LAST:event_imageResourceListMouseMoved

    public String getSelectedImageValue() {
        if (imageResourceList.getSelectedValue() == null) {
            return null;
        }
        return imageResourceList.getSelectedValue().toString();
    }

    private void imageResourceListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imageResourceListMouseClicked
        String imagePath = getSelectedImageValue();
        if (imagePath == null) {
            return;
        }
//        if (tmpWindow != null) {
//            tmpWindow.dispose();
//            tmpWindow = null;
//        }
//        BufferedImage img = FileUtil.getImageOfImageName(imagePath);
//        if (img == null) {
//            System.out.println("imageResource FileUtile getImage fiald");
//            return;
//        }
        TempFrame tmpWindow = new TempFrame();
        tmpWindow.loadImage(imagePath);
        tmpWindow.setVisible(true);
    }//GEN-LAST:event_imageResourceListMouseClicked

    private void newAnimationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newAnimationButtonActionPerformed
        SysData.instance().newAnimation();
    }//GEN-LAST:event_newAnimationButtonActionPerformed

    private void delAnimationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delAnimationButtonActionPerformed
        SysData.instance().deleteCurrentAnimation();
    }//GEN-LAST:event_delAnimationButtonActionPerformed

    private void newFrameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newFrameButtonActionPerformed
        SysData.instance().newFrame();
    }//GEN-LAST:event_newFrameButtonActionPerformed

    private void delFrameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delFrameButtonActionPerformed
        SysData.instance().deleteCurrentFrame();
    }//GEN-LAST:event_delFrameButtonActionPerformed

    private void delPartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delPartButtonActionPerformed
        SysData.instance().deleteCurrentPart();
    }//GEN-LAST:event_delPartButtonActionPerformed

    private void copyFrameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyFrameButtonActionPerformed
        SysData.instance().copyCurrentFrame();
    }//GEN-LAST:event_copyFrameButtonActionPerformed

    private void upFrameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upFrameButtonActionPerformed
        SysData.instance().upCurrentFrame();
    }//GEN-LAST:event_upFrameButtonActionPerformed

    private void downFrameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downFrameButtonActionPerformed
        SysData.instance().downCurrentFrame();
    }//GEN-LAST:event_downFrameButtonActionPerformed

    private void copyPartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyPartButtonActionPerformed
        SysData.instance().copyCurrentPart();
    }//GEN-LAST:event_copyPartButtonActionPerformed

    private void playLoopCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playLoopCheckBoxActionPerformed

    }//GEN-LAST:event_playLoopCheckBoxActionPerformed

    private void playToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playToggleButtonActionPerformed
        if (!playToggleButton.isSelected()) {
            stopAnimation();
            playToggleButton.setText("播放");
        } else {
            playAnimation();
            playToggleButton.setText("停止");
        }
    }//GEN-LAST:event_playToggleButtonActionPerformed

    private void createRectangleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createRectangleButtonActionPerformed
        SysData.instance().ep.createRectangle();
    }//GEN-LAST:event_createRectangleButtonActionPerformed

    private void delRectangleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delRectangleButtonActionPerformed
        SysData.instance().deleteCurrentRectangle();
    }//GEN-LAST:event_delRectangleButtonActionPerformed

    private class PlayThread extends Thread {

        public boolean isStoped;

        int currentFrame = 0;

        @Override
        public void run() {
            while (!isStoped && !Thread.interrupted() && SysData.instance().currentAnimation != null) {
                if (currentFrame >= SysData.instance().currentAnimation.frames.size()) {
                    if (playLoopCheckBox.isSelected()) {
                        currentFrame = 0;
                    } else {
                        playToggleButton.setSelected(false);
                        playToggleButton.setText("播放");
                        playThread = null;
                        frameSliderValueChanged();
                        return;
                    }
                }
                SysData.instance().ep.loadFrame(SysData.instance().currentAnimation.frames.get(currentFrame));
                currentFrame++;
                try {
                    sleep(1000 / 30);
                } catch (InterruptedException ex) {
                    //Logger.getLogger(WorkPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        public void shutdown() {
            isStoped = true;
            interrupt();
        }
    }

    PlayThread playThread = null;

    public void playAnimation() {
        if (playThread == null) {
            playThread = new PlayThread();
            playThread.start();
        }
    }

    public void stopAnimation() {
        if (playThread != null) {
            playThread.shutdown();
            try {
                playThread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(WorkPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            frameSliderValueChanged();
            playThread = null;
        }
    }

    public void updateCurrenAFInfo() {
        AnimationInfo ai = SysData.instance().currentAnimation;
        FrameInfo fi = SysData.instance().currentFrame;
        if (ai != null) {
            animationNameLabel.setText(ai.name);
            if (fi != null) {
                int pI = ai.frames.indexOf(fi);
                frameIndexLabel.setText(String.valueOf(pI));
                if (frameSlider.getValue() != pI) {
                    frameSlider.setValue(pI);
                }

            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addImageButton;
    private javax.swing.JComboBox animationComboBox;
    private javax.swing.JLabel animationNameLabel;
    private javax.swing.JPanel boxPanel;
    private javax.swing.JButton copyFrameButton;
    private javax.swing.JButton copyPartButton;
    private javax.swing.JButton createRectangleButton;
    private javax.swing.JButton delAnimationButton;
    private javax.swing.JButton delFrameButton;
    private javax.swing.JButton delPartButton;
    private javax.swing.JButton delRectangleButton;
    private javax.swing.JButton downFrameButton;
    private javax.swing.JPanel editPanel;
    private javax.swing.JPanel editZonePanel;
    private javax.swing.JLabel frameIndexLabel;
    private javax.swing.JList frameList;
    private javax.swing.JPanel framePanel;
    private javax.swing.JSlider frameSlider;
    private javax.swing.JList imageResourceList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton newAnimationButton;
    private javax.swing.JButton newFrameButton;
    private javax.swing.JPanel panelZone;
    private javax.swing.JList partList;
    private javax.swing.JCheckBox playLoopCheckBox;
    private javax.swing.JToggleButton playToggleButton;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JList rectangleList;
    private javax.swing.JButton upFrameButton;
    // End of variables declaration//GEN-END:variables
}
