package okurun;

import java.util.concurrent.atomic.AtomicLong;

import dev.robocode.tankroyale.botapi.graphics.Color;

public class Util {
    public static enum Direction {
        LEFT(1), RIGHT(-1);
        public final int value;
        Direction(int value) { this.value = value; }
    }

    public static class ToggleColor {
        private static boolean isA = true;
        public static void toggle() { isA = !isA; }

        private final Color colorA;
        private final Color colorB;

        public ToggleColor(Color colorA, Color colorB) {
            this.colorA = colorA;
            this.colorB = colorB;
        }

        public Color get() {
            if (isA) {
                return colorA;
            } else {
                return colorB;
            }
        }
    }

    public static class ExponentialMovingAverage {
        private final AtomicLong bits;

        public ExponentialMovingAverage(double initialValue) {
            bits = new AtomicLong(Double.doubleToRawLongBits(initialValue));
        }

        public double get() {
            return Double.longBitsToDouble(bits.get());
        }

        // prop = prop*0.9 + x*0.1
        public void update(double x) {
            while (true) {
                long currentBits = bits.get();
                double currentVal = Double.longBitsToDouble(currentBits);
                double newVal = currentVal * 0.9 + x * 0.1;
                long newBits = Double.doubleToRawLongBits(newVal);
                if (bits.compareAndSet(currentBits, newBits)) {
                    return;
                }
            }
        }
    }

    public static Color BASE_COLOR = Color.CYAN;
    public static Color LIGHT_COLOR = Color.LIGHT_CYAN;
    public static Color DARK_COLOR = Color.DARK_CYAN;
    public static Color WHITE_COLOR = Color.WHITE;
    public static Color YELLOW_COLOR = Color.YELLOW;
    public static Color ORANGE_COLOR = Color.ORANGE;
    public static Color RED_COLOR = Color.RED;
    public static Color BLUE_COLOR = Color.BLUE;
    public static Color GREEN_COLOR = Color.GREEN;
    public static Color GRAY_COLOR = Color.GRAY;

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

    public static int calcNextFireTurnNum(double gunHeat, double coolingRate) {
        if (gunHeat <= 0) {
            return 0;
        }
        return (int) Math.ceil(gunHeat / coolingRate);
    }
}
