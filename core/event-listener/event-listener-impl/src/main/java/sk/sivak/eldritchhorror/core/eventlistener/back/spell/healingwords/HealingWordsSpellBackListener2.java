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

public class HealingWordsSpellBackListener2 extends AbstractSpellBackListener {

    public HealingWordsSpellBackListener2(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]Target loses one Health.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            changeToResting();
            ServicePlatform.get().getTokenService().loseHealth(1);
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
        TypewriterUtils.confirmInfos("[#GOOD]Target may discard one\nIllness or Injury Condition.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            changeToResting();
            selectIllnessOrInjuryToDiscard();
            ServicePlatform.get().getService().release();
        });
    }

    private void selectIllnessOrInjuryToDiscard() {
        List<ConditionInfo> illnessesOrInjuries = new LinkedList<>();
        illnessesOrInjuries.addAll(ServicePlatform.get().getConditionsDeck().getCondition(getData("restingInvestigator"), ConditionTrait.ILLNESS));
        illnessesOrInjuries.addAll(ServicePlatform.get().getConditionsDeck().getCondition(getData("restingInvestigator"), ConditionTrait.INJURY));
        SelectCardData selectCardData = new SelectCardData();
        selectCardData.setAvailableCards(illnessesOrInjuries);
        selectCardData.setHideText("Display Inventory?");
        selectCardData.setTitleText("Select Illness or Injury Condition");
        ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscard);
    }

    private void changeToResting() {
        if (getData("spellOwnerId") != getData("restingInvestigator")) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(getData("restingInvestigator"));
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        }
    }


}

