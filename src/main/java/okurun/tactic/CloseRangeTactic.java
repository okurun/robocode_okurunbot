package okurun.tactic;

import dev.robocode.tankroyale.botapi.graphics.Color;
import okurun.Commander;
import okurun.Util;

public class CloseRangeTactic extends AbstractTactic {
    public CloseRangeTactic(Commander commander) {
        super(commander);
    }

    @Override
    public Color getScanColor() {
        return Util.BASE_COLOR;
    }
}
