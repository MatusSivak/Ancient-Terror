package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import rx.functions.Action0;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SpendCluePassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class OtherWorld16Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld16Encounter() {
        super(16);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                this::closeThisGateAndEnd,
                () -> loseSanity(1))
                .withOtherWorldFlavor().withoutPassFlavor();


        Action0 onNoAction = () -> {
            TypewriterUtils.confirmInfos(getTextBuilder().withFail().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                becomeDelayed();
                ServicePlatform.get().getService().release();
            });
        };
        EncounterTemplate failTemplate = new SpendCluePassFlavorInfoTemplate(getTextBuilder(), this::closeThisGateAndEnd) {
            @Override
            public void execute() {
                getTextBuilder().addFailPrefix();
                super.execute();
            }
        }.withoutPassFlavor().withoutPassInfo().withOnNoAction(onNoAction);

        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.WILL, 0, passTemplate, failTemplate).execute();
    }
}
