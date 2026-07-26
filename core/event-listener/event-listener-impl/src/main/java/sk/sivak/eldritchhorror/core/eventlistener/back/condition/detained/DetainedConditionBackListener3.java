package sk.sivak.eldritchhorror.core.eventlistener.back.condition.detained;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.condition.AbstractConditionBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import static sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils.TEST_FAILED;
import static sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils.TEST_PASSED;
import static sk.sivak.eldritchhorror.core.eventtype.data.test.TestData.JUST_ONE;

public class DetainedConditionBackListener3 extends AbstractConditionBackListener {

    public DetainedConditionBackListener3(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    protected void execute() {
        // only if can spend clue
        ServicePlatform.get().getTokenService().canSpend(1,0,0,0).subscribe(spendData -> {
            if (spendData.hasEnough()) {
                onCanSpendClue();
            } else {
                onCanNotSpendClue();
            }
        });

    }

    private void onCanNotSpendClue() {
        TypewriterUtils.displayTestConditionButton(Stat.WILL, -1, getConditionInfo(), this::onSuccess, this::onFail);
    }

    private void onSuccess() {
        TypewriterUtils.confirmInfos("Discard this card").subscribe(this::onConfirmSuccessResult);
    }

    private void onFail() {
        TypewriterUtils.confirmInfos(
                "[#BAD]Lose three Sanity[]",
                "[#BAD]Gain a Paranoia Condition[]",
                "[#GOOD]Discard this card[]").subscribe(this::onConfirmFailResult);
    }

    private void onConfirmSuccessResult() {
        ServicePlatform.get().getEncounterService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        discardThisCard();
        ServicePlatform.get().getEncounterService().release();
    }

    private void onConfirmFailResult() {
        ServicePlatform.get().getEncounterService().hold();
        ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        ServicePlatform.get().getTokenService().loseSanity(3);
        ServicePlatform.get().getGameService().gainCondition(ConditionId.PARANOIA);
        discardThisCard();
        ServicePlatform.get().getEncounterService().release();
    }

    private void discardThisCard() {
        ShowCardRequest showCardRequest = new ShowCardRequest();
        showCardRequest.setConditionInfo(getConditionInfo());
        showCardRequest.setHideType(HideType.DISCARD_ALWAYS);
        showCardRequest.setTitle("Discard Detained Condition");
        showCardRequest.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        Single<ShowCardResponse> displayCondition = ServicePlatform.get().getGameService().showCard(showCardRequest);
        displayCondition.subscribe(response -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getService().discardConditionFromInvestigator(getActiveInvestigatorId(), getConditionInfo());
            ServicePlatform.get().getService().release();
        });
    }

    private void onCanSpendClue() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.CHOOSE_ONE);
        String[] buttonTexts = new String[] {
                "[#BAD]Spend one Clue[]",
                "[#BAD]Test Will[]"
        };
        ServicePlatform.get().getEncounterService().displayButtons(buttonTexts).subscribe(choice -> {
            if (choice == 0) {
                onSpendClueChoice();
            } else {
                onTestChoice();
            }
        });
        ServicePlatform.get().getService().release();
    }

    private void onTestChoice() {
        ServicePlatform.get().getEncounterService().hold();
        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
        ServicePlatform.get().getTestService().test(Stat.WILL, -1, JUST_ONE, createConditionFlavorRequest()).subscribe(
                testData -> {
                    if (testData.isSuccessful()) {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                        ServicePlatform.get().getEncounterService().typeInfo(TEST_PASSED);
                        onSuccess();
                        ServicePlatform.get().getService().release();
                    } else {
                        ServicePlatform.get().getService().hold();
                        ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                        ServicePlatform.get().getEncounterService().typeInfo(TEST_FAILED);
                        onFail();
                        ServicePlatform.get().getService().release();
                    }
                }
        );
        ServicePlatform.get().getEncounterService().release();
    }

    private void onSpendClueChoice() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getEncounterService().hideTypewriterPaper();
        ServicePlatform.get().getTokenService().spend(1,0,0,0).subscribe(spendData -> {
            if (spendData.hasEnough()) {
                ServicePlatform.get().getService().hold();
                spendData.pay();
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                onSuccess();
                ServicePlatform.get().getService().release();
            } else {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                onCanNotSpendClue();
                ServicePlatform.get().getService().release();
            }
        });
        ServicePlatform.get().getService().release();
    }

}
