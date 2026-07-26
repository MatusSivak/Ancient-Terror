package sk.sivak.eldritchhorror.core.eventlistener.back.spell.conjuration;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

public class ConjurationSpellBackListener2 extends AbstractSpellBackListener {

    public ConjurationSpellBackListener2(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2, true));
    }

    private void on0() {
        new DiscardThisSpellUnless(getSpellInfo(), getActiveInvestigatorId()).discardOneItemAsset();
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
        TypewriterUtils.confirmInfos("[#GOOD]You may gain any number of Items or Trinkets\n" +
                "from the reserve with total value\n" +
                "equal to or less than your test result[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            List<AssetInfo> itemsOrTrinkets = findItemsOrTrinkets(ServicePlatform.get().getAssetDeck().getReserve());
            selectSingleItemOrTrinket(getTestData().getScore(), itemsOrTrinkets);
            ServicePlatform.get().getService().release();
        });
    }

    private void selectSingleItemOrTrinket(int score, List<AssetInfo> itemsOrTrinkets) {
        ServicePlatform.get().getService().hold();

        IterableUtils.removeIf(itemsOrTrinkets, itemOrTrinket -> itemOrTrinket.getCost() > score);

        if (itemsOrTrinkets.isEmpty()) {
            ServicePlatform.get().getService().release();
            return;
        }
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(itemsOrTrinkets);
        selectCardData.setHideText("Display Reserve?");
        selectCardData.setTitleText("Select Trinket or Item ("+score+")");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(selectedCard -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getGameService().gainAsset((AssetInfo) selectedCard);
            ServicePlatform.get().getService().addEventCommand(in -> {
                itemsOrTrinkets.remove(selectedCard);
                selectSingleItemOrTrinket(score - ((AssetInfo) selectedCard).getCost(), itemsOrTrinkets);
            });
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }

    private List<AssetInfo> findItemsOrTrinkets(List<AssetInfo> reserve) {
        Predicate<AssetInfo> predicate = assetInfo ->
                assetInfo.getTraits().contains(AssetTrait.ITEM) ||
                        assetInfo.getTraits().contains(AssetTrait.TRINKET);

        return Stream.collectToList(reserve, predicate);
    }
}
