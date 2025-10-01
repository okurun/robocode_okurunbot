package okurun.predictor;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.events.*;
import okurun.Commander;
import okurun.battlemanager.BattleManager;
import okurun.battlemanager.BulletData;
import okurun.gunner.ShootingTarget;
import okurun.predictor.model.*;
import okurun.radaroperator.EnemyState;

public class Predictor {
    static enum Distance {
        CLOSE, MID, FAR
    }

    private static Predictor instance;
    public static Predictor getInstance() {
        if (instance == null) {
            instance = new Predictor();
        }
        return instance;
    }

    private final Map<Integer, Map<Distance, Map<String, PredictModel>>> predictModelMap = new HashMap<>();
    private Commander commander;

    private Predictor() {
    }

    public void init(Commander commander) {
        this.commander = commander;
        final IBot bot = commander.getBot();
        final List<Distance> distances = List.of(Distance.CLOSE, Distance.MID, Distance.FAR);
        for (int i = 1; i <= bot.getEnemyCount() + 1; i++) {
            if (i == bot.getMyId()) continue;
            if (predictModelMap.get(i) != null) continue;
            final Map<Distance, Map<String, PredictModel>> distanceMap = new HashMap<>();
            for (Distance distance : distances) {
                distanceMap.put(distance, new HashMap<>(Map.of(
                    SimplePredictModel.class.getSimpleName(), new SimplePredictModel(commander),
                    MoveHistoryPredictModel.class.getSimpleName(), new MoveHistoryPredictModel(commander),
                    MoveHistoryRPredictModel.class.getSimpleName(), new MoveHistoryRPredictModel(commander)
                )));
            }
            predictModelMap.put(i, distanceMap);
        }
    }

    public void clearCache() {
        this.predictModelMap.values().forEach(d -> d.values().forEach(m -> m.values().forEach(PredictModel::clearCache)));
    }

    public PredictData predict(EnemyState enemyState, int predictTurnNum) {
        final IBot bot = commander.getBot();
        final Distance distance = getDistance(bot.distanceTo(enemyState.x, enemyState.y));
        final Collection<PredictModel> predictModels = this.predictModelMap.get(enemyState.enemyId).get(distance).values();
        final PredictModel unUserbleModel = predictModels.stream()
            .min(Comparator.comparingInt(pm -> pm.getFiredCount()))
            .get();
        if (unUserbleModel.getFiredCount() < 10) {
            // どのモデルも最低10発は射撃する
            PredictData predictData = unUserbleModel.predict(enemyState, predictTurnNum);
            if (predictData != null) {
                return predictData;
            }
        }
        // 失敗率の低いモデルを選ぶ
        final List<PredictModel> models = predictModels.stream()
            .sorted(Comparator.comparingDouble(pm -> Math.min(pm.getMissRate(), 0.9))) // 失敗率が100%にならないようにする
            .toList();
        for (PredictModel model : models) {
            final PredictData data = model.predict(enemyState, predictTurnNum);
            if (data != null) {
                return data;
            }
        }
        return null;
    }

    private Distance getDistance(double distanceToEnemy) {
        if (distanceToEnemy < 200) {
            return Distance.CLOSE;
        } else if (distanceToEnemy < 400) {
            return Distance.MID;
        } else {
            return Distance.FAR;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Predictor{\n");
        for (Map.Entry<Integer, Map<Distance, Map<String, PredictModel>>> entry : predictModelMap.entrySet()) {
            final int enemyId = entry.getKey();
            sb.append("\tenemyId=").append(enemyId).append("{\n");
            for (Map.Entry<Distance, Map<String, PredictModel>> distanceEntry : entry.getValue().entrySet()) {
                final Distance distance = distanceEntry.getKey();
                sb.append("\t\tdistance=").append(distance).append("{\n");
                final Map<String, PredictModel> models = distanceEntry.getValue();
                for (PredictModel pm : models.values()) {
                    sb.append("\t\t\t").append(pm.toString()).append("\n");
                }
                sb.append("\t\t}\n");
            }
            sb.append("\t}\n");
        }
        sb.append('}');
        return sb.toString();
    }

    public void onConnected(ConnectedEvent connectedEvent) {
    }

    public void onDisconnected(DisconnectedEvent disconnectedEvent) {
    }

    public void onConnectionError(ConnectionErrorEvent connectionErrorEvent) {
    }

    public void onGameStarted(GameStartedEvent gameStartedEvent) {
        predictModelMap.clear();
    }

    public void onGameEnded(GameEndedEvent gameEndedEvent) {
    }

    public void onRoundStarted(RoundStartedEvent roundStartedEvent) {
    }

    public void onRoundEnded(RoundEndedEvent roundEndedEvent) {
        System.out.println(toString());
    }

    public void onTick(TickEvent tickEvent) {
    }

    public void onBotDeath(BotDeathEvent botDeathEvent) {
    }

    public void onDeath(DeathEvent deathEvent) {
    }

    public void onHitBot(HitBotEvent botHitBotEvent) {
    }

    public void onHitWall(HitWallEvent botHitWallEvent) {
    }

    public void onBulletFired(BulletFiredEvent bulletFiredEvent, Commander commander) {
        final ShootingTarget shootingTarget = commander.getGunner().getShootingTarget(bulletFiredEvent.getTurnNumber());
        if (shootingTarget == null) {
            return;
        }
        if (shootingTarget.predictModel == null) {
            return;
        }
        shootingTarget.predictModel.incrementFiredCount();
    }

    public void onHitByBullet(HitByBulletEvent hitByBulletEvent) {
    }

    public void onBulletHit(BulletHitBotEvent bulletHitBotEvent) {
        final int enemyId = bulletHitBotEvent.getVictimId();
        final int bulletId = bulletHitBotEvent.getBullet().getBulletId();
        final BulletData bulletData = BattleManager.getInstance().getBulletData(bulletId);
        if (bulletData == null) {
            return;
        }
        if (bulletData.shootingTarget.predictModel == null) {
            return;
        }
        if (bulletData.shootingTarget.enemyId == enemyId) {
            bulletData.shootingTarget.predictModel.incrementHitCount();
        } else {
            bulletData.shootingTarget.predictModel.incrementNoCount();
        }
    }

    public void onBulletHitBullet(BulletHitBulletEvent bulletHitBulletEvent) {
        final int bulletId = bulletHitBulletEvent.getBullet().getBulletId();
        final BulletData bulletData = BattleManager.getInstance().getBulletData(bulletId);
        if (bulletData == null) {
            return;
        }
        if (bulletData.shootingTarget.predictModel == null) {
            return;
        }
        bulletData.shootingTarget.predictModel.incrementNoCount();
    }

    public void onBulletHitWall(BulletHitWallEvent bulletHitWallEvent) {
        final int bulletId = bulletHitWallEvent.getBullet().getBulletId();
        final BulletData bulletData = BattleManager.getInstance().getBulletData(bulletId);
        if (bulletData == null) {
            return;
        }
        if (bulletData.shootingTarget.predictModel == null) {
            return;
        }
        bulletData.shootingTarget.predictModel.incrementMissCount();
    }

    public void onScannedBot(ScannedBotEvent scannedBotEvent) {
    }

    public void onSkippedTurn(SkippedTurnEvent skippedTurnEvent) {
    }

    public void onWonRound(WonRoundEvent wonRoundEvent) {
    }

    public void onCustomEvent(CustomEvent customEvent) {
    }

    public void onTeamMessage(TeamMessageEvent teamMessageEvent) {
    }
}
