package sk.sivak.eldritchhorror.core.eventlistener.encounter.research.yig;

import java.util.*;
import rx.functions.Action1;
import sk.sivak.eldritchhorror.core.constants.MysteryCardId;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneId;
import sk.sivak.eldritchhorror.core.constants.artifact.ArtifactId;
import sk.sivak.eldritchhorror.core.constants.asset.AssetTrait;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.location.*;
import sk.sivak.eldritchhorror.core.constants.monster.NonEpicMonsterId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.EventListenerImpl;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.research.AbstractResearchEncounter;
import sk.sivak.eldritchhorror.core.eventlistener.typewriter.TypewriterUtils;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.combat.CombatData;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.LoseImprovementData;
import sk.sivak.eldritchhorror.core.eventtype.data.test.*;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainClueData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;
import static sk.sivak.eldritchhorror.core.constants.investigator.Stat.*;
import static sk.sivak.eldritchhorror.core.constants.condition.ConditionId.*;
import static sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig.YigEffects.*;

/** The 24 research cards each supply one encounter for each space type. */
public final class YigResearchEncounter extends AbstractResearchEncounter {
    private final int page;
    private boolean gainedClue;
    private boolean paperVisible;
    private String openingChoice;
    private static final Runnable NOTHING = () -> {};
    public YigResearchEncounter(int page, LocationType type) {
        super(page, type, AncientOneId.YIG);
        if (page < 1 || page > 24) throw new IllegalArgumentException("Yig research card: " + page);
        this.page = page;
    }
    @Override public void executeWhole() {
        EventListenerImpl<GainClueData> gain = new EventListenerImpl<GainClueData>() {
            public Class<GainClueData> getDataClass() { return GainClueData.class; }
            public void onNotify(GainClueData data) { if (data.getInvestigatorId() == investigator()) gainedClue = true; }
        };
        sequence(() -> {
            p().getEventQueue().addAfterEventListener(gain, BeforeAfterEvent.GAIN_CLUE);
            super.executeWhole();
            later(() -> {
                // Keep the encounter paper available across tests, choices, and follow-up effects.
                p().getEncounterService().finishTypewriterPaper();
                p().getEventQueue().unregisterListener(gain);
                if (gainedClue && p().getMysteryDeck().getCurrentMysteryCard().getMysteryCardId() == MysteryCardId.Yig.MIGRATION_OF_SERPENTS
                        && p().getCluePool().getClueCount(investigator()) > 0)
                    ask("Spend 1 Clue gained in this encounter to advance the Active Mystery?",
                            () -> spend(1,0,0, () -> p().getGameService().advanceActiveMystery(), NOTHING), NOTHING);
            });
        });
    }
    @Override protected void execute() {
        // AbstractResearchEncounter has already opened the paper.
        paperVisible = true;
        String info = getTextBuilder().withInfo().build();
        if (startsWithChoice()) {
            openingChoice = info;
            resolve();
        } else if (info.isEmpty()) {
            resolve();
        } else {
            TypewriterUtils.confirmInfos(info).subscribe(() -> sequence(() -> {
                hidePaper();
                resolve();
            }));
        }
    }
    private boolean startsWithChoice() {
        switch (getLocationType()) {
            case CITY: return page == 23 || (page == 7 && canGain(DARK_PACT)) || (page == 15 && !improvedSkills().isEmpty());
            case WILDERNESS: return page == 6 || (page == 8 && canGain(POISONED))
                    || (page == 21 && canGain(DARK_PACT));
            case SEA: return (page == 3 || page == 12) && canGain(DARK_PACT);
            default: return false;
        }
    }
    private boolean canGain(ConditionId id) {
        return p().getConditionsDeck().canGetConditionId(investigator(), id);
    }
    private void openingChoice(String question, Runnable yes, Runnable no) {
        if (openingChoice == null) {
            ask(question, yes, no);
            return;
        }
        String info = openingChoice;
        openingChoice = null;
        TypewriterUtils.noYesQuestion(info,
                () -> sequence(() -> { hidePaper(); no.run(); }),
                () -> sequence(() -> { hidePaper(); yes.run(); }));
    }
    private void resolve() {
        switch (getLocationType()) {
            case CITY: city(); break;
            case WILDERNESS: wilderness(); break;
            case SEA: sea(); break;
            default: throw new IllegalArgumentException("Unsupported research space");
        }
    }
    private void test(Stat stat,int modifier,Runnable pass,Runnable fail) {
        showPaper();
        // The shared helper hides and restores the paper before either callback.
        TypewriterUtils.displayTestResearchButton(stat, modifier,
                () -> outcome(true, pass), () -> outcome(false, fail));
    }
    private void outcome(boolean passed, Runnable effect) {
        confirmOutcome(outcomeText(passed), effect);
    }
    private Runnable narrated(boolean passed, Runnable effect) {
        return () -> outcome(passed, effect);
    }
    private OutcomeText outcomeText(boolean passed) {
        return new OutcomeText(
                (passed ? getTextBuilder().withPass() : getTextBuilder().withFail()).withFlavor().build(),
                (passed ? getTextBuilder().withPass() : getTextBuilder().withFail()).withInfo().build());
    }
    private OutcomeText outcomeText(boolean passed, int part) {
        return new OutcomeText(
                (passed ? getTextBuilder().withPass() : getTextBuilder().withFail()).withFlavor(part).build(),
                (passed ? getTextBuilder().withPass() : getTextBuilder().withFail()).withInfo(part).build());
    }
    private void optionOutcome(int option, Runnable effect) {
        confirmOutcome(new OutcomeText(getTextBuilder().withOption(option).withFlavor().build(),
                getTextBuilder().withOption(option).withInfo().build()), effect);
    }
    private void aftermath(Runnable effect) {
        confirmOutcome(new OutcomeText(getTextBuilder().withFlavor(2).build(),
                getTextBuilder().withInfo(2).build()), effect);
    }
    private void confirmOutcome(OutcomeText text, Runnable effect) {
        sequence(() -> {
            showPaper();
            p().getEncounterService().typeFlavor("\n" + text.flavor);
            // Even a result without a mechanical effect has an ending to acknowledge.
            TypewriterUtils.confirmInfos(text.info).subscribe(() -> sequence(() -> {
                hidePaper();
                effect.run();
            }));
        });
    }
    private static final class OutcomeText {
        private final String flavor, info;
        private OutcomeText(String flavor, String info) { this.flavor = flavor; this.info = info; }
        private OutcomeText then(OutcomeText next) {
            return new OutcomeText(flavor + "\n" + next.flavor,
                    info.isEmpty() ? next.info : next.info.isEmpty() ? info : info + "\n" + next.info);
        }
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
    private void clue() { gainThisClue(); }
    private void extraClue() { p().getTokenService().gainClueFromPool(); }
    private void twoClues() { clue(); extraClue(); }
    private void artifact(ArtifactId id) { p().getGameService().gainArtifact(id); }
    private void improve(Stat stat) { p().getInvestigatorService().improveSkill(stat); }
    private boolean awake() { return p().getModel().getAncientOne().getAncientOneInfo().isAwaken(); }
    private boolean expedition() { return EXPEDITIONS.contains(getLocationId()); }
    private int artifactCount() { return p().getArtifactsDeck().getArtifacts(investigator()).size(); }
    private void doom() { p().getDoomOmenService().advanceDoom(); }
    private void retreat() { p().getDoomOmenService().retreatDoom(); }
    private void discardClue() { p().getTokenService().discardClue(getLocationId()); }
    private void moveClue() {
        for (ClueInfo c : p().getCluePool().getSpawnedClues()) if (c.getCurrentLocationId() == getLocationId()) {
            moveClueToExpedition(c.getSpawnLocationId(), c.getCurrentLocationId()); return;
        }
    }
    private void ambush(NonEpicMonsterId id, Action1<CombatData> next) {
        hidePaper();
        p().getMonsterService().ambush(id).subscribe(data -> sequence(() -> next.call(data)));
    }
    private boolean defeated(CombatData data) { return data.getMonsterInfo().getCurrentHealth() <= 0; }
    private void cultist(Runnable pass, Runnable fail) {
        ambush(NonEpicMonsterId.CULTIST, d -> {
            boolean rewarded = getLocationType() == LocationType.WILDERNESS && page == 7 ? !has(POISONED) : defeated(d);
            outcome(rewarded, defeated(d) ? pass : fail);
        });
    }
    private void serpent(Action1<CombatData> next) {
        ambush(NonEpicMonsterId.SERPENT_PEOPLE, d -> {
            // These cards award separate results for the two combat tests.
            if (getLocationType() == LocationType.CITY && page == 16) {
                OutcomeText text = d.getHorrorTestResult() != null && d.getHorrorTestResult().getScore() == 0
                        ? outcomeText(false) : outcomeText(true, 2);
                text = text.then(defeated(d) ? outcomeText(true) : outcomeText(false, 2));
                confirmOutcome(text, () -> next.call(d));
            } else if (getLocationType() == LocationType.CITY && page == 18) {
                outcome(d.getHealthLost() == 0, () -> next.call(d));
            } else if (getLocationType() == LocationType.WILDERNESS && page == 17) {
                boolean will = d.getHorrorTestResult() != null && d.getHorrorTestResult().getScore() > 0;
                boolean strength = d.getDamageTestResult() != null && d.getDamageTestResult().getScore() > 0;
                OutcomeText text = outcomeText(will || strength);
                if (will && strength) text = text.then(outcomeText(true, 2));
                confirmOutcome(text, () -> next.call(d));
            } else if (getLocationType() == LocationType.SEA && page == 15) {
                OutcomeText text = d.getHorrorTestResult() != null && d.getHorrorTestResult().getScore() > 0
                        ? outcomeText(true) : outcomeText(false, 2);
                text = text.then(d.getHealthLost() > 0 ? outcomeText(false) : outcomeText(true, 2));
                confirmOutcome(text, () -> next.call(d));
            } else {
                outcome(defeated(d), () -> next.call(d));
            }
        });
    }
    private void offerCondition(ConditionId id, Runnable yes, Runnable no) {
        if (!p().getConditionsDeck().canGetConditionId(investigator(), id)) { no.run(); return; }
        openingChoice("Gain " + id.toString().replace('_',' ') + "?", () -> { condition(id); later(() -> { if (has(id)) yes.run(); else no.run(); }); }, no);
    }
    private void pay(String question, int clues, int health, int sanity, Runnable yes, Runnable no) {
        openingChoice(question, () -> spend(clues, health, sanity, yes, no), no);
    }
    private void city() {
        switch (page) {
            case 1: test(OBSERVATION,-1,this::clue,() -> { damage(0,1); moveClue(); }); break;
            case 2: test(WILL,-1,this::clue,() -> condition(HALLUCINATIONS)); break;
            case 3: test(OBSERVATION,-1,this::clue,() -> { damage(1,0); condition(POISONED); }); break;
            case 4: cultist(this::clue,NOTHING); break;
            case 5: test(INFLUENCE,-1,this::clue,this::moveClue); break;
            case 6: test(LORE,-2*artifactCount(),() -> condition(BLESSED),() -> { damage(2,0); condition(POISONED); }); break;
            case 7: offerCondition(DARK_PACT,narrated(true,() -> { clue(); cureCurse(); }),narrated(false,NOTHING)); break;
            case 8: test(LORE,-1,() -> { clue(); p().getGameService().gainArtifact(AssetTrait.WEAPON); },() -> { damage(0,2); condition(POISONED); }); break;
            case 9: cultist(() -> { clue(); improve(WILL); },NOTHING); break;
            case 10: test(OBSERVATION,-2,this::twoClues,() -> { damage(1,0); condition(LEG_INJURY); }); break;
            case 11: test(LORE,-2,this::twoClues,() -> offerCondition(CURSED,
                    () -> optionOutcome(1,NOTHING),() -> optionOutcome(2,this::doom))); break;
            case 12: test(OBSERVATION,-1,this::clue,() -> condition(POISONED)); break;
            case 13: test(WILL,0,this::clue,() -> { damage(0,2); condition(PARANOIA); }); break;
            case 14: test(OBSERVATION,-1,this::clue,() -> condition(LEG_INJURY)); break;
            case 15: spendImprovement(); break;
            case 16: discardPossessions(AssetTrait.ALLY,1,true,() -> serpent(d -> {
                if (d.getHorrorTestResult() != null && d.getHorrorTestResult().getScore() == 0) condition(CURSED);
                if (defeated(d)) clue();
            })); break;
            case 17: test(LORE,-1,() -> p().getGameService().gainArtifact(AssetTrait.TOME),() -> condition(CURSED)); break;
            case 18: clue(); serpent(d -> { if (d.getHealthLost() > 0) condition(POISONED); }); break;
            case 19: test(OBSERVATION,-1,this::clue,() -> condition(DETAINED)); break;
            case 20: if (has(POISONED)) condition(CURSED); test(OBSERVATION,-1,this::clue,NOTHING); break;
            case 21: rerollTest(0,2,0,this::clue,() -> { condition(AMNESIA); condition(HALLUCINATIONS); condition(PARANOIA); }); break;
            case 22: serpent(d -> { if (defeated(d)) { clue(); cureCurse(); } }); break;
            case 23: pay("Spend 2 Health to gain this Clue?",0,2,0,narrated(true,this::clue),narrated(false,this::moveClue)); break;
            case 24: test(OBSERVATION,0,this::clue,() -> { damage(2,0); condition(LEG_INJURY); }); break;
        }
    }
    private void wilderness() {
        switch (page) {
            case 1: test(STRENGTH,expedition()?-3:-1,this::twoClues,() -> { damage(2,0); condition(BACK_INJURY); }); break;
            case 2: test(LORE,-1,() -> { clue(); cureCurse(); },() -> { damage(2,0); condition(POISONED); }); break;
            case 3: clue(); test(OBSERVATION,-2,() -> artifact(ArtifactId.CURSED_SPHERE),() -> condition(CURSED)); break;
            case 4: test(STRENGTH,has(POISONED)?-2:0,this::clue,() -> { damage(1,0); discardPossessions(AssetTrait.ITEM,2,false,NOTHING); }); break;
            case 5: if (expedition()) condition(POISONED); test(OBSERVATION,-2,this::twoClues,NOTHING); break;
            case 6: pay("Spend 2 Health to gain this Clue and let an investigator discard Cursed?",0,2,0,
                    narrated(true,() -> { clue(); cureCurse(); }),narrated(false,NOTHING));
                later(() -> { if (awake()) aftermath(() -> damage(2,0)); }); break;
            case 7: cultist(() -> { if (!has(POISONED)) clue(); },() -> { if (!has(POISONED)) clue(); }); break;
            case 8: offerCondition(POISONED,narrated(true,this::clue),narrated(false,() -> { discardClue(); discardPossessions(AssetTrait.ALLY,1,true,NOTHING); })); break;
            case 9: test(OBSERVATION,-2,this::twoClues,NOTHING); later(() -> {
                if (expedition()) aftermath(() -> damage(2,0));
            }); break;
            case 10: if (has(POISONED)) doom(); test(OBSERVATION,-1,this::clue,NOTHING); break;
            case 11: test(OBSERVATION,0,this::clue,() -> condition(LOST_IN_TIME_AND_SPACE)); break;
            case 12: test(OBSERVATION,0,() -> pay("Spend 2 Health to gain this Clue?",0,2,0,
                    () -> optionOutcome(1,this::clue),() -> optionOutcome(2,() -> condition(CURSED))),() -> condition(CURSED)); break;
            case 13: test(WILL,has(POISONED)?-2:-1,() -> p().getTokenService().gainSanity(2),() -> condition(PARANOIA)); break;
            case 14: test(LORE,0,this::clue,() -> condition(CURSED)); break;
            case 15: serpent(d -> { if (defeated(d)) { clue(); retreat(); } else condition(POISONED); }); break;
            case 16: test(OBSERVATION,-1,() -> { clue(); p().getGameService().gainSpell(); },() -> condition(HALLUCINATIONS)); break;
            case 17: serpent(d -> {
                boolean will = d.getHorrorTestResult()!=null && d.getHorrorTestResult().getScore()>0;
                boolean strength = d.getDamageTestResult()!=null && d.getDamageTestResult().getScore()>0;
                if (will || strength) clue(); if (will && strength) extraClue();
            }); break;
            case 18: test(INFLUENCE,-1,this::clue,() -> { moveToCity(); condition(DETAINED); }); break;
            case 19: test(INFLUENCE,-1,() -> { clue(); cureCurse(); },() -> condition(CURSED)); break;
            case 20: clue(); test(OBSERVATION,-2,this::extraClue,() -> condition(POISONED)); break;
            case 21: offerCondition(DARK_PACT,narrated(true,() -> { discardClue(); artifact(ArtifactId.SERPENT_CROWN); }),
                    narrated(false,() -> { damage(0,2); discardPossessions(AssetTrait.ALLY,2,true,NOTHING); })); break;
            case 22: test(OBSERVATION,-1,this::twoClues,() -> { condition(POISONED); condition(CURSED); }); break;
            case 23: test(OBSERVATION,-2,() -> artifact(ArtifactId.ELIXIR_OF_LIFE),() -> condition(POISONED)); break;
            case 24: if (has(POISONED)) damage(1,1); test(OBSERVATION,-1,this::clue,NOTHING); break;
        }
    }
    private void sea() {
        switch (page) {
            case 1: test(OBSERVATION,-1,this::clue,() -> condition(POISONED)); break;
            case 2: test(OBSERVATION,-1,this::clue,() -> { condition(AMNESIA); moveClue(); }); break;
            case 3: if (!p().getConditionsDeck().canGetConditionId(investigator(),DARK_PACT)) { outcome(false,NOTHING); break; }
                pay("Spend 2 Sanity and gain Dark Pact to improve two skills?",0,0,2,() -> {
                    condition(DARK_PACT);
                    later(() -> outcome(has(DARK_PACT),() -> { if (has(DARK_PACT)) improveChoice(2,EnumSet.noneOf(Stat.class)); }));
                },narrated(false,NOTHING)); break;
            case 4: test(LORE,0,this::clue,this::doom); break;
            case 5: test(INFLUENCE,-1,this::clue,() -> p().getGameService().gainCondition(ConditionTrait.INJURY)); break;
            case 6: test(INFLUENCE,-1,this::clue,() -> condition(POISONED)); break;
            case 7: cultist(this::clue,NOTHING); break;
            case 8: test(INFLUENCE,-1,this::clue,NOTHING); later(() -> {
                if (artifactCount()>0) aftermath(() -> { damage(0,1); condition(PARANOIA); });
            }); break;
            case 9: test(LORE,-2,() -> artifact(ArtifactId.ZANTHU_TABLETS),() -> condition(HALLUCINATIONS)); break;
            case 10: rerollTest(2,0,-1,this::clue,() -> { damage(0,2); condition(POISONED); }); break;
            case 11: cultist(this::clue,this::moveClue); break;
            case 12: offerCondition(DARK_PACT,narrated(true,this::clue),narrated(false,() -> { discardClue(); delayed(); })); break;
            case 13: discardDefeated(); later(() -> serpent(d -> { if (defeated(d)) { damage(0,1); clue(); } else doom(); })); break;
            case 14: test(OBSERVATION,-1,this::twoClues,() -> damage(2,2)); break;
            case 15: serpent(d -> { if (d.getHorrorTestResult()!=null && d.getHorrorTestResult().getScore()>0) clue(); if (d.getHealthLost()>0) condition(POISONED); }); break;
            case 16: test(WILL,-1,this::clue,() -> condition(BACK_INJURY)); break;
            case 17: test(OBSERVATION,-1,this::clue,() -> { condition(HALLUCINATIONS); moveClue(); }); break;
            case 18: test(OBSERVATION,-1,this::retreat,() -> { damage(0,1); condition(PARANOIA); }); break;
            case 19: test(OBSERVATION,-1,this::clue,() -> condition(LOST_IN_TIME_AND_SPACE)); break;
            case 20: test(OBSERVATION,-1,this::clue,() -> { damage(1,0); condition(POISONED); }); break;
            case 21: test(OBSERVATION,0,this::clue,() -> { damage(2,0); condition(INTERNAL_INJURY); }); break;
            case 22: test(OBSERVATION,0,this::clue,() -> { damage(0,2); condition(HALLUCINATIONS); }); break;
            case 23: test(INFLUENCE,-1,() -> {
                if (!has(CURSED)) clue(); else ask("Discard your Cursed condition instead of gaining this Clue?",
                        () -> optionOutcome(1,() -> p().getService().discardConditionFromInvestigator(investigator(),p().getConditionsDeck().getCondition(investigator(),CURSED))),
                        () -> optionOutcome(2,this::clue));
            },() -> damage(1,0)); break;
            case 24: test(STRENGTH,awake()?-3:-1,this::twoClues,() -> {
                for (Stat s : Stat.values()) { int n=p().getInvestigators().getActiveInvestigator().getStatBonus(s); if(n>0) p().getInvestigatorService().loseImprovement(new LoseImprovementData(s,n)); }
            }); break;
        }
    }
    private List<Stat> improvedSkills() {
        List<Stat> skills = new ArrayList<>();
        for (Stat s : Stat.values()) if (p().getInvestigators().getActiveInvestigator().getStatBonus(s)>0) skills.add(s);
        return skills;
    }
    private void spendImprovement() {
        List<Stat> skills = improvedSkills();
        if (skills.isEmpty()) { outcome(false,() -> condition(DETAINED)); return; }
        // The card text carries the explanation, so the buttons stay short enough for phones.
        openingChoice("Spend an improvement token to let an investigator discard Cursed?",
                () -> chooseImprovement(skills, stat -> {
                    p().getInvestigatorService().loseImprovement(new LoseImprovementData(stat,1));
                    outcome(true,() -> cureCurse());
                }),
                narrated(false,() -> condition(DETAINED)));
    }
    private void chooseImprovement(List<Stat> skills, Action1<Stat> spend) {
        if (skills.size() == 1) { spend.call(skills.get(0)); return; }
        List<Question.Option<Stat>> options = new ArrayList<>();
        for (Stat s : skills) options.add(new Question.Option<>(s.prettyString(),s));
        Question<Stat> q = new Question<>(); q.setTitle("Spend which improvement token?"); q.setOptions(options);
        p().getGameService().ask(q).subscribe(a -> sequence(() -> spend.call(a.getResponseData())));
    }
    private void moveToCity() {
        Set<LocationId> cities=EnumSet.noneOf(LocationId.class);
        for(LocationId l:LocationId.values()) if(p().getLocationMap().getLocationInfo(l).getLocationType()==LocationType.CITY) cities.add(l);
        chooseLocation(nearest(p().getInvestigators().getActiveInvestigator().getCurrentLocationId(),cities),"Move to the nearest City", l -> moveInvestigator(l));
    }
    private void discardDefeated() {
        List<Question.Option<InvestigatorId>> options = new ArrayList<>();
        for(InvestigatorRead i:p().getInvestigators().getDefeatedInvestigators()) if(i.getCurrentLocationId()!=null)
            options.add(new Question.Option<>(i.getInfo().getInvestigatorId().toString(),i.getInfo().getInvestigatorId()));
        if(options.isEmpty()) return;
        Question<InvestigatorId> q=new Question<>(); q.setTitle("Discard a defeated investigator token"); q.setOptions(options);
        p().getGameService().ask(q).subscribe(a -> p().getInvestigatorService().discardDefeatedInvestigator(a.getResponseData()));
    }
    private void rerollTest(int health,int sanity,int modifier,Runnable pass,Runnable fail) {
        EventListenerImpl<TestData> reroll=new EventListenerImpl<TestData>() {
            public Class<TestData> getDataClass(){return TestData.class;}
            public void onNotify(TestData data) {
                sequence(() -> {
                    later(() -> p().getEventQueue().unregisterListener(this));
                    pay("Spend 2 " + (health>0?"Health":"Sanity") + " to reroll up to two dice?",0,health,sanity,() -> {
                        p().getTestService().findCountRerollDice(2,CountRerollDiceData.CountRerollDiceType.OTHER,data);
                        p().getTestService().rerollDice();
                    },NOTHING);
                    p().getService().convertTo(TestData.class,() -> data);
                });
            }
        };
        p().getEventQueue().addBeforeEventListener(reroll,BeforeAfterEvent.CONFIRM_TEST_RESULT);
        test(OBSERVATION, modifier, () -> {
            p().getEventQueue().unregisterListener(reroll);
            pass.run();
        }, () -> {
            p().getEventQueue().unregisterListener(reroll);
            fail.run();
        });
    }
}
