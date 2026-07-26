package sk.sivak.eldritchhorror.core.eventlistener.back.spell.shriveling;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.Iterator;
import java.util.List;

public class ShrivelingSpellBackListener4 extends AbstractSpellBackListener{

    public ShrivelingSpellBackListener4(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
    }

    private void on0() {
        List<MonsterInfo> otherMonstersOnThisLocation = findOtherMonstersOnThisLocation();
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

    private void resolveCombatEncounters(List<MonsterInfo> otherMonstersOnThisLocation) {
        if (otherMonstersOnThisLocation.isEmpty()) {
            return;
        }
        SelectMonsterData selectMonsterData = new SelectMonsterData();
        selectMonsterData.setAvailableMonsters(otherMonstersOnThisLocation);
        selectMonsterData.setTitleText("Select Monster");
        selectMonsterData.setHideText("Display Monsters?");
        ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(monsterInfo -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getTestService().combat(monsterInfo);
            otherMonstersOnThisLocation.remove(monsterInfo);
            resolveCombatEncounters(otherMonstersOnThisLocation);
            ServicePlatform.get().getService().release();
        });
    }

    private void on1() {
        TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
        });
    }

    private List<MonsterInfo> findOtherMonstersOnThisLocation() {
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        return ServicePlatform.get().getMonsterCup().getMonstersAtLocation(currentLocationId);
    }
}
