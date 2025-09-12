package okurun.gunner;

import okurun.predictor.model.PredictModel;

public class ShootingTarget {
    public final int enemyId;
    public final double firePower;
    public final double x;
    public final double y;
    public final double distance;
    public final int turnNum;
    public final PredictModel predictModel;

    public ShootingTarget(int enemyId, double firePower, double x, double y, double distance, int turnNum, PredictModel predictModel) {
        this.enemyId = enemyId;
        this.firePower = firePower;
        this.x = x;
        this.y = y;
        this.distance = distance;
        this.turnNum = turnNum;
        this.predictModel = predictModel;
    }

    @Override
    public String toString() {
        return "ShootigTarget{" +
                "enemyId=" + enemyId +
                ", firePower=" + firePower +
                ", x=" + x +
                ", y=" + y +
                ", distance=" + distance +
                ", turnNum=" + turnNum +
                ", predictModel=" + predictModel.getClass().getSimpleName() +
                '}';
    }
}
