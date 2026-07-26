package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class Africa1Encounter extends AbstractExpeditionEncounter {

    public Africa1Encounter() {
        super(1, LocationId.THE_HEART_OF_AFRICA);
    }

    @Override
    protected void execute() {
        EncounterTemplate passInnerTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                () -> gainCondition(ConditionId.AMNESIA)).withExpeditionFlavor();
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(),
                this::retreatDoom, passInnerTemplate);

        EncounterTemplate failInnerTemplate = new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                this::retreatDoom).withExpeditionFlavor();
        InfoFlavorTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(), () -> {
            loseHealth(1);
            gainCondition(ConditionId.BACK_INJURY);
        }, failInnerTemplate).withTwoInfos();

        new ExpeditionEncounterTemplate(getTextBuilder(), Stat.STRENGTH, 0, passTemplate, failTemplate).execute();
    }
}
