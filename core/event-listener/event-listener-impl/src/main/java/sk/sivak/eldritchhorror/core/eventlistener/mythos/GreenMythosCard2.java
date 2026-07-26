package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.poisoned.PoisonedCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.AssetOriginType;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class GreenMythosCard2 implements MythosCardEventListener{

    @Override
    public void execute() {
        List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : investigators) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getService().hold();
                processSingleInvestigator(investigator);
                ServicePlatform.get().getService().release();
            });

        }
        ServicePlatform.get().getService().release();
    }

    private void processSingleInvestigator(InvestigatorRead investigator) {
        InvestigatorId investigatorId = investigator.getInfo().getInvestigatorId();
        boolean isPoisoned = ServicePlatform.get().getConditionsDeck().hasCondition(investigatorId, ConditionId.POISONED);
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        if (isPoisoned) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle(investigatorId.toString()+" is already poisoned!");
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getTokenService().loseHealth(3);
            });
        } else if (!ServicePlatform.get().getConditionsDeck().canGetConditionId(investigatorId, ConditionId.POISONED)) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("There are no Poisoned Conditions remaining.");
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getTokenService().loseHealth(3);
            });
        } else {
            ShowCardRequest showCardRequest = new ShowCardRequestBuilder(new PoisonedCondition())
                    .withDisplayAssetResponseType(DisplayAssetResponseType.YES_NO)
                    .withTitle("Gain a Poisoned Condition?")
                    .withAssetHideType(HideType.INVENTORY_ON_YES)
                    .withAssetOriginType(AssetOriginType.CENTER).build();

            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(answer -> {
                if (answer == ShowCardResponse.YES) {
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.POISONED, false);
                } else {
                    ServicePlatform.get().getTokenService().loseHealth(3);
                }
            });
        }
    }
}
