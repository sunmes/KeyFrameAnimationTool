/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package per.sunmes.kfat.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import per.sunmes.kfat.data.AnimationProjectInfo;
import per.sunmes.kfat.data.FrameInfo;
import per.sunmes.kfat.data.ImagePartInfo;
import per.sunmes.kfat.sys.SysData;

/**
 *
 * @author Administrator
 */
public class EditorPanel extends javax.swing.JPanel {

    public int originX = 240;
    public int originY = 200;

    public Map<String, BufferedImage> images = new HashMap<>();
    public List<ImagePartInfo> parts;
    public ImagePartInfo selectedImage;
    public List<Rectangle> rects;

    private final List<ImagePartInfo> defaultNullParts = new ArrayList<>();
    private final List<Rectangle> defaultNullRects = new ArrayList<>();

    public boolean isCreateRectangleModel;
    private Rectangle tempRect;

    public void loadFrame(FrameInfo fi) {
        selectedImage = null;
        if (fi != null) {
            parts = fi.parts;
            rects = fi.rectangles;

            for (ImagePartInfo ipi : fi.parts) {
                if (!images.containsKey(ipi.imageName)) {
                    BufferedImage img = SysData.instance().getImageOfName(ipi.imageName);
                    if (img == null) {
                        JOptionPane.showMessageDialog(this, "载入图片资源出错!");
                    }
                    images.put(ipi.imageName, img);
                }
            }

        } else {
            parts = defaultNullParts;
            rects = defaultNullRects;
        }
        repaint();
    }

    public void loadCurrentFrame() {
        selectedImage = null;
        FrameInfo fi = SysData.instance().currentFrame;
        if (fi != null) {
            parts = fi.parts;
            rects = fi.rectangles;

            for (ImagePartInfo ipi : fi.parts) {
                if (!images.containsKey(ipi.imageName)) {
                    BufferedImage img = SysData.instance().getImageOfName(ipi.imageName);
                    if (img == null) {
                        JOptionPane.showMessageDialog(this, "载入图片资源出错!");
                    }
                    images.put(ipi.imageName, img);
                }
            }
        } else {
            parts = defaultNullParts;
            rects = defaultNullRects;
        }
        repaint();
    }

