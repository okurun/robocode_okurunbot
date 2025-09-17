package okurun.predictor.model;

import java.util.HashMap;
import java.util.Map;

import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Util;
import okurun.arenamap.ArenaMap;
import okurun.predictor.PredictData;
import okurun.radaroperator.EnemyState;

public class SimplePredictModel extends PredictModel {
    private final Map<Integer, PredictData> cache = new HashMap<>();

    public PredictData predict(EnemyState enemyState, int predictTurnNum) {
        final var arenaMap = ArenaMap.getInstance();
        final double turnDegree = enemyState.getTurnDegree();
        PredictData newData = new PredictData(
            enemyState.getPosition(),
            enemyState.heading, enemyState.velocity, turnDegree,
            enemyState.scandTurnNum, this
        );
        PredictData prevData;
        if (enemyState.previousState == null) {
            prevData = newData;
        } else {
            prevData = new PredictData(
                enemyState.previousState.getPosition(),
                enemyState.previousState.heading, enemyState.previousState.velocity, enemyState.previousState.getTurnDegree(),
                enemyState.previousState.scandTurnNum, this
            );
        }
        for (int i = enemyState.scandTurnNum + 1; i <= predictTurnNum; i++) {
            if (cache.containsKey(i)) {
                prevData = newData;
                newData = cache.get(i);
                continue;
            }
            final PredictData tempData = newData;
            double[] newPos = Util.calcPosition(
                newData.getPosition(),
                newData.heading, newData.velocity,
                newData.turnDegree,
                1
            );
            newPos = arenaMap.keepPositionInArena(newPos, prevData.getPosition());
            newData = new PredictData(
                newPos,
                newData.heading + turnDegree,
                newData.velocity, newData.turnDegree,
                i, this
            );
            prevData = tempData;
            cache.put(i, newData);
        }
        return newData;
    }

    @Override
    public Color getBulletColor() {
        return Util.RED_COLOR;
    }

    @Override
    public void clearCache() {
        cache.clear();
    }
}
