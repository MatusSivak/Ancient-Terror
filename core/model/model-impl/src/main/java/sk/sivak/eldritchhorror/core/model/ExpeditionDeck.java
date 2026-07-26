package sk.sivak.eldritchhorror.core.model;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.expedition.ExpeditionInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.model.save.ExpeditionDeckSaveData;
import sk.sivak.eldritchhorror.core.model.save.ExpeditionDeckSaveDataRead;
import sk.sivak.eldritchhorror.core.model.util.ExpeditionDeckHelper;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class ExpeditionDeck implements ExpeditionDeckWrite {

    private static final Logger logger = LogManager.getLogger(ExpeditionDeck.class);

    private List<ExpeditionInfo> expeditionDeck;

    @Override
    public void initExpeditionDeck() {
        expeditionDeck = new ExpeditionDeckHelper().init();

        if (!GoogleServicesHolder.isTutorialPassed()) {
            expeditionDeck.add(0, new ExpeditionInfo() {
                @Override
                public LocationId getLocationId() {
                    return LocationId.THE_HIMALAYAS;
                }

                @Override
                public int getPage() {
                    return 3;
                }
            });
        }
    }

    @Override
    public LocationId getExpeditionTokenLocation() {
        return expeditionDeck.get(0).getLocationId();
    }

    @Override
    public int getExpeditionPage() {
        return expeditionDeck.get(0).getPage();
    }

    @Override
    public void discardTopCard() {
        expeditionDeck.remove(0);
        logger.info("New expedition location: " + getExpeditionTokenLocation());
    }

    @Override
    public void destroyCurrentExpeditionLocation() {
        LocationId expeditionLocation = expeditionDeck.get(0).getLocationId();
        IterableUtils.removeIf(expeditionDeck, expeditionInfo -> expeditionInfo.getLocationId() == expeditionLocation);
    }

    @Override
    public ExpeditionDeckSaveData save() {
        ExpeditionDeckSaveData expeditionDeckSaveData = new ExpeditionDeckSaveData();
        expeditionDeckSaveData.setExpeditionDeck(Stream.collectToList(Stream.map(expeditionDeck, ExpeditionInfo::getLocationId)));
        return expeditionDeckSaveData;
    }

    @Override
    public void load(ExpeditionDeckSaveDataRead expeditionDeckSaveData) {
        expeditionDeck = new ExpeditionDeckHelper().init();
        LinkedList<ExpeditionInfo> newExpeditionDeck = new LinkedList<>();
        for (LocationId locationId : expeditionDeckSaveData.getExpeditionDeck()) {
            ExpeditionInfo foundExpedition = Stream.findFirstOrException(expeditionDeck,
                    expeditionInfo -> expeditionInfo.getLocationId().equals(locationId));
            newExpeditionDeck.add(foundExpedition);
            expeditionDeck.remove(foundExpedition);
        }
        expeditionDeck = newExpeditionDeck;
    }
}
