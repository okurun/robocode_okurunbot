package okurun;

public class Util {
    public static enum Direction {
        LEFT(1), RIGHT(-1);
        public final int value;
        Direction(int value) { this.value = value; }
    }

    public static double[] calcPosition(double[] pos, double heading, double velocity, int diffTurnNum) {
        return calcPosition(pos[0], pos[1], heading, velocity, diffTurnNum);
    }

    public static double[] calcPosition(double x, double y, double heading, double velocity, int diffTurnNum) {
        final double rad = Math.toRadians(heading);
        final double newX = x + velocity * Math.cos(rad) * diffTurnNum;
        final double newY = y + velocity * Math.sin(rad) * diffTurnNum;
        return new double[] {newX, newY};
    }

    public static double[] calcPosition(double[] pos, double heading, double velocity, double turnDegree, int diffTurnNum) {
        return calcPosition(pos[0], pos[1], heading, velocity, turnDegree, diffTurnNum);
    }

    public static double[] calcPosition(double x, double y, double heading, double velocity, double turnDegree, int diffTurnNum) {
        double newX = x;
        double newY = y;
        double newHeading = heading + turnDegree;
        for (int i = 0; i < diffTurnNum; i++) {
            final double[] pos = calcPosition(newX, newY, newHeading, velocity, 1);
            newX = pos[0];
            newY = pos[1];
            newHeading += turnDegree;
        }
        return new double[] {newX, newY};
    }
}
