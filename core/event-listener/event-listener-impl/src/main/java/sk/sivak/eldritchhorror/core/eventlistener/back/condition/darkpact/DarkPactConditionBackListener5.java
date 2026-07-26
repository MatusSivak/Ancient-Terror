package sk.sivak.eldritchhorror.core.eventlistener.back.condition.darkpact;

import java8.features.stream.Stream;
import java8.features.util.MapUtils;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Collection;
import java.util.List;

public class DarkPactConditionBackListener5 extends AbstractConditionBackListener {

    public DarkPactConditionBackListener5(ConditionInfo conditionInfo) {
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
                "[#BAD]Another investigator is devoured[]",
                "[#GOOD]Discard this card[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();

            List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
            List<? extends InvestigatorRead> otherInvestigators = Stream.collectToList(investigators, investigator -> investigator.getInfo().getInvestigatorId() != getActiveInvestigatorId());
            Collection<InvestigatorId> otherInvestigatorIds = Stream.map(otherInvestigators, otherInvestigator -> otherInvestigator.getInfo().getInvestigatorId());
            if (otherInvestigators.isEmpty()) {
                discardThisCard();
                ServicePlatform.get().getService().release();
                return;
            }

            InvestigatorRestriction investigatorRestriction = new InvestigatorRestriction(true);
            investigatorRestriction.addAllowedInvestigators(otherInvestigatorIds);
            ServicePlatform.get().getInvestigatorService().selectInvestigator(investigatorRestriction).subscribe(selectedInvestigator -> {
                ServicePlatform.get().getService().hold();
                InvestigatorRead darkPactOwner = ServicePlatform.get().getInvestigators().getActiveInvestigator();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(selectedInvestigator);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                ServicePlatform.get().getInvestigatorService().devourInvestigator(selectedInvestigator);
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(darkPactOwner.getInfo().getInvestigatorId());
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                discardThisCard();
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
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
