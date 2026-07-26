package sk.sivak.eldritchhorror.core.eventlistener.encounter.thekeyandthegate;

import java8.features.function.Consumer;
import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
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

public class TheKeyAndTheGate6Encounter extends AbstractTheKeyAndTheGateEncounter {

    public TheKeyAndTheGate6Encounter() {
        super(6);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = () -> {


            BeforeConfirmTestListener beforeConfirmTestListener = new BeforeConfirmTestListener();
            ServicePlatform.get().getEventQueue().addBeforeEventListener(beforeConfirmTestListener, BeforeAfterEvent.CONFIRM_TEST);

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().typeInfo("Minimum score to pass is three.\n" +
                    "You may spend any number of Sanity\nto roll one additional die.");

            new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.STRENGTH, 0, () -> {
                ServicePlatform.get().getGameService().advanceActiveMystery();
            }, () -> {
                ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
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

        new TheKeyAndTheGateEncounterTemplate(getTextBuilder(), Stat.OBSERVATION, -1, passTemplate, failTemplate).execute();
    }

    private class BeforeConfirmTestListener extends EventListenerImpl<TestData> {

        @Override
        public void onNotify(TestData eventData) {
            ServicePlatform.get().getService().addEventCommand(in -> {
                ServicePlatform.get().getEventQueue().unregisterListener(BeforeConfirmTestListener.this);
            });
            eventData.setMinScoreToBeSuccessful(3);
            eventData.setMinScoreToEndTest(3);

            ClueFocusHealthSanity clueFocusHealthSanity = new ClueFocusHealthSanity(0,0,0,1);
            ServicePlatform.get().getTokenService().canSpend(clueFocusHealthSanity).subscribe(canSpend -> {
                if (!canSpend.hasEnough()) {
                    ServicePlatform.get().getService().convertTo(TestData.class, () -> eventData);
                    return;
                }
                ServicePlatform.get().getService().hold();
                Question<Boolean> question = new Question<>();
                question.setTitle("Spend one Sanity to roll one additional die? (Bonus dice: "+(eventData.getAdditionalDicesCount() + 1)+")");
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
                        eventData.setAdditionalDicesCount(eventData.getAdditionalDicesCount() + 1);
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
