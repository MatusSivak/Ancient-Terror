package sk.sivak.eldritchhorror.core.eventlistener.encounter.location;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;

public class London2Encounter extends AbstractLocationEncounter{

    private InfoFlavorTemplate template;

    public London2Encounter() {
        super(2, LocationEncounter.LocationEncounterType.LONDON);
    }

    @Override
    protected void execute() {
        Stat testStat = Stat.STRENGTH;
        int modifier = 0;
        Runnable onFail = () -> ServicePlatform.get().getGameService().gainCondition(ConditionId.DETAINED);

        TestFailFlavorInfoTemplate innerTemplate = new TestFailFlavorInfoTemplate(getTextBuilder(), testStat, modifier, onFail);
        template = new InfoFlavorTemplate(getTextBuilder(), this::onRewardAction, innerTemplate) {
            @Override
            protected void afterOnReward() {

            }
        };
        template.execute();
    }

    private void onRewardAction() {
        ServicePlatform.get().getService().hold();
        ServicePlatform.get().getService().hideBackground();
        ServicePlatform.get().getGameService().selectLocation(new SelectLocationData(null)).subscribe(locationId -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTokenService().spawnClueAt(locationId);
            ServicePlatform.get().getService().showLocationBackground(LocationId.LONDON);
            template.displayTestButton();
            ServicePlatform.get().getService().release();
        });
        ServicePlatform.get().getService().release();
    }

}
