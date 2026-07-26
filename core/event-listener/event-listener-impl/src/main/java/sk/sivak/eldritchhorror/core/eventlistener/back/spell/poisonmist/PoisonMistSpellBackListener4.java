package sk.sivak.eldritchhorror.core.eventlistener.back.spell.poisonmist;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.AbstractMonsterInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoisonMistSpellBackListener4 extends AbstractSpellBackListener {

    public PoisonMistSpellBackListener4(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2, true));
    }

    private void on0() {
        List<MonsterInfo> otherMonstersOnThisLocation = findMonstersOnThisLocation();
        if (otherMonstersOnThisLocation != null && otherMonstersOnThisLocation.size() > 0) {
            String info = "[#BAD]Immediately resolve a Combat Encounter against each Monster on your space in the order of your choice.[]";
            TypewriterUtils.confirmInfos(info).subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                resolveCombatEncounters(otherMonstersOnThisLocation);
                ServicePlatform.get().getService().release();
            });
        } else {
            TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
        }
    }

    private void resolveCombatEncounters(List<MonsterInfo> monstersOnThisLocation) {
        if (monstersOnThisLocation.isEmpty()) {
            return;
        }
        SelectMonsterData selectMonsterData = new SelectMonsterData();
        selectMonsterData.setAvailableMonsters(monstersOnThisLocation);
        selectMonsterData.setTitleText("Select Monster");
        selectMonsterData.setHideText("Display Monsters?");
        ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(monsterInfo -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTestService().combat(monsterInfo);
            monstersOnThisLocation.remove(monsterInfo);
            resolveCombatEncounters(monstersOnThisLocation);
            ServicePlatform.get().getService().release();
        });
    }

    private List<MonsterInfo> findMonstersOnThisLocation() {
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        return ServicePlatform.get().getMonsterCup().getMonstersAtLocation(currentLocationId);
    }


    private void on1() {
        TypewriterUtils.confirmInfos("[#BAD]Lose one Sanity[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseSanity(1);
            ServicePlatform.get().getService().release();
        });
    }

    private void on2() {
        TypewriterUtils.confirmInfos("[#GOOD]You may choose Monsters\n" +
                "as if each Monster's toughness\n" +
                "is reduced by 1 to a minimum of 1.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();

            Map<MonsterInfo, Integer> monsterToughnessMap = new HashMap<>();

            List<MonsterInfo> monstersOnThisLocation = findMonstersOnThisLocation();
            for (MonsterInfo monsterInfo : monstersOnThisLocation) {
                monsterToughnessMap.put(monsterInfo, monsterInfo.getToughness());
                ((AbstractMonsterInfo)monsterInfo).setToughness(Math.max(1, monsterInfo.getToughness()-1));
            }
            runMainSpellAction();
            ServicePlatform.get().getService().addEventCommand(whatever -> {
                for (MonsterInfo monsterInfo : monstersOnThisLocation) {
                    ((AbstractMonsterInfo)monsterInfo).setToughness(monsterToughnessMap.get(monsterInfo));
                }
            });
            ServicePlatform.get().getService().release();
        });
    }

}
