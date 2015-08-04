/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package per.sunmes.kfat;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import per.sunmes.kfat.sys.ApplicationConfig;
import per.sunmes.kfat.sys.FileUtil;
import per.sunmes.kfat.sys.SysData;
import per.sunmes.kfat.ui.MainFrame;

/**
 *
 * @author Administrator
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        LookAndFeel laf = new WindowsLookAndFeel();
        try {
            UIManager.setLookAndFeel(laf);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        ApplicationConfig config = SysData.instance().getApplicationConfig();
        MainFrame mf = new MainFrame();
        mf.setVisible(true);
        
    }

}
