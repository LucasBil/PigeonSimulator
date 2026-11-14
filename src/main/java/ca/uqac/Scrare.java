package ca.uqac;

import java.util.Random;

class Scrare implements Runnable {
    private final Scene scene;
    private final Random random = new Random();
    private volatile double probability = 0.05;

    Scrare(Scene scene) {
        this.scene = scene;
    }

    double getProbability() { return probability; }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(2000);                       // un "tour" = 2 s
                probability = random.nextDouble() * 0.4;  // nouvelle probabilite a chaque tour
                if (random.nextDouble() < probability) {
                    scene.frightAll();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Effrayeur : erreur " + e);
        }
    }
}