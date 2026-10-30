import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import rx.Completable;
import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.asset.AxeAsset;
import sk.sivak.eldritchhorror.core.constants.monster.CultistMonster;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorBasics;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.displayasset.*;
import sk.sivak.eldritchhorror.core.controller.GameController;
import sk.sivak.eldritchhorror.core.view.asset.AssetView;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import sk.sivak.eldritchhorror.core.view.bigactors.BigActorsManager;
import sk.sivak.eldritchhorror.core.view.components.card.CardDestructionEffect;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.components.sheet.mystery.MysteryCard;
import sk.sivak.eldritchhorror.core.view.game.HudButtons;
import sk.sivak.eldritchhorror.core.view.game.InfoStage;
import sk.sivak.eldritchhorror.core.view.game.MapStage;
import sk.sivak.eldritchhorror.core.view.investigator.InvestigatorView;
import sk.sivak.eldritchhorror.core.view.components.sheet.monster.MonsterCard;
import sk.sivak.eldritchhorror.core.view.map.CameraActor;
import sk.sivak.eldritchhorror.core.view.monster.MonsterViewImpl;
import sk.sivak.eldritchhorror.core.view.utils.FastForwardAction;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Drives the real discard confirmation and current-Mystery resolution without loading a saved game. */
public class CardDestructionPreview extends ApplicationAdapter {
    private Stage stage;

    public static void main(String[] args) {
        LwjglApplicationConfiguration.disableAudio = true;
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960; config.height = 540; config.x = -10000; config.y = -10000; config.forceExit = false;
        new LwjglApplication(new CardDestructionPreview(), config);
    }

    @Override public void create() {
        try {
            Sound silent = (Sound) Proxy.newProxyInstance(Sound.class.getClassLoader(), new Class<?>[]{Sound.class},
                    (p, m, a) -> {
                        if (m.getName().equals("hashCode")) return System.identityHashCode(p);
                        if (m.getName().equals("equals")) return p == a[0];
                        return m.getReturnType() == long.class ? 0L : null;
                    });
            Gdx.audio = (Audio) Proxy.newProxyInstance(Audio.class.getClassLoader(), new Class<?>[]{Audio.class}, (p, m, a) -> silent);
            VisUI.load(); stage = InfoStage.getStageSafe();
            GameController controller = (GameController) Proxy.newProxyInstance(GameController.class.getClassLoader(),
                    new Class<?>[]{GameController.class}, (p, m, a) -> null);
            new HudButtons(controller).init();
            Image background = new Image(CustomAssetManager.getTexture("encounter/background/mythos.jpg"));
            background.setSize(960, 540); background.setColor(.42f, .42f, .42f, 1);
            stage.getRoot().addActorAt(0, background);
            checkSnapshotTransform();
            checkInvestigatorRefreshCamera();
            for (boolean fast : new boolean[]{false, true}) {
                checkDiscard(false, fast);
                checkDiscard(true, fast);
                checkMystery(false, fast);
                checkRumor(fast);
                checkMonster(false, fast);
                checkMonster(true, fast);
            }
            checkMystery(true, false);
            checkSpeedChangeAndInterruption();
            require(tracked() == 0, "Fast-forward actions leaked");
            System.out.println("PASS: card discard, Mystery and monster destruction; encounter camera preservation; snapshot colour/transform; hidden cards; normal/fast-forward; reused sheets; tall text; early removal and GPU cleanup");
        } catch (Throwable error) { error.printStackTrace(); System.exit(1); }
        finally { FastForwardAction.turnOff(); Gdx.app.exit(); }
    }

