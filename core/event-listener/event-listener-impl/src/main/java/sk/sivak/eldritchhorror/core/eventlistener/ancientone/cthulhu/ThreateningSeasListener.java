package sk.sivak.eldritchhorror.core.eventlistener.ancientone.cthulhu;

import java8.features.function.Predicate;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.ancientone.AbstractMysteryListener;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.AvailableEncounters;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.MysteryEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import sk.sivak.eldritchhorror.core.model.VortexesRead;

import java.util.Collections;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;
import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class ThreateningSeasListener extends AbstractMysteryListener implements EventListener<AvailableEncounters>  {

    private AvailableEncounters availableEncounters;

    private int progress;
    private EncounterActiveMysteryListener encounterActiveMysteryListener;

    public ThreateningSeasListener(MysteryCardInfo mysteryCardInfo) {
        super(mysteryCardInfo);
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void register() {
        mysteryCardInfo.setPinLocationsSupplier(() -> ServicePlatform.get().getVortexes().getSpawnedVortexes());

        ServicePlatform.get().getService().hold();
        for (InvestigatorRead investigator : ServicePlatform.get().getInvestigators().getOnBoardInvestigators()) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                VortexesRead vortexes = ServicePlatform.get().getVortexes();
                Predicate<LocationInfo> seaPredicate = location -> location.getLocationType() == LocationType.SEA;
                Predicate<LocationInfo> withoutVortexPredicate = location -> !vortexes.isAtLocation(location.getLocationId());
                Predicate<LocationInfo> combinedPredicate = location -> seaPredicate.test(location) && withoutVortexPredicate.test(location);
                FindNearestData nearest = ServicePlatform.get().getLocationMap().findNearest(investigator.getCurrentLocationId(), combinedPredicate);
                if (nearest == null) {
                    return;
                }
                ServicePlatform.get().getGameService().spawnVortex(nearest.getLocationId());
            });
        }
        ServicePlatform.get().getGameService().showCurrentMysteryCard(true, false);
        ServicePlatform.get().getEventQueue().addDirectEventListener(this, DirectEvent.COLLECT_COMMON_ENCOUNTERS);

        encounterActiveMysteryListener = new EncounterActiveMysteryListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(encounterActiveMysteryListener, DirectEvent.ENCOUNTER_ACTIVE_MYSTERY);
        ServicePlatform.get().getService().release();
    }

    @Override
    public void justAddRedPins() {

    }

    @Override
    public void justRegisterListeners(int progress) {
        encounterActiveMysteryListener = new EncounterActiveMysteryListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(this, DirectEvent.COLLECT_COMMON_ENCOUNTERS);
        ServicePlatform.get().getEventQueue().addDirectEventListener(encounterActiveMysteryListener, DirectEvent.ENCOUNTER_ACTIVE_MYSTERY);
        this.progress = progress;
    }

    @Override
    public void unregister() {
        ServicePlatform.get().getEventQueue().unregisterListener(this);
        ServicePlatform.get().getEventQueue().unregisterListener(encounterActiveMysteryListener);
    }

    private class EncounterActiveMysteryListener extends EventListenerImpl<MysteryEncounter> {

        @Override
        public void onNotify(MysteryEncounter eventData) {
            if (progress >= mysteryCardInfo.getMysteryComplexity()) {
                return;
            }
            Question<Boolean> question = new Question<>();
            question.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
            question.setTitle("Prolong Cthulhu's slumber.");
            question.displayCurrentMysteryCard();
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> onAnswer(eventData));
        }

        private void onAnswer(MysteryEncounter eventData) {
            ServicePlatform.get().getTestService().test(Stat.LORE, -1, JUST_ONE).subscribe(testData -> {
                if (!testData.isSuccessful()) {
                    ServicePlatform.get().getService().convertTo(MysteryEncounter.class, () -> eventData);
                    return;
                }
                onSuccessfulTest(eventData);
            });
        }

        private void onSuccessfulTest(MysteryEncounter eventData) {
            InvestigatorId investigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
            if (ServicePlatform.get().getSpellsDeck().getSpells(investigatorId).isEmpty()) {
                Question<Object> confirmNoSpells = new Question<>();
                confirmNoSpells.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
                confirmNoSpells.setTitle("You don't have any Spells to discard.");
                ServicePlatform.get().getGameService().ask(confirmNoSpells).subscribe(answer2 -> {
                    ServicePlatform.get().getService().convertTo(MysteryEncounter.class, () -> eventData);
                });
                return;
            }
            ServicePlatform.get().getTokenService().spend(1, 0, 0, 0).subscribe(spendData -> {
                if (!spendData.hasEnough()) {
                    Question<Object> confirmNoClues = new Question<>();
                    confirmNoClues.setOptions(Collections.singletonList(new Question.Option<>("OK", Boolean.TRUE)));
                    confirmNoClues.setTitle("You don't have any Clues to discard.");
                    ServicePlatform.get().getGameService().ask(confirmNoClues).subscribe(answer2 -> {
                        ServicePlatform.get().getService().convertTo(MysteryEncounter.class, () -> eventData);
                    });
                    return;
                }
                ServicePlatform.get().getService().hold();
                discardSpell();
                spendData.pay();
                progress++;
                LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
                ServicePlatform.get().getGameService().removeVortex(currentLocationId);
                ServicePlatform.get().getGameService().advanceCurrentMysteryCard(1);
                ServicePlatform.get().getService().convertTo(MysteryEncounter.class, () -> eventData);
                ServicePlatform.get().getService().release();
            });
        }

        private void discardSpell() {
            InvestigatorId activeInvestigatorId = getActiveInvestigatorId();
            List<SpellInfo> spells = ServicePlatform.get().getSpellsDeck().getSpells(activeInvestigatorId);
            SelectCardData selectCardData = new SelectCardData();
            selectCardData.setAvailableCards(spells);
            selectCardData.setHideText("Display Spells?");
            selectCardData.setTitleText("Select Spell to discard");
            ServicePlatform.get().getCardService().selectSingleCard(selectCardData).subscribe(EncounterUtils::onSelectCardToDiscardByChoice);
        }


        @Override
        public Class<MysteryEncounter> getDataClass() {
            return MysteryEncounter.class;
        }
    }


    @Override
    public void onNotify(AvailableEncounters eventData) {
        this.availableEncounters = eventData;
        InvestigatorRead activeInvestigator = ServicePlatform.get().getInvestigators().getActiveInvestigator();
        if (!mysteryCardInfo.getPinLocations().contains(activeInvestigator.getCurrentLocationId())) {
            return;
        }
        if (progress >= mysteryCardInfo.getMysteryComplexity()) {
            return;
        }
        ServicePlatform.get().getTokenService().canSpend(1,0,0,0).subscribe(spendData ->
                onCanSpend(spendData, activeInvestigator.getInfo().getInvestigatorId()));
    }

    private void onCanSpend(SpendData spendData, InvestigatorId investigatorId) {
        MysteryEncounter encounter = new MysteryEncounter(mysteryCardInfo.getName());
        availableEncounters.addEncounter(encounter);
        if (!spendData.hasEnough()) {
            encounter.disable("Requires Clue");
        } else if (ServicePlatform.get().getSpellsDeck().getSpells(investigatorId).isEmpty()) {
            encounter.disable("Requires Spell");
        }
        encounter.setButtonIcon("VORTEX");
        ServicePlatform.get().getService().convertTo(AvailableEncounters.class, () -> availableEncounters);
    }

    @Override
    public Class<AvailableEncounters> getDataClass() {
        return AvailableEncounters.class;
    }

    @Override
    public void advanceActiveMystery() {
        if (progress >= mysteryCardInfo.getMysteryComplexity()) {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Can't advance active mystery.");
            ServicePlatform.get().getGameService().ask(question).subscribe();
        } else {
            progress++;
            ServicePlatform.get().getGameService().advanceCurrentMysteryCard(1);
        }
    }
}
