package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.PathType;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

public class TheButlerInitListener extends AbstractInvestigatorInitListener {
    private boolean preparedForTravel;
    private EventListenerImpl<PathType> selectedTicket;
    private EventListenerImpl<Void> gainedTicket;
    @Override protected InvestigatorId getInvestigatorId() { return InvestigatorId.THE_BUTLER; }
    @Override protected void initInvestigator() {
        justRegisterListeners();
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.LUCKY_CIGARETTE_CASE);
        ServicePlatform.get().getTokenService().gainClueFromPool(getInvestigatorId());
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }
    @Override public void justRegisterListeners() {
        selectedTicket = new EventListenerImpl<PathType>() {
            @Override public Class<PathType> getDataClass() { return PathType.class; }
            @Override public void onNotify(PathType type) {
                preparedForTravel = type != null && ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId()
                        == ServicePlatform.get().getInvestigators().getInvestigator(getInvestigatorId()).getCurrentLocationId();
            }
        };
        gainedTicket = new EventListenerImpl<Void>() {
            @Override public Class<Void> getDataClass() { return Void.class; }
            @Override public void onNotify(Void data) {
                if (!preparedForTravel) return;
                preparedForTravel = false;
                Question<PathType> question = new Question<>();
                question.setTitle("Carson: gain one additional travel ticket");
                question.setPortraitBeforeTitle(getInvestigatorId());
                question.setOptions(java.util.Arrays.asList(new Question.Option<>("Ship", PathType.SHIP), new Question.Option<>("Train", PathType.TRAIN)));
                ServicePlatform.get().getGameService().ask(question).subscribe(answer ->
                        ServicePlatform.get().getBasicActionService().gainTravelTicket(answer.getResponseData()));
            }
        };
        getEventQueue().addAfterEventListener(selectedTicket, BeforeAfterEvent.SELECT_TRAVEL_TICKET);
        getEventQueue().addAfterEventListener(gainedTicket, BeforeAfterEvent.GAIN_TRAVEL_TICKET);
    }
    @Override public void unregisterInvestigator() {
        getEventQueue().unregisterListener(selectedTicket);
        getEventQueue().unregisterListener(gainedTicket);
        getEventQueue().unregisterListener(this);
        preparedForTravel = false;
    }
}

