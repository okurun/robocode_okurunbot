package okurun.driver.handle;

import okurun.Commander;

public class SwervingHandle extends Handle {
    private final double waveLength;
    private final double swarvePower;

    public SwervingHandle(Commander commander, double waveLength, double swarvePower) {
        super(commander);
        this.waveLength = waveLength;
        this.swarvePower = swarvePower;
    }

    @Override
    public double handle(double turnDegree) {
        final int turnNum = commander.getBot().getTurnNumber();
        return turnDegree + ((turnNum % this.waveLength < waveLength / 2) ? this.swarvePower : -this.swarvePower);
    }
}
