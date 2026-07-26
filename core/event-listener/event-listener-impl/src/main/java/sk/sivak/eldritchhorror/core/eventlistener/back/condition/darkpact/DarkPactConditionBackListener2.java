package sk.sivak.eldritchhorror.core.eventlistener.back.condition.darkpact;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.cursed.CursedCondition;
import sk.sivak.eldritchhorror.core.constants.condition.darkpact.DarkPactCondition;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.List;

public class DarkPactConditionBackListener2 extends AbstractConditionBackListener {

    public DarkPactConditionBackListener2(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    public void executeWhole() {
        ShowCardRequest showCardRequest = new ShowCardRequestBuilder(getConditionInfo())
                .withTitle("Roll 1 die. On a 1, flip this card.")
                .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                .build();
        ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> onConfirm());
    }

    @Override
    public void justFlip() {
        ShowCardRequest showCardRequest = new ShowCardRequestBuilder(getConditionInfo())
                .withTitle("Flip this card.")
                .withDisplayAssetResponseType(DisplayAssetResponseType.OK)
                .build();
        ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(ok -> onFlip(null));
    }

    private void onConfirm() {
        RollData rollData = new RollData();
        rollData.setMinSuccessful(2);
        rollData.setMaxFailed(1);
        Single<Integer> singleDieResult = ServicePlatform.get().getTestService().rollDie(rollData);
        singleDieResult.subscribe(this::resolveDiceRoll);
    }

    private void resolveDiceRoll(Integer rolledValue) {
        if (rolledValue <= 1) {
            onFlip(null);
        }
    }

    @Override
    protected void execute() {
        TypewriterUtils.confirmInfos(
                "[#BAD]Each other investigator\ngains a Cursed Condition\nunless he gains a Dark Pact Condition[]",
                "[#GOOD]Discard this card[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();

            if (investigators.size() == 1) {
                discardThisCard();
                ServicePlatform.get().getService().release();
                return;
            }

            InvestigatorRead conditionOwner = ServicePlatform.get().getInvestigators().getActiveInvestigator();
            for (InvestigatorRead investigator : investigators) {
                if (investigator.getInfo().getInvestigatorId() == getActiveInvestigatorId()) {
                    continue;
                }

                askSingleInvestigator(investigator);
            }
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(conditionOwner.getInfo().getInvestigatorId());
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            discardThisCard();
            ServicePlatform.get().getService().release();
        });
    }

    private void askSingleInvestigator(InvestigatorRead investigator) {
        boolean hasDarkPact = ServicePlatform.get().getConditionsDeck().hasCondition(investigator.getInfo().getInvestigatorId(), ConditionId.DARK_PACT);

        if (hasDarkPact) {
            ServicePlatform.get().getService().addEventCommand(whatever -> {
                gainCursedCondition(investigator);
            });
        } else {
            ServicePlatform.get().getService().addEventCommand(whatever -> {
                selectCardToGain(investigator);
            });
        }
    }

    private void gainCursedCondition(InvestigatorRead investigator) {
        if (!ServicePlatform.get().getConditionsDeck().canGetConditionId(investigator.getInfo().getInvestigatorId(), ConditionId.CURSED)) {
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
        ServicePlatform.get().getService().release();
    }

    private void selectCardToGain(InvestigatorRead investigator) {
        if (!ServicePlatform.get().getConditionsDeck().canGetConditionId(investigator.getInfo().getInvestigatorId(), ConditionId.DARK_PACT)) {
            gainCursedCondition(investigator);
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigator.getInfo().getInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setTitleText("Select Condition");
        selectCardData.setHideText("Display Conditions?");
        selectCardData.setAvailableCards(Arrays.asList(new CursedCondition(), new DarkPactCondition()));
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(selectedCard -> {
            ServicePlatform.get().getGameService().gainCondition(((ConditionInfo)selectedCard).getId());
        });
        ServicePlatform.get().getService().release();
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Dark Pact Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
        });
    }
}
