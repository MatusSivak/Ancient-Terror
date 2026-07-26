package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.shub_niggurath;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.basic.SelectLocationData;

import java.util.List;

public class ResearchWilderness9Encounter extends AbstractShubNiggurathResearchEncounter{

    public ResearchWilderness9Encounter() {
        super(9, LocationType.WILDERNESS);
    }

    @Override
    protected void execute() {
        new TestPassFlavorInfoTemplate(getTextBuilder(), Stat.OBSERVATION, 0, () -> {
            TypewriterUtils.noYesQuestion(getTextBuilder().withPass().withQuestion().build(), () -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            }, this::onYes);
        }).withoutHidingPaperBeforePass().withoutPassInfo().withResearchFlavor().execute();
    }

    private void onYes() {
        ServicePlatform.get().getTokenService().canSpend(0,0,2,0).subscribe(canSpend -> {
            if (!canSpend.hasEnough()) {
                TypewriterUtils.confirmInfos(" \nNot enough Health.").subscribe(() -> {
                    ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                });
                return;
            }
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getService().hideBackground();
            ServicePlatform.get().getTokenService().spend(0,0,2,0).subscribe(spendData -> {
                if (!spendData.hasEnough()) {
                    return;
                }
                if (ServicePlatform.get().getMonsterCup().getMonsters().isEmpty()) {
                    return;
                }
                ServicePlatform.get().getService().hold(); // OK
                spendData.pay();
                List<LocationId> locationIds = Stream.collectToList(Stream.map(ServicePlatform.get().getMonsterCup().getMonsters(), MonsterInfo::getCurrentLocation));
                ServicePlatform.get().getGameService().selectLocation(new SelectLocationData(locationIds)).subscribe(selectedLocation -> {
                    ServicePlatform.get().getService().hold(); // OK
                    List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(selectedLocation);
                    for (MonsterInfo monsterInfo : monstersAtLocation) {
                        ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 1);
                    }
                    ServicePlatform.get().getService().release(); // OK
                });
                ServicePlatform.get().getService().release(); // OK
            });
            ServicePlatform.get().getService().release();
        });
    }

}
