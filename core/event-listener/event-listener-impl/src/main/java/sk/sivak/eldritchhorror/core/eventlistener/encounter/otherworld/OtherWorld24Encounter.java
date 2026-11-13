package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

public class OtherWorld24Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld24Encounter() {
        super(24);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::gainArtifact,
                unlessSpendTemplate(new ClueFocusHealthSanity(2, 0, 0, 0),
                        this::becomeDelayed, this::closeThisGateAndEnd));
        EncounterTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> loseSanity(1),
                new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                        NOTHING, () -> gainCondition(ConditionId.PARANOIA)
                ).withOkAfterPassFlavor().withOtherWorldFlavor());


        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.INFLUENCE, 0, passTemplate, failTemplate).execute();
    }
}
