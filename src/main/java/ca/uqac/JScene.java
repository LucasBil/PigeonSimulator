package ca.uqac;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JScene extends JPanel {
    private final Scene scene;

    public JScene(Scene scene) {
        this.scene = scene;
        setPreferredSize(new Dimension(scene.width, scene.height));
        setBackground(new Color(235, 230, 215));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                scene.addFood(e.getX(), e.getY());
            }
        });

        new Timer(16, e -> {
            scene.clear();
            repaint();
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        scene.draw(g2);

        g2.setColor(Color.DARK_GRAY);
        g2.drawString(String.format("Probabilite d'effrayement : %.0f %%",
                scene.probabiliteEffrayement() * 100), 10, 20);
    }
}
