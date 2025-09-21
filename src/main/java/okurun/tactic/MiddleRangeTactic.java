package okurun.tactic;

import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.Util;

public class MiddleRangeTactic extends AbstractTactic {
    public MiddleRangeTactic(Commander commander) {
        super(commander);
    }

    @Override
    public Color getScanColor() {
        return Util.LIGHT_COLOR;
    }
}
