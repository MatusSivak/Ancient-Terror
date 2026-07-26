package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.monster.DefeatMonsterData;

import java.util.Collections;

/**
 * @author msivak
 */
public class TheBountyHunterInitListener extends AbstractInvestigatorInitListener {

    private TheBountyHunterPassiveListener theBountyHunterPassiveListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_BOUNTY_HUNTER;
    }

    @Override
    protected void initInvestigator() {
        theBountyHunterPassiveListener = new TheBountyHunterPassiveListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(theBountyHunterPassiveListener, BeforeAfterEvent.DEFEAT_MONSTER);
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.HANDCUFFS);
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        theBountyHunterPassiveListener = new TheBountyHunterPassiveListener();
        ServicePlatform.get().getEventQueue().addAfterEventListener(theBountyHunterPassiveListener, BeforeAfterEvent.DEFEAT_MONSTER);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(theBountyHunterPassiveListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class TheBountyHunterPassiveListener extends EventListenerImpl<DefeatMonsterData> {

        @Override
        public void onNotify(DefeatMonsterData eventData) {
            if (!eventData.isInCombat()) {
                return;
            }
            if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != InvestigatorId.THE_BOUNTY_HUNTER) {
                return;
            }
            if (ServicePlatform.get().getInvestigators().getActiveInvestigator().getFocusTokens() >= 2) {
                return;
            }

            askQuestion().subscribe(answer -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getTokenService().gainFocus();
                ServicePlatform.get().getService().convertTo(DefeatMonsterData.class, () -> eventData);
                ServicePlatform.get().getService().release();
            });
        }

        private Single<Answer<Boolean, Object>> askQuestion() {
            Question<Boolean> question = new Question<>();

            question.setTitle("Gain 1 Focus for defeating a Monster.");
            question.setPortraitBeforeTitle(InvestigatorId.THE_BOUNTY_HUNTER);
            question.setOptions(Collections.singletonList(
                    new Question.Option<>("OK", true)

            ));
            return ServicePlatform.get().getGameService().ask(question);
        }

        @Override
        public Class<DefeatMonsterData> getDataClass() {
            return DefeatMonsterData.class;
        }
    }
}
