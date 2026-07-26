package sk.sivak.eldritchhorror.core.eventlistener.encounter.builder;

public class RlyehRisenEncounterTextBuilder extends EncounterTextBuilder implements ComplexEncounterTextBuilder{

    private static final String RLYEH_RISEN = "RlyehRisen";
    private String prefix = "";

    public RlyehRisenEncounterTextBuilder(int page) {
        this.page = page;
        if (propertiesMap.get(RLYEH_RISEN) != null) {
            properties = propertiesMap.get(RLYEH_RISEN);
        } else {
            initProperties();
        }

        withPage(page);
    }

    private void initProperties() {
        String fileName = "encounter/rlyeh_risen.properties";
        properties = loadPropertiesWithLocale(fileName);
        propertiesMap.put(RLYEH_RISEN, properties);
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
