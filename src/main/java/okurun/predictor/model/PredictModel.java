package okurun.predictor.model;

import java.util.concurrent.atomic.AtomicInteger;

import okurun.predictor.PredictData;
import okurun.radaroperator.EnemyState;

public abstract class PredictModel {
    private final AtomicInteger firedCount = new AtomicInteger(0);
    private final AtomicInteger hitCount = new AtomicInteger(0);
    private final AtomicInteger missCount = new AtomicInteger(0);
    private final AtomicInteger noCount = new AtomicInteger(0);

    public PredictModel() {
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
            "firedCount=" + firedCount +
            ", hitCount=" + hitCount +
            ", missCount=" + missCount +
            ", noCount=" + noCount +
            ", hitRate=" + getHitRate() +
            ", missRate=" + getMissRate() +
            '}';
    }

    abstract public PredictData predict(EnemyState enemyState, int predictTurnNum);
}
