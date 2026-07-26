package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;

public class Antarctica4Encounter extends AbstractExpeditionEncounter {

    public Antarctica4Encounter() {
        super(4, LocationId.ANTARCTICA);
    }

    @Override
    protected void execute() {
        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                this::retreatDoom,
                () -> {
                    loseSanity(1);
                    gainCondition(ConditionId.PARANOIA);
                }).withExpeditionFlavor().withTwoFailInfos();

        InfoFlavorTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> {
                    loseSanity(1);
                    gainCondition(ConditionId.HALLUCINATIONS);
                }, new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, -1,
                this::gainSpell).withExpeditionFlavor()
        ).withTwoInfos();

        new ExpeditionEncounterTemplate(getTextBuilder(), Stat.WILL, 0, passTemplate, failTemplate).execute();
    }
}
