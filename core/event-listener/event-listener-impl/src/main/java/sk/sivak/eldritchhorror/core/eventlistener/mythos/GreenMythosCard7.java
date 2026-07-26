package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectCardData;
import sk.sivak.eldritchhorror.core.model.AssetDeckRead;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.List;

public class GreenMythosCard7 implements MythosCardEventListener{

    @Override
    public void execute() {
        InvestigatorRead leadInvestigator = ServicePlatform.get().getInvestigators().getLeadInvestigator();
        if (leadInvestigator == null) {
            return;
        }
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getInvestigatorService().changeActiveInvestigator(leadInvestigator.getInfo().getInvestigatorId());
        ServicePlatform.get().getInvestigatorService().showActiveInvestigator(true);
        ServicePlatform.get().getTestService().test(Stat.INFLUENCE, 0, 16).subscribe(testData -> {
            ServicePlatform.get().getService().hold();
            for (int i = 0; i < testData.getScore(); i++) {
                ServicePlatform.get().getTokenService().gainClueFromPool();
            }
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }

}
