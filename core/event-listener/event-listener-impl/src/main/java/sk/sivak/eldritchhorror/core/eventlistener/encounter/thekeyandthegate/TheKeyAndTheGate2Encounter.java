package sk.sivak.eldritchhorror.core.eventlistener.encounter.thekeyandthegate;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionTrait;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.EncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TestPassFlavorInfoFailFlavorInfoTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.template.TheKeyAndTheGateEncounterTemplate;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

public class TheKeyAndTheGate2Encounter extends AbstractTheKeyAndTheGateEncounter {

    public TheKeyAndTheGate2Encounter() {
        super(2);
    }

    @Override
    protected void execute() {

        EncounterTemplate passTemplate = () -> {
            int cultists = Stream.count(ServicePlatform.get().getMonsterCup().getMonsters(),
                    monsterInfo -> monsterInfo.getMonsterId() == NonEpicMonsterId.CULTIST);

            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().typeInfo("Roll one less die\nfor each spawned Cultist.");

            new TestPassFlavorInfoFailFlavorInfoTemplate(getTextBuilder(), Stat.INFLUENCE, -cultists, () -> {
                ServicePlatform.get().getGameService().advanceActiveMystery();
            }, () -> {
                ServicePlatform.get().getDoomOmenService().advanceDoom();
            }).withoutPassFlavor().execute();

            ServicePlatform.get().getService().release();
        };

        EncounterTemplate failTemplate = () -> { // OK
            TypewriterUtils.confirmInfos(
                    getTextBuilder().withInfo(1).build(),
                    getTextBuilder().withInfo(2).build(),
                    getTextBuilder().withInfo(3).build()).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                ServicePlatform.get().getGameService().gainCondition(ConditionId.CURSED);
                ServicePlatform.get().getGameService().gainCondition(ConditionTrait.MADNESS);
                ServicePlatform.get().getGameService().gainCondition(ConditionTrait.INJURY);
                ServicePlatform.get().getService().release();
            });
        };

        new TheKeyAndTheGateEncounterTemplate(getTextBuilder(), Stat.LORE, -1, passTemplate, failTemplate).execute();
    }
}
