package sk.sivak.eldritchhorror.core.eventlistener.encounter.builder;

import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;

public class GeneralEncounterTextBuilder extends EncounterTextBuilder {

    private LocationType locationType;

    public GeneralEncounterTextBuilder(int page, LocationType locationType) {
        this.page = page;
        if (propertiesMap.get(EncounterType.GENERAL.name()) != null) {
            properties = propertiesMap.get(EncounterType.GENERAL.name());
        } else {
            initProperties();
        }

        withPage(page);
        this.locationType = locationType;
        withLocationType(locationType);
    }

    private void initProperties() {
        String fileName = "encounter/general.properties";
        properties = loadPropertiesWithLocale(fileName);
        propertiesMap.put(EncounterType.GENERAL.name(), properties);
    }

    private void withLocationType(LocationType locationType) {
        switch (locationType) {
            case CITY:
                appendToResourceKey(".city");
                return;
            case SEA:
                appendToResourceKey(".sea");
                return;
            case WILDERNESS:
                appendToResourceKey(".wilderness");
                return;
        }
        throw new IllegalArgumentException();
    }

    @Override
    protected void resetKey() {
        super.resetKey();
        withLocationType(locationType);
    }
}
