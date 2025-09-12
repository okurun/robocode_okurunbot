package okurun.predictor;

import okurun.predictor.model.PredictModel;

public class PredictData {
    public final double x;
    public final double y;
    public final double heading;
    public final double velocity;
    public final double turnDegree;
    public final int predictedTurnNum;
    public final PredictModel model;

    public PredictData(double x, double y, double heading, double velocity, double turnDegree, int predictedTurnNum, PredictModel model) {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.velocity = velocity;
        this.turnDegree = turnDegree;
        this.predictedTurnNum = predictedTurnNum;
        this.model = model;
    }

    @Override
    public String toString() {
        return "PredictData{" +
            "x=" + x +
            ", y=" + y +
            ", heading=" + heading +
            ", velocity=" + velocity +
            ", turnDegree=" + turnDegree +
            ", predictedTurnNum=" + predictedTurnNum +
            ", model=" + model +
            '}';
    }
}
