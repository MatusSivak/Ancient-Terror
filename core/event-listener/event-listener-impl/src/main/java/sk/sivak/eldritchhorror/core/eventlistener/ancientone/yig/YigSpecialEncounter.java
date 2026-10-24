package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.util.*;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.location.*;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import static sk.sivak.eldritchhorror.core.constants.investigator.Stat.*;
import static sk.sivak.eldritchhorror.core.constants.condition.ConditionId.*;
import static sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig.YigEffects.*;

/** Eight two-stage encounters in the ruins beneath K'n-yan. */
final class YigSpecialEncounter {
    private final MysteryCardInfo mystery;
    private final Runnable progress;
    private boolean paperVisible;
    private final Runnable nothing = outcome(text("knyan.nothing"), () -> {});
    YigSpecialEncounter(MysteryCardInfo mystery,Runnable progress) {
        this.mystery=mystery;
        this.progress=outcome(text("knyan.progress"), progress);
    }
    void execute() {
        sequence(this::start);
    }
    private void start() {
        p().getService().showKnyanBackground();
        List<Integer> deck=mystery.getSpecialEncounterDeck();
        if(deck.isEmpty()){for(int i=1;i<=8;i++)deck.add(i);Collections.shuffle(deck);}
        int card=deck.remove(0);
        p().getEncounterService().showTypewriterPaper(true);
        paperVisible = true;
        p().getEncounterService().typeHeader(text("knyan.header"));
        p().getEncounterService().typeFlavor(text("knyan." + card + ".intro"));
        switch(card) {
            case 1:
                test(STRENGTH,-1,() -> nextTest(text("knyan.1.pass.flavor"), LORE,0,
                        progress, outcome(text("knyan.1.pass.fail.info"), () -> condition(CURSED))),
                        outcome(text("knyan.1.fail.info"), () -> {
                            damage(1,0);
                            nextTest(text("knyan.1.fail.flavor"), WILL,-1,
                                    nothing, outcome(text("knyan.1.fail.fail.info"), () -> p().getDoomOmenService().advanceDoom()));
                        }));
                break;
            case 2:
                test(LORE,-1,choice(text("knyan.2.pass.question"),
                        () -> spend(1,0,0,progress,outcome(text("knyan.2.paranoia.info"), () -> condition(PARANOIA))),
                        outcome(text("knyan.2.paranoia.info"), () -> condition(PARANOIA))),
                        () -> nextTest(text("knyan.2.fail.flavor"), LORE,0,
                                nothing, outcome(text("knyan.2.fail.fail.info"), () -> {damage(0,1);condition(CURSED);})));
                break;
            case 3:
                test(OBSERVATION,-1,() -> nextTest(text("knyan.3.pass.flavor"), LORE,0,
                        progress, outcome(text("knyan.3.pass.fail.info"), () -> {damage(0,1);condition(PARANOIA);})), () -> {
                    Runnable cursed=outcome(text("knyan.3.cursed.info"), () -> condition(CURSED));
                    if(!p().getConditionsDeck().canGetConditionId(investigator(),POISONED)) cursed.run();
                    else choice(text("knyan.3.fail.question"), () -> {
                        condition(POISONED);later(() -> {if(!has(POISONED))cursed.run();});
                    }, cursed).run();
                });
                break;
            case 4:
                test(WILL,-1,() -> nextTest(text("knyan.4.pass.flavor"), LORE,0,
                        progress, outcome(text("knyan.4.pass.fail.info"), () -> {damage(0,2);delayed();})),
                        outcome(text("knyan.4.fail.info"), () -> {
                            condition(HALLUCINATIONS);
                            nextTest(text("knyan.4.fail.flavor"), LORE,-1,
                                    nothing, outcome(text("knyan.4.fail.fail.info"), () -> damage(0,2)));
                        }));
                break;
            case 5:
                test(WILL,0,() -> nextTest(text("knyan.5.pass.flavor"), INFLUENCE,-1,
                        progress, outcome(text("knyan.5.pass.fail.info"), () -> {
                            List<LocationId> adjacent=new ArrayList<>();
                            for(LocationInfo.Connection c:p().getLocationMap().getLocationInfo(p().getInvestigators().getActiveInvestigator().getCurrentLocationId()).getConnections())adjacent.add(c.getLocationId());
                            chooseLocation(adjacent,text("knyan.5.escape.title"),l -> moveInvestigator(l));delayed();
                        })), outcome(text("knyan.5.fail.info"), () -> {damage(1,0);condition(INTERNAL_INJURY);}));
                break;
            case 6:
                test(OBSERVATION,0,() -> nextTest(text("knyan.6.pass.flavor"), WILL,-1,
                        progress, outcome(text("knyan.6.pass.fail.info"), () -> damage(0,2))),
                        outcome(text("knyan.6.fail.info"), () -> p().getMonsterService().ambush(NonEpicMonsterId.SERPENT_PEOPLE)
                                .subscribe(result -> outcome(result.getMonsterInfo().getCurrentHealth() <= 0
                                        ? text("knyan.6.fail.defeated.info")
                                        : text("knyan.6.fail.survived.info"), () -> {}).run())));
                break;
            case 7:
                test(OBSERVATION,0,() -> nextTest(text("knyan.7.pass.flavor"), STRENGTH,-1,
                        progress, outcome(text("knyan.7.pass.fail.info"), () -> damage(2,0))),
                        outcome(text("knyan.7.fail.info"), () -> condition(LOST_IN_TIME_AND_SPACE)));
                break;
            case 8:
                test(STRENGTH,-1,outcome(text("knyan.8.pass.info"), () -> {
                    p().getInvestigatorService().improveSkill(INFLUENCE);
                    nextTest(text("knyan.8.pass.flavor"), LORE,-1,
                            nothing, outcome(text("knyan.8.pass.fail.info"), () -> condition(POISONED)));
                }), () -> nextTest(text("knyan.8.fail.flavor"), OBSERVATION,-1,progress,nothing));
                break;
        }
        later(() -> {
            p().getEncounterService().finishTypewriterPaper();
            p().getService().hideBackground();
        });
    }
    private void test(Stat stat, int modifier, Runnable pass, Runnable fail) {
        // The shared helper hides and restores the paper before either callback.
        TypewriterUtils.displayTestButton(stat, modifier, () -> sequence(pass), () -> sequence(fail));
    }
    private void nextTest(String flavor, Stat stat, int modifier, Runnable pass, Runnable fail) {
        showPaper();
        p().getEncounterService().typeFlavor("\n" + flavor);
        test(stat,modifier,pass,fail);
    }
    private Runnable outcome(String info, Runnable effect) {
        return () -> sequence(() -> {
            showPaper();
            TypewriterUtils.confirmInfos(info).subscribe(() -> sequence(() -> {
                hidePaper();
                effect.run();
            }));
        });
    }
    private Runnable choice(String question, Runnable yes, Runnable no) {
        return () -> TypewriterUtils.noYesQuestion(question,
                () -> choose(no), () -> choose(yes));
    }
    private void choose(Runnable effect) {
        sequence(() -> {
            hidePaper();
            effect.run();
        });
    }
    private void showPaper() {
        if (!paperVisible) {
            p().getEncounterService().showTypewriterPaper(false);
            paperVisible = true;
        }
    }
    private void hidePaper() {
        if (paperVisible) {
            p().getEncounterService().hideTypewriterPaper();
            paperVisible = false;
        }
    }
}
