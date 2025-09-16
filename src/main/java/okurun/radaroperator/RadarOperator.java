package okurun.radaroperator;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.events.*;
import okurun.Commander;
import okurun.radaroperator.action.RadarAction;

public class RadarOperator {
    private final Map<Integer, EnemyState> enemyStates = new ConcurrentHashMap<>();
    private final Set<Integer> deathBots = Collections.synchronizedSet(new HashSet<>());

    private Commander commander;
    private RadarAction action;

    public RadarOperator() {
    }

    public void init(Commander commander) {
        this.commander = commander;
        enemyStates.clear();
        deathBots.clear();
        final IBot bot = commander.getBot();
        for (int i = 1; i <= bot.getEnemyCount() + 1; i++) {
            if (i == bot.getMyId()) continue;
            enemyStates.put(i, new EnemyState(i));
        }
    }

    public void action() {
        if (action == null) {
            action = commander.getNextRadarAction();
        }
        final RadarAction nextAction = action.action();
        commander.getBot().setTurnRadarLeft(action.getTurnRadarDegree());
        action = nextAction;
    }

    public void setAction(RadarAction action) {
        this.action = action;
    }

    public EnemyState getEnemyState(int enemyId) {
        return enemyStates.get(enemyId);
    }

    public int getEnemyCount() {
        return getEnemyStates().size();
    }

    public Map<Integer, EnemyState> getEnemyStates() {
        return enemyStates.values().stream()
            .filter(e -> e.scandTurnNum > 0)
            .filter(e -> !deathBots.contains(e.enemyId))
            .collect(ConcurrentHashMap::new, (m, e) -> m.put(e.enemyId, e), ConcurrentHashMap::putAll);
    }

    public EnemyState getNearestEnemy() {
        final IBot bot = commander.getBot();
        return getEnemyStates().values().stream()
            .min(Comparator.comparingDouble(e -> bot.distanceTo(e.x, e.y)))
            .orElse(null);
    }

    public void onConnected(ConnectedEvent connectedEvent) {
    }

    public void onDisconnected(DisconnectedEvent disconnectedEvent) {
    }

    public void onConnectionError(ConnectionErrorEvent connectionErrorEvent) {
    }

    public void onGameStarted(GameStartedEvent gameStartedEvent) {
    }

    public void onGameEnded(GameEndedEvent gameEndedEvent) {
    }

    public void onRoundStarted(RoundStartedEvent roundStartedEvent) {
    }

    public void onRoundEnded(RoundEndedEvent roundEndedEvent) {
    }

    public void onTick(TickEvent tickEvent) {
    }

    public void onBotDeath(BotDeathEvent botDeathEvent) {
        final int enemyId = botDeathEvent.getVictimId();
        enemyStates.remove(enemyId);
        deathBots.add(enemyId);
    }

    public void onDeath(DeathEvent deathEvent) {
    }

    public void onHitBot(HitBotEvent botHitBotEvent) {
    }

    public void onHitWall(HitWallEvent botHitWallEvent) {
    }

    public void onBulletFired(BulletFiredEvent bulletFiredEvent) {
    }

    public void onHitByBullet(HitByBulletEvent hitByBulletEvent) {
    }

    public void onBulletHit(BulletHitBotEvent bulletHitBotEvent) {
    }

    public void onBulletHitBullet(BulletHitBulletEvent bulletHitBulletEvent) {
    }

    public void onBulletHitWall(BulletHitWallEvent bulletHitWallEvent) {
    }

    public void onScannedBot(ScannedBotEvent scannedBotEvent) {
        final int enemyId = scannedBotEvent.getScannedBotId();
        EnemyState previousState = enemyStates.get(enemyId);
        enemyStates.put(enemyId, new EnemyState(
                enemyId,
                scannedBotEvent.getX(),
                scannedBotEvent.getY(),
                scannedBotEvent.getDirection(),
                scannedBotEvent.getSpeed(),
                scannedBotEvent.getEnergy(),
                scannedBotEvent.getTurnNumber(),
                previousState
        ));
        int i = 0;
        while ((previousState = previousState.previousState) != null) {
            if (++i >= 10) {
                previousState.deletePreviousState();
                break;
            }
        }
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