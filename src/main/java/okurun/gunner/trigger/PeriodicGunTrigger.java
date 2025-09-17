package okurun.gunner.trigger;

import dev.robocode.tankroyale.botapi.IBot;
import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.Util;
import okurun.battlemanager.BattleManager;
import okurun.battlemanager.BulletData;

public class PeriodicGunTrigger extends AbstractGunTrigger {
    private final int intervalTurnNum;

    public PeriodicGunTrigger(Commander commander, int intervalTurnNum) {
        super(commander);
        this.intervalTurnNum = intervalTurnNum;
    }

    @Override
    protected int calcNextFireTurnNum() {
        final IBot bot = commander.getBot();
        final int turnNum = bot.getTurnNumber();
        final BattleManager battleManager = BattleManager.getInstance();
        final BulletData lastBulletData = battleManager.getLastBulletData();
        if (lastBulletData == null) {
            return Util.calcNextFireTurnNum(bot.getGunHeat(), bot.getGunCoolingRate()) + turnNum;
        }
        return Math.max(
            lastBulletData.firedTurnNum + this.intervalTurnNum,
            Util.calcNextFireTurnNum(bot.getGunHeat(), bot.getGunCoolingRate()) + turnNum
        );
    }

    @Override
    public Color getGunColor() {
        return Util.BLUE_COLOR;
    }
}
