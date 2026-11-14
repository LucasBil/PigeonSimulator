package ca.uqac;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PigeonSimulation {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Scene scene = new Scene(800, 600);
            JScene panneau = new JScene(scene);

            JFrame fenetre = new JFrame("Nourrissons les pigeons !");
            fenetre.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            fenetre.add(panneau);
            fenetre.pack();
            fenetre.setLocationRelativeTo(null);
            fenetre.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    scene.stop();      // interrompt proprement tous les threads
                    fenetre.dispose();
                }
            });
            fenetre.setVisible(true);

            scene.start(10);           // 10 pigeons
        });
    }
}