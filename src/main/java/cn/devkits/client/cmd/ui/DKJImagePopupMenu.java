package cn.devkits.client.cmd.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * https://blog.csdn.net/huiyi789/article/details/84125323
 */
public class DKJImagePopupMenu extends JPopupMenu {
    private Font font = new Font("Dialog", Font.BOLD, 13);
    private ImageIcon imageIcon = null;

    public DKJImagePopupMenu(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
    }

    public DKJImagePopupMenu(String text) {
        this.imageIcon = createImage(text);
    }

    private ImageIcon createImage(String text) {



        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        int height = fm.stringWidth(text) + 20;
        BufferedImage bi = new BufferedImage(30, height, BufferedImage.TYPE_INT_ARGB);
        ImageIcon image = new ImageIcon(bi);
        Graphics2D g2d = bi.createGraphics();

        GradientPaint paint = new GradientPaint(0, 0, Color.YELLOW, 30, 0, Color.RED, true);
        g2d.setPaint(paint);

        g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());

        AffineTransform at = new AffineTransform();
        at.rotate(-Math.PI / 2);

        g2d.setTransform(at);
        g2d.setColor(Color.white);
        g2d.setFont(font);
        g2d.drawString(text, -height + 10, bi.getWidth() / 2);

        return image;
    }

    public Insets getInsets() {
        Insets insets = (Insets) super.getInsets().clone();
        insets.left += imageIcon.getIconWidth();
        return insets;
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (imageIcon != null) {
            Insets insets = getInsets();
            // 每一副图像的位置坐标
            int x = insets.left - imageIcon.getIconWidth();
            int y = insets.top - 2;

            Image image = imageIcon.getImage();
            // 平铺背景图片
            while (true) {
                // 绘制图片
                g.drawImage(image, x, y, this);
                // 如果绘制完毕，退出循环
                if (y > getSize().height)
                    break;
                y += imageIcon.getIconHeight();
            }
        }
    }
}
