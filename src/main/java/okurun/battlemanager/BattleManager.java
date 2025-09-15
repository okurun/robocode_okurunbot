package okurun.battlemanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.events.*;
import okurun.Commander;
import okurun.gunner.ShootingTarget;

public class BattleManager {
    private static BattleManager instance = null;
    public static BattleManager getInstance() {
        if (instance == null) {
            instance = new BattleManager();
        }
        return instance;
    }

    private final Map<Integer, EnemyBattleData> enemyBattleDataMap = new ConcurrentHashMap<>();
    private final Map<Integer, BulletData> bulletDataMap = new ConcurrentHashMap<>();

    private BattleManager() {}

    public void init(Commander commander) {
        final IBot bot = commander.getBot();
        for (int i = 1; i <= bot.getEnemyCount() + 1; i++) {
            if (i == bot.getMyId()) continue;
            final EnemyBattleData enemyBattleData = enemyBattleDataMap.get(i);
            if (enemyBattleData == null) {
                enemyBattleDataMap.put(i, new EnemyBattleData(i));
                continue;
            }
            enemyBattleDataMap.put(i, new EnemyBattleData(
                enemyBattleData.enemyId
            ));
        }
        bulletDataMap.clear();
    }

    public EnemyBattleData getEnemyBattleData(int enemyId) {
        return enemyBattleDataMap.get(enemyId);
    }

    public BulletData getBulletData(int bulletId) {
        return bulletDataMap.get(bulletId);
    }

    public void onConnected(ConnectedEvent connectedEvent) {
    }

    public void onDisconnected(DisconnectedEvent disconnectedEvent) {
    }

    public void onConnectionError(ConnectionErrorEvent connectionErrorEvent) {
    }

    public void onGameStarted(GameStartedEvent gameStartedEvent) {
        enemyBattleDataMap.clear();
        bulletDataMap.clear();
    }

    public void onGameEnded(GameEndedEvent gameEndedEvent) {
    }

    public void onRoundStarted(RoundStartedEvent roundStartedEvent) {
    }

    public void onRoundEnded(RoundEndedEvent roundEndedEvent) {
        enemyBattleDataMap.values().stream().map(EnemyBattleData::toString).forEach(System.out::println);
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
        final int bulletId = bulletFiredEvent.getBullet().getBulletId();
        bulletDataMap.put(bulletId, new BulletData(
            bulletId,
            shootingTarget,
            bulletFiredEvent.getTurnNumber(),
            BulletData.HitState.IN_FLIGHT
        ));
        final EnemyBattleData enemyBattleData = enemyBattleDataMap.get(shootingTarget.enemyId);
        if (enemyBattleData != null) {
            enemyBattleData.incrementTargetedCount();
        }
    }

    public void onHitByBullet(HitByBulletEvent hitByBulletEvent) {
    }

    public void onBulletHit(BulletHitBotEvent bulletHitBotEvent) {
        final int enemyId = bulletHitBotEvent.getVictimId();
        final int bulletId = bulletHitBotEvent.getBullet().getBulletId();
        final BulletData bulletData = bulletDataMap.get(bulletId);
        if (bulletData == null) {
            return;
        }
        bulletDataMap.put(bulletId, new BulletData(
            bulletData.bulletId,
            bulletData.shootingTarget,
            bulletData.firedTurnNum,
            (bulletData.shootingTarget.enemyId == enemyId)
                ? BulletData.HitState.HIT
                : BulletData.HitState.HIT_OTHER
        ));
        final EnemyBattleData enemyBattleData = enemyBattleDataMap.get(bulletData.shootingTarget.enemyId);
        if (enemyBattleData != null) {
            if (bulletData.shootingTarget.enemyId == enemyId) {
                enemyBattleData.incrementHitCount();
            } else {
                enemyBattleData.incrementNoCount();
            }
        }
    }

    public void onBulletHitBullet(BulletHitBulletEvent bulletHitBulletEvent) {
        final int bulletId = bulletHitBulletEvent.getBullet().getBulletId();
        final BulletData bulletData = bulletDataMap.get(bulletId);
        if (bulletData == null) {
            return;
        }
        bulletDataMap.put(bulletId, new BulletData(
            bulletData.bulletId,
            bulletData.shootingTarget,
            bulletData.firedTurnNum,
            BulletData.HitState.HIT_BULLET
        ));
        final EnemyBattleData enemyBattleData = enemyBattleDataMap.get(bulletData.shootingTarget.enemyId);
        if (enemyBattleData != null) {
            enemyBattleData.incrementNoCount();
        }
    }

    public void onBulletHitWall(BulletHitWallEvent bulletHitWallEvent) {
        final int bulletId = bulletHitWallEvent.getBullet().getBulletId();
        final BulletData bulletData = bulletDataMap.get(bulletId);
        if (bulletData == null) {
            return;
        }
        bulletDataMap.put(bulletId, new BulletData(
            bulletData.bulletId,
            bulletData.shootingTarget,
            bulletData.firedTurnNum,
            BulletData.HitState.MISS
        ));
        final EnemyBattleData enemyBattleData = enemyBattleDataMap.get(bulletData.shootingTarget.enemyId);
        if (enemyBattleData != null) {
            enemyBattleData.incrementMissCount();
        }
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
