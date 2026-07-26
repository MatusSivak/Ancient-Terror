package sk.sivak.eldritchhorror.core.eventlistener.back.condition.debt;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils.confirmInfos;

public class DebtConditionBackListener6 extends AbstractConditionBackListener{

    public DebtConditionBackListener6(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        TypewriterUtils.displayTestConditionButton(Stat.INFLUENCE, 0, getConditionInfo(),
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
                    confirmInfos(
                            "[#BAD]Discard two Item possessions[]",
                            "[#GOOD]Discard this card[]").subscribe(() -> {
                        ServicePlatform.get().getService().hold();

                        List<? extends CardInfo> items = findItems();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        if (!items.isEmpty()) {
                            SelectCardData selectCardData = new SelectCardData();
                            selectCardData.setAvailableCards(items);
                            selectCardData.setHideText("Display Inventory?");
                            selectCardData.setTitleText("Select Item");
                            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(selectedItem -> {
                                ServicePlatform.get().getService().hold();
                                EncounterUtils.onSelectCardToDiscard(selectedItem);
                                discardAgain();
                                ServicePlatform.get().getService().release();
                            });
                        } else {
                            discardThisCard();
                        }
                        ServicePlatform.get().getService().release();
                    });
                });
    }

    private void discardAgain() {
        ServicePlatform.get().getService().addEventCommand(in -> {
            ServicePlatform.get().getService().hold();
            List<? extends CardInfo> items = findItems();

            if (!items.isEmpty()) {
                SelectCardData selectCardData = new SelectCardData();
                selectCardData.setAvailableCards(items);
                selectCardData.setHideText("Display Inventory?");
                selectCardData.setTitleText("Select Item");
                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(selectedItem -> {
                    ServicePlatform.get().getService().hold();
                    EncounterUtils.onSelectCardToDiscard(selectedItem);
                    discardThisCard();
                    ServicePlatform.get().getService().release();
                });
            } else {
                discardThisCard();
            }
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

    private List<? extends CardInfo> findItems() {
        return EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ITEM);
    }
}
