/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package per.sunmes.kfat.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class AnimationInfo implements Serializable {

    public String name;
    public List<FrameInfo> frames = new ArrayList<>();

    public AnimationInfo copy() {
        AnimationInfo ai = new AnimationInfo();
        ai.name = name;
        for (FrameInfo fi : frames) {
            ai.frames.add(fi.copy());
        }
        return ai;
    }

    @Override
    public String toString() {
        return name;
    }

}
