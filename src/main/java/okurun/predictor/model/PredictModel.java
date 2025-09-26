package okurun.predictor.model;

import java.util.concurrent.atomic.AtomicInteger;

import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.predictor.PredictData;
import okurun.radaroperator.EnemyState;

public abstract class PredictModel {
    private final AtomicInteger firedCount = new AtomicInteger(0);
    private final AtomicInteger hitCount = new AtomicInteger(0);
    private final AtomicInteger missCount = new AtomicInteger(0);
    private final AtomicInteger noCount = new AtomicInteger(0);
    protected final Commander commander;

    public PredictModel(Commander commander) {
        this.commander = commander;
    }

    public int getFiredCount() {
        return firedCount.get();
    }

    public void incrementFiredCount() {
        firedCount.incrementAndGet();
    }

    public int getHitCount() {
        return hitCount.get();
    }

    public void incrementHitCount() {
        hitCount.incrementAndGet();
    }

    public int getMissCount() {
        return missCount.get();
    }

    public void incrementMissCount() {
        missCount.incrementAndGet();
    }

    public int getNoCount() {
        return noCount.get();
    }

    public void incrementNoCount() {
        noCount.incrementAndGet();
    }

    public double getHitRate() {
        final int fired = firedCount.get() - noCount.get();
        final int hit = hitCount.get();
        if (fired == 0 || hit == 0) return 0.0;
        return (double) hit / fired;
    }

    public double getMissRate() {
        final int fired = firedCount.get() - noCount.get();
        final int miss = missCount.get();
        if (fired == 0 || miss == 0) return 0.0;
        return (double) miss / fired;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
            "firedCount=" + firedCount.get() +
            ", hitCount=" + hitCount.get() +
            ", missCount=" + missCount.get() +
            ", noCount=" + noCount.get() +
            ", hitRate=" + getHitRate() +
            ", missRate=" + getMissRate() +
            '}';
    }

    abstract public PredictData predict(EnemyState enemyState, int predictTurnNum);
    abstract public Color getBulletColor();
    abstract public void clearCache();
}
