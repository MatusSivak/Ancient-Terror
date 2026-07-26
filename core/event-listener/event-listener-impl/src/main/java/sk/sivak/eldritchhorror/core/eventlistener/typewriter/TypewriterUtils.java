package sk.sivak.eldritchhorror.core.eventlistener.typewriter;

import rx.Completable;
import rx.functions.Action0;
import rx.functions.Action1;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public final class TypewriterUtils {

    public static final String SELECT_REWARD = "Select Reward:";
    public static final String SELECT_ONE = "Select One:";
    public static final String CHOOSE_ONE = "Choose one:";
    public static final String TEST_PASSED = "[#GOOD]Test Passed.[]\n ";
    public static final String TEST_FAILED = "[#BAD]Test Failed.[]\n ";

    private TypewriterUtils() {
    }

    public static Completable confirmInfos(String... infos) {
        return Completable.create(onSub -> {
            StringBuilder sb = new StringBuilder();
            boolean multipleInfos = infos.length > 1;
            for (int i = 0; i < infos.length; i++) {
                if (multipleInfos) {
                    sb.append("-");
                }
                sb.append(infos[i]);
                if (i != infos.length - 1) {
                    sb.append("\n");
                }
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().typeInfo(sb.toString());
            ServicePlatform.get().getEncounterService().displayButtons("OK").subscribe(buttonId -> onSub.onCompleted());
            ServicePlatform.get().getService().release();
        });
    }

    public static void displayTestConditionButton(Stat stat, ConditionInfo conditionInfo, Action0 onSuccess, Action0 onFail) {
        displayTestConditionButton(stat, 0, conditionInfo, onSuccess, onFail);
    }

    public static void displayTestConditionButton(Stat stat, ConditionInfo conditionInfo, Action1<TestData> withTestDataCallback) {
        displayTestButton(stat, 0, new TestFlavorRequest(TestFlavorType.CONDITION, conditionInfo), () -> {}, () -> {}, withTestDataCallback);
    }

    public static void displayTestConditionButton(Stat stat, int modifier, ConditionInfo conditionInfo, Action0 onSuccess, Action0 onFail) {
        displayTestButton(stat, modifier, new TestFlavorRequest(TestFlavorType.CONDITION, conditionInfo), onSuccess, onFail, null);
    }

    public static void displayTestExpeditionButton(Stat stat, int modifier, Action0 onSuccess, Action0 onFail) {
        displayTestButton(stat, modifier, new TestFlavorRequest(TestFlavorType.EXPEDITION), onSuccess, onFail, null);
    }

    public static void displayTestOtherWorldButton(Stat stat, int modifier, Action0 onSuccess, Action0 onFail) {
        displayTestButton(stat, modifier, new TestFlavorRequest(TestFlavorType.OTHER_WORLD), onSuccess, onFail, null);
    }

    public static void displayTestResearchButton(Stat stat, int modifier, Action0 onSuccess, Action0 onFail) {
        displayTestButton(stat, modifier, new TestFlavorRequest(TestFlavorType.RESEARCH), onSuccess, onFail, null);
    }

    public static void displayTestButton(Stat stat, int modifier, Action0 onSuccess, Action0 onFail) {
        displayTestButton(stat, modifier, new TestFlavorRequest(), onSuccess, onFail, null);
    }

    public static void displayTestButton(Stat stat, Action0 onSuccess, Action0 onFail) {
        displayTestButton(stat, 0, new TestFlavorRequest(), onSuccess, onFail, null);
    }

    private static void displayTestButton(Stat stat, int modifier, TestFlavorRequest testFlavorRequest,
                                          Action0 onSuccess, Action0 onFail, Action1<TestData> withTestDataCallback) {
        String buttonText = "Test " + stat.prettyString();
        if (modifier > 0) {
            buttonText = "[#GOOD]" + buttonText + "[]";
        } else if (modifier < 0) {
            buttonText = "[#BAD]" + buttonText + "[]";
        }
        ServicePlatform.get().getEncounterService().displayButtons(buttonText)
                .subscribe(onSub -> {
                    Action0 onSuccessWithResult = () -> onWithResult(onSuccess, TEST_PASSED);
                    Action0 onFailWithResult = () -> onWithResult(onFail, TEST_FAILED);
                    ServicePlatform.get().getEncounterService().hold();
                    ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                    test(stat, modifier, testFlavorRequest, onSuccessWithResult, onFailWithResult, withTestDataCallback);
                    ServicePlatform.get().getEncounterService().release();
                });
    }

    public static void noYesQuestion(String question, Action0 onNo, Action0 onYes) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeInfo(question);
        ServicePlatform.get().getEncounterService().displayButtons("[#BAD]No[]", "[#GOOD]Yes[]").subscribe(
                buttonId -> {
                    if (buttonId == 0) {
                        if (onNo != null) {
                            ServicePlatform.get().getService().hold();
                            onNo.call();
                            ServicePlatform.get().getService().release();
                        } else {
                            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        }
                    } else {
                        if (onYes != null) {
                            ServicePlatform.get().getService().hold();
                            onYes.call();
                            ServicePlatform.get().getService().release();
                        } else {
                            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                        }

                    }
                }
        );
        ServicePlatform.get().getService().release();
    }

    private static void onWithResult(Action0 action, String infoText) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
        ServicePlatform.get().getEncounterService().typeInfo(infoText);
        if (action != null) {
            ServicePlatform.get().getService().hold();
            action.call();
            ServicePlatform.get().getService().release();
        } else {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        }
        ServicePlatform.get().getService().release();
    }

    private static void test(Stat stat, int modifier, TestFlavorRequest testFlavorRequest, Action0 onSuccess, Action0 onFail, Action1<TestData> withTestDataCallback) {

        ServicePlatform.get().getTestService().test(stat, modifier, JUST_ONE, testFlavorRequest).subscribe(
                testData -> {
                    if (withTestDataCallback !=null) {
                        withTestDataCallback.call(testData);
                    }
                    if (testData.isSuccessful()) {
                        onSuccess.call();
                    } else {
                        onFail.call();
                    }
                }
        );
    }
}
