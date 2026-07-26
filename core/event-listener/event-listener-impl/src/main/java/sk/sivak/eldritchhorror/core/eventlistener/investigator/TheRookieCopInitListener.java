package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.encounter.EncounterType;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.artifact.PallidMaskListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;
import sk.sivak.eldritchhorror.core.eventtype.data.GainAssetFromDeckData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

/**
 * @author msivak
 */
public class TheRookieCopInitListener extends AbstractInvestigatorInitListener {

    private HideCombatEncountersListener hideCombatEncountersListener;
    private ImproveWillListener improveWillListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_ROOKIE_COP;
    }


    @Override
    protected void initInvestigator() {
        improveWillListener = new ImproveWillListener();
        hideCombatEncountersListener = new HideCombatEncountersListener();

        ServicePlatform.get().getEventQueue().addAfterEventListener(improveWillListener, BeforeAfterEvent.SHOW_PHASE);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(hideCombatEncountersListener, BeforeAfterEvent.DISABLE_EPIC_COMBAT_ENCOUNTERS);

        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.CARBINE_RIFLE);
        getService().convertFromTo(GainAssetFromDeckData.class, InvestigatorId.class, (in) -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        hideCombatEncountersListener = new HideCombatEncountersListener();
        ServicePlatform.get().getEventQueue().addBeforeEventListener(hideCombatEncountersListener, BeforeAfterEvent.DISABLE_EPIC_COMBAT_ENCOUNTERS);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(improveWillListener);
        ServicePlatform.get().getEventQueue().unregisterListener(hideCombatEncountersListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class ImproveWillListener extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                InvestigatorId activeInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(InvestigatorId.THE_ROOKIE_COP);
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                Question<Object> question = new Question<>();
                question.setTitle("The Rookie Cop starts with improved Will.");
                question.setPortraitBeforeTitle(InvestigatorId.THE_ROOKIE_COP);
                question.setOptions(Question.Option.okOption);
                ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getInvestigatorService().improveSkill(Stat.WILL);
                    ServicePlatform.get().getEventQueue().unregisterListener(this);
                    ServicePlatform.get().getService().convertTo(Void.class, () -> null);
                    ServicePlatform.get().getService().release();
                });
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(activeInvestigatorId);
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
        }
    }

    private class HideCombatEncountersListener extends EventListenerImpl<AvailableEncounters> {

        @Override
        public void onNotify(AvailableEncounters eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getService().hold();
                getEventAction(eventData).run();
                ServicePlatform.get().getService().release();
            });
        }

        private Runnable getEventAction(AvailableEncounters input) {
            return () -> {
                if (!Stream.anyMatch(input.getEncounters(), encounter -> encounter.getEncounterType() == EncounterType.COMBAT)) {
                    return;
                }
                InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
                if (ServicePlatform.get().getPerformedEncounters().hasEncounteredAnything(activeInvestigator.getInfo().getInvestigatorId())) {
                    return;
                }
                if (activeInvestigator.getInfo().getInvestigatorId() == InvestigatorId.THE_ROOKIE_COP) {
                    return;
                }

                LocationId rookieCopLocation = ServicePlatform.get().getInvestigators().getInvestigator(InvestigatorId.THE_ROOKIE_COP).getCurrentLocationId();
                if (activeInvestigator.getCurrentLocationId() != rookieCopLocation) {
                    return;
                }

                Question<Boolean> question = new Question<>();
                question.setOptions(Question.Option.noYesOptions);
                question.setTitle("Ignore Monsters?");
                question.setPortraitBeforeTitle(InvestigatorId.THE_ROOKIE_COP);
                ServicePlatform.get().getGameService().ask(question).subscribe(answer -> withAnswer(answer, input));
            };
        }

        private void withAnswer(Answer<Boolean, Object> response, AvailableEncounters input) {
            if (!response.getResponseData()) {
                ServicePlatform.get().getService().convertTo(AvailableEncounters.class, () -> input);
                return;
            }
            input.removeEncounters(encounter -> encounter.getEncounterType() == EncounterType.COMBAT);
            ServicePlatform.get().getService().convertTo(AvailableEncounters.class, () -> input);
        }

        @Override
        public Class<AvailableEncounters> getDataClass() {
            return AvailableEncounters.class;
        }
    }


}
