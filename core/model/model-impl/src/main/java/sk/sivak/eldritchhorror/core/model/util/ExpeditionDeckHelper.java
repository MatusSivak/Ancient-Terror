package sk.sivak.eldritchhorror.core.model.util;

import sk.sivak.eldritchhorror.core.constants.expedition.ExpeditionInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class ExpeditionDeckHelper {

    public List<ExpeditionInfo> init() {
        List<ExpeditionInfo> result = new LinkedList<>();

        for (int page = 1; page <= 4; page++) {
            result.add(new ExpeditionInfoImpl(LocationId.THE_AMAZON, page));
            result.add(new ExpeditionInfoImpl(LocationId.THE_HEART_OF_AFRICA, page));
            result.add(new ExpeditionInfoImpl(LocationId.THE_PYRAMIDS, page));
            result.add(new ExpeditionInfoImpl(LocationId.ANTARCTICA, page));
            result.add(new ExpeditionInfoImpl(LocationId.THE_HIMALAYAS, page));
            result.add(new ExpeditionInfoImpl(LocationId.TUNGUSKA, page));
        }

        Collections.shuffle(result);
        return result;
    }

    private class ExpeditionInfoImpl implements ExpeditionInfo {

        private LocationId locationId;
        private int page;

        public ExpeditionInfoImpl(LocationId locationId, int page) {
            this.locationId = locationId;
            this.page = page;
        }

        public int getPage() {
            return page;
        }

        @Override
        public LocationId getLocationId() {
            return locationId;
        }
    }
}
