package sk.sivak.eldritchhorror.core.model.util;

import sk.sivak.eldritchhorror.core.constants.clue.Clue;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.Collections;
import java.util.List;

/**
 * @author msivak
 */
public class ClueHelper {

    public void initCluePool(List<Clue> clueList) {
        for (LocationId locationId : LocationId.values()) {
            clueList.add(new Clue(locationId));
        }
        Collections.shuffle(clueList);
    }
}
