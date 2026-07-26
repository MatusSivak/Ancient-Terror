package sk.sivak.eldritchhorror.core.eventlistener.back.condition.darkpact;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequestBuilder;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.RollData;

import java.util.LinkedList;
import java.util.List;

public class DarkPactConditionBackListener1 extends AbstractConditionBackListener {

    public DarkPactConditionBackListener1(ConditionInfo conditionInfo) {
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
                "[#BAD]Discard all of your possessions[]",
                "[#BAD]Discard all of your Improvements[]",
                "[#BAD]Lose all but one Health[]",
                "[#BAD]Lose all but one Sanity[]",
                "[#GOOD]Discard this card[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            discardPossessions();
            discardImprovementTokens();
            ServicePlatform.get().getTokenService().spendHealth(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentHealth()-1);
            ServicePlatform.get().getTokenService().spendSanity(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentSanity()-1);
            discardThisCard();
            ServicePlatform.get().getService().release();
        });
    }

    private void discardImprovementTokens() {
        List<Stat> improvedSkills = new LinkedList<>();
        for (Stat stat : Stat.values()) {
            if (ServicePlatform.get().getInvestigators().getActiveInvestigator().getStatBonus(stat) == 0) {
                continue;
            }
            improvedSkills.add(stat);
        }

        if (!improvedSkills.isEmpty()) {
            for (Stat improvedSkill : improvedSkills) {
                ServicePlatform.get().getInvestigatorService().loseImprovement(new LoseImprovementData(improvedSkill, 2));
            }
        }
    }

    private void discardPossessions() {
        List<CardInfo> cards = new LinkedList<>();
        cards.addAll(ServicePlatform.get().getAssetDeck().getAssets(getActiveInvestigatorId()));
        cards.addAll(ServicePlatform.get().getArtifactsDeck().getArtifacts(getActiveInvestigatorId()));
        cards.addAll(ServicePlatform.get().getSpellsDeck().getSpells(getActiveInvestigatorId()));

        for (CardInfo card : cards) {
            EncounterUtils.onSelectCardToDiscard(card);
        }
        for (int i = 0; i < ServicePlatform.get().getCluePool().getClueCount(getActiveInvestigatorId()); i++) {
            ServicePlatform.get().getTokenService().loseClue();
        }
        for (int i = 0; i < ServicePlatform.get().getInvestigators().getActiveInvestigator().getShipTickets(); i++) {
            ServicePlatform.get().getService().convertTo(LocationInfo.Connection.class, () -> new LocationInfo.Connection() {
                @Override
                public LocationId getLocationId() {
                    return null;
                }

                @Override
                public PathType getPathType() {
                    return PathType.SHIP;
                }
            });
            ServicePlatform.get().getBasicActionService().loseTicket();
        };

        for (int i = 0; i < ServicePlatform.get().getInvestigators().getActiveInvestigator().getTrainTickets(); i++) {
            ServicePlatform.get().getService().convertTo(LocationInfo.Connection.class, () -> new LocationInfo.Connection() {
                @Override
                public LocationId getLocationId() {
                    return null;
                }

                @Override
                public PathType getPathType() {
                    return PathType.TRAIN;
                }
            });
            ServicePlatform.get().getBasicActionService().loseTicket();
        };


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
