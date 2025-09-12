package okurun.battlemanager;

import okurun.gunner.ShootingTarget;

public class BulletData {
    public enum HitState {
        IN_FLIGHT,
        HIT,
        MISS,
        HIT_BULLET,
        HIT_OTHER
    }

    public final int bulletId;
    public final ShootingTarget shootingTarget;
    public final int firedTurnNum;
    public final HitState hitState;

    public BulletData(int bulletId, ShootingTarget shootingTarget, int firedTurnNum, HitState hitState) {
        this.bulletId = bulletId;
        this.shootingTarget = shootingTarget;
        this.firedTurnNum = firedTurnNum;
        this.hitState = hitState;
    }

    @Override
    public String toString() {
        return "BulletData{" +
                "bulletId=" + bulletId +
                ", ShootigTarget=" + shootingTarget +
                ", firedTurnNum=" + firedTurnNum +
                ", hitState=" + hitState +
                '}';
    }
}
