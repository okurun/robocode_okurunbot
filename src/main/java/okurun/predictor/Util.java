package okurun.predictor;

public class Util {
    public static double[] calcPosition(double x, double y, double heading, double velocity, int turnNum) {
        final double rad = Math.toRadians(heading);
        final double newX = x + velocity * Math.cos(rad) * turnNum;
        final double newY = y + velocity * Math.sin(rad) * turnNum;
        return new double[] {newX, newY};
    }

    public static double[] calcPosition(double x, double y, double heading, double velocity, double turnDegree, int turnNum) {
        double newX = x;
        double newY = y;
        double newHeading = heading + turnDegree;
        for (int i = 0; i < turnNum; i++) {
            final double[] pos = calcPosition(newX, newY, newHeading, velocity, 1);
            newX = pos[0];
            newY = pos[1];
            newHeading += turnDegree;
        }
        return new double[] {newX, newY};
    }

    public static int calcNextFireTurnNum(double gunHeat, double coolingRate) {
        if (gunHeat <= 0) {
            return 0;
        }
        return (int) Math.ceil(gunHeat / coolingRate);
    }
}
