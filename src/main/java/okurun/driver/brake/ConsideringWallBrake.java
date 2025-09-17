package okurun.driver.brake;

import dev.robocode.tankroyale.botapi.Constants;
import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.arenamap.ArenaMap;
import okurun.arenamap.ArenaMap.Wall;

public class ConsideringWallBrake extends Brake {
    public ConsideringWallBrake(Commander commander) {
        super(commander);
    }

    @Override
    public double brake(double speed, double forwardDistance) {
        if (speed == 0) {
            return 0;
        }

        final IBot bot = commander.getBot();
        final ArenaMap arenaMap = ArenaMap.getInstance();
        final Wall nearestWall = arenaMap.getNearestWall(bot);
        final double degreeToWall = nearestWall.degreeTo(bot);
        if (forwardDistance > 0 && Math.abs(degreeToWall) > 90 || forwardDistance < 0 && Math.abs(degreeToWall) < 90) {
            return speed;
        }

        final double distanceToWall = nearestWall.distanceTo(bot) - (Commander.BODY_SIZE / 2);
        if (distanceToWall - 5 < (Constants.MAX_SPEED + (Constants.DECELERATION * 3))) {
            return 0;
        }
        if (distanceToWall - 5 < (Constants.MAX_SPEED + (Constants.DECELERATION * 2))) {
            return Math.min(Math.abs(Constants.DECELERATION), speed) * (forwardDistance > 0 ? 1 : -1);
        }
        if (distanceToWall - 5 < (Constants.MAX_SPEED + Constants.DECELERATION)) {
            return Math.min(Math.abs(Constants.DECELERATION * 2), speed) * (forwardDistance > 0 ? 1 : -1);
        }
        if (distanceToWall - 5 < Constants.MAX_SPEED) {
            return Math.min(Math.abs(Constants.DECELERATION * 3), speed) * (forwardDistance > 0 ? 1 : -1);
        }
        return speed;
    }
}