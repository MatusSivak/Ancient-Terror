package sk.sivak.eldritchhorror.core.eventlistener.encounter.otherworld;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.OtherWorldEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.SomethingUnlessSpendTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

public class OtherWorld24Encounter extends AbstractOtherWorldEncounter {

    public OtherWorld24Encounter() {
        super(24);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(), this::gainArtifact,
                new SomethingUnlessSpendTemplate(getTextBuilder(), new ClueFocusHealthSanity(2, 0, 0, 0),
                        this::becomeDelayed)
                        .setComplementaryOnSpendAction(() -> {
                            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                            ServicePlatform.get().getEncounterService().typeFlavor(" \n" + getTextBuilder().withPass().withFlavor().build());
                            TypewriterUtils.confirmInfos(getTextBuilder().withPass().withInfo().build()).subscribe(() -> {
                                ServicePlatform.get().getEncounterService().hold();
                                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                closeThisGateAndEnd();
                                ServicePlatform.get().getEncounterService().release();
                            });
                        })
        );

        EncounterTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(),
                () -> loseSanity(1),
                new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.WILL, 0,
                        () -> gainCondition(ConditionId.PARANOIA)
                ).withoutFailFlavor().withOtherWorldFlavor());


        new OtherWorldEncounterTemplate(getTextBuilder(), Stat.INFLUENCE, 0, passTemplate, failTemplate).execute();
    }
}
