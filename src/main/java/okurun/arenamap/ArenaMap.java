package okurun.arenamap;

import okurun.Commander;

public class ArenaMap {
    private static final ArenaMap instance = new ArenaMap();
    public static ArenaMap getInstance() {
        return instance;
    }

    private int width;
    private int height;

    private ArenaMap() {
    }

    public void init(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double[] keepPositionInArena(double[] pos, double[] beforePos) {
        return keepPositionInArena(pos[0], pos[1], beforePos[0], beforePos[1]);
    }

    public double[] keepPositionInArena(double x, double y, double beforeX, double beforeY) {
        final double minX = Commander.BODY_SIZE;
        final double maxX = width - Commander.BODY_SIZE;
        final double minY = Commander.BODY_SIZE;
        final double maxY = height - Commander.BODY_SIZE;
        final double x0 = beforeX;
        final double y0 = beforeY;
        final double x1 = x;
        final double y1 = y;
        final double dx = x1 - x0;
        final double dy = y1 - y0;
        if (dx == 0 && dy == 0) {
            // no movement
            return new double[]{clamp(x, minX, maxX), clamp(y, minY, maxY)};
        }
        double[] tt = new double[]{0, 1};
        if (!clip(-dx, x0 - minX, tt)) return new double[]{clamp(x0, minX, maxX), clamp(y0, minY, maxY)};
        if (!clip(dx, maxX - x0, tt)) return new double[]{clamp(x0, minX, maxX), clamp(y0, minY, maxY)};
        if (!clip(-dy, y0 - minY, tt)) return new double[]{clamp(x0, minX, maxX), clamp(y0, minY, maxY)};
        if (!clip(dy, maxY - y0, tt)) return new double[]{clamp(x0, minX, maxX), clamp(y0, minY, maxY)};
        final double tHit = Math.min(1, tt[1]);
        double x2 = x0 + dx * tHit;
        double y2 = y0 + dy * tHit;
        return new double[]{clamp(x2, minX, maxX), clamp(y2, minY, maxY)};
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static boolean clip(double p, double q, double[] tt) {
        double t0 = tt[0];
        double t1 = tt[1];
        if (p == 0) {
            if (q < 0) return false;
        } else {
            double r = q / p;
            if (p < 0) {
                if (r > t1) return false;
                if (r > t0) t0 = r;
            } else {
                if (r < t0) return false;
                if (r < t1) t1 = r;
            }
        }
        tt[0] = t0;
        tt[1] = t1;
        return true;
    }
}
