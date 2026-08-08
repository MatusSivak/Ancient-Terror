package sk.sivak.eldritchhorror.core.eventlistener.action.character;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.action.ActionButtonData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.LinkedList;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

/**
 * @author msivak
 */
public class TheBountyHunterActionListener extends AbstractActionPhaseListener<TheBountyHunterActionListener.TheBountyHunterAction> {

    private static final Logger logger = LogManager.getLogger(TheBountyHunterActionListener.class);

    public TheBountyHunterActionListener() {
        name = "Trade\nFocus";
    }

    @Override
    protected String getAdditionalInfo() {
        return null;
    }

    @Override
    protected String getGeneralDescription() {
        return "Test Influence. If you pass,\n" +
                "you may spend up to\n" +
                "2 Focus to gain 1 Clue\n" +
                "for each Focus spent.";
    }

    @Override
    protected TheBountyHunterActionListener.TheBountyHunterAction createAction() {
        return new TheBountyHunterActionListener.TheBountyHunterAction();
    }

    @Override
    protected boolean isVisible() {
        return getInvestigators().getActiveInvestigatorId() == InvestigatorId.THE_BOUNTY_HUNTER;
    }

    @Override
    protected boolean isDisabled() {
        int focusTokens = getActiveInvestigator().getFocusTokens();
        if (focusTokens <= 0) {
            disabledReason = "You do not have any Focus tokens.";
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
        return "investigator/ICON_THE_BOUNTY_HUNTER.png";
    }

    @Override
    protected boolean getNeedsScaleDown() {
        return true;
    }

    @Override
    protected boolean isNotRecommended() {
        return false;
    }

    protected class TheBountyHunterAction extends AbstractActionPhaseAction {

        @Override
        public void execute() {
            logger.info("Executing The Bounty Hunter Action...");
            ServicePlatform.get().getTestService().test(Stat.INFLUENCE, 0, JUST_ONE).subscribe(this::afterTest);
        }

        private void afterTest(TestData testData) {
            if (testData.isSuccessful()) {
                afterSuccessfulTest();
            } else {
                ServicePlatform.get().getService().convertToNull();
            }
        }

        private void afterSuccessfulTest() {
            ServicePlatform.get().getTokenService().canSpend(0, 1, 0, 0).subscribe(this::afterCanSpend);
        }

        private void afterCanSpend(SpendData spendData) {
            if (spendData.hasEnough()) {
                if (getActiveInvestigator().getFocusTokens() == 1) {
                    Answer<Integer, Object> answer = new Answer<>();
                    answer.setResponseData(1);
                    afterQuestion(answer);
                } else {
                    Question<Integer> question = new Question<>();
                    question.setTitle("How many Focus tokens do you want to spend?");
                    question.setOptions(new LinkedList<>());
                    for (int i = 1; i <= getActiveInvestigator().getFocusTokens(); i++) {
                        question.getOptions().add(new Question.Option<>("" + i, i));
                    }
                    ServicePlatform.get().getGameService().ask(question).subscribe(this::afterQuestion);
                }
            } else {
                ServicePlatform.get().getService().convertToNull();
            }
        }

        private void afterQuestion(Answer<Integer, Object> answer) {
            ServicePlatform.get().getService().hold();
            for (int i = 0; i < answer.getResponseData(); i++) {
                ServicePlatform.get().getTokenService().spend(0, 1, 0, 0).subscribe(spendData -> {
                    if (spendData.hasEnough()) {
                        ServicePlatform.get().getService().hold();
                        spendData.pay();
                        ServicePlatform.get().getTokenService().gainClueFromPool();
                        ServicePlatform.get().getService().release();
                    }
                });
            }
            ServicePlatform.get().getService().convertToNull();
            ServicePlatform.get().getService().release();


        }
    }
}
