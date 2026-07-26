package sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils.confirmInfos;

public class DebtConditionBackListener5 extends AbstractConditionBackListener{

    public DebtConditionBackListener5(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestConditionButton(Stat.OBSERVATION, 0, getConditionInfo(),
                () -> {
                    confirmInfos(
                            "[#GOOD]Discard this card[]").subscribe(() -> {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        discardThisCard();
                        ServicePlatform.get().getService().release();
                    });
                },
                () -> {
                    if (findAllies().isEmpty()) {
                        becomeDetained();
                    } else {
                        TypewriterUtils.noYesQuestion("[#BAD]Discard one Ally instead of prison?[]", this::becomeDetained, () -> {
                            ServicePlatform.get().getService().hold();
                            ServicePlatform.get().getEncounterService().hideTypewriterPaper();

                            List<? extends CardInfo> investigatorItems = findAllies();
                            SelectCardData selectCardData = new SelectCardData();
                            selectCardData.setAvailableCards(investigatorItems);
                            selectCardData.setHideText("Display Inventory?");
                            selectCardData.setTitleText("Select Ally");
                            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(selectedAlly -> {
                                ServicePlatform.get().getService().hold();
                                EncounterUtils.onSelectCardToDiscardByChoice(selectedAlly);
                                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                                TypewriterUtils.confirmInfos("[#GOOD]Discard this card.").subscribe(() -> {
                                    ServicePlatform.get().getService().hold();
                                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                                    discardThisCard();
                                    ServicePlatform.get().getService().release();
                                });
                                ServicePlatform.get().getService().release();
                            });

                            ServicePlatform.get().getService().release();
                        });
                    }
                });
    }

    private List<? extends CardInfo> findAllies() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ALLY);
    }

    private void becomeDetained() {
        confirmInfos(
                "Move to the nearest City",
                "[#BAD]Gain a Detained Condition[]",
                "[#GOOD]Discard this card[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
            LocationId nearestCity = ServicePlatform.get().getLocationMap().findNearest(currentLocationId,
                    locationInfo -> locationInfo.getLocationType() == LocationType.CITY).getLocationId();

            ServicePlatform.get().getBasicActionService().travelToLocation(new LocationInfo.Connection() {
                @Override
                public LocationId getLocationId() {
                    return nearestCity;
                }

                @Override
                public PathType getPathType() {
                    return PathType.WALK;
                }
            });

            ServicePlatform.get().getGameService().gainCondition(ConditionId.DETAINED);
            discardThisCard();
            ServicePlatform.get().getService().release();
        });
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Debt Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
        });
    }
}
