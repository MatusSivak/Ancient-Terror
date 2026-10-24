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
        this.progress=outcome("[#GOOD]Advance the Active Mystery.[]", progress);
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
                test(STRENGTH,-1,() -> nextTest("You heave the door open. The glyphs around the stone form a warning, and perhaps a map of what lies below.", LORE,0,
                        progress, outcome("[#BAD]Gain Cursed.[]", () -> condition(CURSED))),
                        outcome("[#BAD]Lose 1 Health.[]", () -> {
                            damage(1,0);
                            nextTest("The worn steps crumble and you tumble into the dark. Something vast below stirs, and its attention presses against your mind.", WILL,-1,
                                    nothing, outcome("[#BAD]Advance Doom by 1.[]", () -> p().getDoomOmenService().advanceDoom()));
                        }));
                break;
            case 2:
                test(LORE,-1,choice("[#BAD]Spend 1 Clue[] to decipher the hidden record and [#GOOD]advance the Active Mystery[]? Otherwise, [#BAD]gain Paranoia.[]",
                        () -> spend(1,0,0,progress,outcome("[#BAD]Gain Paranoia.[]", () -> condition(PARANOIA))),
                        outcome("[#BAD]Gain Paranoia.[]", () -> condition(PARANOIA))),
                        () -> nextTest("The crawling script coils around your thoughts and drags you deeper into the record. You struggle to tear your mind free.", LORE,0,
                                nothing, outcome("[#BAD]Lose 1 Sanity and gain Cursed.[]", () -> {damage(0,1);condition(CURSED);})));
                break;
            case 3:
                test(OBSERVATION,-1,() -> nextTest("The trail ends at an unfinished experiment, still twitching in its vat. Its makers' notes lie beside it, if you can read them.", LORE,0,
                        progress, outcome("[#BAD]Lose 1 Sanity and gain Paranoia.[]", () -> {damage(0,1);condition(PARANOIA);})), () -> {
                    Runnable cursed=outcome("[#BAD]Gain Cursed.[]", () -> condition(CURSED));
                    if(!p().getConditionsDeck().canGetConditionId(investigator(),POISONED)) cursed.run();
                    else choice("[#BAD]Gain Poisoned[] to avoid gaining Cursed?", () -> {
                        condition(POISONED);later(() -> {if(!has(POISONED))cursed.run();});
                    }, cursed).run();
                });
                break;
            case 4:
                test(WILL,-1,() -> nextTest("You wall off your own thoughts and let the whisper speak. Its message arrives in fragments you must piece together.", LORE,0,
                        progress, outcome("[#BAD]Lose 2 Sanity and become Delayed.[]", () -> {damage(0,2);delayed();})),
                        outcome("[#BAD]Gain Hallucinations.[]", () -> {
                            condition(HALLUCINATIONS);
                            nextTest("The whisper floods your mind, and the blue-lit streets fill with impossible visions. You search for a pattern in the madness.", LORE,-1,
                                    nothing, outcome("[#BAD]Lose 2 Sanity.[]", () -> damage(0,2)));
                        }));
                break;
            case 5:
                test(WILL,0,() -> nextTest("You meet the spokesman's ageless stare without flinching. The silent assembly waits to hear what you can offer them.", INFLUENCE,-1,
                        progress, outcome("[#BAD]Move to an adjacent space and become Delayed.[]", () -> {
                            List<LocationId> adjacent=new ArrayList<>();
                            for(LocationInfo.Connection c:p().getLocationMap().getLocationInfo(p().getInvestigators().getActiveInvestigator().getCurrentLocationId()).getConnections())adjacent.add(c.getLocationId());
                            chooseLocation(adjacent,"Escape to an adjacent space",l -> moveInvestigator(l));delayed();
                        })), outcome("[#BAD]Lose 1 Health and gain Internal Injury.[]", () -> {damage(1,0);condition(INTERNAL_INJURY);}));
                break;
            case 6:
                test(OBSERVATION,0,() -> nextTest("Among the carvings, a pair of eyes blinks. You steady your nerves and step forward to face the temple's guardian.", WILL,-1,
                        progress, outcome("[#BAD]Lose 2 Sanity.[]", () -> damage(0,2))),
                        outcome("[#BAD]A Serpent People ambushes you.[]", () -> p().getMonsterService().ambush(NonEpicMonsterId.SERPENT_PEOPLE)
                                .subscribe(result -> outcome(result.getMonsterInfo().getCurrentHealth() <= 0
                                        ? "[#GOOD]You defeated the Serpent People.[]\nThe encounter ends without advancing the Active Mystery."
                                        : "The ambush is over.\nThe encounter ends without advancing the Active Mystery.", () -> {}).run())));
                break;
            case 7:
                test(OBSERVATION,0,() -> nextTest("You glimpse the chamber's entrance just as the walls begin to grind shut. You throw your weight against the stone.", STRENGTH,-1,
                        progress, outcome("[#BAD]Lose 2 Health.[]", () -> damage(2,0))),
                        outcome("[#BAD]Gain Lost in Time and Space.[]", () -> condition(LOST_IN_TIME_AND_SPACE)));
                break;
            case 8:
                test(STRENGTH,-1,outcome("[#GOOD]Improve Influence.[]", () -> {
                    p().getInvestigatorService().improveSkill(INFLUENCE);
                    nextTest("Your victory wins the crowd's favor. A robed elder brings forward a strange offering, and the arena falls silent as you examine it.", LORE,-1,
                            nothing, outcome("[#BAD]Gain Poisoned.[]", () -> condition(POISONED)));
                }), () -> nextTest("You are hurled back into the jeering crowd. In the confusion, you slip away and search for a path deeper into the city.", OBSERVATION,-1,progress,nothing));
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
        "Behind the mound's false wall, a stairway spirals down toward a blue glow. A stone door bars the way, its glyphs ringing a pulsing green stone.",
        "Metal cylinders in a buried library hold the chronicles of K'n-yan, back to the age when serpent men ruled. The script crawls whenever you look away.",
        "The serpent men's laboratories lie shattered and abandoned, yet something has been dragging itself between the broken vessels. You follow its trail.",
        "A soft voice rises from beneath the streets, offering every secret of the city in exchange for a place in your mind. You fight to keep your thoughts your own.",
        "In a sunken hall, the deathless people of K'n-yan receive you in perfect silence. A single spokesman addresses you, weighing every word you say.",
        "Shed scales litter the dust before an idol of the Father of Serpents. Whatever guards this temple is still alive, and still watching.",
        "The chamber you seek lies somewhere in these ruins, but the walls slide and fold like the coils of a great snake whenever you draw near.",
        "You stumble into an arena where the people of K'n-yan watch captives fight for sport. A guard shoves you onto the sand as the crowd roars."
    };
}
