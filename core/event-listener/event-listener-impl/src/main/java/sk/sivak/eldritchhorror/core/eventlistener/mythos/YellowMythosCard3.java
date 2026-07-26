package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class YellowMythosCard3 implements MythosCardEventListener{

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorId);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            boolean hasBlessedCondition = ServicePlatform.get().getConditionsDeck().hasCondition(investigatorId, ConditionId.BLESSED);
            if (hasBlessedCondition) {
                ConditionInfo blessedCondition = ServicePlatform.get().getConditionsDeck().getCondition(investigatorId, ConditionId.BLESSED);
                ShowCardRequest showCardRequest = new ShowCardRequestBuilder(blessedCondition)
                        .withAssetHideType(HideType.DISCARD_ON_YES)
                        .withTitle("Discard Blessed Condition?")
                        .withDisplayAssetResponseType(DisplayAssetResponseType.YES_NO)
                        .build();
                ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(showCardResponse -> {
                    if (showCardResponse == ShowCardResponse.YES) {
                        ServicePlatform.get().getService().discardConditionFromInvestigator(investigatorId, blessedCondition);
                    } else {
                        loseHealthAndSanity();
                    }
                });
            } else {
                loseHealthAndSanity();
            }
        }
        ServicePlatform.get().getService().release();
    }

    private void loseHealthAndSanity() {
        ServicePlatform.get().getTokenService().loseHealth(2);
        ServicePlatform.get().getTokenService().loseSanity(2);
    }
}
