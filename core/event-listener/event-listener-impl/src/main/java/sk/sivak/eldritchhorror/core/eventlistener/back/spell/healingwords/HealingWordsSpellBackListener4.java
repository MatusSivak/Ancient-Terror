package sk.sivak.eldritchhorror.core.eventlistener.back.spell.healingwords;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class HealingWordsSpellBackListener4 extends AbstractSpellBackListener {

    public HealingWordsSpellBackListener4(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]Target loses one Health[]","[#BAD]Target loses one Sanity[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            changeToResting();
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on1() {
        TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            changeToResting();
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        TypewriterUtils.confirmInfos("[#GOOD]Target may discard a Cursed Condition.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            changeToResting();
            discardCursedCondition();
            ServicePlatform.get().getService().release();
        });
    }

    private void discardCursedCondition() {
        boolean isCursed = ServicePlatform.get().getConditionsDeck().hasCondition(getData("restingInvestigator"), ConditionId.CURSED);
        if (!isCursed) {
            return;
        }
        ConditionInfo cursedCondition = ServicePlatform.get().getConditionsDeck().getCondition(getData("restingInvestigator"), ConditionId.CURSED);
        ShowCardRequest request = new ShowCardRequest();
        request.setHideType(HideType.DISCARD_ALWAYS);
        request.setConditionInfo(cursedCondition);
        request.setTitle("Discard Cursed Condition");
        request.setDisplayAssetResponseType(DisplayAssetResponseType.NOTHING);
        ServicePlatform.get().getGameService().showCard(request).subscribe(out -> {
            ServicePlatform.get().getService().discardConditionFromInvestigator(getData("restingInvestigator"), cursedCondition);
        });
    }

    private void changeToResting() {
        if (getData("spellOwnerId") != getData("restingInvestigator")) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("restingInvestigator"));
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        }
    }


}

