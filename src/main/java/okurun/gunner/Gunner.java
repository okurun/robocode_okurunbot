package okurun.gunner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.events.*;
import okurun.Commander;
import okurun.Util;
import okurun.gunner.action.GunAction;
import okurun.gunner.trigger.GunTrigger;

public class Gunner {
    private final Map<Integer, ShootingTarget> shootingTargets = new ConcurrentHashMap<>();

    private Commander commander;
    private GunTrigger trigger;
    private GunAction action;
    
    public void init(Commander commander) {
        this.commander = commander;
        shootingTargets.clear();
    }

    public void action() {
        if (this.action == null) {
            this.action = commander.getNextGunAction();
        }
        if (this.trigger == null) {
            this.trigger = commander.getNextGunTrigger();
        }
        final GunAction nextAction = this.action.action(this.trigger);
        final ShootingTarget shootingTarget = this.action.getShootingTarget();
        final IBot bot = commander.getBot();
        if (shootingTarget == null) {
            bot.setFire(0);
        } else {
            bot.setTurnGunLeft(bot.gunBearingTo(shootingTarget.x, shootingTarget.y));
            bot.setFire(shootingTarget.firePower);
            if (shootingTarget.predictModel == null) {
                bot.setBulletColor(Util.WHITE_COLOR);
            } else {
                bot.setBulletColor(shootingTarget.predictModel.getBulletColor());
            }
            shootingTargets.put(bot.getTurnNumber(), shootingTarget);
        }
        shootingTargets.remove(bot.getTurnNumber() - 2);
        this.action = nextAction;
    }

    public void setTrigger(GunTrigger trigger) {
        this.trigger = trigger;
    }

    public void setAction(GunAction action) {
        this.action = action;
    }

    public ShootingTarget getShootingTarget(int turnNumber) {
        return shootingTargets.get(turnNumber);
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
