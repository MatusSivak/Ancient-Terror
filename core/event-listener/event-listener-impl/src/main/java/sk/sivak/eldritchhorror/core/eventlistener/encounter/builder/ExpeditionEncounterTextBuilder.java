package sk.sivak.eldritchhorror.core.eventlistener.encounter.builder;

import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class ExpeditionEncounterTextBuilder extends EncounterTextBuilder implements ComplexEncounterTextBuilder{

    private LocationId locationId;
    private String prefix = "";

    public ExpeditionEncounterTextBuilder(int page, LocationId locationId) {
        this.page = page;
        if (propertiesMap.get(EncounterType.EXPEDITION.name()) != null) {
            properties = propertiesMap.get(EncounterType.EXPEDITION.name());
        } else {
            initProperties();
        }

        withPage(page);
        this.locationId = locationId;
        withLocationId(locationId);
    }

    private void initProperties() {
        String fileName = "encounter/expedition.properties";
        properties = loadPropertiesWithLocale(fileName);
        propertiesMap.put(EncounterType.EXPEDITION.name(), properties);
    }

    private void withLocationId(LocationId locationId) {
        appendToResourceKey("."+locationId.lastWordToLowercase());
    }

    private void withPrefix() {
        appendToResourceKey(prefix);
    }

    @Override
    protected void resetKey() {
        super.resetKey();
        withLocationId(locationId);
        withPrefix();

    }

    @Override
    public void addPassPrefix() {
        prefix = ".pass";
        resetKey();
    }

    @Override
    public void addFailPrefix() {
        prefix = ".fail";
        resetKey();
    }
}
