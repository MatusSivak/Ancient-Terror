package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class SanFrancisco2Encounter extends AbstractLocationEncounter{

    public SanFrancisco2Encounter() {
        super(2, LocationEncounter.LocationEncounterType.SAN_FRANCISCO);
    }

    @Override
    protected void execute() {
        Runnable autoRewardAction = () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.OBSERVATION);
        Stat testStat = Stat.LORE;
        int modifier = 0;
        Runnable onFail = () -> {
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getTokenService().loseSanity(1);
        };

        EncounterTemplate innerTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), testStat, modifier, onFail).withTwoFailInfos();
        new InfoFlavorTemplate(getTextBuilder(), autoRewardAction, innerTemplate).execute();
    }
}
