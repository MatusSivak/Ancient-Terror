package sk.sivak.eldritchhorror.core.model.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.location.Location;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.Path;
import sk.sivak.eldritchhorror.core.constants.location.PathType;

import java.util.Map;

import static sk.sivak.eldritchhorror.core.constants.location.LocationId.*;
import static sk.sivak.eldritchhorror.core.constants.location.LocationType.*;

/**
 * @author msivak
 */
public class LocationHelper {

    private static final Logger logger = LogManager.getLogger(LocationHelper.class);

    public void initLocations(Map<LocationId, Location> locations) {
        locations.put(SPACE_1, new Location(SPACE_1, CITY,
                new Path(SPACE_19, PathType.SHIP),
                new Path(SAN_FRANCISCO, PathType.SHIP),
                new Path(SPACE_4, PathType.WALK)
        ));

        locations.put(SPACE_2, new Location(SPACE_2, SEA,
                new Path(TOKYO, PathType.SHIP),
                new Path(SAN_FRANCISCO, PathType.SHIP)
        ));

        locations.put(SPACE_3, new Location(SPACE_3, SEA,
                new Path(SYDNEY, PathType.SHIP),
                new Path(BUENOS_AIRES, PathType.SHIP)
        ));

        locations.put(SPACE_4, new Location(SPACE_4, WILDERNESS,
                new Path(SPACE_1, PathType.WALK),
                new Path(SPACE_5, PathType.WALK)
        ));

        locations.put(SPACE_5, new Location(SPACE_5, CITY,
                new Path(SAN_FRANCISCO, PathType.TRAIN),
                new Path(SPACE_4, PathType.WALK),
                new Path(ARKHAM, PathType.TRAIN)
        ));

        locations.put(SPACE_6, new Location(SPACE_6, CITY,
                new Path(SAN_FRANCISCO, PathType.TRAIN),
                new Path(ARKHAM, PathType.TRAIN),
                new Path(SPACE_7, PathType.TRAIN)
        ));

        locations.put(SPACE_7, new Location(SPACE_7, CITY,
                new Path(SAN_FRANCISCO, PathType.SHIP),
                new Path(SPACE_6, PathType.TRAIN),
                new Path(SPACE_8, PathType.SHIP),
                new Path(THE_AMAZON, PathType.WALK),
                new Path(BUENOS_AIRES, PathType.SHIP)
        ));

        locations.put(SPACE_8, new Location(SPACE_8, SEA,
                new Path(ARKHAM, PathType.SHIP),
                new Path(SPACE_7, PathType.SHIP),
                new Path(SPACE_10, PathType.SHIP),
                new Path(BUENOS_AIRES, PathType.SHIP)
        ));

        locations.put(SPACE_9, new Location(SPACE_9, WILDERNESS,
                new Path(ARKHAM, PathType.SHIP)
        ));

        locations.put(SPACE_10, new Location(SPACE_10, WILDERNESS,
                new Path(SPACE_8, PathType.SHIP),
                new Path(ROME, PathType.SHIP),
                new Path(THE_PYRAMIDS, PathType.WALK),
                new Path(SPACE_15, PathType.SHIP)
        ));

        locations.put(SPACE_11, new Location(SPACE_11, SEA,
                new Path(BUENOS_AIRES, PathType.SHIP),
                new Path(SPACE_15, PathType.SHIP)
        ));

        locations.put(SPACE_12, new Location(SPACE_12, SEA,
                new Path(BUENOS_AIRES, PathType.SHIP),
                new Path(ANTARCTICA, PathType.SHIP)
        ));

        locations.put(SPACE_13, new Location(SPACE_13, SEA,
                new Path(LONDON, PathType.SHIP)
        ));

        locations.put(SPACE_14, new Location(SPACE_14, CITY,
                new Path(ROME, PathType.TRAIN),
                new Path(SPACE_16, PathType.TRAIN)
        ));

        locations.put(SPACE_15, new Location(SPACE_15, CITY,
                new Path(SPACE_11, PathType.SHIP),
                new Path(SPACE_10, PathType.SHIP),
                new Path(THE_HEART_OF_AFRICA, PathType.WALK),
                new Path(SPACE_17, PathType.SHIP),
                new Path(SPACE_18, PathType.SHIP)
        ));

        locations.put(SPACE_16, new Location(SPACE_16, CITY,
                new Path(ISTANBUL, PathType.TRAIN),
                new Path(SPACE_14, PathType.TRAIN),
                new Path(TUNGUSKA, PathType.TRAIN)
        ));

        locations.put(SPACE_17, new Location(SPACE_17, CITY,
                new Path(ISTANBUL, PathType.TRAIN),
                new Path(THE_HIMALAYAS, PathType.WALK),
                new Path(SHANGHAI, PathType.TRAIN),
                new Path(SPACE_20, PathType.SHIP),
                new Path(SPACE_15, PathType.SHIP)
        ));

        locations.put(SPACE_18, new Location(SPACE_18, SEA,
                new Path(SPACE_15, PathType.SHIP),
                new Path(SYDNEY, PathType.SHIP)
        ));

        locations.put(SPACE_19, new Location(SPACE_19, WILDERNESS,
                new Path(TUNGUSKA, PathType.TRAIN),
                new Path(SHANGHAI, PathType.TRAIN),
                new Path(SPACE_1, PathType.SHIP),
                new Path(TOKYO, PathType.SHIP)
        ));

        locations.put(SPACE_20, new Location(SPACE_20, CITY,
                new Path(SPACE_17, PathType.SHIP),
                new Path(SHANGHAI, PathType.SHIP),
                new Path(TOKYO, PathType.SHIP),
                new Path(SYDNEY, PathType.SHIP)
        ));

        locations.put(SPACE_21, new Location(SPACE_21, WILDERNESS,
                new Path(SYDNEY, PathType.WALK)
        ));

        locations.put(SAN_FRANCISCO, new Location(SAN_FRANCISCO, CITY,
                new Path(SPACE_1, PathType.SHIP),
                new Path(SPACE_2, PathType.SHIP),
                new Path(SPACE_5, PathType.TRAIN),
                new Path(SPACE_6, PathType.TRAIN),
                new Path(SPACE_7, PathType.SHIP)
        ));

        locations.put(ARKHAM, new Location(ARKHAM, CITY,
                new Path(SPACE_5, PathType.TRAIN),
                new Path(SPACE_9, PathType.SHIP),
                new Path(LONDON, PathType.SHIP),
                new Path(SPACE_8, PathType.SHIP),
                new Path(SPACE_6, PathType.TRAIN)
        ));

        locations.put(BUENOS_AIRES, new Location(BUENOS_AIRES, CITY,
                new Path(SPACE_3, PathType.SHIP),
                new Path(SPACE_7, PathType.SHIP),
                new Path(THE_AMAZON, PathType.WALK),
                new Path(SPACE_8, PathType.SHIP),
                new Path(SPACE_11, PathType.SHIP),
                new Path(SPACE_12, PathType.SHIP)
        ));

        locations.put(LONDON, new Location(LONDON, CITY,
                new Path(ARKHAM, PathType.SHIP),
                new Path(SPACE_13, PathType.SHIP),
                new Path(ROME, PathType.SHIP)
        ));

        locations.put(ROME, new Location(ROME, CITY,
                new Path(SPACE_10, PathType.SHIP),
                new Path(LONDON, PathType.SHIP),
                new Path(SPACE_14, PathType.TRAIN),
                new Path(ISTANBUL, PathType.TRAIN),
                new Path(THE_PYRAMIDS, PathType.SHIP)

        ));

        locations.put(ISTANBUL, new Location(ISTANBUL, CITY,
                new Path(ROME, PathType.TRAIN),
                new Path(SPACE_16, PathType.TRAIN),
                new Path(SPACE_17, PathType.TRAIN),
                new Path(THE_PYRAMIDS, PathType.TRAIN)

        ));

        locations.put(SHANGHAI, new Location(SHANGHAI, CITY,
                new Path(SPACE_17, PathType.TRAIN),
                new Path(THE_HIMALAYAS, PathType.WALK),
                new Path(SPACE_19, PathType.TRAIN),
                new Path(TOKYO, PathType.SHIP),
                new Path(SPACE_20, PathType.SHIP)
        ));

        locations.put(TOKYO, new Location(TOKYO, CITY,
                new Path(SHANGHAI, PathType.SHIP),
                new Path(SPACE_19, PathType.SHIP),
                new Path(SPACE_2, PathType.SHIP),
                new Path(SPACE_20, PathType.SHIP)
        ));

        locations.put(SYDNEY, new Location(SYDNEY, CITY,
                new Path(SPACE_21, PathType.WALK),
                new Path(SPACE_18, PathType.SHIP),
                new Path(ANTARCTICA, PathType.SHIP),
                new Path(SPACE_20, PathType.SHIP),
                new Path(SPACE_3, PathType.SHIP)
        ));

        locations.put(THE_AMAZON, new Location(THE_AMAZON, WILDERNESS,
                new Path(BUENOS_AIRES, PathType.WALK),
                new Path(SPACE_7, PathType.WALK)
        ));

        locations.put(THE_PYRAMIDS, new Location(THE_PYRAMIDS, WILDERNESS,
                new Path(SPACE_10, PathType.WALK),
                new Path(ROME, PathType.SHIP),
                new Path(ISTANBUL, PathType.TRAIN),
                new Path(THE_HEART_OF_AFRICA, PathType.WALK)
        ));

        locations.put(THE_HEART_OF_AFRICA, new Location(THE_HEART_OF_AFRICA, WILDERNESS,
                new Path(THE_PYRAMIDS, PathType.WALK),
                new Path(SPACE_15, PathType.WALK)
        ));

        locations.put(ANTARCTICA, new Location(ANTARCTICA, SEA,
                new Path(SPACE_12, PathType.SHIP),
                new Path(SYDNEY, PathType.SHIP)
        ));

        locations.put(THE_HIMALAYAS, new Location(THE_HIMALAYAS, WILDERNESS,
                new Path(SPACE_17, PathType.WALK),
                new Path(SHANGHAI, PathType.WALK)
        ));

        locations.put(TUNGUSKA, new Location(TUNGUSKA, WILDERNESS,
                new Path(SPACE_16, PathType.TRAIN),
                new Path(SPACE_19, PathType.TRAIN)
        ));

        logger.info("Locations connected");
    }
}
