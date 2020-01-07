package cn.devkits.client.tray.frame.assist;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

/**
 * 
 * 文件预览
 * @author shaofeng liu
 * @version 1.0.0
 * @time 2019年11月11日 下午11:09:31
 */
public class FileChoosePreviewerComponent extends JComponent implements PropertyChangeListener {

    /** serialVersionUID */
    private static final long serialVersionUID = 5051395170955469946L;
    ImageIcon thumbnail = null;

    public FileChoosePreviewerComponent(JFileChooser fc) {
        setPreferredSize(new Dimension(100, 50));
        fc.addPropertyChangeListener(this);
    }

    public void loadImage(File f) {
        if (f == null) {
            thumbnail = null;
        } else {
            ImageIcon tmpIcon = new ImageIcon(f.getPath());
            if (tmpIcon.getIconWidth() > 90) {
                thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(90, -1, Image.SCALE_DEFAULT));
            } else {
                thumbnail = tmpIcon;
            }
        }
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            if (isShowing()) {
                loadImage((File) e.getNewValue());
                repaint();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        if (thumbnail != null) {
            int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
            int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;
            if (y < 0) {
                y = 0;
            }

            if (x < 5) {
                x = 5;
            }
            thumbnail.paintIcon(this, g, x, y);
        }
    }
}