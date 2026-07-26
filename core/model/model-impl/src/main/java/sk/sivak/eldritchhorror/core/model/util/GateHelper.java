package sk.sivak.eldritchhorror.core.model.util;

import sk.sivak.eldritchhorror.core.constants.gate.Gate;
import sk.sivak.eldritchhorror.core.constants.gate.GateColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static sk.sivak.eldritchhorror.core.constants.gate.GateColor.*;
import static sk.sivak.eldritchhorror.core.constants.location.LocationId.*;

/**
 * @author msivak
 */
public class GateHelper {

    public void initGateStack(List<Gate> gateList) {
        Stack<GateColor> gateColors = new Stack<>();
        gateColors.addAll(Arrays.asList(RED, RED, RED, RED, BLUE, BLUE, BLUE, GREEN, GREEN));
        Collections.shuffle(gateColors);

        /*
        gateList.add(new Gate(RED, ARKHAM));
        gateList.add(new Gate(RED, SYDNEY));
        gateList.add(new Gate(GREEN, SAN_FRANCISCO));
        gateList.add(new Gate(BLUE, TOKYO));
        gateList.add(new Gate(RED, ROME));
        gateList.add(new Gate(BLUE, LONDON));
        gateList.add(new Gate(BLUE, BUENOS_AIRES));
        gateList.add(new Gate(GREEN, ISTANBUL));
        gateList.add(new Gate(RED, SHANGHAI));
        */

        gateList.add(new Gate(gateColors.pop(), ARKHAM));
        gateList.add(new Gate(gateColors.pop(), SYDNEY));
        gateList.add(new Gate(gateColors.pop(), SAN_FRANCISCO));
        gateList.add(new Gate(gateColors.pop(), TOKYO));
        gateList.add(new Gate(gateColors.pop(), ROME));
        gateList.add(new Gate(gateColors.pop(), LONDON));
        gateList.add(new Gate(gateColors.pop(), BUENOS_AIRES));
        gateList.add(new Gate(gateColors.pop(), ISTANBUL));
        gateList.add(new Gate(gateColors.pop(), SHANGHAI));

        Collections.shuffle(gateList);
    }
}
