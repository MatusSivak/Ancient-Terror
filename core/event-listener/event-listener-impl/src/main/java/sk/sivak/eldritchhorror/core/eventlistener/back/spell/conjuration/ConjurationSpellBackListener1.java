package sk.sivak.eldritchhorror.core.eventlistener.back.spell.conjuration;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

public class ConjurationSpellBackListener1 extends AbstractSpellBackListener {

    public ConjurationSpellBackListener1(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        if (getTestData().hasRolledAny4()) {
            TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getService().release();
            });
        } else {
            TypewriterUtils.confirmInfos("[#BAD]Discard this card[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                justDiscard();
                ServicePlatform.get().getService().release();
            });
        }
    }

    private void justDiscard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setSpellInfo(getSpellInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard " +getSpellInfo().getName()+ " Spell");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displaySpell = ServicePlatform.get().getGameService().showCard(showCardRequest);

        displaySpell.subscribe(response ->
                ServicePlatform.get().getService().discardSpellFromInvestigator(getActiveInvestigatorId(), getSpellInfo()));
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Sanity.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        TypewriterUtils.confirmInfos("[#GOOD]Gain one additional Item or Trinket\nfrom the reserve[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();

            List<AssetInfo> itemsOrTrinkets = getData("itemsOrTrinkets");
            if (itemsOrTrinkets.isEmpty()) {
                Question<Object> question = new Question<>();
                question.setTitle("There are no Items or Trinkets.");
                question.setOptions(Question.Option.okOption);
                ServicePlatform.get().getGameService().ask(question).subscribe();
                return;
            }

            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setAvailableCards(itemsOrTrinkets);
            selectCardData.setHideText("Display Reserve?");
            selectCardData.setTitleText("Select Trinket or Item");
            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(selectedCard -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getGameService().gainAsset((AssetInfo) selectedCard);
                ServicePlatform.get().getCardService().refillReserve();
                ServicePlatform.get().getService().release();
            });

            ServicePlatform.get().getService().release();
        });
    }

    private List<AssetInfo> findItemsOrTrinkets(List<AssetInfo> reserve) {
        Predicate<AssetInfo> predicate = assetInfo ->
                assetInfo.getTraits().contains(AssetTrait.ITEM) ||
                        assetInfo.getTraits().contains(AssetTrait.TRINKET);

        return Stream.collectToList(reserve, predicate);
    }
}
