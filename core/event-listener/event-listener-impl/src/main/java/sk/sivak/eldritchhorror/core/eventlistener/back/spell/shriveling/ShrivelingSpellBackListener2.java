package sk.sivak.eldritchhorror.core.eventlistener.back.spell.shriveling;

import java8.features.function.Predicate;
import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;

import java.util.List;

public class ShrivelingSpellBackListener2 extends AbstractSpellBackListener {

    public ShrivelingSpellBackListener2(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]Lose two Health.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            ServicePlatform.get().getTokenService().loseHealth(2);
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
        List<MonsterInfo> otherMonstersOnThisLocation = findOtherMonstersOnThisLocation();
        if (otherMonstersOnThisLocation != null && otherMonstersOnThisLocation.size() > 0) {
            TypewriterUtils.confirmInfos("[#GOOD]Each other Monster on your space also loses two Health.[]").subscribe(() -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
                for (MonsterInfo monsterInfo : otherMonstersOnThisLocation) {
                    ServicePlatform.get().getMonsterService().dealDamageToMonster(monsterInfo, 2);
                }
                ServicePlatform.get().getService().release();
            });
        } else {
            TypewriterUtils.confirmInfos("No additional effect.").subscribe(() -> {
                ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            });
        }
    }

    private List<MonsterInfo> findOtherMonstersOnThisLocation() {
        LocationId currentLocationId = ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId();
        List<MonsterInfo> allMonstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(currentLocationId);
        Predicate<MonsterInfo> monsterInfoPredicate = monsterInfo -> !monsterInfo.equals(getData("monsterInfo"));
        return Stream.collectToList(allMonstersAtLocation, monsterInfoPredicate);
    }
}
