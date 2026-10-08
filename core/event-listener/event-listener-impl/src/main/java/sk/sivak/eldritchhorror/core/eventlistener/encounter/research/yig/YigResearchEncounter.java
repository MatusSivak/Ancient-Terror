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
            case CITY: return page == 23 || (page == 7 && canGain(DARK_PACT));
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
        String info = (passed ? getTextBuilder().withPass() : getTextBuilder().withFail()).withInfo().build();
        confirmOutcome(info, effect);
    }
    private void confirmOutcome(String info, Runnable effect) {
        sequence(() -> {
            showPaper();
            if (info.isEmpty()) {
                hidePaper();
                effect.run();
            } else {
                TypewriterUtils.confirmInfos(info).subscribe(() -> sequence(() -> {
                    hidePaper();
                    effect.run();
                }));
            }
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
                String info = (d.getHorrorTestResult() != null && d.getHorrorTestResult().getScore() == 0
                        ? getTextBuilder().withFail().withInfo().build() : "");
                if (defeated(d)) info += "\n" + getTextBuilder().withPass().withInfo().build();
                confirmOutcome(info.trim(), () -> next.call(d));
            } else if (getLocationType() == LocationType.CITY && page == 18) {
                confirmOutcome(d.getHealthLost() > 0 ? getTextBuilder().withFail().withInfo().build() : "", () -> next.call(d));
            } else if (getLocationType() == LocationType.WILDERNESS && page == 17) {
                boolean will = d.getHorrorTestResult() != null && d.getHorrorTestResult().getScore() > 0;
                boolean strength = d.getDamageTestResult() != null && d.getDamageTestResult().getScore() > 0;
                String info = will || strength ? getTextBuilder().withPass().withInfo().build() : "";
                if (will && strength) info += "\n" + getTextBuilder().withPass().withInfo(2).build();
                confirmOutcome(info, () -> next.call(d));
            } else if (getLocationType() == LocationType.SEA && page == 15) {
                String info = d.getHorrorTestResult() != null && d.getHorrorTestResult().getScore() > 0
                        ? getTextBuilder().withPass().withInfo().build() : "";
                if (d.getHealthLost() > 0) info += "\n" + getTextBuilder().withFail().withInfo().build();
                confirmOutcome(info.trim(), () -> next.call(d));
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
            case 7: offerCondition(DARK_PACT,() -> { clue(); cureCurse(); },NOTHING); break;
            case 8: test(LORE,-1,() -> { clue(); p().getGameService().gainArtifact(AssetTrait.WEAPON); },() -> { damage(0,2); condition(POISONED); }); break;
            case 9: cultist(() -> { clue(); improve(WILL); },NOTHING); break;
            case 10: test(OBSERVATION,-2,this::twoClues,() -> { damage(1,0); condition(LEG_INJURY); }); break;
            case 11: test(LORE,-2,this::twoClues,() -> offerCondition(CURSED,NOTHING,this::doom)); break;
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
            case 23: pay("Spend 2 Health to gain this Clue?",0,2,0,this::clue,this::moveClue); break;
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
            case 6: pay("Spend 2 Health to gain this Clue and let an investigator discard Cursed?",0,2,0,() -> { clue(); cureCurse(); },NOTHING);
                later(() -> { if (awake()) damage(2,0); }); break;
            case 7: cultist(() -> { if (!has(POISONED)) clue(); },() -> { if (!has(POISONED)) clue(); }); break;
            case 8: offerCondition(POISONED,this::clue,() -> { discardClue(); discardPossessions(AssetTrait.ALLY,1,true,NOTHING); }); break;
            case 9: test(OBSERVATION,-2,this::twoClues,NOTHING); later(() -> {
                if (expedition()) confirmOutcome(getTextBuilder().withInfo(2).build(), () -> damage(2,0));
            }); break;
            case 10: if (has(POISONED)) doom(); test(OBSERVATION,-1,this::clue,NOTHING); break;
            case 11: test(OBSERVATION,0,this::clue,() -> condition(LOST_IN_TIME_AND_SPACE)); break;
            case 12: test(OBSERVATION,0,() -> pay("Spend 2 Health to gain this Clue?",0,2,0,this::clue,() -> condition(CURSED)),() -> condition(CURSED)); break;
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
            case 21: offerCondition(DARK_PACT,() -> { discardClue(); artifact(ArtifactId.SERPENT_CROWN); },() -> { damage(0,2); discardPossessions(AssetTrait.ALLY,2,true,NOTHING); }); break;
            case 22: test(OBSERVATION,-1,this::twoClues,() -> { condition(POISONED); condition(CURSED); }); break;
            case 23: test(OBSERVATION,-2,() -> artifact(ArtifactId.ELIXIR_OF_LIFE),() -> condition(POISONED)); break;
            case 24: if (has(POISONED)) damage(1,1); test(OBSERVATION,-1,this::clue,NOTHING); break;
        }
    }
    private void sea() {
        switch (page) {
            case 1: test(OBSERVATION,-1,this::clue,() -> condition(POISONED)); break;
            case 2: test(OBSERVATION,-1,this::clue,() -> { condition(AMNESIA); moveClue(); }); break;
            case 3: if (!p().getConditionsDeck().canGetConditionId(investigator(),DARK_PACT)) break;
                pay("Spend 2 Sanity and gain Dark Pact to improve two skills?",0,0,2,() -> { condition(DARK_PACT); later(() -> { if (has(DARK_PACT)) improveChoice(2,EnumSet.noneOf(Stat.class)); }); },NOTHING); break;
            case 4: test(LORE,0,this::clue,this::doom); break;
            case 5: test(INFLUENCE,-1,this::clue,() -> p().getGameService().gainCondition(ConditionTrait.INJURY)); break;
            case 6: test(INFLUENCE,-1,this::clue,() -> condition(POISONED)); break;
            case 7: cultist(this::clue,NOTHING); break;
            case 8: test(INFLUENCE,-1,this::clue,NOTHING); later(() -> {
                if (artifactCount()>0) confirmOutcome(getTextBuilder().withInfo(2).build(), () -> { damage(0,1); condition(PARANOIA); });
            }); break;
            case 9: test(LORE,-2,() -> artifact(ArtifactId.ZANTHU_TABLETS),() -> condition(HALLUCINATIONS)); break;
            case 10: rerollTest(2,0,-1,this::clue,() -> { damage(0,2); condition(POISONED); }); break;
            case 11: cultist(this::clue,this::moveClue); break;
            case 12: offerCondition(DARK_PACT,this::clue,() -> { discardClue(); delayed(); }); break;
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
                        () -> p().getService().discardConditionFromInvestigator(investigator(),p().getConditionsDeck().getCondition(investigator(),CURSED)),this::clue);
            },() -> damage(1,0)); break;
            case 24: test(STRENGTH,awake()?-3:-1,this::twoClues,() -> {
                for (Stat s : Stat.values()) { int n=p().getInvestigators().getActiveInvestigator().getStatBonus(s); if(n>0) p().getInvestigatorService().loseImprovement(new LoseImprovementData(s,n)); }
            }); break;
        }
    }
    private void spendImprovement() {
        List<Question.Option<Stat>> options = new ArrayList<>();
        for (Stat s : Stat.values()) if (p().getInvestigators().getActiveInvestigator().getStatBonus(s)>0) options.add(new Question.Option<>(s.prettyString(),s));
        options.add(new Question.Option<>("Do not spend an improvement; gain Detained",null));
        Question<Stat> q = new Question<>(); q.setTitle("Spend an improvement token to let an investigator discard Cursed?"); q.setOptions(options);
        p().getGameService().ask(q).subscribe(a -> sequence(() -> {
            if(a.getResponseData()==null) condition(DETAINED);
            else { p().getInvestigatorService().loseImprovement(new LoseImprovementData(a.getResponseData(),1)); cureCurse(); }
        }));
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
