package cn.devkits.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Main {

    public static void main(String[] args) {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY), new EmptyBorder(4, 4, 4, 4)));
        final JLabel status = new JLabel();
        statusBar.add(status);

        JLabel content = new JLabel("Content in the middle");
        content.setHorizontalAlignment(JLabel.CENTER);

        final JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(content);
        frame.add(statusBar, BorderLayout.SOUTH);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                status.setText(frame.getWidth() + "x" + frame.getHeight());
            }
        });

        frame.setBounds(20, 20, 200, 200);
        frame.setVisible(true);
    }

}
