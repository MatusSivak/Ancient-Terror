package sk.sivak.eldritchhorror.core.eventlistener.back.condition.leginjury;

import rx.Single;
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
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;

import static sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils.getActiveInvestigatorId;

public class LegInjuryConditionBackListener3 extends AbstractConditionBackListener {

    public LegInjuryConditionBackListener3(ConditionInfo conditionInfo) {
        super(conditionInfo);
    }

    @Override
    public void executeWhole() {
        ShowCardRequest request = new ShowCardRequest();
        request.setHideType(HideType.RETURN);
        request.setConditionInfo(getConditionInfo());
        request.setTitle("Test Strength");
        request.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        ServicePlatform.get().getGameService().showCard(request).subscribe(this::onTest);
    }

    private void onTest(ShowCardResponse showCardResponse) {
        ServicePlatform.get().getTestService().test(Stat.STRENGTH, 0, 1,
                new TestFlavorRequest(TestFlavorType.CONDITION, getConditionInfo())).subscribe(testResult -> {
            if (!testResult.isSuccessful()) {
                onFlip(null);
            }
        });
    }

    @Override
    protected void execute() {
        if (ServicePlatform.get().getInvestigators().getActiveInvestigator().isDelayed()) {
            TypewriterUtils.confirmInfos("[#BAD]Lose two Health[]").subscribe(() -> {
                ServicePlatform.get().getEncounterService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getTokenService().loseHealth(2);
                ServicePlatform.get().getEncounterService().release();
            });
            return;
        }
        ServicePlatform.get().getEncounterService().hold();
        ServicePlatform.get().getEncounterService().typeInfo(TypewriterUtils.SELECT_ONE);
        ServicePlatform.get().getEncounterService().displayButtons(
                "[#BAD]Lose two Health[]",
                "[#BAD]Become Delayed[]"
        ).subscribe(x -> {
            if (x == 0) {
                ServicePlatform.get().getEncounterService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getTokenService().loseHealth(2);
                ServicePlatform.get().getEncounterService().release();
            } else {
                ServicePlatform.get().getEncounterService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getInvestigatorService().becomeDelayed(new DelayedData(getActiveInvestigatorId(), false));
                ServicePlatform.get().getEncounterService().release();
            }
        });
        ServicePlatform.get().getEncounterService().release();
    }

}
