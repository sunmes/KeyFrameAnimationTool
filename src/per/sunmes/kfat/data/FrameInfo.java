/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package per.sunmes.kfat.data;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class FrameInfo implements Serializable {

    public int id;

    public List<ImagePartInfo> parts = new ArrayList<>();
    public List<Rectangle> rectangles = new ArrayList<>();

    public FrameInfo(int id) {
        this.id = id;
    }

    public FrameInfo copy() {
        FrameInfo fi = new FrameInfo(id);
        for (ImagePartInfo ipi : parts) {
            fi.parts.add(ipi.copy());
        }
        for (Rectangle rectangle : rectangles) {
            Rectangle rect = new Rectangle(rectangle);
            fi.rectangles.add(rect);
        }
        return fi;
    }

    @Override
    public String toString() {
        return String.format("Frame %02d", id);
    }

}
