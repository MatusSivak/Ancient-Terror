package sk.sivak.eldritchhorror.core.eventlistener.encounter.template;

import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.builder.EncounterTextBuilder;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class TestFailFlavorInfoTemplate extends TestPassFlavorInfoFailFlavorInfoTemplate {

    public TestFailFlavorInfoTemplate(EncounterTextBuilder textBuilder, Stat testStat, int modifier, Runnable onFail) {
        super(textBuilder, testStat, modifier, null, onFail);
    }
}
