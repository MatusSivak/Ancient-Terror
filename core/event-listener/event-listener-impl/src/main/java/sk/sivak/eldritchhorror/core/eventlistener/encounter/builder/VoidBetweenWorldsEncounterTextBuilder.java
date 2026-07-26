package sk.sivak.eldritchhorror.core.eventlistener.encounter.builder;

public class VoidBetweenWorldsEncounterTextBuilder extends EncounterTextBuilder implements ComplexEncounterTextBuilder{

    private static final String VOID_BETWEEN_WORLDS = "VoidBetweenWorlds";
    private String prefix = "";

    public VoidBetweenWorldsEncounterTextBuilder(int page) {
        this.page = page;
        if (propertiesMap.get(VOID_BETWEEN_WORLDS) != null) {
            properties = propertiesMap.get(VOID_BETWEEN_WORLDS);
        } else {
            initProperties();
        }

        withPage(page);
    }

    private void initProperties() {
        String fileName = "encounter/void_between_worlds.properties";
        properties = loadPropertiesWithLocale(fileName);
        propertiesMap.put(VOID_BETWEEN_WORLDS, properties);
    }

    private void withPrefix() {
        appendToResourceKey(prefix);
    }

    @Override
    protected void resetKey() {
        super.resetKey();
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
