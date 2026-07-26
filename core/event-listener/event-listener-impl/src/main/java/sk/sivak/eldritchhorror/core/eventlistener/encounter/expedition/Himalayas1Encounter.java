package sk.sivak.eldritchhorror.core.eventlistener.encounter.expedition;

import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ComplexEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.ExpeditionEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.InfoFlavorTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;

public class Himalayas1Encounter extends AbstractExpeditionEncounter {

    public Himalayas1Encounter() {
        super(1, LocationId.THE_HIMALAYAS);
    }
    private MonsterInfo spawnedMonster;

    @Override
    protected void execute() {
        InfoFlavorTemplate passTemplate = new InfoFlavorTemplate(getTextBuilder(),
                this::retreatDoom,
                new TestFailFlavorInfoTemplate(getTextBuilder(), Stat.LORE, 0,
                        () -> loseSanity(2)).withExpeditionFlavor());


        EncounterTemplate failTemplate = new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0,
                () -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getService().hideBackground();
                    ServicePlatform.get().getMonsterService().discardMonster(spawnedMonster);
                    retreatDoom();
                    ServicePlatform.get().getService().release();
                },
                () -> loseSanity(1)
        ).withTwoPassInfos().withoutPassFlavor().withExpeditionFlavor();

        new ExpeditionEncounterTemplate(getTextBuilder(), null, 0, passTemplate, failTemplate){

            @Override
            public void execute() {
                TypewriterUtils.confirmInfos("[#BAD]Spawn a Monster on your space\nand encounter it[]").subscribe(this::spawnMonster);
            }

            private void spawnMonster() {
                SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
                spawnMonsterData.setLocationId(LocationId.THE_HIMALAYAS);
                spawnMonsterData.setMonsterId(null);
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().hideTypewriterPaper();
                ServicePlatform.get().getService().hideBackground();
                ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
                ServicePlatform.get().getService().<SpawnMonsterData>addEventCommand(spawnMonsterDataOutput -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getTestService().combat(spawnMonsterDataOutput.getMonsterInfo());
                    ServicePlatform.get().getService().<CombatData>addEventCommand(combatOutput -> {
                        if (combatOutput.getMonsterInfo().isAlive()) {
                            spawnedMonster = combatOutput.getMonsterInfo();
                            ServicePlatform.get().getService().hold();
                            ServicePlatform.get().getService().showLocationBackground(LocationId.THE_HIMALAYAS);
                            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                            onFail();
                            ServicePlatform.get().getService().release();
                        } else {
                            ServicePlatform.get().getService().hold();
                            ServicePlatform.get().getService().showLocationBackground(LocationId.THE_HIMALAYAS);
                            ServicePlatform.get().getEncounterService().showTypewriterPaper(false);
                            onSuccess();
                            ServicePlatform.get().getService().release();
                        }
                    });
                    ServicePlatform.get().getService().release();
                });
                ServicePlatform.get().getService().release();
            }
        }.execute();
    }
}