    private void checkInvestigatorRefreshCamera() {
        InvestigatorBasics basics = (InvestigatorBasics) Proxy.newProxyInstance(InvestigatorBasics.class.getClassLoader(),
                new Class<?>[]{InvestigatorBasics.class}, (p, m, a) -> {
                    if (m.getName().equals("getInvestigatorId")) return InvestigatorId.values()[0];
                    if (m.getName().equals("getLocationId")) return LocationId.LONDON;
                    throw new AssertionError("Unexpected investigator lookup: " + m.getName());
                });
        GameController controller = (GameController) Proxy.newProxyInstance(GameController.class.getClassLoader(),
                new Class<?>[]{GameController.class}, (p, m, a) -> basics);
        AtomicInteger refreshed = new AtomicInteger();
        InvestigatorView view = new InvestigatorView(controller) {
            @Override public Completable updateHud(InvestigatorBasics current) {
                return Completable.fromAction(refreshed::incrementAndGet);
            }
        };
        OrthographicCamera camera = MapStage.getCamera();
        for (boolean fast : new boolean[]{false, true}) {
            if (fast) FastForwardAction.turnOn(); else FastForwardAction.turnOff();
            camera.position.set(1234, 678, 0); camera.zoom = 1.7f; camera.update();
            AtomicInteger done = new AtomicInteger();
            view.showActiveInvestigator(false, false).subscribe(done::incrementAndGet);
            for (Actor actor : MapStage.getStage().getActors())
                require(!(actor instanceof CameraActor), "Encounter refresh queued a camera movement");
            require(done.get() == 1 && camera.position.x == 1234 && camera.position.y == 678 && camera.zoom == 1.7f,
                    "Encounter refresh changed camera position or zoom");
        }
        require(refreshed.get() == 2, "Camera preservation skipped the HUD refresh");
        FastForwardAction.turnOff(); tick(100);
    }

    private void checkMonster(boolean hidden, boolean fast) throws Exception {
        FastForwardAction.turnOff();
        CultistMonster monster = new CultistMonster();
        MonsterCard card = BigActorsManager.getMonsterCard();
        AtomicInteger dismissed = new AtomicInteger();
        if (!hidden) {
            BigActorsManager.initMonsterCard(monster, () -> {}, dismissed::incrementAndGet);
            BigActorsManager.displayOrHideMonsterCard(); tick(100);
            require(dismissed.get() == 1, "Monster failed to open");
            card.setOnDisplayHideAction(dismissed::incrementAndGet);
        }
        int buffers = buffers();
        AtomicInteger done = new AtomicInteger();
        Completable destroy = new MonsterViewImpl().tearMonsterCardApart(monster);
        require(buffers() == buffers && done.get() == 0, "Monster destruction ran before subscription");
        if (fast) FastForwardAction.turnOn();
        destroy.subscribe(done::incrementAndGet, error -> { throw new RuntimeException(error); });
        Group animation = stage.getRoot().findActor("card-destruction");
        for (int frame = 0; animation == null && frame < 180; frame++) {
            stage.act(1f / 60);
            animation = stage.getRoot().findActor("card-destruction");
        }
        require(animation != null && done.get() == 0, "Missing monster tear");
        runEffect(animation, done, !hidden && !fast ? "monster" : null, false);
        require(dismissed.get() == (hidden ? 0 : 1), "Monster destruction replayed dismissal");
        require(!BigActorsManager.isMonsterCardDisplayed() && !BigActorsManager.isLocked(), "Destroyed monster kept sheet state");
        require(card.getStage() == null && buffers() == buffers && tracked() == 0, "Monster snapshot or actions leaked");
        FastForwardAction.turnOff();
        BigActorsManager.initMonsterCard(monster, () -> {}, null);
        BigActorsManager.displayOrHideMonsterCard(); tick(100);
        require(card.getStage() == stage && card.getColor().a == 1 && card.getScaleX() == 1,
                "Next monster did not reopen at full opacity and size");
        BigActorsManager.displayOrHideMonsterCard(); tick(100);
    }

