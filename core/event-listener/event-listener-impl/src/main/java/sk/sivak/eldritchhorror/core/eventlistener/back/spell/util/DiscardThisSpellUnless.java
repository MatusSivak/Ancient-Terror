package sk.sivak.eldritchhorror.core.eventlistener.back.spell.util;

import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

public final class DiscardThisSpellUnless {

    private final SpellInfo spellInfo;
    private final InvestigatorId spellOwnerId;

    public DiscardThisSpellUnless(SpellInfo spellInfo,
                                  InvestigatorId spellOwnerId) {
        this.spellInfo = spellInfo;
        this.spellOwnerId = spellOwnerId;
    }

    public void discardOneItemAsset() {
        List<AssetInfo> assets = ServicePlatform.get().getAssetDeck().getAssets(spellOwnerId);
        List<AssetInfo> investigatorItems = Stream.collectToList(assets, assetInfo -> assetInfo.getTraits().contains(AssetTrait.ITEM));

        if (investigatorItems.isEmpty()) {
            TypewriterUtils.confirmInfos("[#BAD]Discard this card[]").subscribe(this::finishAndDiscard);
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
        String[] buttonTexts = new String[]{
                "[#BAD]Discard this card[]",
                "[#BAD]Discard one Item Asset[]"
        };
        ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
            if (choice == 0) {
                finishAndDiscard();
            } else {
                onDiscardOneItemChoice();
            }
        });
        ServicePlatform.get().getService().release();
    }

    private void onDiscardOneItemChoice() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        List<AssetInfo> assets = ServicePlatform.get().getAssetDeck().getAssets(spellOwnerId);
        List<AssetInfo> investigatorItems = Stream.collectToList(assets, assetInfo -> assetInfo.getTraits().contains(AssetTrait.ITEM));

        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(investigatorItems);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Item");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);

        ServicePlatform.get().getService().release();
    }

    public void lose2Sanity() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
        String[] buttonTexts = new String[]{
                "[#BAD]Discard this card[]",
                "[#BAD]Lose two Sanity[]"
        };
        ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
            if (choice == 0) {
                finishAndDiscard();
            } else {
                onLoseSanityChoice();
            }
        });
        ServicePlatform.get().getService().release();
    }

    public void lose2Health() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
        String[] buttonTexts = new String[]{
                "[#BAD]Discard this card[]",
                "[#BAD]Lose two Health[]"
        };
        ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
            if (choice == 0) {
                finishAndDiscard();
            } else {
                onLoseHealthChoice(2);
            }
        });
        ServicePlatform.get().getService().release();
    }

    public void lose3Health() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
        String[] buttonTexts = new String[]{
                "[#BAD]Discard this card[]",
                "[#BAD]Lose three Health[]"
        };
        ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
            if (choice == 0) {
                finishAndDiscard();
            } else {
                onLoseHealthChoice(3);
            }
        });
        ServicePlatform.get().getService().release();
    }

    public void spendOneFocus() {
        ServicePlatform.get().getTokenService().canSpend(0,1,0,0).subscribe(canSpend -> {
            ServicePlatform.get().getService().hold();
            if (!canSpend.hasEnough()) {
                TypewriterUtils.confirmInfos("[#BAD]Discard this card[]").subscribe(this::finishAndDiscard);
            } else {
                ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
                String[] buttonTexts = new String[]{
                        "[#BAD]Discard this card[]",
                        "[#BAD]Spend one Focus[]"
                };
                ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
                    if (choice == 0) {
                        finishAndDiscard();
                    } else {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        ServicePlatform.get().getTokenService().spend(0,1,0,0).subscribe(spend -> {
                            if (!spend.hasEnough()) {
                                justDiscard();
                            } else {
                                spend.pay();
                            }
                        });
                        ServicePlatform.get().getService().release();
                    }
                });
            }
            ServicePlatform.get().getService().release();
        });
    }

    public void gainCondition(ConditionId conditionId) {
        if (ServicePlatform.get().getConditionsDeck().hasCondition(spellOwnerId, conditionId)) {
            TypewriterUtils.confirmInfos("[#BAD]Discard this card[]").subscribe(this::finishAndDiscard);
        } else {
            ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
            String[] buttonTexts = new String[] {
                    "[#BAD]Discard this card[]",
                    "[#BAD]Gain "+conditionId.asString()+" Condition[]"
            };
            ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
                if (choice == 0) {
                    finishAndDiscard();
                } else {
                    onGainCondition(conditionId);
                }
            });
        }
    }

    public void gainCondition(ConditionTrait conditionTrait) {
        if (ServicePlatform.get().getConditionsDeck().hasTrait(spellOwnerId, conditionTrait)) {
            TypewriterUtils.confirmInfos("[#BAD]Discard this card[]").subscribe(this::finishAndDiscard);
        } else {
            ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
            String[] buttonTexts = new String[] {
                    "[#BAD]Discard this card[]",
                    "[#BAD]Gain "+conditionTrait.asString()+" Condition.[]"
            };
            ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
                if (choice == 0) {
                    finishAndDiscard();
                } else {
                    onGainCondition(conditionTrait);
                }
            });
        }
    }

    private void onGainCondition(ConditionId conditionId) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getGameService().gainCondition(conditionId);
        ServicePlatform.get().getService().release();
    }

    private void onGainCondition(ConditionTrait conditionTrait) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getGameService().gainCondition(conditionTrait);
        ServicePlatform.get().getService().release();
    }

    private void finishAndDiscard() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        justDiscard();
        ServicePlatform.get().getService().release();
    }

    private void onLoseSanityChoice() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getTokenService().loseSanity(2);
        ServicePlatform.get().getService().release();
    }

    private void onLoseHealthChoice(int amount) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getTokenService().loseHealth(amount);
        ServicePlatform.get().getService().release();
    }

    private void justDiscard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setSpellInfo(spellInfo);
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard " +spellInfo.getName()+ " Spell");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);

        displaySpell.subscribe(response ->
                ServicePlatform.get().getService().discardSpellFromInvestigator(spellOwnerId, spellInfo));
    }


}
