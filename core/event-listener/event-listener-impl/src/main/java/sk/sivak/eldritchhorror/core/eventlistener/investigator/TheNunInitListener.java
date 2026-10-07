package sk.sivak.eldritchhorror.core.eventlistener.investigator;

import sk.sivak.eldritchhorror.core.constants.asset.AssetId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;

public class TheNunInitListener extends AbstractInvestigatorInitListener {

    @Override protected InvestigatorId getInvestigatorId() { return InvestigatorId.THE_NUN; }

    @Override protected void initInvestigator() {
        justRegisterListeners();
        getService().hold();
        getService().gainAssetFromDeck(getInvestigatorId(), AssetId.HOLY_WATER);
        ServicePlatform.get().getTokenService().gainClueFromPool(getInvestigatorId());
        getService().convertTo(Object.class, () -> eventData);
        getService().registerNewInvestigatorAction(getInvestigatorId());
        getService().release();
    }

    @Override public void justRegisterListeners() {

    }

    @Override public void unregisterInvestigator() {
        getEventQueue().unregisterListener(this);
    }

}