    public void createRectangle() {
        isCreateRectangleModel = true;
        setSelectedImage(null);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    public void endCreateRectangle() {
        isCreateRectangleModel = false;
        if (tempRect != null) {
            rects.add(tempRect);
            tempRect = null;
        }
        setCursor(Cursor.getDefaultCursor());
    }

    public void addPart(ImagePartInfo part) {
        parts.add(part);
        if (!images.containsKey(part.imageName)) {
            BufferedImage imgBufferedImage = SysData.instance().getImageOfName(part.imageName);
            if (imgBufferedImage == null) {
                JOptionPane.showMessageDialog(this, "载入图片资源出错!");
            }
            images.put(part.imageName, imgBufferedImage);
        }
        repaint();
    }

    public void removePart(ImagePartInfo part) {
        parts.remove(part);
        if (SysData.instance().currentPart == part) {
            SysData.instance().currentPart = null;
            SysData.instance().ippp.loadPartInfo(null);
        }
        repaint();
    }

    public ImagePartInfo addImage(String imageName) {
        if (parts == null) {
            System.out.println("EditorPanel addImage. parts is null!");
            return null;
        }
        AnimationProjectInfo project = SysData.instance().getProject();

        ImagePartInfo part = new ImagePartInfo();
        part.imageName = imageName;
        part.x = 0;
        part.y = 0;
        if (images.containsKey(imageName)) {
            BufferedImage img = images.get(imageName);
            part.imageWidth = img.getWidth();
            part.imageHeight = img.getHeight();
        } else {
            BufferedImage img = SysData.instance().getImageOfName(part.imageName);
            if (img == null) {
                JOptionPane.showMessageDialog(this, "载入图片资源出错!");
                return null;
            }
            part.imageWidth = img.getWidth();
            part.imageHeight = img.getHeight();
            images.put(part.imageName, img);
//            try {
//                File imgFile = new File(project.getImageFilePath(imageName));
//                BufferedImage img = ImageIO.read(imgFile);
//                images.put(imageName, img);
//                part.imageWidth = img.getWidth();
//                part.imageHeight = img.getHeight();
//            } catch (IOException ex) {
//                JOptionPane.showMessageDialog(this, String.format("载入图片文件\n[%s]\n出错!", project.getImageFilePath(imageName)));
//                return null;
//            }
        }
        parts.add(part);
//        } else {
//            String imagePartName = imageName.substring(0, p);
//            part = new ImagePartInfo();
//            part.imageName = imagePartName;
//            part.x = 0;
//            part.y = 0;
//            if (images.containsKey(imagePartName)) {
//                BufferedImage img = images.get(imagePartName);
//                part.imageWidth = img.getWidth();
//                part.imageHeight = img.getHeight();
//            } else {
//                String plistFileString = imageName.substring(p + 1);
//                MPlist plist = project.plistPool.get(plistFileString);
//                if (plist == null) {
//                    JOptionPane.showMessageDialog(this, String.format("Plist文件\n[%s]\n未被导入!", plistFileString));
//                    return null;
//                }
//                BufferedImage img = PlistUtil.getImageFromPlist(imagePartName, plist);
//                images.put(imagePartName, img);
//                part.imageWidth = img.getWidth();
//                part.imageHeight = img.getHeight();
//            }
//            parts.add(part);
//        }
        repaint();
        return part;
    }

    public void setSelectedImage(ImagePartInfo p) {
        if (selectedImage != p) {
            selectedImage = p;
            SysData.instance().currentPart = p;
            SysData.instance().ippp.loadPartInfo(p);
            SysData.instance().wp.updatePartsAndRectangles();
            repaint();
        }
    }

    /**
     * Creates new form EditorPanel
     */
    public EditorPanel() {
        initComponents();
    }

    public void resetOriginPoint() {
        originX = getWidth() / 2;
        originY = (getHeight() - bottomPanel.getHeight()) / 2;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bottomPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        zoomSpinner = new javax.swing.JSpinner();
        downButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        positionLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                formMouseReleased(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        bottomPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("缩放:");
        jLabel1.setEnabled(false);

        zoomSpinner.setEnabled(false);

        downButton.setText("下移一层");
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        upButton.setText("上移一层");
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(zoomSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(upButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downButton)
                .addGap(0, 160, Short.MAX_VALUE))
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addComponent(zoomSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(downButton)
                    .addComponent(upButton)))
        );

        jLabel2.setText("指针坐标:");

        positionLabel.setText("0,0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(positionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(positionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 338, Short.MAX_VALUE)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private int getOX(int x) {
        return x - originX;
    }

    private int getPX(int ox) {
        return originX + ox;
    }

    private int getOY(int y) {
        return originY - y;
    }

    private int getPY(int oy) {
        return originY - oy;
    }

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        positionLabel.setText(String.format("%d,%d", getOX(evt.getX()), getOY(evt.getY())));
    }//GEN-LAST:event_formMouseMoved

    private int sOffX, sOffY;
    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        requestFocus();
        tmpX = evt.getX();
        tmpY = evt.getY();
        if (parts == null) {
            return;
        }
        int ox = getOX(evt.getX());
        int oy = getOY(evt.getY());

        if (isCreateRectangleModel) {
            tempRect = new Rectangle(new Point(getOX(tmpX), getOY(tmpY)));
            return;
        }

