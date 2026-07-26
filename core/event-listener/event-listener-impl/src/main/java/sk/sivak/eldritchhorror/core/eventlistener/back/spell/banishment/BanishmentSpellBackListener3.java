package sk.sivak.eldritchhorror.core.eventlistener.back.spell.banishment;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.location.FindNearestData;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;

import java.util.List;

public class BanishmentSpellBackListener3 extends AbstractSpellBackListener{

    public BanishmentSpellBackListener3(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1, this::moveCameraToInvestigator));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]Monsters surge the nearest Gate![]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            FindNearestData nearestGate = ServicePlatform.get().getLocationMap().findNearest(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId(),
                    locationInfo -> ServicePlatform.get().getGateStackRead().isGateAtLocation(locationInfo.getLocationId()));
            for (int i = 0; i < ServicePlatform.get().getModel().getReferenceCard().getMonsterSurge(); i++) {
                SpawnMonsterData spawnMonsterData = new SpawnMonsterData();
                spawnMonsterData.setLocationId(nearestGate.getLocationId());
                ServicePlatform.get().getGameService().spawnMonster(spawnMonsterData);
            }
            ServicePlatform.get().getService().release();
        });
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Sanity.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        TypewriterUtils.confirmInfos("[#GOOD]Discard any number of Monsters on that space\n" +
                "with total toughness equal to or less than\n" +
                "your test result.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            if (getData("monsterInfo") == null) {
                ServicePlatform.get().getService().release();
                return;
            }
            if (((MonsterInfo) getData("monsterInfo")).getToughness() == null) {
                ServicePlatform.get().getService().release();
                return;
            }
            int powerRemaining = getTestData().getScore() - ((MonsterInfo) getData("monsterInfo")).getToughness();

            if (powerRemaining == 0) {
                ServicePlatform.get().getService().release();
                return;
            }
            oneMoreTime(powerRemaining);

            ServicePlatform.get().getService().release();
        });
    }

    private void oneMoreTime(int powerRemaining) {
        List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(getData("locationId"));
        List<MonsterInfo> filteredMonsters = Stream.collectToList(monstersAtLocation,
                monsterInfo -> !monsterInfo.isEpic() && monsterInfo.getToughness() <= powerRemaining);
        if (!filteredMonsters.isEmpty()) {
            SelectMonsterData selectMonsterData = new SelectMonsterData();
            selectMonsterData.setAvailableMonsters(filteredMonsters);
            ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(monsterInfo -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getMonsterService().discardMonster(monsterInfo);
                ServicePlatform.get().getService().addEventCommand( in -> {
                    oneMoreTime(powerRemaining - monsterInfo.getToughness());
                });
                ServicePlatform.get().getService().release();
            });
        } else {
            Question<Object> question = new Question<>();
            question.setOptions(Question.Option.okOption);
            question.setTitle("Can't discard another Monster.");
            ServicePlatform.get().getGameService().ask(question).subscribe(ok -> {
                moveCameraToInvestigator();
            });
        }
    }

    private void moveCameraToInvestigator() {
        ServicePlatform.get().getService().moveCameraToLocation(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
    }
}
