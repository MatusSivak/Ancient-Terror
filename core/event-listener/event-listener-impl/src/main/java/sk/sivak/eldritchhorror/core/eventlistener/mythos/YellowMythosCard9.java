package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

public class YellowMythosCard9 extends RandomInvestigatorMythosEffect{
    @Override
    protected void executeFor(InvestigatorRead targetInvestigator) {
        if (targetInvestigator == null) {
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(targetInvestigator.getInfo().getInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        ServicePlatform.get().getGameService().gainAsset(ServicePlatform.get().getAssetDeck().findFirst(AssetTrait.ALLY));
        ServicePlatform.get().getService().release();
    }
}
