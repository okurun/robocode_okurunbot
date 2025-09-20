package okurun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.events.*;
import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Util.ToggleColor;
import okurun.arenamap.ArenaMap;
import okurun.battlemanager.BattleManager;
import okurun.driver.Driver;
import okurun.driver.action.DriveAction;
import okurun.driver.handle.Handle;
import okurun.driver.trancemission.Trancemission;
import okurun.gunner.Gunner;
import okurun.gunner.action.GunAction;
import okurun.gunner.trigger.GunTrigger;
import okurun.predictor.PredictData;
import okurun.predictor.Predictor;
import okurun.radaroperator.EnemyState;
import okurun.radaroperator.RadarOperator;
import okurun.radaroperator.action.RadarAction;
import okurun.tactic.*;

public class TankCommander implements Commander {
    private final AtomicInteger targetEnemyId = new AtomicInteger(0);
    private final RadarOperator radarOperator = new RadarOperator();
    private final Gunner gunner = new Gunner(); 
    private final Driver driver = new Driver();
    private final Map<String, TacticStrategy> tactics = new HashMap<>();

    private IBot bot;
    private TacticStrategy tactic;

    @Override
    public void init(IBot bot) {
        this.bot = bot;
        ArenaMap.getInstance().init(bot.getArenaWidth(), bot.getArenaHeight());
        BattleManager.getInstance().init(this);
        Predictor.getInstance().init(this);
        radarOperator.init(this);
        gunner.init(this);
        driver.init(this);

        tactics.put(CloseRangeTactic.class.getName(), new CloseRangeTactic(this));
        tactics.put(MiddleRangeTactic.class.getName(), new MiddleRangeTactic(this));
        tactics.put(LongRangeTactic.class.getName(), new LongRangeTactic(this));
        tactics.put(SurvivalTactic.class.getName(), new SurvivalTactic(this));}

    @Override
    public IBot getBot() {
        return bot;
    }

    @Override
    public int getTargetEnemyId() {
        return targetEnemyId.get();
    }

    @Override
    public void setTargetEnemyId(int enemyId) {
        targetEnemyId.set(enemyId);
    }

    @Override
    public EnemyState getTargetEnemy() {
        return radarOperator.getEnemyState(targetEnemyId.get());
    }

    @Override
    public Gunner getGunner() {
        return gunner;
    }

    @Override
    public RadarOperator getRadarOperator() {
        return radarOperator;
    }

    @Override
    public Driver getDriver() {
        return driver;
    }

    @Override
    public RadarAction getNextRadarAction() {
        if (tactic != null) {
            return tactic.getNextRadarAction();
        }
        return null;
    }

    @Override
    public GunAction getNextGunAction() {
        if (tactic != null) {
            return tactic.getNextGunAction();
        }
        return null;
    }

    @Override
    public GunTrigger getNextGunTrigger() {
        if (tactic != null) {
            return tactic.getNextGunTrigger();
        }
        return null;
    }

    @Override
    public DriveAction getNextDriveAction() {
        if (tactic != null) {
            return tactic.getNextDriveAction();
        }
        return null;
    }

    @Override
    public void action() {
        bot.setAdjustGunForBodyTurn(true);
        bot.setAdjustRadarForGunTurn(true);
        bot.setAdjustRadarForGunTurn(true);
        bot.setBodyColor(getBodyColor());

        Predictor.getInstance().clearCache();

        final TacticStrategy nextTactic = getNextTactic();
        if (tactic == null || tactic != nextTactic) {
            tactic = nextTactic;
        }
        bot.setScanColor(tactic.getScanColor());

        if (getTargetEnemy() == null) {
            targetEnemyId.set(tactic.getTargetEnemyId());
        }
        tactic.action();
        ToggleColor.toggle();
        bot.go();
    }

    private Color getBodyColor() {
        if (bot.getEnergy() < 10) {
            final ToggleColor toggleColor = new ToggleColor(Util.BASE_COLOR, Util.RED_COLOR);
            return toggleColor.get();
        }
        if (bot.getEnergy() < 20) {
            final ToggleColor toggleColor = new ToggleColor(Util.BASE_COLOR, Util.ORANGE_COLOR);
            return toggleColor.get();
        }
        if (bot.getEnergy() < 30) {
            final ToggleColor toggleColor = new ToggleColor(Util.BASE_COLOR, Util.YELLOW_COLOR);
            return toggleColor.get();
        }
        if (bot.getEnergy() < 40) {
            final ToggleColor toggleColor = new ToggleColor(Util.BASE_COLOR, Util.WHITE_COLOR);
            return toggleColor.get();
        }
        if (bot.getEnergy() < 50) {
            final ToggleColor toggleColor = new ToggleColor(Util.BASE_COLOR, Util.BLUE_COLOR);
            return toggleColor.get();
        }
        return Util.BASE_COLOR;
    }

