/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package per.sunmes.kfat.ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Administrator
 */
public class ProjectFileFilter extends FileFilter {

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
        if (type.equals("kfa")) {
            return true;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Kfa 工程文件";
    }
}
