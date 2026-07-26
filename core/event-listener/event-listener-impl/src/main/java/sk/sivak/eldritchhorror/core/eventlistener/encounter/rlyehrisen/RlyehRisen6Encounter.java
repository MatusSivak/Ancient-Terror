package sk.sivak.eldritchhorror.core.eventlistener.encounter.rlyehrisen;

import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.RlyehRisenEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

public class RlyehRisen6Encounter extends AbstractRlyehRisenEncounter {

    public RlyehRisen6Encounter() {
        super(6);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, -1, () -> {
            ServicePlatform.get().getGameService().advanceActiveMystery();
        }, () -> {
            gainCondition(ConditionId.PARANOIA);
        });

        EncounterTemplate failTemplate = new InfoFlavorTemplate(getTextBuilder(), () -> {
            gainCondition(ConditionId.LEG_INJURY);
        }, () -> {
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo(2).build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getService().hideBackground();
                SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
                LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
                spawnMonsterData.setLocationId(currentLocationId);
                ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
                ServicePlatform.get().getService().release();
            });
        });


        new InfoFlavorTemplate(getTextBuilder(), () -> {
            loseSanity(1);
        }, new RlyehRisenEncounterTemplate(getTextBuilder(), Stat.OBSERVATION, -1, passTemplate, failTemplate)).execute();
    }
}
