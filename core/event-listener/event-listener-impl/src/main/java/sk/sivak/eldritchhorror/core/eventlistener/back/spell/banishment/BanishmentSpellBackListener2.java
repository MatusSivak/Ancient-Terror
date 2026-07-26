package sk.sivak.eldritchhorror.core.eventlistener.back.spell.banishment;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.List;

public class BanishmentSpellBackListener2 extends AbstractSpellBackListener{

    public BanishmentSpellBackListener2(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1));
    }

    private void on0() {
        new DiscardThisSpellUnless(getSpellInfo(), getActiveInvestigatorId()).lose2Sanity();
    }

    private void on1() {
        TypewriterUtils.confirmInfos("[#GOOD]Discard additional Monster on that space\n" +
                "with toughness equal to or less than\n" +
                "your test result.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(getData("locationId"));
            List<MonsterInfo> filteredMonsters = Stream.collectToList(monstersAtLocation,
                    monsterInfo -> !monsterInfo.isEpic() && monsterInfo.getToughness() <= getTestData().getScore());
            if (!filteredMonsters.isEmpty()) {
                SelectMonsterData selectMonsterData = new SelectMonsterData();
                selectMonsterData.setAvailableMonsters(filteredMonsters);
                ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(monsterInfo -> {
                    ServicePlatform.get().getService().hold();
                    ServicePlatform.get().getMonsterService().discardMonster(monsterInfo);
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
            ServicePlatform.get().getService().release();
        });
    }

    private void moveCameraToInvestigator() {
        ServicePlatform.get().getService().moveCameraToLocation(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
    }
}
