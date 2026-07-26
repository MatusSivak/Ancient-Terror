package sk.sivak.eldritchhorror.core.eventlistener.back.spell.healingwords;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;

import java.util.LinkedList;
import java.util.List;

public class HealingWordsSpellBackListener3 extends AbstractSpellBackListener {

    public HealingWordsSpellBackListener3(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]Target loses one Sanity.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            changeToResting();
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
        TypewriterUtils.confirmInfos("[#GOOD]Target may discard one Madness Condition.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            changeToResting();
            selectMadnessCondition();
            ServicePlatform.get().getService().release();
        });
    }

    private void selectMadnessCondition() {
        List<ConditionInfo> madnesses = ServicePlatform.get().getConditionsDeck().getCondition(getData("restingInvestigator"), ConditionTrait.MADNESS);
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(madnesses);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Madness Condition");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }

    private void changeToResting() {
        if (getData("spellOwnerId") != getData("restingInvestigator")) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("restingInvestigator"));
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        }
    }


}

