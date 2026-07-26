package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yog_sothoth;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class ResearchWilderness11Encounter extends AbstractYogSothothResearchEncounter {

    public ResearchWilderness11Encounter() {
        super(11, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {

        TypewriterUtils.noYesQuestion(getTextBuilder().withQuestion().build(), () -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        }, () -> {

            ServicePlatform.get().getTokenService().canSpend(0,0,0,2).subscribe(canSpendData -> {
                if (!canSpendData.hasEnough()) {
                    TypewriterUtils.confirmInfos("You can't spend two Sanity.").subscribe(() -> {
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    });
                    return;
                }
                if (!ServicePlatform.get().getConditionsDeck().canGetConditionId(getActiveInvestigatorId(), ConditionId.LOST_IN_TIME_AND_SPACE)) {
                    TypewriterUtils.confirmInfos("You can't gain\nLost in Time and Space Condition.").subscribe(() -> {
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                    });
                    return;
                }

                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getTokenService().spend(0,0,0,2).subscribe(spendData -> {
                    if (!spendData.hasEnough()) {
                        return;
                    }
                    ServicePlatform.get().getService().hold();
                    spendData.pay();
                    gainThisClue();
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.LOST_IN_TIME_AND_SPACE);
                    ServicePlatform.get().getService().release();
                });
                ServicePlatform.get().getService().release();
            });
        });

    }
}
