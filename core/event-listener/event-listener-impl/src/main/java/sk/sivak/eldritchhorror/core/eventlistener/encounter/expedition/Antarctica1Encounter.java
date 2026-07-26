package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class Antarctica1Encounter extends AbstractExpeditionEncounter {

    public Antarctica1Encounter() {
        super(1, LocationId.ANTARCTICA);
    }

    @Override
    protected void execute() {
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(),
                this::retreatDoom,
                new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                        () -> gainCondition(ConditionId.AMNESIA)).withExpeditionFlavor());

        InfoFlavorTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> {
                    loseHealth(1);
                    gainCondition(ConditionId.BACK_INJURY);
                }, new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                this::retreatDoom).withExpeditionFlavor())
                .withTwoInfos();

        new ExpeditionEncounterTemplate(getTextBuilder(), Stat.STRENGTH, 0, passTemplate, failTemplate).execute();
    }
}
