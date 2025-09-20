package okurun.tactic;

import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.Util;

public class LongRangeTactic extends AbstractTactic {
    public LongRangeTactic(Commander commander) {
        super(commander);
    }

    @Override
    public Color getScanColor() {
        return Util.WHITE_COLOR;
    }
}
