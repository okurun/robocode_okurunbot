package okurun.driver.handle;

import java.util.Random;

import okurun.Commander;

public class RandmoSwervingHandle extends Handle {
    private final double swarvePower;
    private final Random random = new Random();

    public RandmoSwervingHandle(Commander commander, double swarvePower) {
        super(commander);
        this.swarvePower = swarvePower;
    }

    @Override
    public double handle(double turnDegree) {
        return turnDegree + ((random.nextInt(2) < 1) ? this.swarvePower : -this.swarvePower);
    }
}
