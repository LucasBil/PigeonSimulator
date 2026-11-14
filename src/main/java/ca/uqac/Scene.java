package ca.uqac;

import ca.uqac.entities.Food;
import ca.uqac.entities.Pigeon;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Scene {
    public final int width, height;

    private final List<Food> foods = new ArrayList<>();
    private final List<Pigeon> pigeons = new ArrayList<>();
    private final List<Thread> threads = new ArrayList<>();

    private final ReentrantLock locker = new ReentrantLock();
    private final Condition activity = locker.newCondition();

    private Scrare scare;
    private Thread threadScare;

    public Scene(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void start(int nbPigeons) {
        Random r = new Random();
        locker.lock();
        try {
            for (int i = 0; i < nbPigeons; i++) {
                Pigeon p = new Pigeon(this, r.nextInt(width), r.nextInt(height));
                pigeons.add(p);
                Thread t = new Thread(p, "Pigeon-" + i);
                t.setDaemon(true);
                threads.add(t);
            }
        } finally {
            locker.unlock();
        }
        for (Thread t : threads) t.start();

        scare = new Scrare(this);
        threadScare = new Thread(scare, "Effrayeur");
        threadScare.setDaemon(true);
        threadScare.start();
    }

    public void stop() {
        if (threadScare != null) threadScare.interrupt();
        for (Thread t : threads) t.interrupt();
    }

    public void addFood(double x, double y) {
        locker.lock();
        try {
            foods.add(new Food(x, y));
            activity.signalAll();
        } finally {
            locker.unlock();
        }
    }

    public void removeFood(Food n) {
        locker.lock();
        try {
            foods.remove(n);
        } finally {
            locker.unlock();
        }
    }

    /** Nourriture fraiche la plus recente, ou null s'il n'y en a pas. */
    public Food freshest() {
        locker.lock();
        try {
            Food best = null;
            for (Food n : foods) {
                if (n.isEatable() && (best == null || n.created > best.created)) {
                    best = n;
                }
            }
            return best;
        } finally {
            locker.unlock();
        }
    }

    /** Nettoie la nourriture trop vieille (appele periodiquement). */
    public void clear() {
        locker.lock();
        try {
            foods.removeIf(Food::isExpired);
        } finally {
            locker.unlock();
        }
    }

    /** Bloque le pigeon tant qu'il n'y a rien a faire (plus de nourriture fraiche, pas effraye). */
    public void waitActivity(Pigeon p) throws InterruptedException {
        locker.lock();
        try {
            while (freshest() == null && !p.getIsFright()) {
                activity.await();
            }
        } finally {
            locker.unlock();
        }
    }

    /* ----- effrayement ----- */

    public void frightAll() {
        Random r = new Random();
        locker.lock();
        try {
            for (Pigeon p : pigeons) {
                p.frightened(r.nextInt(width), r.nextInt(height));
            }
            activity.signalAll();
        } finally {
            locker.unlock();
        }
    }

    /* ----- dessin : on verrouille pendant le parcours ----- */

    public void draw(Graphics2D g) {
        locker.lock();
        try {
            for (Food n : foods) {
                g.setColor(n.isEatable() ? new Color(205, 170, 60) : Color.GRAY);
                g.fillOval((int) n.x - 5, (int) n.y - 5, 10, 10);
            }
            for (Pigeon p : pigeons) {
                g.setColor(p.getIsFright() ? new Color(220, 90, 90) : new Color(90, 100, 120));
                g.fillOval((int) p.getX() - 12, (int) p.getY() - 12, 24, 24);
            }
        } finally {
            locker.unlock();
        }
    }

    double probabiliteEffrayement() {
        return scare == null ? 0 : scare.getProbability();
    }
}