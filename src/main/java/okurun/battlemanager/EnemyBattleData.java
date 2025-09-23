package okurun.battlemanager;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import okurun.Util.ExponentialMovingAverage;

public class EnemyBattleData {
    public final int enemyId;

    private final AtomicInteger targetedCount = new AtomicInteger(0);
    private final AtomicInteger hitCount = new AtomicInteger(0);
    private final AtomicInteger missCount = new AtomicInteger(0);
    private final AtomicInteger noCount = new AtomicInteger(0);
    private final ExponentialMovingAverage hitDistanceEma = new ExponentialMovingAverage(0);

    public EnemyBattleData(int enemyId) {
        this.enemyId = enemyId;
    }

    public EnemyBattleData(int enemyId, Map<Integer, BulletData> targetedBullets) {
        this.enemyId = enemyId;
    }

    public int getTargetedCount() {
        return targetedCount.get();
    }

    public void incrementTargetedCount() {
        targetedCount.incrementAndGet();
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

    public void updateHitDistanceEma(double distance) {
        hitDistanceEma.update(distance);
    }

    public double getHitRate() {
        final int targeted = targetedCount.get() - noCount.get();
        final int hit = hitCount.get();
        if (targeted == 0 || hit == 0) return 0.0;
        return (double) hit / targeted;
    }

    @Override
    public String toString() {
        return "EnemyBattleData{" +
                "enemyId=" + enemyId +
                ", targetedCount=" + targetedCount +
                ", hitCount=" + hitCount +
                ", missCount=" + missCount +
                ", noCount=" + noCount +
                ", hitDistanceEma=" + hitDistanceEma.get() +
                ", hitRate=" + getHitRate() +
                '}';
    }
}