    private void checkSnapshotTransform() throws Exception {
        Group parent = new Group(); parent.setPosition(360, 120); parent.setScale(.8f, .9f); parent.setRotation(11);
        Group card = new Group(); card.setSize(160, 220); card.setOrigin(80, 110);
        Image bottom = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        bottom.setBounds(0, 0, 160, 110); bottom.setColor(.1f, .2f, .9f, 1);
        Image top = new Image(CustomAssetManager.getTexture(CustomAssetManager.PURE_WHITE_BACKGROUND));
        top.setBounds(0, 110, 160, 110); top.setColor(.9f, .2f, .1f, 1);
        card.addActor(bottom); card.addActor(top); parent.addActor(card); stage.addActor(parent);
        Vector2 a = card.localToStageCoordinates(new Vector2(80, 40));
        Vector2 b = card.localToStageCoordinates(new Vector2(80, 180));
        renderStage(); Pixmap before = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        AtomicInteger acted = new AtomicInteger(), done = new AtomicInteger();
        card.addAction(Actions.run(acted::incrementAndGet));
        int buffers = buffers();
        Completable effect = CardDestructionEffect.discard(card);
        require(card.getStage() == stage && buffers() == buffers, "Effect ran before subscription");
        effect.subscribe(done::incrementAndGet);
        require(acted.get() == 0 && card.getRotation() == 0 && card.getScaleX() == 1 && card.getX() == 0,
                "Snapshot advanced actions or changed the original transform");
        renderStage(); Pixmap after = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        for (Vector2 point : new Vector2[]{a, b}) {
            Color c1 = new Color(before.getPixel(Math.round(point.x), Math.round(point.y)));
            Color c2 = new Color(after.getPixel(Math.round(point.x), Math.round(point.y)));
            require(Math.abs(c1.r - c2.r) < .03f && Math.abs(c1.b - c2.b) < .03f,
                    "Snapshot flipped, moved or darkened the card: " + c1 + " -> " + c2);
        }
        before.dispose(); after.dispose(); tick(100); parent.remove();
        require(done.get() == 1 && buffers() == buffers, "Snapshot lifecycle");
    }

    private void checkDiscard(boolean hidden, boolean fast) throws Exception {
        FastForwardAction.turnOff();
        int buffers = buffers();
        AssetView view = new AssetView();
        ShowCardRequest request = new ShowCardRequest();
        request.setAssetInfo(new AxeAsset()); request.setAssetOriginType(AssetOriginType.CENTER);
        request.setHideType(HideType.DISCARD_ALWAYS); request.setHiddenTemplate(hidden);
        AtomicInteger done = new AtomicInteger();
        view.showCard(request).subscribe(response -> {
            require(response == ShowCardResponse.OK, "Discard changed the response"); done.incrementAndGet();
        }, error -> { throw new RuntimeException(error); });
        loadArtwork();
        Actor original = (Actor) field(view, "cardTemplate");
        require(hidden || ((CardTemplate) original).getForeground() != null, "Card artwork is not fully loaded");
        float x = original.getX(), y = original.getY(), sx = original.getScaleX();
        Button button = ((Button[]) field(view, "buttons"))[0];
        if (fast) FastForwardAction.turnOn();
        click(button);
        require(done.get() == 0, "Discard advances before the effect finishes");
        require(original.getX() == x && original.getY() == y && original.getScaleX() == sx, "Discard mutated the card transform");
        Group animation = stage.getRoot().findActor("card-destruction");
        require(animation != null && original.getStage() == null, "Missing discard animation");
        runEffect(animation, done, !hidden && !fast ? "card" : null, false);
        tick(50);
        require(buffers() == buffers && tracked() == 0, "Discard leaked resources");
    }

