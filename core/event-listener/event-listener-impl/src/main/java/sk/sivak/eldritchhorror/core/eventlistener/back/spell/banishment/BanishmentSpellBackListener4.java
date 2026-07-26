package sk.sivak.eldritchhorror.core.eventlistener.back.spell.banishment;

import java8.features.stream.Stream;
import sk.sivak.eldritchhorror.core.constants.gate.GateInfo;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.constants.spell.SpellInfo;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.AbstractSpellBackListener;
import sk.sivak.eldritchhorror.core.eventlistener.back.spell.util.DiscardThisSpellUnless;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.SelectMonsterData;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static sk.sivak.eldritchhorror.core.eventlistener.spell.BanishmentListener.findNearestGates;

public class BanishmentSpellBackListener4 extends AbstractSpellBackListener{

    public BanishmentSpellBackListener4(SpellInfo spellInfo) {
        super(spellInfo);
    }

    @Override
    protected void fillSpellBackEffectList(List<ConcreteSpellBackEffect> spellBackEffectList) {
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on0));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on1, this::moveCameraToInvestigator));
        spellBackEffectList.add(new ConcreteSpellBackEffect(this::on2, true));
    }

    private void on0() {
        TypewriterUtils.confirmInfos("[#BAD]One Monster moves to your space\n" +
                "and you immediately encounter it[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            List<MonsterInfo> regularMonsters = Stream.collectToList(ServicePlatform.get().getMonsterCup().getMonsters(), monsterInfo -> !monsterInfo.isEpic());
            Collections.shuffle(regularMonsters);
            ServicePlatform.get().getMonsterService().moveMonster(regularMonsters.get(0), ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
            ServicePlatform.get().getTestService().combat(regularMonsters.get(0));
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
        TypewriterUtils.confirmInfos("[#GOOD]Discard one Monster on the nearest Gate\n" +
                "regardless of its toughness instead.[]").subscribe(() -> {
            ServicePlatform.get().getService().hold();
            ServicePlatform.get().getEncounterService().finishTypewriterPaper();
            List<GateInfo> nearestGates = findNearestGates();
            List<MonsterInfo> filteredMonsters = new LinkedList<>();
            for (GateInfo nearestGate : nearestGates) {
                List<MonsterInfo> monstersAtLocation = ServicePlatform.get().getMonsterCup().getMonstersAtLocation(nearestGate.getLocationId());
                filteredMonsters.addAll(Stream.collectToList(monstersAtLocation,
                        monsterInfo -> !monsterInfo.isEpic()));
            }
            if (filteredMonsters.isEmpty()) {
                Question<Object> question = new Question<>();
                question.setOptions(Question.Option.okOption);
                question.setTitle("No Monster can be discarded.");
                ServicePlatform.get().getGameService().ask(question).subscribe();
                ServicePlatform.get().getService().release();
                return;
            }
            SelectMonsterData selectMonsterData = new SelectMonsterData();
            selectMonsterData.setAvailableMonsters(filteredMonsters);
            ServicePlatform.get().getMonsterService().selectSingleMonster(selectMonsterData).subscribe(monsterInfo -> {
                ServicePlatform.get().getService().hold();
                ServicePlatform.get().getMonsterService().discardMonster(monsterInfo);
                moveCameraToInvestigator();
                ServicePlatform.get().getService().release();
            });
            ServicePlatform.get().getService().release();
        });
    }

    private void moveCameraToInvestigator() {
        ServicePlatform.get().getService().moveCameraToLocation(ServicePlatform.get().getInvestigators().getActiveInvestigator().getCurrentLocationId());
    }


}
