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
    private final Runnable nothing = outcome("Nothing else happens.", () -> {});
    YigSpecialEncounter(MysteryCardInfo mystery,Runnable progress) {
        this.mystery=mystery;
        this.progress=outcome("[#GOOD]Place 1 Eldritch token on the Active Mystery.[]", progress);
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
        p().getEncounterService().typeHeader("K'n-yan");
        p().getEncounterService().typeFlavor(INTRO[card-1]);
        switch(card) {
            case 1:
                test(STRENGTH,-1,() -> nextTest("The sealed door yields. You study the warning around the stone.", LORE,0,
                        progress, outcome("[#BAD]Gain Cursed.[]", () -> condition(CURSED))),
                        outcome("[#BAD]Lose 1 Health.[]", () -> {
                            damage(1,0);
                            nextTest("The stairway gives way beneath you. A presence below presses against your thoughts.", WILL,-1,
                                    nothing, outcome("[#BAD]Advance Doom by 1.[]", () -> p().getDoomOmenService().advanceDoom()));
                        }));
                break;
            case 2:
                test(LORE,-1,choice("[#BAD]Spend 1 Clue[] to decipher the hidden record and [#GOOD]place 1 Eldritch token on the Active Mystery[]? Otherwise, [#BAD]gain Paranoia.[]",
                        () -> spend(1,0,0,progress,outcome("[#BAD]Gain Paranoia.[]", () -> condition(PARANOIA))),
                        outcome("[#BAD]Gain Paranoia.[]", () -> condition(PARANOIA))),
                        () -> nextTest("The changing script draws you deeper into the record. You search for a way to break its hold.", LORE,0,
                                nothing, outcome("[#BAD]Lose 1 Sanity and gain Cursed.[]", () -> {damage(0,1);condition(CURSED);})));
                break;
            case 3:
                test(OBSERVATION,-1,() -> nextTest("The tracks lead to an unfinished experiment. Its notes may explain what happened here.", LORE,0,
                        progress, outcome("[#BAD]Lose 1 Sanity and gain Paranoia.[]", () -> {damage(0,1);condition(PARANOIA);})), () -> {
                    Runnable cursed=outcome("[#BAD]Gain Cursed.[]", () -> condition(CURSED));
                    if(!p().getConditionsDeck().canGetConditionId(investigator(),POISONED)) cursed.run();
                    else choice("[#BAD]Gain Poisoned[] to avoid gaining Cursed?", () -> {
                        condition(POISONED);later(() -> {if(!has(POISONED))cursed.run();});
                    }, cursed).run();
                });
                break;
            case 4:
                test(WILL,-1,() -> nextTest("You separate your own thoughts from the whisper. Now you try to understand its message.", LORE,0,
                        progress, outcome("[#BAD]Lose 2 Sanity and become Delayed.[]", () -> {damage(0,2);delayed();})),
                        outcome("[#BAD]Gain Hallucinations.[]", () -> {
                            condition(HALLUCINATIONS);
                            nextTest("The whisper fills the streets with impossible visions. You seek a pattern in the confusion.", LORE,-1,
                                    nothing, outcome("[#BAD]Lose 2 Sanity.[]", () -> damage(0,2)));
                        }));
                break;
            case 5:
                test(WILL,0,() -> nextTest("You hold the spokesman's gaze. The silent audience waits for your reply.", INFLUENCE,-1,
                        progress, outcome("[#BAD]Move to an adjacent space and become Delayed.[]", () -> {
                            List<LocationId> adjacent=new ArrayList<>();
                            for(LocationInfo.Connection c:p().getLocationMap().getLocationInfo(p().getInvestigators().getActiveInvestigator().getCurrentLocationId()).getConnections())adjacent.add(c.getLocationId());
                            chooseLocation(adjacent,"Escape to an adjacent space",l -> moveInvestigator(l));delayed();
                        })), outcome("[#BAD]Lose 1 Health and gain Internal Injury.[]", () -> {damage(1,0);condition(INTERNAL_INJURY);}));
                break;
            case 6:
                test(OBSERVATION,0,() -> nextTest("You find the guardian's watchful presence among the carvings. You steady yourself and approach.", WILL,-1,
                        progress, outcome("[#BAD]Lose 2 Sanity.[]", () -> damage(0,2))),
                        outcome("[#BAD]A Serpent People ambushes you.[]", () -> p().getMonsterService().ambush(NonEpicMonsterId.SERPENT_PEOPLE).subscribe()));
                break;
            case 7:
                test(OBSERVATION,0,() -> nextTest("You find the chamber, but the shifting walls are closing its entrance. You force your way through.", STRENGTH,-1,
                        progress, outcome("[#BAD]Lose 2 Health.[]", () -> damage(2,0))),
                        outcome("[#BAD]Gain Lost in Time and Space.[]", () -> condition(LOST_IN_TIME_AND_SPACE)));
                break;
            case 8:
                test(STRENGTH,-1,outcome("[#GOOD]Improve Influence.[]", () -> {
                    p().getInvestigatorService().improveSkill(INFLUENCE);
                    nextTest("Your display wins the audience's attention. You examine the strange offering they bring forward.", LORE,-1,
                            nothing, outcome("[#BAD]Gain Poisoned.[]", () -> condition(POISONED)));
                }), () -> nextTest("Forced back into the crowd, you search for another route through the underground city.", OBSERVATION,-1,progress,nothing));
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
    private static final String[] INTRO={
        "A sealed stairway descends beneath K'n-yan. Beyond it, a carved warning surrounds a pulsing green stone.",
        "The buried library preserves an account of the serpent kingdom. Its script changes whenever you look away.",
        "Something has been moving through the abandoned laboratories. You follow its tracks between the broken vessels.",
        "A whisper beneath the streets promises to reveal the city's secrets. You try to keep your own thoughts intact.",
        "The inhabitants of a sunken hall receive you in silence. Their spokesman measures every word you speak.",
        "Fresh marks interrupt the dust on a temple floor. A living guardian may still be watching this place.",
        "You search for a passage through the ruins. The walls themselves seem to shift around the chamber you need.",
        "A struggle in the underground city draws an unexpected audience. Their attention may offer another way forward."
    };
}
