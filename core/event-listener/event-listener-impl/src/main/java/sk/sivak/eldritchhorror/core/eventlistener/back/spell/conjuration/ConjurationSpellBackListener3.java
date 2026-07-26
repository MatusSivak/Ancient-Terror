package sk.sivak.eldritchhorror.core.eventlistener.back.spell.conjuration;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.List;

public class ConjurationSpellBackListener3 extends AbstractSpellBackListener {

    public ConjurationSpellBackListener3(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2, true));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]Discard this card and\neach Item possession you have[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            discardThisCard();
            for (CardInfo itemPossession : EncounterUtils.getPossession(getActiveInvestigatorId(), AssetTrait.ITEM)) {
                EncounterUtils.onSelectCardToDiscard(itemPossession);
            }
            ServicePlatform.get().getService().release();
        });
    }


    private void discardThisCard() {
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
        TypewriterUtils.noYesQuestion("[#GOOD]Gain one Artifact instead?[]", () -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            runMainSpellAction();
        }, () -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getGameService().gainArtifact();
        });
    }
}
