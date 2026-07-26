package sk.sivak.eldritchhorror.core.eventlistener.back.spell.poisonmist;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.LinkedList;
import java.util.List;

public class PoisonMistSpellBackListener2 extends AbstractSpellBackListener {

    public PoisonMistSpellBackListener2(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));

    }

    private void on0() {
        if (getTestData().hasRolledAny1()) {
            TypewriterUtils.confirmInfos("[#BAD]Discard this card[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                justDiscard();
                ServicePlatform.get().getService().release();
            });
        } else {
            TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
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
                ServicePlatform.get().getService().discardSpellFromInvestigator(
                        ServicePlatform.get().getInvestigators().getActiveInvestigatorId(), getSpellInfo()));
    }


    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Sanity[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().release();
        });
    }

}
