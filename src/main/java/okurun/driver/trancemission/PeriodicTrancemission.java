package okurun.driver.trancemission;

import okurun.Commander;

public class PeriodicTrancemission extends Trancemission {
    private final int forwardTurnNum;
    private final int backwardTurnNum;

    public PeriodicTrancemission(Commander commander, int forwardTurnNum, int backwardTurnNum) {
        super(commander);
        this.forwardTurnNum = forwardTurnNum;
        this.backwardTurnNum = backwardTurnNum;
    }

    @Override
    public double changeGear(double speed) {
        final int turnNum = commander.getBot().getTurnNumber();
        final double flg = (turnNum % (this.forwardTurnNum + this.backwardTurnNum) < this.forwardTurnNum) ? 1 : -1;
        return speed * flg;
    }
}