    private TacticStrategy getNextTactic() {
        if (radarOperator.getEnemyCount() > 1) {
            return tactics.get(SurvivalTactic.class.getName());
        }
        final EnemyState enemy = getTargetEnemy();
        if (enemy == null) {
            return tactics.get(MiddleRangeTactic.class.getName());
        }
        final Predictor predictor = Predictor.getInstance();
        final PredictData predictData = predictor.predict(enemy, bot.getTurnNumber());
        final double[] enemyPos = predictData != null ? predictData.getPosition() : new double[] {enemy.x, enemy.y};
        final double distanceToEnemy = bot.distanceTo(enemyPos[0], enemyPos[1]);
        if (distanceToEnemy < 150) {
            return tactics.get(CloseRangeTactic.class.getName());
        }
        if (distanceToEnemy < 300) {
            return tactics.get(MiddleRangeTactic.class.getName());
        }
        return tactics.get(LongRangeTactic.class.getName());
    }

    @Override
    public List<Handle> getHandles() {
        if (tactic != null) {
            return tactic.getHandles();
        }
        return null;
    }

    @Override
    public Trancemission getTrancemission() {
        if (tactic != null) {
            return tactic.getTrancemission();
        }
        return null;
    }

    @Override
    public void onConnected(ConnectedEvent connectedEvent) {
        radarOperator.onConnected(connectedEvent);
        gunner.onConnected(connectedEvent);
        driver.onConnected(connectedEvent);
        BattleManager.getInstance().onConnected(connectedEvent);
        Predictor.getInstance().onConnected(connectedEvent);
    }

    @Override
    public void onDisconnected(DisconnectedEvent disconnectedEvent) {
        radarOperator.onDisconnected(disconnectedEvent);
        gunner.onDisconnected(disconnectedEvent);
        driver.onDisconnected(disconnectedEvent);
        BattleManager.getInstance().onDisconnected(disconnectedEvent);
        Predictor.getInstance().onDisconnected(disconnectedEvent);
    }

    @Override
    public void onConnectionError(ConnectionErrorEvent connectionErrorEvent) {
        radarOperator.onConnectionError(connectionErrorEvent);
        gunner.onConnectionError(connectionErrorEvent);
        driver.onConnectionError(connectionErrorEvent);
        BattleManager.getInstance().onConnectionError(connectionErrorEvent);
        Predictor.getInstance().onConnectionError(connectionErrorEvent);
    }

    @Override
    public void onGameStarted(GameStartedEvent gameStartedEvent) {
        radarOperator.onGameStarted(gameStartedEvent);
        gunner.onGameStarted(gameStartedEvent);
        driver.onGameStarted(gameStartedEvent);
        BattleManager.getInstance().onGameStarted(gameStartedEvent);
        Predictor.getInstance().onGameStarted(gameStartedEvent);
    }

    @Override
    public void onGameEnded(GameEndedEvent gameEndedEvent) {
        radarOperator.onGameEnded(gameEndedEvent);
        gunner.onGameEnded(gameEndedEvent);
        driver.onGameEnded(gameEndedEvent);
        BattleManager.getInstance().onGameEnded(gameEndedEvent);
        Predictor.getInstance().onGameEnded(gameEndedEvent);
    }

    @Override
    public void onRoundStarted(RoundStartedEvent roundStartedEvent) {
        radarOperator.onRoundStarted(roundStartedEvent);
        gunner.onRoundStarted(roundStartedEvent);
        driver.onRoundStarted(roundStartedEvent);
        BattleManager.getInstance().onRoundStarted(roundStartedEvent);
        Predictor.getInstance().onRoundStarted(roundStartedEvent);
    }

    @Override
    public void onRoundEnded(RoundEndedEvent roundEndedEvent) {
        radarOperator.onRoundEnded(roundEndedEvent);
        gunner.onRoundEnded(roundEndedEvent);
        driver.onRoundEnded(roundEndedEvent);
        BattleManager.getInstance().onRoundEnded(roundEndedEvent);
        Predictor.getInstance().onRoundEnded(roundEndedEvent);
    }

    @Override
    public void onTick(TickEvent tickEvent) {
        radarOperator.onTick(tickEvent);
        gunner.onTick(tickEvent);
        driver.onTick(tickEvent);
        BattleManager.getInstance().onTick(tickEvent);
        Predictor.getInstance().onTick(tickEvent);
    }

    @Override
    public void onBotDeath(BotDeathEvent botDeathEvent) {
        radarOperator.onBotDeath(botDeathEvent);
        gunner.onBotDeath(botDeathEvent);
        driver.onBotDeath(botDeathEvent);
        BattleManager.getInstance().onBotDeath(botDeathEvent);
        Predictor.getInstance().onBotDeath(botDeathEvent);
    }

    @Override
    public void onDeath(DeathEvent deathEvent) {
        radarOperator.onDeath(deathEvent);
        gunner.onDeath(deathEvent);
        driver.onDeath(deathEvent);
        BattleManager.getInstance().onDeath(deathEvent);
        Predictor.getInstance().onDeath(deathEvent);
    }

