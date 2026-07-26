package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.tracker.AnalyticsCategory;
import sk.sivak.eldritchhorror.core.constants.tracker.GoogleServicesHolder;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AncientOneInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_HEIGHT;
import static sk.sivak.eldritchhorror.core.constants.ViewProperties.VIEWPORT_WIDTH;

public class AwakenYogSothothInitListener extends AncientOneInitListener {

    private static final Logger logger = LogManager.getLogger(AwakenYogSothothInitListener.class);
    private ReckoningListener reckoningListener;
    private BeforeReplaceInvestigatorsListener beforeReplaceInvestigatorsListener;
    private BeforeAdvanceDoomListener beforeAdvanceDoomListener;

    @Override
    public void register() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getGameService().showAncientOneCard();
        ServicePlatform.get().getService().release();

        reckoningListener = new ReckoningListener();
        beforeReplaceInvestigatorsListener = new BeforeReplaceInvestigatorsListener();
        beforeAdvanceDoomListener = new BeforeAdvanceDoomListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_ANCIENT_ONE);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeReplaceInvestigatorsListener, BeforeAfterEvent.REPLACE_INVESTIGATORS);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);
    }

    @Override
    public void load() {
        reckoningListener = new ReckoningListener();
        beforeReplaceInvestigatorsListener = new BeforeReplaceInvestigatorsListener();
        beforeAdvanceDoomListener = new BeforeAdvanceDoomListener();

        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);
        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_ANCIENT_ONE);
        ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeReplaceInvestigatorsListener, BeforeAfterEvent.REPLACE_INVESTIGATORS);
    }

    private class BeforeReplaceInvestigatorsListener extends EventListenerImpl<Object> {

        @Override
        public void onNotify(Object eventData) {
            if (ServicePlatform.get().getInvestigators().getToBeReplacedInvestigators().isEmpty()) {
                return;
            }
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Defeated investigators can't be replaced!");
            question.setPortraitBeforeTitle(AncientOneId.YOG_SOTHOTH);

            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                if (ServicePlatform.get().getInvestigators().getPlayers() == 0) {
                    endGame("defeat");
                } else {
                    ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.REPLACE_INVESTIGATORS, null);
                }
                ServicePlatform.get().getService().release();
            });
        }



        @Override
        public Class<Object> getDataClass() {
            return Object.class;
        }
    }

    private class ReckoningListener extends EventListenerImpl<ReckoningFireType> {

        @Override
        public void onNotify(ReckoningFireType eventData) {
            if (eventData == ReckoningFireType.CHARGE) {
                return;
            }

            List<? extends InvestigatorRead> investigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
            List<InvestigatorId> investigatorsOnGates = new LinkedList<>();
            for (InvestigatorRead investigator : investigators) {
                if (ServicePlatform.get().getGateStackRead().isGateAtLocation(investigator.getCurrentLocationId())) {
                    investigatorsOnGates.add(investigator.getInfo().getInvestigatorId());
                }
            }

            if (investigatorsOnGates.isEmpty()) {
                return;
            }

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getDoomOmenService().onTriggeredReckoning();
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Yog-Sothoth's power rises for each investigator on a Gate.");
            question.setPortraitBeforeTitle(AncientOneId.YOG_SOTHOTH);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {

                for (InvestigatorId investigatorsOnGate : investigatorsOnGates) {
                    ServicePlatform.get().getService().addEventCommand(in -> {
                        ServicePlatform.get().getService().hold();
                        if (ServicePlatform.get().getSpellsDeck().getSpells(investigatorsOnGate).isEmpty()) { // NO SPELLS
                            LocationId currentLocationId = ServicePlatform.get().getInvestigators().getInvestigator(investigatorsOnGate).getCurrentLocationId();
                            ServicePlatform.get().getService().moveCameraToLocation(currentLocationId);
                            ServicePlatform.get().getDoomOmenService().increaseAncientOnePower(1);
                            checkAncientOnePower();
                        } else { // HE HAS SPELL
                            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorsOnGate);
                            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                            Question<Boolean> discardSpellQuestion = new Question<>();
                            discardSpellQuestion.setOptions(Question.Option.noYesOptions);
                            discardSpellQuestion.setTitle("Discard one Spell? Yog-Sothoth's power will not rise.");
                            Question.SelectComponentsTableData<SpellInfo> selectSpellTableData = new Question.SelectComponentsTableData<>();
                            selectSpellTableData.setAvailableKeys(ServicePlatform.get().getSpellsDeck().getSpells(investigatorsOnGate));
                            selectSpellTableData.setType(Question.SelectComponentsTableType.CARDS);
                            discardSpellQuestion.setSelectComponentsTableData(selectSpellTableData);

                            ServicePlatform.get().getGameService().ask(discardSpellQuestion).subscribe(answer -> {
                                if (!answer.getResponseData()) { // NO
                                    ServicePlatform.get().getDoomOmenService().increaseAncientOnePower(1);
                                    checkAncientOnePower();
                                    return;
                                }
                                if (answer.getAdditionalData() != null) {
                                    EncounterUtils.onSelectCardToDiscard(((CardInfo) answer.getAdditionalData()));
                                    return;
                                }
                                SelectCardData selectCardData = new SelectCardData();
                                selectCardData.setAvailableCards(ServicePlatform.get().getSpellsDeck().getSpells(investigatorsOnGate));
                                selectCardData.setHideText("Display Spells?");
                                selectCardData.setTitleText("Select Spell");
                                ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscardByChoice);
                            });
                        }
                        ServicePlatform.get().getService().release();
                    });
                }



            });
            ServicePlatform.get().getService().release();
        }

        @Override
        public Class<ReckoningFireType> getDataClass() {
            return ReckoningFireType.class;
        }
    }

    private class BeforeAdvanceDoomListener extends EventListenerImpl<Integer> {

        @Override
        public void onNotify(Integer amount) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Yog-Sothoth's power rises!");
            question.setPortraitBeforeTitle(AncientOneId.YOG_SOTHOTH);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getDoomOmenService().increaseAncientOnePower(amount);
                checkAncientOnePower();
                ServicePlatform.get().getService().skipAfterEvent(BeforeAfterEvent.ADVANCE_DOOM, null);
                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<Integer> getDataClass() {
            return Integer.class;
        }
    }

    private void checkAncientOnePower() {
        ServicePlatform.get().getService().addEventCommand(in -> {
            if (ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getPower() >= 3) {
                Question<Object> question = new Question<>();
                question.setOptions(Question.Option.okOption);
                question.setTitle("Yog-Sothoth is in full power!");
                question.setPortraitBeforeTitle(AncientOneId.YOG_SOTHOTH);
                ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                    endGame("power");
                });
            }
        });
    }

    private void endGame(String label) {
        ServicePlatform.get().getService().hold();
        String endGameText = ServicePlatform.get().getModel().getAncientOne().getAncientOneInfo().getEndGameText();
        GoogleServicesHolder.getAnalyticsTracker().trackInteraction(AnalyticsCategory.GAME_ENDED, "YOG-SOTHOTH", label);
        ServicePlatform.get().getTutorialService().displayChalkboard(endGameText,
                VIEWPORT_WIDTH/2, VIEWPORT_HEIGHT/2, true);
        ServicePlatform.get().getService().addEventCommand(in -> {
            ServicePlatform.get().getGameService().restartGame() ;
        });
        ServicePlatform.get().getService().release();
    }
}
