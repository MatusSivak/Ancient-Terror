package sk.sivak.eldritchhorror.core.eventlistener.encounter.builder;

public class TheKeyAndTheGateEncounterTextBuilder extends EncounterTextBuilder implements ComplexEncounterTextBuilder{

    private static final String THE_KEY_AND_THE_GATE = "TheKeyAndTheGate";
    private String prefix = "";

    public TheKeyAndTheGateEncounterTextBuilder(int page) {
        this.page = page;
        if (propertiesMap.get(THE_KEY_AND_THE_GATE) != null) {
            properties = propertiesMap.get(THE_KEY_AND_THE_GATE);
        } else {
            initProperties();
        }

        withPage(page);
    }

    private void initProperties() {
        String fileName = "encounter/the_key_and_the_gate.properties";
        properties = loadPropertiesWithLocale(fileName);
        propertiesMap.put(THE_KEY_AND_THE_GATE, properties);
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
