package sk.sivak.eldritchhorror.core.eventlistener.encounter.builder;

import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class LocationEncounterTextBuilder extends EncounterTextBuilder {

    public LocationEncounterTextBuilder(int page, LocationEncounter.LocationEncounterType locationType) {
        this.page = page;
        if (propertiesMap.get(locationType.name()) != null) {
            properties = propertiesMap.get(locationType.name());
        } else {
            initProperties(locationType);
        }

        withPage(page);
    }

    private void initProperties(LocationEncounter.LocationEncounterType locationType) {
        String fileName = "encounter/"+locationType.name()+".properties";
        properties = loadPropertiesWithLocale(fileName);
        propertiesMap.put(locationType.name(), properties);
    }

    @Override
    protected void resetKey() {
        super.resetKey();
    }
}