    private void checkMystery(boolean tall, boolean fast) throws Exception {
        FastForwardAction.turnOff();
        MysteryCardInfo info = mystery(tall);
        MysteryCard card = BigActorsManager.getMysteryCard();
        card.init(info, 2, 3);
        AtomicInteger dismissed = new AtomicInteger(); card.setAfterHideAction(dismissed::incrementAndGet);
        BigActorsManager.displayOrHideCurrentMystery(); tick(100);
        require(card.getStage() == stage, "Mystery failed to open");
        int buffers = buffers();
        AtomicInteger done = new AtomicInteger();
        if (fast) FastForwardAction.turnOn();
        new MonsterViewImpl().tearMysteryCardApart(info).subscribe(done::incrementAndGet);
        Group animation = stage.getRoot().findActor("mystery-resolution");
        require(animation != null && done.get() == 0, "Missing Mystery resolution");
        runEffect(animation, done, !fast ? (tall ? "tall-mystery" : "mystery") : null, true);
        require(dismissed.get() == 0, "Resolution replayed the old dismissal callback");
        require(!(Boolean) field(field(card, "displayHide"), "displayed"), "Destroyed Mystery stayed open");
        require(card.getStage() == null && buffers() == buffers, "Mystery leaked its card or snapshot");
        require(!BigActorsManager.isLocked(), "Mystery left the controls locked");
        // The same card instance is reused by the game for the next mystery.
        FastForwardAction.turnOff();
        card.init(mystery(false), 3, 3);
        BigActorsManager.displayOrHideCurrentMystery(); tick(100);
        require(card.getStage() == stage && card.getColor().a == 1 && card.getScaleX() == 1,
                "Next Mystery did not reopen at full size");
        BigActorsManager.displayOrHideCurrentMystery(); tick(100);
        require(card.getStage() == null && tracked() == 0, "Reopened Mystery did not close");
    }

    private void runEffect(Group animation, AtomicInteger done, String frames, boolean mystery) throws Exception {
        boolean split = false;
        for (int frame = 0; frame < 180 && done.get() == 0; frame++) {
            stage.act(1f / 60);
            Actor shard = animation.findActor("paper-fragment-0");
            split |= shard.isVisible();
            if (frame == (mystery ? 25 : 11) && !FastForwardAction.isOn()) {
                require(animation.findActor("intact-card").isVisible(), "No buildup before tearing");
            }
            if (frame == (mystery ? 42 : 24) && !FastForwardAction.isOn()) {
                require(animation.getHeight() * animation.getScaleY() <= 540
                        && animation.getWidth() * animation.getScaleX() <= 960, "Intact card clips at peak size");
                require(shard.isVisible() && shard.getColor().a > .98f, "Fragments fade before the tear is visible");
            }
            if (frames != null && frame % 2 == 0) draw(frames + "/frame-" + String.format("%03d", frame / 2));
        }
        require(split && done.get() == 1 && animation.getStage() == null, "Effect completion or cleanup");
    }

