package sk.sivak.eldritchhorror.core.model.util;

import sk.sivak.eldritchhorror.core.constants.reference.Reference;
import sk.sivak.eldritchhorror.core.constants.reference.ReferenceInfo;

/**
 * @author msivak
 */
public class ReferenceCardHelper {

    public ReferenceInfo initReferenceCard(int players) {
        switch (players) {
            case 1:
                return new Reference(1, 1, 2/*1*/, 1);
            case 2:
                return new Reference(2, 1, 1, 1);
            case 3:
                return new Reference(3, 1, 2, 2);
            case 4:
                return new Reference(4, 1, 2, 2);
            case 5:
                return new Reference(5, 2, 3, 2);
            case 6:
                return new Reference(6, 2, 3, 2);
            case 7:
                return new Reference(7, 2, 4, 3);
            case 8:
                return new Reference(8, 2, 4, 3);
            default:
                throw new IllegalArgumentException("Not supported amount of players: " + players);
        }

    }
}