        if (selectedImage != null) {
            ImagePartInfo p = selectedImage;
            if (ox >= p.getPaintX() && ox <= p.getPaintX() + p.imageWidth && oy <= p.getPaintY() && oy >= p.getPaintY() - p.imageHeight) {
                sOffX = ox - p.x;
                sOffY = oy - p.y;
                return;
            }
        }
        for (int i = parts.size() - 1; i >= 0; i--) {
            ImagePartInfo p = parts.get(i);
            if (ox >= p.getPaintX() && ox <= p.getPaintX() + p.imageWidth && oy <= p.getPaintY() && oy >= p.getPaintY() - p.imageHeight) {
                sOffX = ox - p.x;
                sOffY = oy - p.y;
                setSelectedImage(p);
                return;
            }
        }
        setSelectedImage(null);
    }//GEN-LAST:event_formMousePressed

    private int tmpX, tmpY;

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        if (isMoveStage) {
            originX += evt.getX() - tmpX;
            originY += evt.getY() - tmpY;
            //System.out.println("MoveStage");
        } else if (isCreateRectangleModel && tempRect != null) {
            tempRect.width = getOX(evt.getX()) - tempRect.x;
            tempRect.height = tempRect.y - getOY(evt.getY());
        } else if (selectedImage != null) {
            selectedImage.x = getOX(evt.getX()) - sOffX;
            selectedImage.y = getOY(evt.getY()) - sOffY;
            SysData.instance().ippp.loadPartInfo(selectedImage);
        }
        tmpX = evt.getX();
        tmpY = evt.getY();
        //System.out.println("Drag");
        repaint();
    }//GEN-LAST:event_formMouseDragged

    private void formMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseReleased
        if (isCreateRectangleModel) {
            endCreateRectangle();
            repaint();
        }
    }//GEN-LAST:event_formMouseReleased

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        if (parts == null) {
            return;
        }
        if (selectedImage != null) {
            int index = parts.indexOf(selectedImage);
            if (index < parts.size() - 1) {
                ImagePartInfo upPart = parts.get(index + 1);
                parts.set(index + 1, selectedImage);
                parts.set(index, upPart);
                SysData.instance().wp.updatePartsAndRectangles();
            }
        }
        repaint();
    }//GEN-LAST:event_upButtonActionPerformed

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        if (parts == null) {
            return;
        }
        if (selectedImage != null) {
            int index = parts.indexOf(selectedImage);
            if (index > 0) {
                ImagePartInfo downPart = parts.get(index - 1);
                parts.set(index - 1, selectedImage);
                parts.set(index, downPart);
                SysData.instance().wp.updatePartsAndRectangles();
            }
        }
        repaint();
    }//GEN-LAST:event_downButtonActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_CONTROL) {
            isMoveStage = true;
            return;
        }

        if (selectedImage != null) {
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_UP:
                    selectedImage.y++;
                    break;
                case KeyEvent.VK_LEFT:
                    selectedImage.x--;
                    break;
                case KeyEvent.VK_RIGHT:
                    selectedImage.x++;
                    break;
                case KeyEvent.VK_DOWN:
                    selectedImage.y--;
                    break;
                default:
                    break;
            }
            SysData.instance().ippp.loadPartInfo(selectedImage);
            repaint();
        }
    }//GEN-LAST:event_formKeyPressed

    private boolean isMoveStage;

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_CONTROL) {
            isMoveStage = false;
        }
    }//GEN-LAST:event_formKeyReleased

    private int imgX, imgY;

    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.

        //drawOriginLine;
        g.setColor(Color.BLACK);
        g.drawLine(0, originY, getWidth(), originY);
        g.drawLine(originX, 0, originX, getHeight() - bottomPanel.getHeight());

        if (parts == null) {
            return;
        }
        for (ImagePartInfo p : parts) {
            BufferedImage img = images.get(p.imageName);
            if (img == null) {
                //System.out.println("paintImage null!!.");
                g.setColor(Color.GRAY);
                g.drawRect(imgX, imgY, 30, 30);
                continue;
            }
            imgX = getPX(p.getPaintX());
            imgY = getPY(p.getPaintY());
            //g.drawImage(img, imgX, imgY, this);
            g.drawImage(img, imgX + (p.flipX ? (1) : (0)) * p.imageWidth, imgY + (p.flipY ? (1) : (0)) * p.imageHeight, (p.flipX ? (-1) : (1)) * p.imageWidth, (p.flipY ? (-1) : (1)) * p.imageHeight, this);
            if (selectedImage == p) {
                g.setColor(Color.RED);
                g.drawRect(imgX, imgY, img.getWidth(), img.getHeight());
            }
        }

        g.setColor(Color.BLUE);
        for (Rectangle rectangle : rects) {
            g.drawRect(getPX(rectangle.x), getPY(rectangle.y), rectangle.width, rectangle.height);
        }

        if (tempRect != null) {
            g.setColor(Color.GREEN);
            g.drawRect(getPX(tempRect.x), getPY(tempRect.y), tempRect.width, tempRect.height);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton downButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel positionLabel;
    private javax.swing.JButton upButton;
    private javax.swing.JSpinner zoomSpinner;
    // End of variables declaration//GEN-END:variables
}
