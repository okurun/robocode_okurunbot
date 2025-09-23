package okurun.driver.trancemission;

import java.util.Random;

import okurun.Commander;

/**
 * ランダムに前進、後進を切り替える
 */
public class RandomTrancemission extends Trancemission {
    private final int forwardTurnNum;
    private final int backwardTurnNum;
    private final Random random = new Random();

    public RandomTrancemission(Commander commander, int forwardTurnNum, int backwardTurnNum) {
        super(commander);
        this.forwardTurnNum = forwardTurnNum;
        this.backwardTurnNum = backwardTurnNum;
    }

    @Override
    public double changeGear(double speed) {
        final double flg = (random.nextInt(forwardTurnNum + backwardTurnNum) < this.forwardTurnNum) ? 1 : -1;
        return speed * flg;
    }
}
