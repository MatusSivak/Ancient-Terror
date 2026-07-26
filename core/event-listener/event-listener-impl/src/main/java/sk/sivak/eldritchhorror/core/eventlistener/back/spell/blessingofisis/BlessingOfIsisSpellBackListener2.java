package sk.sivak.eldritchhorror.core.eventlistener.back.spell.blessingofisis;

import java8.features.util.IterableUtils;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class BlessingOfIsisSpellBackListener2 extends AbstractSpellBackListener {

    public BlessingOfIsisSpellBackListener2(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Health.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseHealth(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#GOOD]Each investigator on your space\n" +
                "that does not have a Blessed Condition\n" +
                "may gain a Blessed Condition.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            for (InvestigatorRead availableInvestigator : findAvailableInvestigators()) {
                changeActiveInvestigator(availableInvestigator.getInfo().getInvestigatorId());
                Question<Boolean> question = new Question<>();
                question.setOptions(Question.Option.noYesOptions);
                question.setTitle("Gain a Blessed Condition?");
                ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                    if (!answer.getResponseData()) {
                        return;
                    }
                    ServicePlatform.get().getGameService().gainCondition(ConditionId.BLESSED);
                });
            }
            InvestigatorId spellOwnerId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getService().hold();
                changeActiveInvestigator(spellOwnerId);
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
    }

    private void changeActiveInvestigator(InvestigatorId newActiveInvestigator) {
        if (getActiveInvestigatorId() != newActiveInvestigator) {
            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(newActiveInvestigator);
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
        }
    }

    private List<? extends InvestigatorRead> findAvailableInvestigators() {
        List<? extends InvestigatorRead> allInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        IterableUtils.removeIf(allInvestigators, investigator -> {
            boolean isOnDifferentLocation = investigator.getCurrentLocationId() != ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
            boolean hasBlessedCondition = ServicePlatform.get().getConditionsDeck().hasCondition(investigator.getInfo().getInvestigatorId(), ConditionId.BLESSED);
            return isOnDifferentLocation || hasBlessedCondition;
        });
        return allInvestigators;
    }
}
