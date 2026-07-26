package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;

public class Himalayas4Encounter extends AbstractExpeditionEncounter {

    public Himalayas4Encounter() {
        super(4, LocationId.THE_HIMALAYAS);
    }

    @Override
    protected void execute() {


        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1,
                () -> {
                    gainSpell();
                    gainSpell();
                },
                () -> gainCondition(ConditionId.HALLUCINATIONS)).withExpeditionFlavor();

        EncounterTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> gainCondition(ConditionId.LEG_INJURY),
                new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, -1,
                        this::gainTwoClues,
                        () -> gainCondition(ConditionId.CURSED)).withExpeditionFlavor()
        );

        new ExpeditionEncounterTemplate(getTextBuilder(), Stat.STRENGTH, 0, passTemplate, failTemplate).execute();
    }
}
