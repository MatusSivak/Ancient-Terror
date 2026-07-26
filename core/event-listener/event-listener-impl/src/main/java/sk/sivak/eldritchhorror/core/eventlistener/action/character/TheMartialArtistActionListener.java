package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author msivak
 */
public class TheMartialArtistActionListener extends AbstractActionPhaseListener<TheMartialArtistActionListener.TheMartialArtistAction> {

    private static final Logger logger = LogManager.getLogger(TheMartialArtistActionListener.class);

    public TheMartialArtistActionListener() {
        name = "Spend and\nRecover";
    }

    @Override
    protected String getAdditionalInfo() {
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "Spend any number of\n" +
                "Health or Sanity,\n" +
                "then recover\n" +
                "an equal number of\n" +
                "Health or Sanity.";
    }

    @Override
    protected TheMartialArtistAction createAction() {
        return new TheMartialArtistAction();
    }

    @Override
    protected ActionButtonData.ActionButtonId getActionButtonId() {
        return ActionButtonData.ActionButtonId.INVESTIGATOR;
    }

    @Override
    protected String getTexturePath() {
        return "investigator/THE_MARTIAL_ARTIST.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_MARTIAL_ARTIST;
    }

    @Override
    protected boolean isDisabled() {
        InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        if (activeInvestigator.getCurrentSanity() + activeInvestigator.getCurrentHealth() <= 2) {
            disabledReason = "Cannot spend Health or Sanity";
            return true;
        }
        int maxTokens = activeInvestigator.getInfo().getMaxSanity() + activeInvestigator.getInfo().getMaxHealth();
        if (activeInvestigator.getCurrentSanity() + activeInvestigator.getCurrentHealth() == maxTokens) {
            disabledReason = "Cannot recover Health or Sanity";
            return true;
        }
        return false;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheMartialArtistAction extends AbstractActionPhaseAction {

        private static final String SANITY = "Sanity";
        private static final String HEALTH = "Health";

        private String tokenType;
        private Integer amount;
        @Override
        public void execute() {
            InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
            if (activeInvestigator.getCurrentHealth() <= 1 ||
                    activeInvestigator.getCurrentSanity() == activeInvestigator.getInfo().getMaxSanity()) {
                Answer<String, Object> answer = new Answer<>();
                answer.setResponseData(SANITY);
                onAnswerTokenType(answer);
            } else if (activeInvestigator.getCurrentSanity() <= 1 ||
                    activeInvestigator.getCurrentHealth() == activeInvestigator.getInfo().getMaxHealth()) {
                Answer<String, Object> answer = new Answer<>();
                answer.setResponseData(HEALTH);
                onAnswerTokenType(answer);
            } else {
                Question<String> question = new Question<>();
                question.setOptions(Arrays.asList(
                        new Question.Option<>(HEALTH, HEALTH, 0x800000ff),
                        new Question.Option<>(SANITY, SANITY, 0x000080ff)));
                question.setTitle("What do you want to spend?");
                ServicePlatform.get().getGameService().ask(question).subscribe(this::onAnswerTokenType);
            }
        }

        private void onAnswerTokenType(Answer<String, Object> answer) {
            this.tokenType = answer.getResponseData();
            int maxCanSpend;
            int maxCanRecover;
            InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
            if (HEALTH.equals(tokenType)) {
                maxCanSpend = activeInvestigator.getCurrentHealth() - 1;
                maxCanRecover = activeInvestigator.getInfo().getMaxSanity() - activeInvestigator.getCurrentSanity();
            } else {
                maxCanSpend = activeInvestigator.getCurrentSanity() - 1;
                maxCanRecover = activeInvestigator.getInfo().getMaxHealth() - activeInvestigator.getCurrentHealth();
            }

            LinkedList<Question.Option<Integer>> options = new LinkedList<>();
            for (int i = 1; i <= Math.min(maxCanSpend, maxCanRecover); i++) {
                options.add(new Question.Option<>(""+i, i, HEALTH.equals(tokenType) ? 0x800000ff : 0x000080ff));
            }
            Question<Integer> question = new Question<>();
            question.setOptions(options);
            question.setTitle("How many "+tokenType+" do you want to spend?");
            ServicePlatform.get().getGameService().ask(question).subscribe(this::onAnswerAmount);
        }

        private void onAnswerAmount(Answer<Integer, Object> answer) {
            this.amount = answer.getResponseData();
            if (HEALTH.equals(tokenType)) {
                ServicePlatform.get().getTokenService()
                        .spend(0,0, amount,0).subscribe(this::onSpend);
            } else {
                ServicePlatform.get().getTokenService()
                        .spend(0,0, 0,amount).subscribe(this::onSpend);
            }

        }

        private void onSpend(SpendData spendData) {
            ServicePlatform.get().getService().hold();
            spendData.pay();
            if (HEALTH.equals(tokenType)) {
                ServicePlatform.get().getTokenService().gainSanity(amount);
            } else {
                ServicePlatform.get().getTokenService().gainHealth(amount);
            }
            ServicePlatform.get().getService().convertToNull();
            ServicePlatform.get().getService().release();
        }

    }
}
