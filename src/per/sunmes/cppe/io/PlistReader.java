package per.sunmes.cppe.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import per.sunmes.cppe.io.data.MPlist;
import per.sunmes.cppe.io.data.MPlist.Frame;
import per.sunmes.cppe.io.data.MPlist.Metadata;

public class PlistReader {

    public static MPlist readPlist(File file) {
        SAXBuilder sax = new SAXBuilder();
        MPlist plist = null;
        try {
            Document document = sax.build(file);
            plist = new MPlist();
            Element root = document.getRootElement();
            plist.version = root.getAttributeValue("version");
            readData(plist, root);
        } catch (JDOMException | IOException e) {
        }
        return plist;
    }

    private static void readData(MPlist plist, Element root) {
        List<Element> childs = root.getChild("dict").getChildren();
        for (int i = 0; i < childs.size(); i++) {
            Element el = childs.get(i);
            if ("key".equals(el.getName())) {
                switch (el.getText()) {
                    case "frames":
                        readFrames(plist, childs.get(++i));
                        break;
                    case "metadata":
                        readMetaData(plist, childs.get(++i));
                }
            }
        }
    }

    private static void readFrames(MPlist plist, Element frames) {
        List<Element> childs = frames.getChildren();
        if (plist.frames == null) {
            plist.frames = new ArrayList<>();
        }
        for (int i = 0; i < childs.size(); i++) {
            Element el = childs.get(i);
            if ("key".equals(el.getName())) {
                Frame frame = plist.new Frame();
                frame.name = el.getText();
                readFrame(frame, childs.get(++i));
                plist.frames.add(frame);
                plist.imageFrames.put(frame.name, frame);
            }
        }
    }

    private static void readMetaData(MPlist plist, Element element) {
        List<Element> childs = element.getChildren();
        Metadata metadata = plist.new Metadata();
        for (int i = 0; i < childs.size(); i++) {
            Element el = childs.get(i);
            if ("key".equals(el.getName())) {
                switch (el.getText()) {
                    case "format":
                        metadata.format = Integer.parseInt(childs.get(++i).getText());
                        break;
                    case "realTextureFileName":
                        metadata.realTextureFileName = childs.get(++i).getText();
                        break;
                    case "size":
                        metadata.size = MPlist.getPoints(childs.get(++i).getText());
                        break;
                    case "smartupdate":
                        metadata.smartupdate = childs.get(++i).getText();
                        break;
                    case "textureFileName":
                        metadata.textureFileName = childs.get(++i).getText();
                        break;
                }
            }
        }
        plist.metadata = metadata;
    }

    private static void readFrame(Frame frame, Element e) {
        List<Element> childs = e.getChildren();
        for (int i = 0; i < childs.size(); i++) {
            Element el = childs.get(i);
            if ("key".equals(el.getName())) {
                switch (el.getText()) {
                    case "frame":
                        frame.frame = MPlist.getPoints(childs.get(++i).getText());
                        break;
                    case "offset":
                        frame.offset = MPlist.getPoints(childs.get(++i).getText());
                        break;
                    case "rotated":
                        frame.rotated = "true".equals(childs.get(++i).getName());
                        break;
                    case "sourceColorRect":
                        frame.sourceColorRect = MPlist.getPoints(childs.get(++i).getText());
                        break;
                    case "sourceSize":
                        frame.sourceSize = MPlist.getPoints(childs.get(++i).getText());
                        break;
                }
            }
        }
    }

}
