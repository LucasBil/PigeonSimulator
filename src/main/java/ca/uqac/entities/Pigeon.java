package ca.uqac.entities;

import ca.uqac.Scene;

public class Pigeon implements Runnable {
    private static final double VITESSE = 3.0;
    private static final long PAS_MS = 16;
    private static final double RAYON_CONTACT = 10;

    private final Scene scene;
    private volatile double x, y;
    private volatile boolean isFright = false;
    private volatile double targetX, targetY;

    public Pigeon(Scene scene, double x, double y) {
        this.scene = scene;
        this.x = x;
        this.y = y;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public boolean getIsFright() { return isFright; }

    public void frightened(double cx, double cy) {
        targetX = cx;
        targetY = cy;
        isFright = true;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                scene.waitActivity(this);   // dort tant qu'il n'y a rien a faire

                if (isFright) {
                    if (travelTo(targetX, targetY)) isFright = false;
                } else {
                    Food n = scene.freshest();
                    if (n != null) {
                        if (travelTo(n.x, n.y)) {
                            // contact : un seul pigeon reussit tenterManger()
                            if (n.tryEat()) {
                                scene.removeFood(n);
                            }
                        }
                    }
                }
                Thread.sleep(PAS_MS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // arret normal
        } catch (Exception e) {
            System.err.println(Thread.currentThread().getName() + " : erreur " + e);
        }
    }

    private boolean travelTo(double tx, double ty) {
        double dx = tx - x, dy = ty - y;
        double dist = Math.hypot(dx, dy);
        if (dist <= RAYON_CONTACT) return true;
        x += dx / dist * VITESSE;
        y += dy / dist * VITESSE;
        return false;
    }
}
