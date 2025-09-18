package okurun.driver;

import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.events.*;
import okurun.Commander;
import okurun.driver.action.DriveAction;
import okurun.driver.brake.*;

public class Driver {
    private Commander commander;
    private DriveAction action;
    private Brake brake;

    public Driver() {
    }

    public void init(Commander commander) {
        this.commander = commander;
        this.brake = new ConsideringWallBrake(commander);
    }

    public void action() {
        if (action == null) {
            action = commander.getNextDriveAction();            
        }
        final DriveAction nextAction = action.action();
        final IBot bot = commander.getBot();
        bot.setTracksColor(action.getTracksColor());
        bot.setTurnLeft(action.getTurnDegree());
        bot.setForward(action.getForwardDistance());
        bot.setTargetSpeed(brake.brake(action.getTargetSpeed(), action.getForwardDistance()));
        action = nextAction;
    }

    public void setAction(DriveAction action) {
        this.action = action;
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
