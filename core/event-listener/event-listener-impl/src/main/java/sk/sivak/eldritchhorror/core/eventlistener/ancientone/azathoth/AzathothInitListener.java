package sk.sivak.eldritchhorror.core.eventlistener.ancientone.azathoth;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenColor;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AncientOneInitListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.model.MythosDeckRead;

import java.util.Collections;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;
import static sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent.ADVANCE_OMEN;

/**
 * @author msivak
 */
public class AzathothInitListener extends AncientOneInitListener {

    private static final Logger logger = LogManager.getLogger(AzathothInitListener.class);

    @Override
    public void register() {

        if (!GoogleServicesHolder.isTutorialPassed()) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getMythosDeck().initSelectedMythosCards(
                    new MythosDeckRead.MythosStageImpl(1,2,1),
                    new MythosDeckRead.MythosStageImpl(2,3,1),
                    new MythosDeckRead.MythosStageImpl(2,4,0));
            ServicePlatform.get().getDoomOmenService().addTokenToOmenTrack(OmenId.NORTH);
            ServicePlatform.get().getService().release();
            ServicePlatform.get().getEventQueue().addAfterEventListener(new AfterAdvanceOmenListener(), ADVANCE_OMEN);
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getGameService().showAncientOneCard();
        ServicePlatform.get().getMythosDeck().initSelectedMythosCards(
                new MythosDeckRead.MythosStageImpl(1,2,1),
                new MythosDeckRead.MythosStageImpl(2,3,1),
                new MythosDeckRead.MythosStageImpl(2,4,0));
        ServicePlatform.get().getDoomOmenService().addTokenToOmenTrack(OmenId.NORTH);
        ServicePlatform.get().getService().release();

        ServicePlatform.get().getEventQueue().addAfterEventListener(new AfterAdvanceOmenListener(), ADVANCE_OMEN);
        ServicePlatform.get().getEventQueue().addAfterEventListener(new AfterAdvanceDoomListener(), BeforeAfterEvent.ADVANCE_DOOM);

    }

    @Override
    public void load() {
        ServicePlatform.get().getEventQueue().addAfterEventListener(new AfterAdvanceOmenListener(), ADVANCE_OMEN);
        ServicePlatform.get().getEventQueue().addAfterEventListener(new AfterAdvanceDoomListener(), BeforeAfterEvent.ADVANCE_DOOM);
    }

    private class AfterAdvanceDoomListener extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            if (ServicePlatform.get().getDoomTrack().getCurrentDoom() > 0) {
                return;
            }
            ServicePlatform.get().getService().hold();
            GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.GAME_ENDED, "AZATHOTH", "defeat");
            String endGameText = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getEndGameText();
            ServicePlatform.get().getTutorialService().displayChalkboard(endGameText,
                    VIEWPORT_WIDTH/2, VIEWPORT_HEIGHT/2, true);
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getGameService().restartGame() ;
            });
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }
    private class AfterAdvanceOmenListener extends EventListenerImpl<OmenInfo> {

        @Override
        public void onNotify(OmenInfo eventData) {
            if (eventData.getOmenColor() != OmenColor.GREEN) {
                return;
            }
            if (eventData.getTokensCount() == 0) {
                return;
            }
            int tokensCount = eventData.getTokensCount();
            Question<Object> question = new Question<>();
            question.setTitle("Doom advances for each Orb on the Green Omen.");
            question.setPortraitBeforeTitle(AncientOneId.AZATHOTH);
            question.setOptions(Collections.singletonList(
                    new Question.Option<>("OK", true)));
            ServicePlatform.get().getGameService().ask(question).subscribe(x-> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getDoomOmenService().advanceDoom(tokensCount);
                ServicePlatform.get().getService().convertTo(OmenInfo.class, () -> eventData);
                ServicePlatform.get().getService().release();

            });

        }

        @Override
        public Class<OmenInfo> getDataClass() {
            return OmenInfo.class;
        }
    }
}
