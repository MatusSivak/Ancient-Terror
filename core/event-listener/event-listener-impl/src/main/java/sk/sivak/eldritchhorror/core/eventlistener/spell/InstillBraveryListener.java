package sk.sivak.eldritchhorror.core.eventlistener.spell;

import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.spell.instillbravery.InstillBraverySpell;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.card.EnableCardListener;
import sk.sivak.eldritchhorror.core.eventqueue.EventListener;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import sk.sivak.eldritchhorror.core.eventtype.data.token.LoseTokenData;

import java.util.Arrays;
import java.util.List;

public class InstillBraveryListener extends AbstractSpellListener<InstillBraverySpell> {

    private EnableCardListener enableCardListener;
    private BeforeLoseSanityListener beforeLoseSanityListener;

    @Override
    protected List<EventListener> getEventListeners() {
        return Arrays.asList(beforeLoseSanityListener, enableCardListener);
    }

    @Override
    protected void register() {
        beforeLoseSanityListener = new BeforeLoseSanityListener();
        getEventQueue().addBeforeEventListener(beforeLoseSanityListener, BeforeAfterEvent.LOSE_SANITY);

        enableCardListener = new EnableCardListener(spellInfo);
        getEventQueue().addDirectEventListener(enableCardListener, DirectEvent.REENABLE_DISABLED_ABILITIES);
    }

    private class BeforeLoseSanityListener extends EventListenerImpl<LoseTokenData> {

        private AbstractSpellBackListener spellBackListener;

        @Override
        public void onNotify(LoseTokenData eventData) {
            ServicePlatform.get().getService().<LoseTokenData>addEventCommand((input) -> {
                getEventAction(input).run();
            });
        }

        private Runnable getEventAction(LoseTokenData input) {
            return () -> {
                if (!enabled) {
                    return;
                }
                if (input.getAmount() <= 0) {
                    return;
                }

                InvestigatorId targetInvestigatorId = ServicePlatform.get().getInvestigators().getActiveInvestigatorId();

                ServicePlatform.get().getService().hold();
                Question<Boolean> question = new Question<>();
                question.setTitle("Cast Instill Bravery?");
                question.setPortraitBeforeTitle(spellOwnerId);
                question.setHideTestUi(true);
                question.setOptions(Question.Option.noYesOptions);

                ServicePlatform.get().getGameService().ask(question)
                        .subscribe(answer -> onAnswer(Boolean.TRUE.equals(answer.getResponseData()), input, targetInvestigatorId));
                ServicePlatform.get().getService().release();

            };
        }

        private void onAnswer(boolean cast, LoseTokenData input, InvestigatorId targetInvestigatorId) {
            if (!cast) {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getService().convertTo(LoseTokenData.class, () -> input);
                ServicePlatform.get().getService().release();
                return;
            }

            ServicePlatform.get().getService().hold();
            // Wait for consent before focusing the caster and rolling their Lore.
            if (spellOwnerId != targetInvestigatorId) {
                ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(spellOwnerId);
            }
            ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
            ServicePlatform.get().getTestService().test(Stat.LORE, 0, 16,
                    new TestFlavorRequest(TestFlavorType.SPELL)).subscribe(
                            testData -> disableAndFlipCard(testData, input, targetInvestigatorId));
            ServicePlatform.get().getService().release();
        }

        private void disableAndFlipCard(TestData testData,
                                        LoseTokenData input,
                                        InvestigatorId targetInvestigatorId) {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().disableCard(spellInfo);

            spellBackListener = findSpellBackListener();
            spellBackListener.setTestData(testData);
            spellBackListener.setMainSpellAction(createMainSpellAction(input));
            spellBackListener.setData("targetInvestigatorId", targetInvestigatorId);
            spellBackListener.setData("spellOwnerId", spellOwnerId);
            spellBackListener.setData("loseTokenData", input);
            spellBackListener.executeWhole();

            ServicePlatform.get().getService().addEventCommand(in -> {
                if (ServicePlatform.get().getInvestigators().getActiveInvestigatorId() != targetInvestigatorId) {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(targetInvestigatorId);
                    ServicePlatform.get().getInvestigatorService().showActiveInvestigator(false);
                    ServicePlatform.get().getService().release();
                }
            });

            ServicePlatform.get().getService().convertTo(LoseTokenData.class, () -> input);
            ServicePlatform.get().getService().release();
        }

        private Runnable createMainSpellAction(LoseTokenData input) {
            return () -> {
                input.setAmount(Math.max(0, input.getAmount() - 2));
            };
        }

        @Override
        public Class<LoseTokenData> getDataClass() {
            return LoseTokenData.class;
        }
    }

}
