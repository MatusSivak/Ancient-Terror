package sk.sivak.eldritchhorror.core.eventlistener.back.condition;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.displayasset.DisplayAssetResponseType;
import sk.sivak.eldritchhorror.core.constants.displayasset.HideType;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardRequest;
import sk.sivak.eldritchhorror.core.constants.displayasset.ShowCardResponse;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorInfo;
import sk.sivak.eldritchhorror.core.constants.test.TestFlavorType;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestFlavorRequest;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

public abstract class AbstractConditionBackListener {

    private ConditionInfo conditionInfo;

    public AbstractConditionBackListener(ConditionInfo conditionInfo) {
        this.conditionInfo = conditionInfo;
    }

    protected ConditionInfo getConditionInfo() {
        return conditionInfo;
    }

    private Object convertToObject;

    public void justFlip() {}

    public void executeWhole() {
        ShowCardRequest request = new ShowCardRequest();
        request.setHideType(HideType.RETURN);
        request.setConditionInfo(conditionInfo);
        request.setTitle("Flip " + conditionInfo.getName() +" Condition");
        request.setDisplayAssetResponseType(DisplayAssetResponseType.OK);
        ServicePlatform.get().getGameService().showCard(request).subscribe(this::onFlip);
    }

    protected void onFlip(ShowCardResponse response) {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getTestService().setTestFlavor(new TestFlavorRequest(TestFlavorType.CONDITION, conditionInfo));
        ServicePlatform.get().getEncounterService().showTypewriterPaper(true);
        ServicePlatform.get().getEncounterService().typeHeader(conditionInfo.getName());
        ServicePlatform.get().getEncounterService().typeInfo(conditionInfo.getConditionBack().getTitle()+"\n ");
        ServicePlatform.get().getEncounterService().typeFlavor(conditionInfo.getConditionBack().getFlavorText());
        execute();
        ServicePlatform.get().getTestService().unsetTestFlavor();
        if (convertToObject != null) {
            ServicePlatform.get().getService().convertTo(Object.class, () -> convertToObject);
        } else {
            ServicePlatform.get().getService().convertToNull();
        }
        ServicePlatform.get().getService().release();
    }

    public  void setConvertToObject(Object convertToObject) {
        this.convertToObject = convertToObject;
    }

    protected abstract void execute();

    protected InvestigatorId getActiveInvestigatorId() {
        return ServicePlatform.get().getInvestigators().getActiveInvestigatorId();
    }

    protected InvestigatorRead getActiveInvestigator() {
        return ServicePlatform.get().getInvestigators().getActiveInvestigator();
    }

    protected TestFlavorRequest createConditionFlavorRequest() {
        return new TestFlavorRequest(TestFlavorType.CONDITION, getConditionInfo());
    }
}
