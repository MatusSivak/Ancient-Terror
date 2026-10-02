package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.util.*;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.location.*;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import static sk.sivak.eldritchhorror.core.constants.investigator.Stat.*;
import static sk.sivak.eldritchhorror.core.constants.condition.ConditionId.*;
import static sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig.YigEffects.*;

/** Eight two-stage encounters in the ruins beneath K'n-yan. */
final class YigSpecialEncounter {
    private final MysteryCardInfo mystery;
    private final Runnable progress;
    private static final Runnable NOTHING=() -> {};
    YigSpecialEncounter(MysteryCardInfo mystery,Runnable progress){this.mystery=mystery;this.progress=progress;}
    void execute() {
        List<Integer> deck=mystery.getSpecialEncounterDeck();
        if(deck.isEmpty()){for(int i=1;i<=8;i++)deck.add(i);Collections.shuffle(deck);}
        int card=deck.remove(0);
        p().getGameService().displayText(INTRO[card-1]);
        switch(card) {
            case 1:test(STRENGTH,-1,() -> test(LORE,0,progress,() -> condition(CURSED)),() -> {
                damage(1,0);test(WILL,-1,NOTHING,() -> p().getDoomOmenService().advanceDoom());
            });break;
            case 2:test(LORE,-1,() -> ask("Spend 1 Clue to decipher the hidden record?",
                    () -> spend(1,0,0,progress,() -> condition(PARANOIA)),() -> condition(PARANOIA)),
                    () -> test(LORE,0,NOTHING,() -> {damage(0,1);condition(CURSED);}));break;
            case 3:test(OBSERVATION,-1,() -> test(LORE,0,progress,() -> {damage(0,1);condition(PARANOIA);}),() -> {
                if(!p().getConditionsDeck().canGetConditionId(investigator(),POISONED))condition(CURSED);
                else ask("Gain Poisoned to avoid the curse?",() -> {condition(POISONED);later(() -> {if(!has(POISONED))condition(CURSED);});},() -> condition(CURSED));
            });break;
            case 4:test(WILL,-1,() -> test(LORE,0,progress,() -> {damage(0,2);delayed();}),() -> {
                condition(HALLUCINATIONS);test(LORE,-1,NOTHING,() -> damage(0,2));
            });break;
            case 5:test(WILL,0,() -> test(INFLUENCE,-1,progress,() -> {
                List<LocationId> adjacent=new ArrayList<>();
                for(LocationInfo.Connection c:p().getLocationMap().getLocationInfo(p().getInvestigators().getActiveInvestigator().getCurrentLocationId()).getConnections())adjacent.add(c.getLocationId());
                chooseLocation(adjacent,"Escape to an adjacent space",l -> moveInvestigator(l));delayed();
            }),() -> {damage(1,0);condition(INTERNAL_INJURY);});break;
            case 6:test(OBSERVATION,0,() -> test(WILL,-1,progress,() -> damage(0,2)),
                    () -> p().getMonsterService().ambush(NonEpicMonsterId.SERPENT_PEOPLE).subscribe());break;
            case 7:test(OBSERVATION,0,() -> test(STRENGTH,-1,progress,() -> damage(2,0)),() -> condition(LOST_IN_TIME_AND_SPACE));break;
            case 8:test(STRENGTH,-1,() -> {p().getInvestigatorService().improveSkill(INFLUENCE);test(LORE,-1,NOTHING,() -> condition(POISONED));},
                    () -> test(OBSERVATION,-1,progress,NOTHING));break;
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
