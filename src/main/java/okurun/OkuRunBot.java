package okurun;

import dev.robocode.tankroyale.botapi.*;
import dev.robocode.tankroyale.botapi.events.*;

public class OkuRunBot extends Bot {

    public static void main(String[] args) {
        new OkuRunBot().start();
    }

    private final Commander commander;

    public OkuRunBot() {
        super();
        commander = new TankCommander();
    }

    @Override
    public void run() {
        commander.init(this);
        while (isRunning()) {
            commander.action();
        }
    }

    @Override
    public void onConnected(ConnectedEvent connectedEvent) {
        commander.onConnected(connectedEvent);
    }

    @Override
    public void onDisconnected(DisconnectedEvent disconnectedEvent) {
        commander.onDisconnected(disconnectedEvent);
    }

    @Override
    public void onConnectionError(ConnectionErrorEvent connectionErrorEvent) {
        commander.onConnectionError(connectionErrorEvent);
    }

    @Override
    public void onGameStarted(GameStartedEvent gameStartedEvent) {
        commander.onGameStarted(gameStartedEvent);
    }

    @Override
    public void onGameEnded(GameEndedEvent gameEndedEvent) {
        commander.onGameEnded(gameEndedEvent);
    }

    @Override
    public void onRoundStarted(RoundStartedEvent roundStartedEvent) {
        commander.onRoundStarted(roundStartedEvent);
    }

    @Override
    public void onRoundEnded(RoundEndedEvent roundEndedEvent) {
        commander.onRoundEnded(roundEndedEvent);
    }

    @Override
    public void onTick(TickEvent tickEvent) {
        commander.onTick(tickEvent);
    }

    @Override
    public void onBotDeath(BotDeathEvent botDeathEvent) {
        commander.onBotDeath(botDeathEvent);
    }

    @Override
    public void onDeath(DeathEvent deathEvent) {
        commander.onDeath(deathEvent);
    }

    @Override
    public void onHitBot(HitBotEvent botHitBotEvent) {
        commander.onHitBot(botHitBotEvent);
    }

    @Override
    public void onHitWall(HitWallEvent botHitWallEvent) {
        commander.onHitWall(botHitWallEvent);
    }

    @Override
    public void onBulletFired(BulletFiredEvent bulletFiredEvent) {
        commander.onBulletFired(bulletFiredEvent);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent hitByBulletEvent) {
        commander.onHitByBullet(hitByBulletEvent);
    }

    @Override
    public void onBulletHit(BulletHitBotEvent bulletHitBotEvent) {
        commander.onBulletHit(bulletHitBotEvent);
    }

    @Override
    public void onBulletHitBullet(BulletHitBulletEvent bulletHitBulletEvent) {
        commander.onBulletHitBullet(bulletHitBulletEvent);
    }

    @Override
    public void onBulletHitWall(BulletHitWallEvent bulletHitWallEvent) {
        commander.onBulletHitWall(bulletHitWallEvent);
    }

    @Override
    public void onScannedBot(ScannedBotEvent scannedBotEvent) {
        commander.onScannedBot(scannedBotEvent);
    }

    @Override
    public void onSkippedTurn(SkippedTurnEvent skippedTurnEvent) {
        commander.onSkippedTurn(skippedTurnEvent);
    }

    @Override
    public void onWonRound(WonRoundEvent wonRoundEvent) {
        commander.onWonRound(wonRoundEvent);
    }

    @Override
    public void onCustomEvent(CustomEvent customEvent) {
        commander.onCustomEvent(customEvent);
    }

    @Override
    public void onTeamMessage(TeamMessageEvent teamMessageEvent) {
        commander.onTeamMessage(teamMessageEvent);
    }
}
