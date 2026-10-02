package sk.sivak.eldritchhorror.core.eventlistener.ancientone.yig;

import java.util.*;
import rx.functions.Action1;
import sk.sivak.eldritchhorror.core.constants.asset.*;
import sk.sivak.eldritchhorror.core.constants.card.CardInfo;
import sk.sivak.eldritchhorror.core.constants.condition.*;
import sk.sivak.eldritchhorror.core.constants.investigator.*;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterId;
import sk.sivak.eldritchhorror.core.constants.question.Question;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventlistener.encounter.utils.EncounterUtils;
import sk.sivak.eldritchhorror.core.eventtype.data.SpawnMonsterData;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

/** Shared choices keep all Yig effects on the game's command queue. */
public final class YigEffects {
    private YigEffects() {}
    public static ServicePlatform p() { return ServicePlatform.get(); }
    public static InvestigatorId investigator() { return p().getInvestigators().getActiveInvestigator().getInfo().getInvestigatorId(); }
    public static boolean has(ConditionId id) { return p().getConditionsDeck().hasCondition(investigator(), id); }
    public static void sequence(Runnable action) {
        p().getService().hold();
        try { action.run(); } finally { p().getService().release(); }
    }
    public static void later(Runnable action) { p().getService().addEventCommand(in -> { sequence(action); }); }
    public static void ask(String title, Runnable yes, Runnable no) {
        Question<Boolean> q = new Question<>(); q.setTitle(title); q.setOptions(Question.Option.noYesOptions);
        p().getGameService().ask(q).subscribe(a -> sequence(a.getResponseData() ? yes : no));
    }
    public static void chooseLocation(Collection<LocationId> locations, String title, Action1<LocationId> action) {
        if (locations.isEmpty()) return;
        List<Question.Option<LocationId>> options = new ArrayList<>();
        for (LocationId l : new LinkedHashSet<>(locations)) options.add(new Question.Option<>(l.toString(), l));
        Question<LocationId> q = new Question<>(); q.setTitle(title); q.setOptions(options);
        p().getGameService().ask(q).subscribe(a -> sequence(() -> action.call(a.getResponseData())));
    }
    public static void cureCurse() {
        List<Question.Option<InvestigatorId>> options = new ArrayList<>();
        for (InvestigatorRead i : p().getInvestigators().getOnBoardInvestigators()) {
            InvestigatorId id = i.getInfo().getInvestigatorId();
            if (p().getConditionsDeck().hasCondition(id, ConditionId.CURSED)) options.add(new Question.Option<>(id.toString(), id));
        }
        if (options.isEmpty()) return;
        options.add(new Question.Option<>("Keep the conditions", null));
        Question<InvestigatorId> q = new Question<>(); q.setTitle("Choose an investigator who may discard Cursed"); q.setOptions(options);
        p().getGameService().ask(q).subscribe(a -> {
            InvestigatorId id = a.getResponseData();
            if (id != null) p().getService().discardConditionFromInvestigator(id, p().getConditionsDeck().getCondition(id, ConditionId.CURSED));
        });
    }
    public static void spawn(MonsterId monster, LocationId location) {
        SpawnMonsterData data = new SpawnMonsterData(); data.setMonsterId(monster); data.setLocationId(location);
        p().getGameService().spawnMonster(data);
    }
    public static void discardPossessions(AssetTrait trait, int count, boolean assetsOnly, Runnable done) {
        if (count == 0) { done.run(); return; }
        List<Question.Option<CardInfo>> options = new ArrayList<>();
        for (CardInfo card : EncounterUtils.getPossession(investigator(), trait)) {
            if (!assetsOnly || card instanceof AssetInfo) options.add(new Question.Option<>(card.getName(), card));
        }
        if (options.isEmpty()) { done.run(); return; }
        Question<CardInfo> q = new Question<>(); q.setTitle("Discard " + count + " " + trait + " possession(s)"); q.setOptions(options);
        p().getGameService().ask(q).subscribe(a -> sequence(() -> {
            EncounterUtils.onSelectCardToDiscard(a.getResponseData());
            later(() -> discardPossessions(trait, count - 1, assetsOnly, done));
        }));
    }
    public static void spend(int clues, int health, int sanity, Runnable yes, Runnable no) {
        p().getTokenService().spend(clues, 0, health, sanity).subscribe(data -> sequence(() -> {
            if (data.hasEnough()) { data.pay(); later(yes); } else no.run();
        }));
    }
    public static void test(Stat stat, int modifier, Runnable pass, Runnable fail) {
        p().getTestService().test(stat, modifier, 1).subscribe(result -> sequence(result.getScore() > 0 ? pass : fail));
    }
    public static final Set<LocationId> EXPEDITIONS = EnumSet.of(LocationId.THE_AMAZON, LocationId.THE_HEART_OF_AFRICA,
            LocationId.THE_PYRAMIDS, LocationId.ANTARCTICA, LocationId.THE_HIMALAYAS, LocationId.TUNGUSKA);
    /** Breadth-first search retains every tied nearest space so the player can choose. */
    public static List<LocationId> nearest(LocationId start, Set<LocationId> targets) {
        Set<LocationId> seen = EnumSet.noneOf(LocationId.class);
        List<LocationId> frontier = Collections.singletonList(start);
        while (!frontier.isEmpty()) {
            List<LocationId> matches = new ArrayList<>(), next = new ArrayList<>();
            for (LocationId id : frontier) if (targets.contains(id)) matches.add(id);
            if (!matches.isEmpty()) return matches;
            seen.addAll(frontier);
            for (LocationId id : frontier)
                for (sk.sivak.eldritchhorror.core.constants.location.LocationInfo.Connection c : p().getLocationMap().getLocationInfo(id).getConnections())
                    if (seen.add(c.getLocationId())) next.add(c.getLocationId());
            frontier = next;
        }
        return Collections.emptyList();
    }
    public static void moveClueToExpedition(LocationId spawn, LocationId current) {
        chooseLocation(nearest(current, EXPEDITIONS), "Move the Clue to the nearest Expedition space",
                destination -> p().getTokenService().moveClue(spawn, destination));
    }
    public static void moveInvestigator(LocationId destination) {
        p().getBasicActionService().travelToLocation(new sk.sivak.eldritchhorror.core.constants.location.LocationInfo.Connection() {
            public LocationId getLocationId() { return destination; }
            public sk.sivak.eldritchhorror.core.constants.location.PathType getPathType() { return sk.sivak.eldritchhorror.core.constants.location.PathType.WALK; }
        });
    }
    public static void delayed() {
        p().getInvestigatorService().becomeDelayed(new sk.sivak.eldritchhorror.core.eventtype.data.investigator.DelayedData(investigator(), true));
    }
    public static void condition(ConditionId id) { p().getGameService().gainCondition(id); }
    public static void damage(int health, int sanity) {
        if (health > 0) p().getTokenService().loseHealth(health);
        if (sanity > 0) p().getTokenService().loseSanity(sanity);
    }
    public static void improveChoice(int count, Set<Stat> excluded) {
        if (count <= 0) return;
        List<Question.Option<Stat>> options = new ArrayList<>();
        for (Stat s : Stat.values()) if (!excluded.contains(s) && p().getInvestigators().getActiveInvestigator().getStatBonus(s) < 2)
            options.add(new Question.Option<>(s.prettyString(), s));
        if (options.isEmpty()) return;
        Question<Stat> q = new Question<>(); q.setTitle("Choose a skill to improve"); q.setOptions(options);
        p().getGameService().ask(q).subscribe(a -> sequence(() -> {
            Stat s = a.getResponseData(); p().getInvestigatorService().improveSkill(s); excluded.add(s);
            later(() -> improveChoice(count - 1, excluded));
        }));
    }
}
