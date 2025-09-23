package okurun.arenamap;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;

public class ArenaMap {
    private static final ArenaMap instance = new ArenaMap();
    public static ArenaMap getInstance() {
        return instance;
    }

    public static enum WallId {
        LEFT, TOP, RIGHT, BOTTOM
    }

    public class Wall {
        public final WallId id;
        public final int x, y;

        Wall(WallId id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public double distanceTo(IBot bot) {
            return distanceTo(bot.getX(), bot.getY());
        }

        public double distanceTo(double px, double py) {
            return switch (id) {
                case LEFT, RIGHT -> Math.abs(px - x);
                case TOP, BOTTOM -> Math.abs(py - y);
            };
        }

        public double degreeTo(IBot bot) {
            return switch (id) {
                case TOP, BOTTOM -> bot.bearingTo(bot.getX(), y);
                case LEFT, RIGHT -> bot.bearingTo(x, bot.getY());
            };
        }

        public Wall getOppositeWall() {
            return walls.get(switch (id) {
                case LEFT -> WallId.RIGHT;
                case TOP -> WallId.BOTTOM;
                case RIGHT -> WallId.LEFT;
                case BOTTOM -> WallId.TOP;
            });
        }

        public boolean isFacing(double direction) {
            return switch (id) {
                case TOP -> (direction > 0 && direction < 180);
                case BOTTOM -> (direction > 180 && direction < 360);
                case LEFT -> (direction < 270 && direction > 90);
                case RIGHT -> (direction < 90 || direction > 270);
            };
        }
    }

    public static enum CornerId {
        TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT
    }

    public class Corner {
        public final CornerId id;
        public final int x, y;

        Corner(CornerId id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }

        public double distanceTo(IBot bot) {
            return bot.distanceTo(this.x, this.y);
        }

        public double distanceTo(double[] pos) {
            return distanceTo(pos[0], pos[1]);
        }

        public double distanceTo(double x, double y) {
            return Math.hypot(x - this.x, y - this.y);
        }

        public Corner getOppositeCorner() {
            return switch (id) {
                case TOP_LEFT -> corners.get(CornerId.BOTTOM_RIGHT);
                case TOP_RIGHT -> corners.get(CornerId.BOTTOM_LEFT);
                case BOTTOM_RIGHT -> corners.get(CornerId.TOP_LEFT);
                case BOTTOM_LEFT -> corners.get(CornerId.TOP_RIGHT);
            };
        }

        public List<Corner> getNeighboringCornersWithSelf() {
            return switch (id) {
                case TOP_LEFT -> List.of(this, corners.get(CornerId.TOP_RIGHT), corners.get(CornerId.BOTTOM_LEFT));
                case TOP_RIGHT -> List.of(this, corners.get(CornerId.TOP_LEFT), corners.get(CornerId.BOTTOM_RIGHT));
                case BOTTOM_RIGHT -> List.of(this, corners.get(CornerId.BOTTOM_LEFT), corners.get(CornerId.TOP_RIGHT));
                case BOTTOM_LEFT -> List.of(this, corners.get(CornerId.BOTTOM_RIGHT), corners.get(CornerId.TOP_LEFT));
            };
        }
    }

    private int width;
    private int height;
    private Map<WallId, Wall> walls;
    private Map<CornerId, Corner> corners;

    private ArenaMap() {
    }

    public void init(int width, int height) {
        this.width = width;
        this.height = height;
        this.walls = Map.of(
            WallId.LEFT, new Wall(WallId.LEFT, 0, -1),
            WallId.TOP, new Wall(WallId.TOP, -1, height),
            WallId.RIGHT, new Wall(WallId.RIGHT, width, -1),
            WallId.BOTTOM, new Wall(WallId.BOTTOM, -1, 0)
        );
        this.corners = Map.of(
            CornerId.TOP_LEFT, new Corner(CornerId.TOP_LEFT, 0, height),
            CornerId.TOP_RIGHT, new Corner(CornerId.TOP_RIGHT, width, height),
            CornerId.BOTTOM_RIGHT, new Corner(CornerId.BOTTOM_RIGHT, width, 0),
            CornerId.BOTTOM_LEFT, new Corner(CornerId.BOTTOM_LEFT, 0, 0)
        );
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public Wall getNearestWall(IBot bot) {
        return getNearestWall(bot.getX(), bot.getY());
    }

    public Wall getNearestWall(double x, double y) {
        return walls.values().stream()
            .min(Comparator.comparingDouble(w -> w.distanceTo(x, y)))
            .orElse(null);
    }

    public Corner getNearestCorner(IBot bot) {
        return corners.values().stream()
            .min(Comparator.comparingDouble(c -> c.distanceTo(bot)))
            .orElse(null);
    }

    public double[] keepPositionInArena(double[] pos, double[] beforePos) {
        return keepPositionInArena(pos[0], pos[1], beforePos[0], beforePos[1]);
    }

    /**
     * 壁にぶつかった場合にぶつかった位置を維持する
     */
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
