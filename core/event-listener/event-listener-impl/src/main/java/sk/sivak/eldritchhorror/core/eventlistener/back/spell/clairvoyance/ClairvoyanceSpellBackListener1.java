package sk.sivak.eldritchhorror.core.eventlistener.back.spell.clairvoyance;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.investigator.TheExpeditionLeaderInitListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.EncounterResult;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import java.util.Collections;
import java.util.List;

public class ClairvoyanceSpellBackListener1 extends AbstractSpellBackListener {

    public ClairvoyanceSpellBackListener1(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0, this::showActiveInvestigator));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1, this::showActiveInvestigator));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2, this::showActiveInvestigator));
    }

    private void showActiveInvestigator() {
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
    }

    private void on0() {
        new DiscardThisSpellUnless(getSpellInfo(), getActiveInvestigatorId()).gainCondition(ConditionId.PARANOIA);
    }


    private void on1() {
        TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        });
    }

    private void on2() {
        TypewriterUtils.confirmInfos("[#GOOD]You may roll one additional die\n" +
                "when resolving tests\n" +
                "during the Research Encounter.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();

            RollAdditionalDieListener rollAdditionalDieListener = new RollAdditionalDieListener();
            ServicePlatform.get().getEventQueue().addDirectEventListener(rollAdditionalDieListener, DirectEvent.REGISTER_BONUS_DICE);
            ServicePlatform.get().getEventQueue().addDirectEventListener(new RemoveRollAdditionalDieListener(rollAdditionalDieListener), DirectEvent.INVESTIGATOR_FINISHED_ENCOUNTER);

            ServicePlatform.get().getService().release();
        });
    }

    private class RollAdditionalDieListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            if (ServicePlatform.get().getTestFlavor().getFlavorType() != TestFlavorType.RESEARCH) {
                return;
            }
            ShowCardRequest showCardRequest = new ShowCardRequest();
            showCardRequest.setSpellInfo(getSpellInfo());
            showCardRequest.setTitle("Roll one additional die?");
            showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.YES_NO);
            ServicePlatform.get().getGameService().showCard(showCardRequest).subscribe(showCardResponse -> onShowCard(showCardResponse, eventData));
        }

        private void onShowCard(ShowCardResponse showCardResponse, TestData eventData) {
            if (showCardResponse == ShowCardResponse.NO) {
                ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                return;
            }
            eventData.setAdditionalDicesCount(eventData.getAdditionalDicesCount() + 1);
            ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class RemoveRollAdditionalDieListener extends EventListenerImpl<EncounterResult> {

        private final RollAdditionalDieListener rollAdditionalDieListener;

        public RemoveRollAdditionalDieListener(RollAdditionalDieListener rollAdditionalDieListener) {
            this.rollAdditionalDieListener = rollAdditionalDieListener;
        }

        @Override
        public void onNotify(EncounterResult eventData) {
            ServicePlatform.get().getEventQueue().unregisterListener(this);
            ServicePlatform.get().getEventQueue().unregisterListener(rollAdditionalDieListener);
        }

        @Override
        public Class<EncounterResult> getDataClass() {
            return EncounterResult.class;
        }
    }
}
