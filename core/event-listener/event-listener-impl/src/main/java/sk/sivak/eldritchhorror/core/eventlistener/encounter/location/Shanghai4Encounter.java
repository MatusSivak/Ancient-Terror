package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class Shanghai4Encounter extends AbstractLocationEncounter{

    public Shanghai4Encounter() {
        super(4, LocationEncounter.LocationEncounterType.SHANGHAI);
    }

    @Override
    protected void execute() {
        Runnable autoRewardAction = () -> ServicePlatform.get().getInvestigatorService().improveSkill(Stat.LORE);
        Stat testStat = Stat.OBSERVATION;
        int modifier = 0;
        Runnable onFail = () -> ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), true));

        TestFailFlavorInfoTemplate innerTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), testStat, modifier, onFail);
        new InfoFlavorTemplate(getTextBuilder(),autoRewardAction, innerTemplate).execute();
    }
}
