package okurun.gunner.trigger;

import dev.robocode.tankroyale.botapi.IBot;
import okurun.Commander;
import okurun.Util;

public class QuickGunTrigger extends AbstractGunTrigger {
    public QuickGunTrigger(Commander commander) {
        super(commander);
    }

    @Override
    protected int calcNextFireTurnNum() {
        final IBot bot = commander.getBot();
        return Util.calcNextFireTurnNum(bot.getGunHeat(), bot.getGunCoolingRate()) + bot.getTurnNumber();
    }
}