    private void checkRumor(boolean fast) throws Exception {
        FastForwardAction.turnOff();
        sk.sivak.eldritchhorror.core.constants.RumorCardInfo info =
                (sk.sivak.eldritchhorror.core.constants.RumorCardInfo) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[]{sk.sivak.eldritchhorror.core.constants.RumorCardInfo.class}, (p,m,a) -> {
                    switch(m.getName()) {
                        case "getId": return "preview-rumor";
                        case "getTitleText": return "A Rumor Resolved";
                        case "getFlavorText": return "The whispers finally fade.";
                        case "getObjectiveText": return "Spend 2 Clues to resolve this rumor.";
                        case "getFailureText": return "Lose 1 Sanity.";
                        case "getReckoningText": return "Advance the countdown.";
                        case "getCluesRequired": return 2;
                        case "getTimeRemaining": return 3;
                        case "isStormSpawned": return false;
                        default: return null;
                    }
                });
        sk.sivak.eldritchhorror.core.view.components.sheet.rumor.RumorCard card = BigActorsManager.getRumorCard();
        card.init(info); BigActorsManager.displayOrHideRumor(); tick(100);
        require(card.getStage()==stage,"Rumor did not open");
        int buffers = buffers(); AtomicInteger done = new AtomicInteger();
        if(fast)FastForwardAction.turnOn();
        new MonsterViewImpl().tearRumorCardApart().subscribe(done::incrementAndGet);
        Group animation = stage.getRoot().findActor("mystery-resolution");
        require(animation!=null,"Rumor still uses the old tear");
        runEffect(animation,done,fast?null:"rumor",true);
        require(card.getStage()==null&&buffers()==buffers&&!BigActorsManager.isLocked(),"Rumor destruction leaked state");
        FastForwardAction.turnOff();
        card.init(info);BigActorsManager.displayOrHideRumor();tick(100);
        require(card.getStage()==stage&&card.getColor().a==1,"Next rumor could not reopen");
        BigActorsManager.displayOrHideRumor();tick(100);
    }

    private void checkSpeedChangeAndInterruption() throws Exception {
        for (boolean interrupt : new boolean[]{false, true}) {
            FastForwardAction.turnOff();
            Image card = new Image(CustomAssetManager.getTexture("card/card_template_hidden.jpg"));
            card.setSize(410, 440); card.setPosition(275, 50); stage.addActor(card);
            int buffers = buffers(); AtomicInteger done = new AtomicInteger();
            CardDestructionEffect.discard(card).subscribe(done::incrementAndGet);
            Group animation = stage.getRoot().findActor("card-destruction");
            tick(35);
            require(buffers() == buffers + 1, "Expected one shared snapshot, not one per fragment");
            if (interrupt) {
                animation.remove();
                require(done.get() == 0, "Interrupted destruction completed");
            } else {
                FastForwardAction.turnOn(); tick(15);
                require(done.get() == 1, "Changing speed did not complete");
            }
            require(buffers() == buffers && tracked() == 0, "Interrupted/speed-changed effect leaked resources");
        }
    }

    private MysteryCardInfo mystery(boolean tall) {
        return (MysteryCardInfo) Proxy.newProxyInstance(MysteryCardInfo.class.getClassLoader(), new Class<?>[]{MysteryCardInfo.class},
                (p, m, a) -> {
                    switch (m.getName()) {
                        case "getName": return "mystery.azathoth.voice.name";
                        case "getFlavorText": return "mystery.azathoth.voice.flavor";
                        case "getMysteryText": return tall ? String.join("\n", java.util.Collections.nCopies(5,
                                "At the end of the Mythos Phase, an investigator may spend clues to solve this Mystery.")) : "mystery.azathoth.voice.text";
                        case "getMysteryComplexity": case "getProgress": return 3;
                        case "getClueCredit": return 0;
                        default: return null;
                    }
                });
    }

    private void click(Button button) {
        InputEvent event = new InputEvent(); event.setListenerActor(button); event.setTarget(button);
        for (int i = button.getListeners().size - 1; i >= 0; i--) {
            EventListener listener = button.getListeners().get(i);
            if (listener instanceof ClickListener && listener != button.getClickListener()) ((ClickListener) listener).clicked(event, 0, 0);
        }
    }
    private void loadArtwork() throws Exception {
        java.lang.reflect.Method get = CustomAssetManager.class.getDeclaredMethod("get"); get.setAccessible(true);
        com.badlogic.gdx.assets.AssetManager assets = (com.badlogic.gdx.assets.AssetManager) get.invoke(null);
        // This deterministic preview stays inside create(), so the desktop frame ID does not advance.
        // Pump uploads explicitly instead of relying on the production once-per-frame loader.
        for (int i = 0; i < 180; i++) {
            Thread.sleep(4); assets.update(16); ((LwjglApplication) Gdx.app).executeRunnables(); stage.act(1f / 60);
        }
    }
    private void tick(int frames) { for (int i = 0; i < frames; i++) stage.act(1f / 60); }
    private static Object field(Object owner, String name) throws Exception {
        Field field = owner.getClass().getDeclaredField(name); field.setAccessible(true); return field.get(owner);
    }
    private int tracked() throws Exception { return ((Map<?, ?>) field(InfoStage.getFastForwardButton(), "fastForwardActions")).size(); }
    private static int buffers() {
        Matcher matches = Pattern.compile("\\d+").matcher(FrameBuffer.getManagedStatus());
        int count = 0; while (matches.find()) count += Integer.parseInt(matches.group()); return count;
    }
    private void renderStage() {
        Gdx.gl.glClearColor(.07f, .08f, .08f, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.draw();
    }
    private void draw(String name) throws Exception {
        renderStage(); Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 960, 540);
        PixmapIO.PNG png = new PixmapIO.PNG(); png.setFlipY(true);
        png.write(Gdx.files.local("../build/card-destruction-preview/" + name + ".png"), pixels);
        png.dispose(); pixels.dispose();
    }
    private static void require(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
