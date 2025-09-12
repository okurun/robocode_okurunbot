package okurun.predictor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.predictor.model.*;
import okurun.radaroperator.EnemyState;

public class Predictor {
    private static Predictor instance;
    public static Predictor getInstance() {
        if (instance == null) {
            instance = new Predictor();
        }
        return instance;
    }

    private final Map<Integer, Map<String, PredictModel>> predictModels = new HashMap<>();

    private Predictor() {
    }

    public void reset() {
        predictModels.clear();
    }

    public void init(Commander commander) {
        final IBot bot = commander.getBot();
        for (int i = 1; i <= bot.getEnemyCount() + 1; i++) {
            if (i == bot.getMyId()) continue;
            if (predictModels.get(i) != null) continue;
            predictModels.put(i, Map.of(
                SimplePredictModel.class.getSimpleName(), new SimplePredictModel()
            ));
        }
    }

    public PredictData predict(EnemyState enemyState, int predictTurnNum) {
        final List<PredictModel> models = predictModels.get(enemyState.enemyId).values().stream()
            .sorted(Comparator.comparingDouble(PredictModel::getMissRate))
            .toList();
        for (PredictModel model : models) {
            final PredictData data = model.predict(enemyState, predictTurnNum);
            if (data != null) {
                return data;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Predictor{" +
            "predictModels=\n\t" + predictModels.values().stream().map(pm -> pm.toString()).collect(Collectors.joining("\n\t")) +
            '}';
    }
}
