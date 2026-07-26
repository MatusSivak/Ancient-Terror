package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Rome5Encounter extends AbstractLocationEncounter{

    public Rome5Encounter() {
        super(5, LocationEncounter.LocationEncounterType.ROME);
    }

    @Override
    protected void execute() {
        Runnable autoRewardAction = () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.WILL);
        Stat testStat = Stat.INFLUENCE;
        int modifier = 0;
        Runnable onFail = () -> {
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getTokenService().loseSanity(1);
        };

        EncounterTemplate innerTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), testStat, modifier, onFail).withTwoFailInfos();
        new InfoFlavorTemplate(getTextBuilder(),autoRewardAction, innerTemplate).execute();
    }
}
