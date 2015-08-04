/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package per.sunmes.kfat.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Administrator
 */
public class ImageResourceFileFilter extends FileFilter {

    List<String> fileType = new ArrayList<>(Arrays.asList(
            "PNG", "JPG", "BMP", "GIF", "JPEG", "plist"
    ));

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        int p = f.getName().lastIndexOf('.');
        if (p <= 0) {
            return false;
        }
        String type = f.getName().substring(p + 1);
        for (String t : fileType) {
            if (t.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "plist和图片文件";
    }

}
