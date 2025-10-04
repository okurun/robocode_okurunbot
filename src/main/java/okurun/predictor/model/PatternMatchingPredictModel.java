package okurun.predictor.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.Util;
import okurun.arenamap.ArenaMap;
import okurun.predictor.PredictData;
import okurun.radaroperator.EnemyState;

public class PatternMatchingPredictModel extends PredictModel {

    public PatternMatchingPredictModel(Commander commander) {
        super(commander);
    }

    private final Map<Integer, PredictData> cache = new HashMap<>();
    private List<EnemyState> nearestNeighbors = null;

    @Override
    public PredictData predict(EnemyState enemyState, int predictTurnNum) {
        // Find K-Nearest Neighbors (KNN)
        if (nearestNeighbors == null) {
            final IBot bot = commander.getBot();
            final List<EnemyState> historicalData = new ArrayList<>();
            for (EnemyState state = enemyState.previousState; state != null; state = state.previousState) {
                if (state.scandTurnNum > bot.getTurnNumber() - 10 || state.scandTurnNum == 0) {
                    continue;
                }
                historicalData.add(state);
            }
            // If we don't have enough historical data, we can't predict.
            if (historicalData.size() < 10) {
                return null;
            }

            final EnemyState nearestNeighbor = historicalData.stream()
                .parallel() // Use parallel stream for performance
                .min(Comparator.comparingDouble(s -> s.calculateDistance(enemyState)))
                .orElse(null);

            nearestNeighbors = historicalData.stream()
                .filter(e -> e.scandTurnNum >= nearestNeighbor.scandTurnNum)
                .sorted(Comparator.comparingInt(e -> e.scandTurnNum))
                .limit(20)
                .collect(Collectors.toList());
        }


        // Simulate Future Movement
        PredictData newData = new PredictData(
            enemyState.getPosition(),
            enemyState.heading, enemyState.velocity, enemyState.getTurnDegree(),
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

        final ArenaMap arenaMap = ArenaMap.getInstance();
        for (int i = enemyState.scandTurnNum + 1; i <= predictTurnNum; i++) {
            if (cache.containsKey(i)) {
                prevData = newData;
                newData = cache.get(i);
                continue;
            }
            final PredictData tempData = newData;
            final EnemyState pattern = nearestNeighbors.get(i % nearestNeighbors.size());
            final double turnDegree = pattern.getTurnDegree();
            double[] newPos = Util.calcPosition(
                newData.getPosition(),
                newData.heading,
                pattern.velocity,
                turnDegree,
                1
            );
            newPos = arenaMap.keepPositionInArena(newPos, prevData.getPosition());
            newData = new PredictData(
                newPos,
                newData.heading + turnDegree,
                newData.velocity, turnDegree,
                i, this
            );
            prevData = tempData;
            cache.put(i, newData);
        }
        return newData;
    }

    @Override
    public Color getBulletColor() {
        return Util.BASE_COLOR;
    }

    @Override
    public void clearCache() {
        cache.clear();
        nearestNeighbors = null;
    }
}