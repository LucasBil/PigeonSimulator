package ca.uqac.entities;

import java.util.concurrent.atomic.AtomicBoolean;

public class Food {
    public static final long DUREE_FRAICHEUR_MS = 8000;
    public static final long DUREE_VIE_MS = 20000;

    public final double x, y;
    public final long created = System.currentTimeMillis();
    private final AtomicBoolean isEaten = new AtomicBoolean(false);

    public Food(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean isEatable() {
        return !isEaten.get() && (System.currentTimeMillis() - created) < DUREE_FRAICHEUR_MS;
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() - created) > DUREE_VIE_MS;
    }

    public boolean tryEat() {
        return isEatable() && isEaten.compareAndSet(false, true);
    }

    public boolean getIsEaten() {
        return isEaten.get();
    }
}
