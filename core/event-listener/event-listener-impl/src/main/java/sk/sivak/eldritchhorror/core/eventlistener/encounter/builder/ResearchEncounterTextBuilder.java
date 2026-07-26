package sk.sivak.eldritchhorror.core.eventlistener.encounter.builder;

import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;

public class ResearchEncounterTextBuilder extends EncounterTextBuilder {

    private LocationType locationType;
    private AncientOneId ancientOneId;

    public ResearchEncounterTextBuilder(int page, LocationType locationType, AncientOneId ancientOneId) {
        this.ancientOneId = ancientOneId;
        this.page = page;
        if (propertiesMap.get(EncounterType.RESEARCH.name()) != null) {
            properties = propertiesMap.get(EncounterType.RESEARCH.name());
        } else {
            initProperties();
        }

        withPage(page);
        this.locationType = locationType;
        withLocationType(locationType);
    }

    private void initProperties() {
        String fileName = "encounter/research_"+ancientOneId.name()+".properties";
        properties = loadPropertiesWithLocale(fileName);
        propertiesMap.put(EncounterType.RESEARCH.name(), properties);
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
