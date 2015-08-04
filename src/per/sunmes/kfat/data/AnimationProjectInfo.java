/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package per.sunmes.kfat.data;

import com.sun.tracing.dtrace.NameAttributes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import per.sunmes.cppe.io.data.MPlist;

/**
 *
 * @author Administrator
 */
public class AnimationProjectInfo implements Serializable {

    public String name;

    public String projectDirectory;

    public String getImageFilePath(String imageName) {
        return String.format("%s/res/%s", projectDirectory, imageName);
    }

//    /**
//     * 包括 .png 单幅图 .plist 集合图
//     */
//    public List<String> imageResources = new ArrayList<>();
//
//    /**
//     * 将 plist 图片分为一张张的小图 与 单幅图 的 所有小图列表.
//     */
//    public List<String> imageNames = new ArrayList<>();
//
//    public Map<String, MPlist> plistPool = new HashMap<>();

    public List<AnimationInfo> animations = new ArrayList<>();

}
