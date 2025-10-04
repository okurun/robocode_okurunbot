package okurun.radaroperator;

import dev.robocode.tankroyale.botapi.Constants;

public class EnemyState {
    public final int enemyId;
    public final double x;
    public final double y;
    public final double heading;
    public final double velocity;
    public final double energy;
    public final int scandTurnNum;
    public final double distanceToMe;
    public final double distanceToForwardWall;
    public EnemyState previousState;

    public EnemyState(int enemyId) {
        this.enemyId = enemyId;
        this.x = 0;
        this.y = 0;
        this.heading = 0;
        this.velocity = 0;
        this.energy = 100;
        this.scandTurnNum = 0;
        this.distanceToMe = 0;
        this.distanceToForwardWall = 0;
        this.previousState = null;
    }

    public EnemyState(
            int enemyId,
            double x, double y,
            double heading, double velocity,
            double energy, int scandTurnNum,
            double distanceToMe, double distanceToForwardWall,
            EnemyState previousState) {
        this.enemyId = enemyId;
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.velocity = velocity;
        this.energy = energy;
        this.scandTurnNum = scandTurnNum;
        this.distanceToMe = distanceToMe;
        this.distanceToForwardWall = distanceToForwardWall;
        this.previousState = previousState;
    }

    public double getTurnDegree() {
        if (previousState == null) return 0.0;
        final double diffHeading = heading - previousState.heading;
        final int diffTurnNum = scandTurnNum - previousState.scandTurnNum;
        if (diffHeading == 0 || diffTurnNum == 0) return 0.0;
        return diffHeading / diffTurnNum;
    }

    public double[] getPosition() {
        return new double[]{x, y};
    }

    public void deletePreviousState() {
        this.previousState = null;
    }

    /**
     * 他のデータポイントとの「距離」を計算する
     * この値が小さいほど「似ている状況」と判断する
     */
    public double calculateDistance(EnemyState other) {
        // 各要素の差の2乗和（重み付けは後で調整）
        // 正規化のために、妥当な最大値で割る
        double dist = 0;
        dist += Math.pow((this.distanceToMe - other.distanceToMe) / 1000.0, 2);
        dist += Math.pow((this.distanceToForwardWall - other.distanceToForwardWall) / 1000.0, 2);
        dist += Math.pow((this.velocity - other.velocity) / Constants.MAX_SPEED, 2);
        return dist;
    }

    @Override
    public String toString() {
        return "EnemyState{" +
                "enemyId=" + enemyId +
                ", x=" + x +
                ", y=" + y +
                ", heading=" + heading +
                ", velocity=" + velocity +
                ", energy=" + energy +
                ", scandTurnNum=" + scandTurnNum +
                '}';
    }
}