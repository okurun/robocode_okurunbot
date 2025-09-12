package okurun.tactic;

import dev.robocode.tankroyale.botapi.events.*;
import okurun.driver.action.DriveAction;
import okurun.gunner.action.GunAction;
import okurun.radaroperator.action.RadarAction;

public interface TacticStrategy {
    void action();

    int getTargetEnemyId();

    RadarAction getNextRadarAction();

    GunAction getNextGunAction();

    DriveAction getNextDriveAction();

    default void onConnected(ConnectedEvent connectedEvent) {
    }

    default void onDisconnected(DisconnectedEvent disconnectedEvent) {
    }

    default void onConnectionError(ConnectionErrorEvent connectionErrorEvent) {
    }

    default void onGameStarted(GameStartedEvent gameStatedEvent) {
    }

    default void onGameEnded(GameEndedEvent gameEndedEvent) {
    }

    default void onRoundStarted(RoundStartedEvent roundStartedEvent) {
    }

    default void onRoundEnded(RoundEndedEvent roundEndedEvent) {
    }

    default void onTick(TickEvent tickEvent) {
    }

    default void onBotDeath(BotDeathEvent botDeathEvent) {
    }

    default void onDeath(DeathEvent deathEvent) {
    }

    default void onHitBot(HitBotEvent botHitBotEvent) {
    }

    default void onHitWall(HitWallEvent botHitWallEvent) {
    }

    default void onBulletFired(BulletFiredEvent bulletFiredEvent) {
    }

    default void onHitByBullet(HitByBulletEvent hitByBulletEvent) {
    }

    default void onBulletHit(BulletHitBotEvent bulletHitBotEvent) {
    }

    default void onBulletHitBullet(BulletHitBulletEvent bulletHitBulletEvent) {
    }

    default void onBulletHitWall(BulletHitWallEvent bulletHitWallEvent) {
    }

    default void onScannedBot(ScannedBotEvent scannedBotEvent) {
    }

    default void onSkippedTurn(SkippedTurnEvent skippedTurnEvent) {
    }

    default void onWonRound(WonRoundEvent wonRoundEvent) {
    }

    default void onCustomEvent(CustomEvent customEvent) {
    }

    default void onTeamMessage(TeamMessageEvent teamMessageEvent) {
    }
}
