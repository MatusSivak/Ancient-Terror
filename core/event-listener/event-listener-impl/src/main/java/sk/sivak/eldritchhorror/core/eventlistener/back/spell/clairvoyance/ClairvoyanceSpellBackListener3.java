package sk.sivak.eldritchhorror.core.eventlistener.back.spell.clairvoyance;

import java8.features.function.Predicate;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.EncounterResult;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.List;

public class ClairvoyanceSpellBackListener3 extends AbstractSpellBackListener {

    public ClairvoyanceSpellBackListener3(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1, this::showActiveInvestigator));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2, this::showActiveInvestigator));
    }

    private void showActiveInvestigator() {
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
    }

    private void on0() {
        if (getTestData().hasRolledAny1()) {
            TypewriterUtils.confirmInfos("[#BAD]Discard the nearest Clue").subscribe(() -> {

                LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
                Predicate<LocationInfo> predicate = locationInfo -> ServicePlatform.get().getCluePool().isClueAt(locationInfo.getLocationId());
                FindNearestData nearestClue = ServicePlatform.get().getLocationMap().findNearest(currentLocationId, predicate);

                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getTokenService().discardClue(nearestClue.getLocationId());
                ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                ServicePlatform.get().getService().release();
            });
        } else {
            TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getService().release();
            });
        }
    }


    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Roll one less die\n" +
                "when resolving tests\n" +
                "during the Research Encounter.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();

            RollOneLessDieListener rollOneLessDieListener = new RollOneLessDieListener();
            ServicePlatform.get().getEventQueue().addDirectEventListener(rollOneLessDieListener, DirectEvent.REGISTER_BONUS_DICE);
            ServicePlatform.get().getEventQueue().addDirectEventListener(new RemoveRollOneLessDieListener(rollOneLessDieListener), DirectEvent.INVESTIGATOR_FINISHED_ENCOUNTER);

            ServicePlatform.get().getService().release();
        });
    }

    private class RollOneLessDieListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            if (ServicePlatform.get().getTestFlavor().getFlavorType() != TestFlavorType.RESEARCH) {
                return;
            }
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(getSpellInfo());
            showCardRequest.setTitle("Roll one less die.");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(showCardResponse -> onShowCard(eventData));
        }

        private void onShowCard(TestData eventData) {
            eventData.setAdditionalDicesCount(eventData.getAdditionalDicesCount() - 1);
            ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class RemoveRollOneLessDieListener extends EventListenerImpl<EncounterResult> {

        private final RollOneLessDieListener rollOneLessDieListener;

        public RemoveRollOneLessDieListener(RollOneLessDieListener rollOneLessDieListener) {
            this.rollOneLessDieListener = rollOneLessDieListener;
        }

        @Override
        public void onNotify(EncounterResult eventData) {
            ServicePlatform.get().getEventQueue().unregisterListener(this);
            ServicePlatform.get().getEventQueue().unregisterListener(rollOneLessDieListener);
        }

        @Override
        public Class<EncounterResult> getDataClass() {
            return EncounterResult.class;
        }
    }

    private void on2() {
        TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        });
    }
}