    @Override
    public void onHitBot(HitBotEvent botHitBotEvent) {
        radarOperator.onHitBot(botHitBotEvent);
        gunner.onHitBot(botHitBotEvent);
        driver.onHitBot(botHitBotEvent);
        BattleManager.getInstance().onHitBot(botHitBotEvent);
        Predictor.getInstance().onHitBot(botHitBotEvent);
    }

    @Override
    public void onHitWall(HitWallEvent botHitWallEvent) {
        radarOperator.onHitWall(botHitWallEvent);
        gunner.onHitWall(botHitWallEvent);
        driver.onHitWall(botHitWallEvent);
        BattleManager.getInstance().onHitWall(botHitWallEvent);
        Predictor.getInstance().onHitWall(botHitWallEvent);
        System.out.println("!!! Hit Wall !!!");
    }

    @Override
    public void onBulletFired(BulletFiredEvent bulletFiredEvent) {
        radarOperator.onBulletFired(bulletFiredEvent);
        gunner.onBulletFired(bulletFiredEvent);
        driver.onBulletFired(bulletFiredEvent);
        BattleManager.getInstance().onBulletFired(bulletFiredEvent, this);
        Predictor.getInstance().onBulletFired(bulletFiredEvent, this);
        targetEnemyId.set(tactic.getTargetEnemyId());
    }

    @Override
    public void onHitByBullet(HitByBulletEvent hitByBulletEvent) {
        radarOperator.onHitByBullet(hitByBulletEvent);
        gunner.onHitByBullet(hitByBulletEvent);
        driver.onHitByBullet(hitByBulletEvent);
        BattleManager.getInstance().onHitByBullet(hitByBulletEvent);
        Predictor.getInstance().onHitByBullet(hitByBulletEvent);
    }

    @Override
    public void onBulletHit(BulletHitBotEvent bulletHitBotEvent) {
        radarOperator.onBulletHit(bulletHitBotEvent);
        gunner.onBulletHit(bulletHitBotEvent);
        driver.onBulletHit(bulletHitBotEvent);
        BattleManager.getInstance().onBulletHit(bulletHitBotEvent);
        Predictor.getInstance().onBulletHit(bulletHitBotEvent);
    }

    @Override
    public void onBulletHitBullet(BulletHitBulletEvent bulletHitBulletEvent) {
        radarOperator.onBulletHitBullet(bulletHitBulletEvent);
        gunner.onBulletHitBullet(bulletHitBulletEvent);
        driver.onBulletHitBullet(bulletHitBulletEvent);
        BattleManager.getInstance().onBulletHitBullet(bulletHitBulletEvent);
        Predictor.getInstance().onBulletHitBullet(bulletHitBulletEvent);
    }

    @Override
    public void onBulletHitWall(BulletHitWallEvent bulletHitWallEvent) {
        radarOperator.onBulletHitWall(bulletHitWallEvent);
        gunner.onBulletHitWall(bulletHitWallEvent);
        driver.onBulletHitWall(bulletHitWallEvent);
        BattleManager.getInstance().onBulletHitWall(bulletHitWallEvent);
        Predictor.getInstance().onBulletHitWall(bulletHitWallEvent);
    }

    @Override
    public void onScannedBot(ScannedBotEvent scannedBotEvent) {
        radarOperator.onScannedBot(scannedBotEvent);
        gunner.onScannedBot(scannedBotEvent);
        driver.onScannedBot(scannedBotEvent);
        BattleManager.getInstance().onScannedBot(scannedBotEvent);
        Predictor.getInstance().onScannedBot(scannedBotEvent);
    }

    @Override
    public void onSkippedTurn(SkippedTurnEvent skippedTurnEvent) {
        radarOperator.onSkippedTurn(skippedTurnEvent);
        gunner.onSkippedTurn(skippedTurnEvent);
        driver.onSkippedTurn(skippedTurnEvent);
        BattleManager.getInstance().onSkippedTurn(skippedTurnEvent);
        Predictor.getInstance().onSkippedTurn(skippedTurnEvent);
    }

    @Override
    public void onWonRound(WonRoundEvent wonRoundEvent) {
        radarOperator.onWonRound(wonRoundEvent);
        gunner.onWonRound(wonRoundEvent);
        driver.onWonRound(wonRoundEvent);
        BattleManager.getInstance().onWonRound(wonRoundEvent);
        Predictor.getInstance().onWonRound(wonRoundEvent);
    }

    @Override
    public void onCustomEvent(CustomEvent customEvent) {
        radarOperator.onCustomEvent(customEvent);
        gunner.onCustomEvent(customEvent);
        driver.onCustomEvent(customEvent);
        BattleManager.getInstance().onCustomEvent(customEvent);
        Predictor.getInstance().onCustomEvent(customEvent);
    }

    @Override
    public void onTeamMessage(TeamMessageEvent teamMessageEvent) {
        radarOperator.onTeamMessage(teamMessageEvent);
        gunner.onTeamMessage(teamMessageEvent);
        driver.onTeamMessage(teamMessageEvent);
        BattleManager.getInstance().onTeamMessage(teamMessageEvent);
        Predictor.getInstance().onTeamMessage(teamMessageEvent);
    }
}
