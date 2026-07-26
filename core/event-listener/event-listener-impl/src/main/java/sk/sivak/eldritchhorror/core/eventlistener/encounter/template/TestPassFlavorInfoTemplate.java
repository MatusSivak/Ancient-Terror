package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class TestPassFlavorInfoTemplate extends TestPassFlavorInfoFailFlavorInfoTemplate {

    public TestPassFlavorInfoTemplate(EncounterTextBuilder textBuilder, Stat testStat, int modifier, Runnable onSuccess) {
        super(textBuilder, testStat, modifier, onSuccess, null);
    }
}
