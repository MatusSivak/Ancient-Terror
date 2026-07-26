package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Answer;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellId;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

/**
 * @author msivak
 */
public class TheWaitressInitListener extends AbstractInvestigatorInitListener {

    private TheWaitressPassiveListener theWaitressPassiveListener;

    @Override
    protected InvestigatorId getInvestigatorId() {
        return InvestigatorId.THE_WAITRESS;
    }

    @Override
    protected void initInvestigator() {
        justRegisterListeners();
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.PROFANE_TOME);
        getService().gainSpellFromDeck(getInvestigatorId(), SpellId.STORM_OF_SPIRITS);
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override
    public void justRegisterListeners() {
        theWaitressPassiveListener = new TheWaitressPassiveListener();
        ServicePlatform.get().getEventQueue().addDirectEventListener(theWaitressPassiveListener, DirectEvent.REGISTER_BONUS_DICE);
    }

    @Override
    public void unregisterInvestigator() {
        ServicePlatform.get().getEventQueue().unregisterListener(theWaitressPassiveListener);
        ServicePlatform.get().getEventQueue().unregisterListener(this);
    }

    private class TheWaitressPassiveListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            if (getActiveInvestigatorId() != InvestigatorId.THE_WAITRESS) {
                return;
            }
            if (ServicePlatform.get().getTestFlavor().getFlavorType() != TestFlavorType.SPELL) {
                return;
            }
            if (eventData.getStat() != Stat.LORE) {
                return;
            }
            ServicePlatform.get().getTokenService().canSpend(0,0,1,0).subscribe(canSpend -> {
                if (!canSpend.hasEnough()) {
                    ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                    return;
                }
                askQuestion().subscribe(answer -> {
                    if (!answer.getResponseData()) {
                        ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                        return;
                    }
                    ServicePlatform.get().getTokenService().spend(0,0,1,0).subscribe(spendData -> {
                        if (!spendData.hasEnough()) {
                            ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                            return;
                        }
                        eventData.setAdditionalDicesCount(eventData.getAdditionalDicesCount() + 2);
                        ServicePlatform.get().getService().hold();
                        spendData.pay();
                        ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                        ServicePlatform.get().getService().release();
                    });
                });
            });
        }

        private Single<Answer<Boolean, Object>> askQuestion() {
            Question<Boolean> question = new Question<>();

            question.setTitle("Spend one Health to roll two additional dice?");
            question.setPortraitBeforeTitle(InvestigatorId.THE_WAITRESS);
            question.setOptions(Question.Option.noYesOptions);
            return ServicePlatform.get().getGameService().ask(question);
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }
}
