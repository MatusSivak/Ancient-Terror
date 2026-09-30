import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import sk.sivak.eldritchhorror.core.constants.test.DiceRoll;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.view.components.diceroller.*;
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
import sk.sivak.eldritchhorror.core.view.test.TestViewImpl;
import sk.sivak.eldritchhorror.core.view.utils.ButtonBuilder;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;
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
            drawPanel(panel, "combat-panel-horror-" + count);
            panel.highlightDamageAndToughness();
            tick(100);
            drawPanel(panel, "combat-panel-damage-" + count);
            panel.updateHorror(0).subscribe();
            panel.updateDamage(1).subscribe();
            tick(100);
            drawPanel(panel, "combat-panel-updated-" + count);
            panel.remove();
        }
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
