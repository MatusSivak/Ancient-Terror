package sk.sivak.eldritchhorror.core.eventlistener.encounter.builder;

import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;

public class DefeatedEncounterTextBuilder extends EncounterTextBuilder {

    private final InvestigatorId investigatorId;
    private final boolean health;

    public DefeatedEncounterTextBuilder(InvestigatorId investigatorId, boolean health) {
        this.investigatorId = investigatorId;
        this.health = health;
        if (propertiesMap.get(EncounterType.DEFEATED_INVESTIGATOR.name()) != null) {
            properties = propertiesMap.get(EncounterType.DEFEATED_INVESTIGATOR.name());
        } else {
            initProperties();
        }

        withInvestigatorId();
        withInjuredOrInsanity();
    }

    private void initProperties() {
        String fileName = "encounter/defeated.properties";
        properties = loadPropertiesWithLocale(fileName);
        propertiesMap.put(EncounterType.DEFEATED_INVESTIGATOR.name(), properties);
    }



    @Override
    protected void withPage(int page) {

    }

    private void withInvestigatorId() {
        appendToResourceKey(investigatorId.name());
        appendToResourceKey(".");
    }

    private void withInjuredOrInsanity() {
        appendToResourceKey(health ? "health" : "sanity");
    }

    @Override
    protected void resetKey() {
        super.resetKey();
        withInvestigatorId();
        withInjuredOrInsanity();
    }
}
