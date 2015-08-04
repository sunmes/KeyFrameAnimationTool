/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package per.sunmes.kfat.data;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class ImagePartInfo implements Serializable {

    public String imageName;
    public int x;
    public int y;
    public boolean flipX;
    public boolean flipY;

    public int imageWidth;
    public int imageHeight;

    public int getPaintX() {
        return x - imageWidth / 2;
    }

    public int getPaintY() {
        return y + imageHeight / 2;
    }

    public ImagePartInfo copy() {
        ImagePartInfo ipi = new ImagePartInfo();
        ipi.imageName = imageName;
        ipi.x = x;
        ipi.y = y;
        ipi.flipX = flipX;
        ipi.flipY = flipY;
        ipi.imageWidth = imageWidth;
        ipi.imageHeight = imageHeight;
        return ipi;
    }

    @Override
    public String toString() {
        return imageName;
    }

}
