package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.SpellTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class Arkham6Encounter extends AbstractLocationEncounter{

    public Arkham6Encounter() {
        super(6, LocationEncounter.LocationEncounterType.ARKHAM);
    }

    @Override
    protected void execute() {
        Runnable autoRewardAction = () -> ServicePlatform.get().getGameService().gainSpell(SpellTrait.INCANTATION);
        Stat testStat = Stat.WILL;
        int modifier = +1;
        Runnable onFail = () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA);

        EncounterTemplate innerTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), testStat, modifier, onFail).withoutFailFlavor();
        new InfoFlavorTemplate(getTextBuilder(),autoRewardAction, innerTemplate).execute();
    }

}
