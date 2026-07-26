package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yogsothoth;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AncientOneInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.ReckoningFireType;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.MythosDeckRead;

import java.util.LinkedList;
import java.util.List;

public class YogSothothInitListener extends AncientOneInitListener {

    private static final Logger logger = LogManager.getLogger(YogSothothInitListener.class);
    private ReckoningListener reckoningListener;
    private AfterAdvanceDoomListener afterAdvanceDoomListener;

    @Override
    public void register() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getGameService().showAncientOneCard();
        ServicePlatform.get().getMythosDeck().initSelectedMythosCards(
                new MythosDeckRead.MythosStageImpl(0,2,1),
                new MythosDeckRead.MythosStageImpl(2,3,1),
                new MythosDeckRead.MythosStageImpl(3,4,0));
        ServicePlatform.get().getService().release();

        reckoningListener = new ReckoningListener();
        afterAdvanceDoomListener = new AfterAdvanceDoomListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_ANCIENT_ONE);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);
    }

    @Override
    public void load() {
        reckoningListener = new ReckoningListener();
        afterAdvanceDoomListener = new AfterAdvanceDoomListener();

        ServicePlatform.get().getEventQueue().addDirectEventListener(reckoningListener, DirectEvent.RECKONING_ANCIENT_ONE);
        ServicePlatform.get().getEventQueue().addAfterEventListener(afterAdvanceDoomListener, BeforeAfterEvent.ADVANCE_DOOM);
    }

    private class AfterAdvanceDoomListener extends EventListenerImpl<Void> {

        @Override
        public void onNotify(Void eventData) {
            if (ServicePlatform.get().getDoomTrack().getCurrentDoom() > 0) {
                return;
            }
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Yog-Sothoth awakens! Investigators can't be replaced.");
            question.setPortraitBeforeTitle(AncientOneId.YOG_SOTHOTH);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getDoomOmenService().ancientOneAwakens();
                ServicePlatform.get().getEventQueue().unregisterListener(reckoningListener);
                ServicePlatform.get().getEventQueue().unregisterListener(afterAdvanceDoomListener);
                ServicePlatform.get().getService().convertTo(Void.class, () -> null);
                ServicePlatform.get().getService().release();

            });
        }

        @Override
        public Class<Void> getDataClass() {
            return Void.class;
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
            question.setTitle("Doom advances for each investigator on a Gate.");
            question.setPortraitBeforeTitle(AncientOneId.YOG_SOTHOTH);
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {

                for (InvestigatorId investigatorsOnGate : investigatorsOnGates) {
                    ServicePlatform.get().getService().addEventCommand(in -> {
                        ServicePlatform.get().getService().hold();
                        if (ServicePlatform.get().getSpellsDeck().getSpells(investigatorsOnGate).isEmpty()) { // NO SPELLS
                            LocationId currentLocationId = ServicePlatform.get().getInvestigators().getInvestigator(investigatorsOnGate).getCurrentLocationId();
                            ServicePlatform.get().getService().moveCameraToLocation(currentLocationId);
                            ServicePlatform.get().getDoomOmenService().advanceDoom();
                        } else { // HE HAS SPELL
                            ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(investigatorsOnGate);
                            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
                            Question<Boolean> discardSpellQuestion = new Question<>();
                            discardSpellQuestion.setOptions(Question.Option.noYesOptions);
                            discardSpellQuestion.setTitle("Discard one Spell? Doom will not advance.");
                            Question.SelectComponentsTableData<SpellInfo> selectSpellTableData = new Question.SelectComponentsTableData<>();
                            selectSpellTableData.setAvailableKeys(ServicePlatform.get().getSpellsDeck().getSpells(investigatorsOnGate));
                            selectSpellTableData.setType(Question.SelectComponentsTableType.CARDS);
                            discardSpellQuestion.setSelectComponentsTableData(selectSpellTableData);

                            ServicePlatform.get().getGameService().ask(discardSpellQuestion).subscribe(answer -> {
                                if (!answer.getResponseData()) { // NO
                                    ServicePlatform.get().getDoomOmenService().advanceDoom();
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
}
