package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;
import sk.sivak.eldritchhorror.core.model.CluePoolRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

/**
 * @author msivak
 */
public class TheActressActionListener extends AbstractActionPhaseListener<TheActressActionListener.TheActressAction> {

    private static final Logger logger = LogManager.getLogger(TheActressActionListener.class);

    public TheActressActionListener() {
        name = "Transfer\nSkill Bonus";
    }

    @Override
    protected String getAdditionalInfo() {
        if (isDisabled()) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        InvestigatorRead actress = ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ACTRESS);
        for (Stat stat : Stat.values()) {
            stringBuilder.append(stat.prettyString());
            stringBuilder.append(" (");
            stringBuilder.append(actress.getStatBonus(stat));
            stringBuilder.append(")\n");
        }
        return stringBuilder.toString();
    }

    @Override
    protected String getGeneralDescription() {
        return "Spend any number\n" +
                "of Skill Improvements.\n" +
                "Then improve 1 Skill\n" +
                "of your choice\n" +
                "for each Improvement spent.";
    }

    @Override
    protected TheActressActionListener.TheActressAction createAction() {
        return new TheActressActionListener.TheActressAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_ACTRESS;
    }

    @Override
    protected boolean isDisabled() {
        InvestigatorRead actress = ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ACTRESS);
        int totalStatBonuses = 0;
        for (Stat stat : Stat.values()) {
            totalStatBonuses += actress.getStatBonus(stat);
        }
        if (totalStatBonuses == 0) {
            disabledReason = "No Skill improved.";
            return true;
        }
        return false;
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.INVESTIGATOR;
    }

    @Override
    protected String getTexturePath() {
        return "investigator/ICON_THE_ACTRESS.png";
    }

    @Override
    protected float getScaleDownPercentage() {
        return 0.75f;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheActressAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing actress action...");
            ServicePlatform.get().getService().hold();
            LoseImprovementData loseImprovementData = new LoseImprovementData(null, 1);
            ServicePlatform.get().getInvestigatorService().loseImprovement(loseImprovementData);
            ServicePlatform.get().getService().addEventCommand(loseImprovement -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().convertTo(Stat.class, () -> null);
                ServicePlatform.get().getInvestigatorService().improveSkill(null);
                ServicePlatform.get().getService().addEventCommand(this::afterSkillImprovement);
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getTokenService().convertToNull();
            ServicePlatform.get().getService().release();
        }

        private void afterSkillImprovement(Stat improvedSkill) {
            InvestigatorRead actress = ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ACTRESS);
            int remainingStatBonuses = 0;
            for (Stat stat : Stat.values()) {
                if (stat == improvedSkill) {
                    continue;
                }
                remainingStatBonuses += actress.getStatBonus(stat);
            }
            if (remainingStatBonuses == 0) {
                return;
            }
            if (actress.getStatBonus(improvedSkill) == 2) {
                return; // OK
            }
            Question<Boolean> question = new Question<>();
            question.setOptions(Question.Option.noYesOptions);
            question.setPortraitBeforeTitle(InvestigatorId.THE_ACTRESS);
            question.setTitle("Spend one more Skill Improvement?");
            ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                if (answer.getResponseData()) { // OK
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getInvestigatorService().loseImprovement(new LoseImprovementData(null, 1));
                    ServicePlatform.get().getInvestigatorService().improveSkill(improvedSkill);
                    ServicePlatform.get().getService().release();
                } // OK
            });
        }
    }
}
