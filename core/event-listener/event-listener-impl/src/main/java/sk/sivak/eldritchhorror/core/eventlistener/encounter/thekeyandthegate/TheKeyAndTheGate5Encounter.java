package sk.sivak.eldritchhorror.core.eventlistener.encounter.thekeyandthegate;

import java8.features.function.Consumer;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TheKeyAndTheGateEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class TheKeyAndTheGate5Encounter extends AbstractTheKeyAndTheGateEncounter {

    public TheKeyAndTheGate5Encounter() {
        super(5);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = () -> {

            BeforeConfirmTestResultListener beforeConfirmTestResultListener = new BeforeConfirmTestResultListener();
            ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeConfirmTestResultListener, BeforeAfterEvent.CONFIRM_TEST_RESULT);
            AfterConfirmTestListener afterConfirmTestListener = new AfterConfirmTestListener();
            ServicePlatform.get().getEventQueue().addAfterEventListener(afterConfirmTestListener, BeforeAfterEvent.CONFIRM_TEST);

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().typeInfo("Minimum score to pass is three.\n" +
                    "You may spend any number of Clues\nto add one success to your test result.");

            new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, 0, () -> {
                ServicePlatform.get().getGameService().advanceActiveMystery();
            }, () -> {
                ServicePlatform.get().getTokenService().loseSanity(3);
            }).execute();

            ServicePlatform.get().getService().release();
        };

        EncounterTemplate failTemplate = () -> {
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo().build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getService().hideBackground();
                ServicePlatform.get().getService().moveCameraToLocation(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
                ServicePlatform.get().getInvestigatorService().devourInvestigator(getActiveInvestigatorId());
                ServicePlatform.get().getService().release();
            });
        };

        new TheKeyAndTheGateEncounterTemplate(getTextBuilder(), Stat.WILL, -1, passTemplate, failTemplate).execute();
    }


    private class AfterConfirmTestListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(AfterConfirmTestListener.this);
            });
            eventData.setMinScoreToBeSuccessful(3);
            eventData.setMinScoreToEndTest(3);
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }

    private class BeforeConfirmTestResultListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(BeforeConfirmTestResultListener.this);
            });
            if (eventData.isSuccessful()) {
                return;
            }
            ClueFocusHealthSanity clueFocusHealthSanity = new ClueFocusHealthSanity(1,0,0,0);
            ServicePlatform.get().getTokenService().canSpend(clueFocusHealthSanity).subscribe(canSpend -> {
                if (!canSpend.hasEnough()) {
                    ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                    return;
                }
                ServicePlatform.get().getService().hold();
                Question<Boolean> question = new Question<>();
                question.setTitle("Spend one Clue to add one success? (New result: "+(eventData.getScore() + 1)+")");
                question.setOptions(Question.Option.noYesOptions);
                ServicePlatform.get().getGameService().ask(question).subscribe(answer -> {
                    if (!answer.getResponseData()) {
                        ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                        return;
                    }
                    ServicePlatform.get().getTokenService().spend(clueFocusHealthSanity).subscribe(spendData -> {
                        if (!spendData.hasEnough()) {
                            ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                            return;
                        }
                        ServicePlatform.get().getService().hold();
                        spendData.pay();
                        eventData.setScoreBonus(eventData.getScoreBonus() + 1);
                        ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                        ServicePlatform.get().getService().addEventCommand((Consumer<Object>) in -> onNotify(eventData));
                        ServicePlatform.get().getService().release();
                    });
                });

                ServicePlatform.get().getService().release();
            });
        }

        @Override
        public Class<TestData> getDataClass() {
            return TestData.class;
        }
    }


}
