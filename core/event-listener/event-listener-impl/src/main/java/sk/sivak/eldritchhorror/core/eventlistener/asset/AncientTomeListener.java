package sk.sivak.eldritchhorror.core.eventlistener.asset;

import sk.sivak.eldritchhorror.core.constants.action.CardInfoAware;
import sk.sivak.eldritchhorror.core.constants.asset.AncientTomeAsset;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseAction;
import sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;

import java.util.Arrays;
import java.util.List;

public class AncientTomeListener extends AbstractAssetListener<AncientTomeAsset> {

    private AncientTomeActionListener ancientTomeActionListener;
    private EnableCardListener enableCardListener;

    @Override
    protected void register() {
        ancientTomeActionListener = new AncientTomeActionListener();
        getEventQueue().addBeforeEventListener(ancientTomeActionListener, BeforeAfterEvent.FIND_ACTIONS);
        enableCardListener = new EnableCardListener(getAssetInfo());
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(ancientTomeActionListener, enableCardListener);
    }

    private enum Reward { NONE, CLUE, SPELL }

    private class AncientTomeActionListener extends AbstractActionPhaseListener<AncientTomeActionListener.AncientTomeAction> {

        public AncientTomeActionListener() {
            name = "Ancient\nTome";
        }

        @Override
        protected String getAdditionalInfo() {
            return null;
        }

        @Override
        protected String getGeneralDescription() {
            return cardActionText(getAssetInfo().getDescription());
        }

        @Override
        protected String getTexturePath() {
            return "card/asset/ANCIENT_TOME.jpg";
        }

        @Override
        protected float getScaleDownPercentage() {
            return 1.0f;
        }

        @Override
        protected boolean getNeedsMask() {
            return true;
        }

        @Override
        protected AncientTomeAction createAction() {
            return new AncientTomeAction();
        }

        @Override
        protected boolean isVisible() {
            return getInvestigators().getActiveInvestigatorId() == investigatorId;
        }

        @Override
        protected boolean isDisabled() {
            if (!enabled) {
                disabledReason = "Action already performed this round";
                return true;
            }
            return false;
        }

        @Override
        protected boolean isNotRecommended() {
            return false;
        }

        protected class AncientTomeAction extends AbstractActionPhaseAction implements CardInfoAware<AncientTomeAsset> {

            @Override
            public void execute() {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().disableCard(getAssetInfo());
                ServicePlatform.get().getTestService().test(Stat.LORE, 0, 1).subscribe(this::afterTest);
                ServicePlatform.get().getService().release();
            }

            private void afterTest(TestData testData) {
                if (!testData.isSuccessful()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }
                Question<Reward> question = new Question<>();
                question.setTitle("Spend 1 Sanity to gain a reward?");
                question.addOption(new Question.Option<>("No", Reward.NONE, 0x800000ff))
                        .addOption(new Question.Option<>("1 Clue", Reward.CLUE, 0x008000ff))
                        .addOption(new Question.Option<>("1 Spell", Reward.SPELL, 0x008000ff));
                ServicePlatform.get().getGameService().ask(question).subscribe(this::onAnswer);
            }

            private void onAnswer(Answer<Reward, Object> answer) {
                Reward reward = answer.getResponseData();
                if (reward == null || reward == Reward.NONE) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }
                ServicePlatform.get().getTokenService().spend(0, 0, 0, 1).subscribe(spendData -> onSpend(spendData, reward));
            }

            private void onSpend(SpendData spendData, Reward reward) {
                if (!spendData.hasEnough()) {
                    ServicePlatform.get().getInvestigatorService().convertToNull();
                    return;
                }
                ServicePlatform.get().getService().hold();
                spendData.pay();
                if (reward == Reward.CLUE) {
                    ServicePlatform.get().getTokenService().gainClueFromPool();
                } else {
                    ServicePlatform.get().getGameService().gainSpell();
                }
                ServicePlatform.get().getService().convertToNull();
                ServicePlatform.get().getService().release();
            }

            @Override
            public AncientTomeAsset getCardInfo() {
                return getAssetInfo();
            }
        }
    }
}
