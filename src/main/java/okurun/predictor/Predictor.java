package okurun.predictor;

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

    public void init(Commander commander) {
        final IBot bot = commander.getBot();
        for (int i = 1; i <= bot.getEnemyCount() + 1; i++) {
            if (i == bot.getMyId()) continue;
            if (predictModels.get(i) != null) continue;
            predictModels.put(i, Map.of(
                SimplePredictModel.class.getSimpleName(), new SimplePredictModel(),
                MovePatternPredictModel.class.getSimpleName(), new MovePatternPredictModel()
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
        StringBuilder sb = new StringBuilder("Predictor{\n");
        for (Map.Entry<Integer, Map<String, PredictModel>> entry : predictModels.entrySet()) {
            final int enemyId = entry.getKey();
            sb.append("\tenemyId=").append(enemyId).append("{\n");
            final Map<String, PredictModel> models = entry.getValue();
            for (PredictModel pm : models.values()) {
                sb.append("\t\t").append(pm.toString()).append("\n");
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
        predictModels.clear();
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
            System.out.println("@@@@");
            return;
        }
        if (shootingTarget.predictModel == null) {
            System.out.println("****");
            return;
        }
        shootingTarget.predictModel.incrementFiredCount();
        System.out.println(shootingTarget.predictModel);
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
