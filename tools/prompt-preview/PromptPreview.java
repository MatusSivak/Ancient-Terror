import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import rx.Subscription;
import sk.sivak.eldritchhorror.core.constants.TokenCardInfo;
import sk.sivak.eldritchhorror.core.constants.combat.CombatOverviewTableData;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.view.components.combat.CombatOverviewTable;
import sk.sivak.eldritchhorror.core.constants.investigator.Stat;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.view.components.diceroller.*;
import sk.sivak.eldritchhorror.core.view.components.combat.Fireball;
import sk.sivak.eldritchhorror.core.view.components.combat.TokenInFrame;
import sk.sivak.eldritchhorror.core.view.components.combat.TokenInFrameBar;
import sk.sivak.eldritchhorror.core.view.components.combat.MonsterCombatTable;
import sk.sivak.eldritchhorror.core.constants.combat.MonsterCombatTableData;
import sk.sivak.eldritchhorror.core.view.test.TestTableStack;
import sk.sivak.eldritchhorror.core.view.test.TestResultTable;
import rx.Completable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.components.sheet.mystery.MysteryCard;
import sk.sivak.eldritchhorror.core.view.components.table.LabelTable;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorImage;
import sk.sivak.eldritchhorror.core.view.map.investigator.InvestigatorUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import sk.sivak.eldritchhorror.core.view.test.TestViewImpl;
import sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
import sk.sivak.eldritchhorror.core.view.utils.UiText;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/** Real Scene2D rendering and animation checks; does not load or alter a saved game. */
public class PromptPreview extends ApplicationAdapter {
    private Stage stage;

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000;
        config.forceExit = false;
        new LwjglApplication(new PromptPreview(), config);
    }

    @Override public void create() {
        try {
            Sound silent = (Sound) Proxy.newProxyInstance(Sound.class.getClassLoader(),
                    new Class<?>[]{Sound.class}, (proxy, method, args) -> {
                        if (method.getName().equals("hashCode")) return System.identityHashCode(proxy);
                        if (method.getName().equals("equals")) return proxy == args[0];
                        return method.getReturnType() == long.class ? 0L : null;
                    });
            Gdx.audio = (Audio) Proxy.newProxyInstance(Audio.class.getClassLoader(),
                    new Class<?>[]{Audio.class}, (proxy, method, args) -> silent);
            VisUI.load();
            stage = InfoStage.getStageSafe();
            checkMystery();
            checkDice(false);
            checkDice(true);
            for (boolean fast : new boolean[]{false, true}) {
                checkCombatDamage(0, fast, false);
                checkCombatDamage(1, fast, false);
            }
            checkCombatDamage(0, false, true);
            previewCombatTokens();
            previewCombatPanel();
            previewTestSetup();
            previewMonsterHealthHit();
            previewMonsterAttack();
            previewMonsterAttackOnStand();
            previewCombatOverview();
            System.out.println("PASS: mystery prompt bounds/restoration; dice reroll completion, prompt visibility and cleanup at normal/fast-forward speed");
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(1);
        } finally {
            Gdx.app.exit();
        }
    }

    private void checkMystery() throws Exception {
        MysteryCardInfo info = (MysteryCardInfo) Proxy.newProxyInstance(
                MysteryCardInfo.class.getClassLoader(), new Class<?>[]{MysteryCardInfo.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getName": return "mystery.azathoth.voice.name";
                        case "getFlavorText": return "mystery.azathoth.voice.flavor";
                        case "getMysteryText": return "mystery.azathoth.voice.text";
                        case "getMysteryComplexity": return 2;
                        case "getProgress": return 0;
                        default: return null;
                    }
                });
        MysteryCard card = new MysteryCard();
        card.init(info, 2, 3);
        // Open state without starting a game or creating its HUD controllers.
        Object display = field(card, "displayHide");
        Field displayed = display.getClass().getDeclaredField("displayed");
        displayed.setAccessible(true); displayed.set(display, true);
        card.setTouchable(Touchable.enabled);
        card.setX((960 - card.getWidth()) / 2);
        stage.addActor(card);
        card.act(0);
        float centered = card.getY();
        Button button = ButtonBuilder.buildButton("OK");
        InfoStage.showButton(button);
        card.act(0);
        require(card.getY() >= button.getTop() + 10, "Standalone button overlaps card");
        InfoStage.setBottomHeight(button.getTop() + 5);
        LabelTable prompt = LabelTable.createAndShowTable(0, "Gain 'Requiem per Shuggay' Artifact instead?");
        tick(100);
        require(card.getY() >= prompt.getTop() + 10, "Prompt overlaps card");
        require(card.getTop() <= 540, "Card clipped at top");
        draw("mystery-prompt");
        button.remove(); prompt.remove(); card.act(0);
        require(Math.abs(card.getY() - centered) < 0.01f, "Card did not recenter");
        draw("mystery-centered");
        card.remove();
    }

    private void checkDice(boolean fast) throws Exception {
        FastForwardAction.turnOff();
        if (fast) FastForwardAction.turnOn();
        Group layer = InfoStage.getDiceLayer();
        TestViewImpl view = new TestViewImpl();
        DiceRoller roller = ((DiceRollerStack) field(view, "diceRollerStack")).push();
        roller.setDiceLayer(layer);
        List<DiceRoll> rolls = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            DiceRoll roll = new DiceRoll(DiceRoll.Score.BAD);
            roll.setDiceNr(i); roll.setDiceValue(2 + i % 2); rolls.add(roll);
        }
        roller.init(rolls);
        AtomicInteger completed = new AtomicInteger();
        roller.rollDice().subscribe(completed::incrementAndGet);
        tickDice(layer, 600);
        require(completed.get() == 1, "Initial roll completion");
        completed.set(0);
        roller.rollDice(0).subscribe(() -> {
            completed.incrementAndGet();
            DiceAreaCalculator area = (DiceAreaCalculator) field(roller, "diceAreaCalculator");
            List<DiceImage> dice = (List<DiceImage>) field(roller, "dices");
            for (int i = 0; i < dice.size(); i++) {
                Vector2 target = area.getReturnArea(i);
                require(Math.abs(dice.get(i).getX() - target.x) < 0.01f
                        && Math.abs(dice.get(i).getY() - target.y) < 0.01f,
                        "Reroll completed before all dice returned");
            }
        });
        tickDice(layer, 600);
        require(completed.get() == 1, "Reroll completion");
        checkRerollPrompts(view, layer);
        if (fast) FastForwardAction.turnOn();
        completed.set(0);
        roller.moveUp(45).subscribe(() -> {
            completed.incrementAndGet();
            for (Actor die : layer.getChildren()) require(Math.abs(die.getY() - 45
                    + (DiceImage.DICE_SIZE - DiceImage.ACTUAL_DICE_SIZE) / 2f) < 0.01f,
                    "Result confirmed before every die was positioned");
            roller.hideDices();
        });
        tickDice(layer, 150);
        require(completed.get() == 1 && layer.getChildren().size == 0, "Failed dice linger after cleanup");
        FastForwardAction.turnOff();
    }

    private void checkRerollPrompts(TestViewImpl view, Group layer) throws Exception {
        for (boolean focus : new boolean[]{true, false}) {
            for (int choice = 0; choice < 2; choice++) {
                InfoStage.setBottomHeight(InfoStage.getInvestigatorHud().getPrefHeight() + 5);
                Map<Actor, Vector2> positions = new HashMap<>();
                for (Actor die : layer.getChildren()) positions.put(die, new Vector2(die.getX(), die.getY()));
                AtomicInteger answer = new AtomicInteger(-1);
                (focus ? view.askRerollUsingFocus(null) : view.askRerollUsingClue(null))
                        .subscribe(value -> answer.set(value ? 1 : 0));
                tick(100);
                require(answer.get() == -1, "Reroll prompt answered without input");
                for (Actor actor : layer.getChildren()) {
                    DiceImage die = (DiceImage) actor;
                    Vector2 before = positions.get(die);
                    require(die.isVisible() && die.getColor().a > 0 && die.getTop() > 0,
                            "Dice must remain visible during reroll decisions");
                    require(die.getX() == before.x && die.getY() == before.y,
                            "Reroll prompt moved the dice");
                    require(die.getDiceValue() == 2 + die.getDiceNumber() % 2,
                            "Reroll prompt changed the values");
                }
                if (!focus && choice == 0) draw("clue-reroll-visible-dice");
                Button button = ((Button[]) field(view, "buttons"))[choice];
                Vector2 point = button.localToStageCoordinates(new Vector2(button.getWidth() / 2, button.getHeight() / 2));
                stage.stageToScreenCoordinates(point);
                stage.touchDown((int) point.x, (int) point.y, 0, 0);
                stage.touchUp((int) point.x, (int) point.y, 0, 0);
                require(answer.get() == choice, "Reroll choice did not complete");
                tick(100);
                for (Actor die : layer.getChildren()) require(die.isVisible(), "Answer hid dice");
            }
        }
    }

    /** Exercises the real result -> destroy-claws -> hide-dice path, including its zero-success shortcut. */
    private void checkCombatDamage(int successes, boolean fast, boolean reproduceOldOrder) throws Exception {
        if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
        TestViewImpl view = new TestViewImpl();
        MonsterCombatTable table = new MonsterCombatTable();
        InfoStage.addSmallActorToInfoStage(table);
        MonsterCombatTableData data = new MonsterCombatTableData();
        data.setMonsterClassName("ElderThingMonster"); data.setMonsterName("Elder Thing");
        data.setHorror(2); data.setDamage(3); data.setToughness(3); data.setCurrentHealth(3);
        table.init(data);
        table.setPosition(MonsterCombatTable.X_POSITION, 540 - table.getHeight());
        table.setCentered();
        ((Stack<MonsterCombatTable>) field(view, "monsterCombatTableStack")).push(table);
        List<DiceRoll> rolls = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            DiceRoll roll = new DiceRoll(i < successes ? DiceRoll.Score.GOOD : DiceRoll.Score.BAD);
            roll.setDiceNr(i); roll.setDiceValue(i < successes ? 5 : 2); rolls.add(roll);
        }
        view.showRolledTestDices(rolls).subscribe();
        tickCombat(600, fast);
        DiceRoller roller = ((DiceRollerStack) field(view, "diceRollerStack")).peek();
        AtomicInteger completed = new AtomicInteger();
        if (reproduceOldOrder) {
            // Before the fix, confirmation launched movement without awaiting it.
            TestResultTable result = TestResultTable.createScoreTable(0);
            result.setY(5);
            ((TestTableStack) field(view, "testTableStack")).push(result);
            InfoStage.showActor(result);
            roller.moveUp(result.getTop() + 5).subscribe();
            view.destroyDamage(rolls).subscribe(completed::incrementAndGet);
        } else {
            view.confirmTestResult(true, successes > 0, successes)
                    .andThen(Completable.defer(() -> view.destroyDamage(rolls)))
                    .subscribe(completed::incrementAndGet);
        }
        tickCombat(1200, fast);
        require(completed.get() == 1, "Combat damage phase did not complete once");
        boolean allBelowScreen = true;
        for (Actor die : InfoStage.getDiceLayer().getChildren()) {
            allBelowScreen &= die.getTop() <= 0.001f;
            require(die.isVisible(), "Combat should slide dice down, not toggle their visibility");
        }
        require(allBelowScreen != reproduceOldOrder,
                reproduceOldOrder ? "Old ordering no longer reproduces lingering failed dice"
                        : "Combat dice stayed on-screen: successes=" + successes + ", fast=" + fast);
        draw("combat-dice-" + (reproduceOldOrder ? "old-order" : successes + "-successes-" + fast));
        if (successes == 0) {
            completed.set(0);
            view.destroyMonsterHealth(rolls).subscribe(completed::incrementAndGet);
            tickCombat(100, fast);
            require(completed.get() == 1 && InfoStage.getDiceLayer().getChildren().size == 0,
                    "Zero-success combat did not remove its finished roll");
        } else {
            roller.hideDices();
            ((TestTableStack) field(view, "testTableStack")).pop().remove();
            tickCombat(100, fast);
        }
        table.remove();
        FastForwardAction.turnOff();
        System.out.println("PASS: combat damage successes=" + successes + ", fast=" + fast
                + ", reproduceOldOrder=" + reproduceOldOrder);
    }

    private void tickCombat(int frames, boolean fast) {
        for (int i = 0; i < frames; i++) {
            if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
            stage.act(1f / 60);
        }
    }

    /** Health hearts ride above the roaming monster; health fireballs stop it and land on its body. */
    private void previewMonsterHealthHit() throws Exception {
        MonsterCombatTable table = new MonsterCombatTable();
        InfoStage.addSmallActorToInfoStage(table);
        MonsterCombatTableData data = new MonsterCombatTableData();
        data.setMonsterClassName("ElderThingMonster"); data.setMonsterName("Elder Thing");
        data.setHorror(0); data.setDamage(2); data.setToughness(4); data.setCurrentHealth(4);
        table.init(data);
        table.setPosition(table.getCenteredX(), table.getTopY());
        table.highlightDamageAndToughness();
        tick(200);
        Actor image = table.getMonsterImage();
        Actor plate = table.getStatusPlate();
        require(plate.getParent() == InfoStage.getCreatureLayer(), "status plate not on the creature layer");
        require(plate.getY() >= image.getY() + image.getHeight() * 0.3f && plate.getTop() <= 540,
                "health plate not above the monster " + bounds(plate) + " / " + bounds(image));
        draw("monster-health-plate");

        AtomicInteger completed = new AtomicInteger();
        table.destroyMonsterHealth(Arrays.asList(new Vector2(300, 80), new Vector2(600, 80))).subscribe(completed::incrementAndGet);
        float startX = image.getX();
        boolean drawnMidFlight = false;
        Set<Actor> fireballs = new HashSet<>();
        for (int i = 0; i < 600 && completed.get() == 0; i++) {
            stage.act(1f / 60);
            collectFireballs(fireballs);
            require(Math.abs(image.getX() - startX) < 6f, "monster kept walking while being shot at");
            if (i == 25 && !drawnMidFlight) { draw("monster-health-fireballs"); drawnMidFlight = true; }
        }
        require(completed.get() == 1, "monster health destruction did not complete");
        require(fireballs.size() == 2, "expected one fireball per die, saw " + fireballs.size());
        tick(120);
        draw("monster-health-after");
        table.remove();
        tick(60);
        require(plate.getStage() == null && image.getStage() == null, "status plate / image left behind");
        System.out.println("PASS: monster health plate and aimed fireballs");
    }

    /** Dice burn the monster's horror (blue), then the monster hurls its remaining horror (blue) and damage at the investigator. */
    private void previewMonsterAttack() throws Exception {
        MonsterCombatTable table = new MonsterCombatTable();
        InfoStage.addSmallActorToInfoStage(table);
        MonsterCombatTableData data = new MonsterCombatTableData();
        data.setMonsterClassName("SkeletonMonster"); data.setMonsterName("Skeleton");
        data.setHorror(3); data.setDamage(2); data.setToughness(3); data.setCurrentHealth(3);
        table.init(data);
        table.highlightHorror();
        tick(200);
        Actor image = table.getMonsterImage();
        Group plate = table.getStatusPlate();
        Map<Actor, Vector2> slots = new LinkedHashMap<>();
        for (Actor actor : descendants(plate)) {
            if (actor.getParent() instanceof TokenInFrame && ((TokenInFrame) actor.getParent()).getImage() == actor) {
                slots.put(actor, slotPosition(actor, plate));
            }
        }
        require(slots.size() == 5, "expected 3 horror + 2 damage tokens on the plate, got " + slots.size());

        AtomicInteger completed = new AtomicInteger();
        table.destroyHorror(Arrays.asList(new Vector2(420, 90), new Vector2(470, 90))).subscribe(completed::incrementAndGet);
        Set<Actor> fireballs = new HashSet<>();
        for (int i = 0; i < 600 && completed.get() == 0; i++) {
            stage.act(1f / 60);
            collectFireballs(fireballs);
            requireTokensInSlots(slots, plate);
            if (i == 30) draw("monster-horror-shootdown-1");
            if (i == 60) draw("monster-horror-shootdown-2");
        }
        require(completed.get() == 1, "horror destruction did not complete");
        require(fireballs.size() == 2, "expected one fireball per die, saw " + fireballs.size());
        tick(30);

        for (boolean sanity : new boolean[]{true, false}) {
            completed.set(0);
            List<Vector2> investigator = Arrays.asList(new Vector2(110, 40), new Vector2(150, 40));
            (sanity ? table.destroySanity(investigator) : table.destroyHealth(investigator)).subscribe(completed::incrementAndGet);
            float startX = image.getX();
            fireballs.clear();
            for (int i = 0; i < 900 && completed.get() == 0; i++) {
                stage.act(1f / 60);
                collectFireballs(fireballs);
                require(Math.abs(image.getX() - startX) < 6f, "monster kept walking while attacking sanity=" + sanity + " i=" + i + " dx=" + (image.getX() - startX));
                requireTokensInSlots(slots, plate);
                if (i == 60) draw("monster-attack-" + (sanity ? "sanity" : "health"));
            }
            require(completed.get() == 1, "monster attack did not complete (sanity=" + sanity + ")");
            require(fireballs.size() == investigator.size(), "expected one fireball per point lost, saw " + fireballs.size());
            tick(60);
        }
        table.remove();
        tick(60);
        require(image.getStage() == null && table.getStatusPlate().getStage() == null, "monster left behind after attack");
        System.out.println("PASS: monster horror burn and attacks on the investigator");
    }

    /**
     * Through TestViewImpl: the monster's attack fireballs land on the investigator's stand on the map.
     * The map wraps, so the stand exists at x - MAP_WIDTH, x and x + MAP_WIDTH; the on-screen copy must be used.
     */
    @SuppressWarnings("unchecked")
    private void previewMonsterAttackOnStand() throws Exception {
        MapStage.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        InvestigatorId id = InvestigatorId.values()[0];
        float mapWidth = sk.sivak.eldritchhorror.core.constants.ViewProperties.MAP_WIDTH;
        InvestigatorImage visibleStand = null;
        for (float dx : new float[]{-mapWidth, 0, mapWidth}) {
            InvestigatorImage stand = new InvestigatorImage(id, null);
            MapStage.addToLayer(stand, InvestigatorUtils.getIdLayerResolver(id, false));
            stand.setPosition(900 + dx, 500);
            if (dx == 0) visibleStand = stand;
        }
        OrthographicCamera camera = MapStage.getCamera();
        camera.zoom = 1f; camera.position.set(1000, 600, 0); camera.update();
        Vector2 expected = new Vector2(900 - 1000 + 480 + visibleStand.getWidth() / 2f, 500 - 600 + 270 + visibleStand.getHeight() / 2f);

        TestViewImpl view = new TestViewImpl();
        MonsterCombatTable table = new MonsterCombatTable();
        InfoStage.addSmallActorToInfoStage(table);
        MonsterCombatTableData data = new MonsterCombatTableData();
        data.setMonsterClassName("SkeletonMonster"); data.setMonsterName("Skeleton");
        data.setHorror(2); data.setDamage(2); data.setToughness(3); data.setCurrentHealth(3);
        table.init(data);
        ((Stack<MonsterCombatTable>) field(view, "monsterCombatTableStack")).push(table);
        Field idField = TestViewImpl.class.getDeclaredField("combatInvestigatorId");
        idField.setAccessible(true); idField.set(view, id);
        tick(120);

        for (boolean sanity : new boolean[]{true, false}) {
            AtomicInteger completed = new AtomicInteger();
            (sanity ? view.destroySanity(2) : view.destroyHealth(2)).subscribe(completed::incrementAndGet);
            Map<Actor, Vector2> landing = new HashMap<>();
            for (int i = 0; i < 900 && completed.get() == 0; i++) {
                stage.act(1f / 60);
                for (Actor actor : descendants(stage.getRoot())) {
                    if (actor instanceof Fireball && actor.getActions().size > 0) {
                        landing.put(actor, new Vector2(actor.getX() + actor.getWidth() / 2f, actor.getY() + actor.getHeight() / 2f));
                    }
                }
            }
            require(completed.get() == 1, "attack on the stand did not complete (sanity=" + sanity + ")");
            require(landing.size() == 2, "expected 2 fireballs at the stand, saw " + landing.size());
            for (Vector2 end : landing.values()) {
                require(end.dst(expected) < 30f, "fireball landed at " + end + ", stand centre is " + expected);
            }
            tick(60);
        }
        table.remove();
        MapStage.removeActor(InvestigatorUtils.getIdLayerResolver(id, false));
        tick(60);
        System.out.println("PASS: monster fireballs hit the on-screen copy of the investigator stand");
    }
    /** Tokens may shake (a few px) and scale in place, but never leave their slot on the plate. */
    /** Unscaled position (scaling around the centre is allowed) of a token relative to the plate. */
    private static Vector2 slotPosition(Actor token, Group plate) {
        return token.getParent().localToAscendantCoordinates(plate, new Vector2(token.getX(), token.getY()));
    }

    private static void requireTokensInSlots(Map<Actor, Vector2> slots, Group plate) {
        for (Map.Entry<Actor, Vector2> slot : slots.entrySet()) {
            require(slot.getKey().getParent() instanceof TokenInFrame && slot.getKey().getStage() != null, "token left the plate");
            Vector2 now = slotPosition(slot.getKey(), plate);
            require(now.dst(slot.getValue()) <= 4.5f, "token drifted from its slot by " + now.dst(slot.getValue()));
        }
    }

    private void collectFireballs(Set<Actor> seen) {
        for (Actor actor : descendants(stage.getRoot())) {
            if (actor instanceof Fireball) seen.add(actor);
        }
    }

    private static List<Actor> descendants(Group group) {
        List<Actor> result = new ArrayList<>();
        for (Actor actor : group.getChildren()) {
            result.add(actor);
            if (actor instanceof Group) result.addAll(descendants((Group) actor));
        }
        return result;
    }

    private static Object field(Object owner, String name) {
        try {
            Field field = owner.getClass().getDeclaredField(name);
            field.setAccessible(true); return field.get(owner);
        } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
    }

    private void previewCombatTokens() throws Exception {
        for (String type : new String[]{"horror", "damage"}) {
            for (int count : new int[]{4, 12}) {
                TokenInFrameBar bar = new TokenInFrameBar(count,
                        CustomAssetManager.getTexture("combat/" + type + ".png"));
                stage.addActor(bar);
                bar.setSize(bar.getPrefWidth(), bar.getPrefHeight());
                bar.validate();
                bar.highlightRemainingTokens();
                tick(100);
                draw("combat-" + type + "-" + count);
                for (Actor token : bar.getDetachedList()) token.remove();
                bar.remove();
            }
        }
    }

    private void previewCombatPanel() throws Exception {
        for (int count : new int[]{2, 8}) {
            MonsterCombatTable panel = new MonsterCombatTable();
            InfoStage.addSmallActorToInfoStage(panel);
            MonsterCombatTableData data = new MonsterCombatTableData();
            data.setMonsterClassName("SkeletonMonster");
            data.setMonsterName(count == 2 ? "Skeleton" : "A very long monster name");
            data.setHorror(count); data.setDamage(count == 2 ? 1 : count);
            data.setToughness(count); data.setCurrentHealth(count);
            panel.init(data);
            panel.setPosition((960 - panel.getWidth()) / 2, (540 - panel.getHeight()) / 2);
            panel.highlightHorror();
            tick(100);
            draw("combat-panel-horror-" + count);
            panel.highlightDamageAndToughness();
            tick(100);
            draw("combat-panel-damage-" + count);
            panel.updateHorror(0).subscribe();
            panel.updateDamage(1).subscribe();
            tick(100);
            draw("combat-panel-updated-" + count);
            require(panel.getStatusPlate().getTop() <= 540 && panel.getStatusPlate().getX() >= 0 && panel.getStatusPlate().getRight() <= 960, "status plate off screen " + bounds(panel.getStatusPlate()));
            panel.remove();
            tick(60);
        }
    }

    /** Asset selection before a (combat) test: panels must not overlap each other or the fixed HUD controls. */
    private void previewTestSetup() throws Exception {
        float hudTop = InfoStage.getInvestigatorHud().getPrefHeight();
        Map<String, Rectangle> reserved = new LinkedHashMap<>();
        reserved.put("side menu", new Rectangle(0, 0, 72, 540));
        reserved.put("clock", new Rectangle(72, 468, 74, 72));
        reserved.put("menu button", new Rectangle(898, 483, 62, 57));
        reserved.put("fast forward", new Rectangle(898, 0, 62, 57));
        reserved.put("investigator hud", new Rectangle(0, 0, 265, hudTop));
        for (boolean combat : new boolean[]{true, false}) {
            for (int cards : new int[]{1, 2, 3}) {
                TestViewImpl view = new TestViewImpl();
                MonsterCombatTable panel = null;
                if (combat) {
                    MonsterCombatTableData data = new MonsterCombatTableData();
                    data.setMonsterClassName("SkeletonMonster"); data.setMonsterName("Skeleton");
                    data.setHorror(3); data.setDamage(3); data.setToughness(4); data.setCurrentHealth(4);
                    view.showCombatTable(data).subscribe();
                    tick(120);
                    panel = ((Stack<MonsterCombatTable>) field(view, "monsterCombatTableStack")).peek();
                    if (cards == 1) {
                        // The portrait roams the screen on its own, behind panels, cards and dice, and never leaves the safe area.
                        draw("test-setup-combat-centred");
                        Actor image = panel.getMonsterImage();
                        require(image.getParent() == InfoStage.getCreatureLayer() && image.getTouchable() == Touchable.disabled, "monster image must be on the creature layer and not touchable");
                        Vector2 start = new Vector2(image.getX(), image.getY());
                        float travelled = 0;
                        Rectangle area = new Rectangle(panel.getWanderArea());
                        area.setHeight(area.height + 8); // stepping bounce
                        for (int i = 0; i < 1200; i++) {
                            stage.act(1f / 60);
                            require(area.contains(image.getX(), image.getY()), "monster image left wander area at " + image.getX() + "," + image.getY());
                            travelled = Math.max(travelled, start.dst(image.getX(), image.getY()));
                        }
                        require(travelled > 90, "monster image did not wander (" + travelled + ")");
                        draw("test-setup-combat-wander");
                    }
                }
                List<UsableAsset> assets = new ArrayList<>();
                for (int i = 0; i < cards; i++) {
                    UsableAsset asset = new UsableAsset();
                    asset.setCardInfo(TokenCardInfo.buildClueTokenCardInfo());
                    asset.setDicePoolBonus(1);
                    assets.add(asset);
                }
                view.confirmTest(Stat.WILL, -1, 3, combat ? 0 : 1, assets, combat ? 0 : 1, combat).subscribe(selected -> { });
                for (int i = 0; i < 120; i++) {
                    // Card art loads on an IO thread and completes through posted render-thread runnables.
                    Thread.sleep(5);
                    ((LwjglApplication) Gdx.app).executeRunnables();
                    stage.act(1f / 60);
                }
                Map<String, Actor> actors = new LinkedHashMap<>();
                actors.put("cards", (Actor) field(view, "selectAssetsToUseActor"));
                actors.put("test info", (Actor) field(view, "testInfoTable"));
                actors.put("test button", (Actor) field(view, "testButton"));
                String name = "test-setup-" + (combat ? "combat-" : "plain-") + cards;
                if (combat) {
                    Group root = stage.getRoot();
                    int creature = root.getChildren().indexOf(InfoStage.getCreatureLayer(), true);
                    require(creature < root.getChildren().indexOf(actors.get("cards").getParent(), true)
                                    && creature < root.getChildren().indexOf(InfoStage.getDiceLayer(), true),
                            name + ": cards and dice must be drawn over the monster");
                }
                draw(name);
                List<String> names = new ArrayList<>(actors.keySet());
                for (int i = 0; i < names.size(); i++) {
                    Rectangle a = bounds(actors.get(names.get(i)));
                    require(a.x >= 0 && a.y >= 0 && a.x + a.width <= 960 && a.y + a.height <= 540,
                            name + ": " + names.get(i) + " off screen " + a);
                    for (int j = i + 1; j < names.size(); j++) {
                        require(!a.overlaps(bounds(actors.get(names.get(j)))),
                                name + ": " + names.get(i) + " overlaps " + names.get(j));
                    }
                    for (Map.Entry<String, Rectangle> hud : reserved.entrySet()) {
                        require(!a.overlaps(hud.getValue()), name + ": " + names.get(i) + " overlaps " + hud.getKey() + " " + a);
                    }
                }
                for (Actor actor : actors.values()) actor.remove();
                if (panel != null) { panel.remove(); tick(60); }
                require(panel == null || panel.getMonsterImage().getStage() == null, "monster image left behind");
                for (Subscription s : (List<Subscription>) field(view, "monsterCombatTableSubscriptions")) s.unsubscribe();
                System.out.println("PASS: " + name);
            }
        }
    }

    private static Rectangle bounds(Actor actor) {
        Vector2 corner = actor.localToStageCoordinates(new Vector2());
        // Unscaled bounds: the only scaled actor is the pulsing button, which shrinks around its centre.
        return new Rectangle(corner.x, corner.y, actor.getWidth(), actor.getHeight());
    }

    private void previewCombatOverview() throws Exception {
        CombatOverviewTableData data = new CombatOverviewTableData();
        data.setInvestigatorId(Arrays.stream(InvestigatorId.values()).max(Comparator.comparingInt(id -> id.toString().length())).get());
        CombatOverviewTableData.StatRowData will = new CombatOverviewTableData.StatRowData(Stat.WILL, 3);
        will.setModifier(-1);
        data.setHorrorRowData(will);
        CombatOverviewTableData.StatRowData strength = new CombatOverviewTableData.StatRowData(Stat.STRENGTH, 2);
        strength.setBonus(1);
        data.setDamageRowData(strength);
        data.setMonsterClassName("SkeletonMonster"); data.setMonsterName("Skeleton");
        data.setHorror(1); data.setDamage(2); data.setToughness(3); data.setCurrentHealth(3);
        TestViewImpl view = new TestViewImpl();
        view.showCombatOverview(data).subscribe();
        tick(60);
        CombatOverviewTable overview = null;
        for (Actor actor : descendants(stage.getRoot())) {
            if (actor instanceof CombatOverviewTable) overview = (CombatOverviewTable) actor;
        }
        require(overview != null, "combat overview not shown");
        Rectangle box = bounds(overview);
        require(Math.abs(box.x + box.width / 2 - 480) < 1, "combat overview not centred " + box);
        require(box.y >= InfoStage.getInvestigatorHud().getPrefHeight() && box.y + box.height <= 500, "combat overview overlaps HUD/title " + box);
        draw("combat-overview");
        overview.remove();
        InfoStage.hideLabel(UiText.get("combat.title"));
        for (Actor actor : descendants(stage.getRoot())) {
            if (actor instanceof Button && actor.getParent() != null && actor.getParent().getParent() == stage.getRoot()
                    && actor.getY() < 20) actor.remove(); // the Fight button
        }
        TestResultTable result = TestResultTable.createScoreTable(2);
        result.setPosition(480 - result.getWidth() / 2, 5);
        stage.addActor(result);
        tick(5);
        draw("test-result");
        result.remove();
    }

    private void drawPanel(Actor panel, String filename) throws Exception {
        Gdx.gl.glClearColor(0.12f, 0.15f, 0.14f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap((int) panel.getX() - 8, (int) panel.getY() - 8,
                (int) panel.getWidth() + 16, (int) panel.getHeight() + 16);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/prompt-preview/" + filename + ".png"), pixels);
        png.dispose(); pixels.dispose();
    }
    private void tick(int frames) { for (int i = 0; i < frames; i++) stage.act(1f / 60); }
    private void tickDice(Group layer, int frames) {
        // Act dice directly so the idle fast-forward button cannot reset the selected test speed.
        for (int i = 0; i < frames; i++) layer.act(1f / 60);
    }
    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    private void draw(String filename) throws Exception {
        Gdx.gl.glClearColor(0.12f, 0.15f, 0.14f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/prompt-preview/" + filename + ".png"), pixels);
        png.dispose(); pixels.dispose();
    }
}
