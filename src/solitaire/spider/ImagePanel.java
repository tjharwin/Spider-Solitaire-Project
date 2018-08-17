package solitaire.spider;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * <h1>solitaire.spider.ImagePanel</h1>
 * This is a class for creating the background image. It extends the JPanel class and overrides the paintComponent
 * method to achieve this.
 *
 * @author http://www.java2s.com
 * @since 2015
 */

class ImagePanel extends JPanel {

    private Image img;

    public ImagePanel(String img) {

        this(new ImageIcon(img).getImage());
    }

    public ImagePanel(Image img) {
        this.img = img;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
    }
}
