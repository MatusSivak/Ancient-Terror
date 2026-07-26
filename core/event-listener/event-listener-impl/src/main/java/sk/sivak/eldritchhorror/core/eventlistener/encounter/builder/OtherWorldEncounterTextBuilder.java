package sk.sivak.eldritchhorror.core.eventlistener.encounter.builder;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;

public class OtherWorldEncounterTextBuilder extends EncounterTextBuilder implements ComplexEncounterTextBuilder{

    private static final String OTHER_WORLD = "OtherWorld";
    private String prefix = "";

    public OtherWorldEncounterTextBuilder(int page) {
        this.page = page;
        if (propertiesMap.get(OTHER_WORLD) != null) {
            properties = propertiesMap.get(OTHER_WORLD);
        } else {
            initProperties();
        }

        withPage(page);
    }

    private void initProperties() {
        String fileName = "encounter/otherworld.properties";
        properties = loadPropertiesWithLocale(fileName);
        propertiesMap.put(OTHER_WORLD, properties);
    }

    private void withPrefix() {
        appendToResourceKey(prefix);
    }

    @Override
    protected void resetKey() {
        super.resetKey();
        withPrefix();
    }

    public OtherWorldEncounterTextBuilder withTitle() {
        appendToResourceKey(".title");
        return this;
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
