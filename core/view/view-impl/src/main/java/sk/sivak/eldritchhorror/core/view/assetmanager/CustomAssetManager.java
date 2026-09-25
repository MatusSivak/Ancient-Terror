package sk.sivak.eldritchhorror.core.view.assetmanager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import java8.features.function.Consumer;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.view.font.FontGlyphEnricher;
import sk.sivak.eldritchhorror.core.view.font.BitmapFontSizing;
import sk.sivak.eldritchhorror.core.view.utils.UiText;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author msivak
 */
public class CustomAssetManager extends AssetManager {

    private PublishSubject<Float> progressSubject;

    private Consumer<String> cardAssetLoadedCallback;
    private Consumer<String> cardConditionLoadedCallback;
    private Consumer<String> cardSpellLoadedCallback;
    private Consumer<String> cardArtifactLoadedCallback;
    private final Map<String, BitmapFont> sizedFonts = new ConcurrentHashMap<>();

    public CustomAssetManager() {

    }

    // ---- Parallel texture decoding -------------------------------------------------------------
    // AssetManager decodes one texture at a time on a single thread and needs several frame round
    // trips per asset. Plain PNG/JPG textures are instead decoded on a small pool and only the GL
    // upload stays on the render thread (inside update()), so the rest of the API is unchanged.

    private static final int ASYNC_PUMP_MS = 8;
    private static final int DECODE_THREADS =
            Math.max(1, Math.min(4, Runtime.getRuntime().availableProcessors() - 1));
    /** Bounds decoded-but-not-uploaded pixmaps so eager decoding cannot pile up native memory. */
    private static final int MAX_DECODED_WAITING = DECODE_THREADS * 3;
    private static ExecutorService decodePool;

    private final Map<String, PendingTexture> pendingTextures = new HashMap<>();
    private final ConcurrentLinkedQueue<PendingTexture> decodedTextures = new ConcurrentLinkedQueue<>();
    private final Semaphore decodedSlots = new Semaphore(MAX_DECODED_WAITING);
    private volatile boolean decodingCancelled;
    private int batchTextures;
    private int batchTexturesDone;
    private int batchManagerAssets;
    private long lastPumpedFrame = -1;

    private static final class PendingTexture {
        final String fileName;
        final AtomicBoolean claimed = new AtomicBoolean();
        int references = 1;
        volatile Pixmap pixmap;
        volatile boolean failed;
        boolean slotHeld;

        PendingTexture(String fileName) {
            this.fileName = fileName;
        }
    }

    private static synchronized ExecutorService decodePool() {
        if (decodePool == null) {
            AtomicInteger counter = new AtomicInteger();
            decodePool = Executors.newFixedThreadPool(DECODE_THREADS, runnable -> {
                Thread thread = new Thread(runnable, "TextureDecoder-" + counter.incrementAndGet());
                thread.setDaemon(true);
                thread.setPriority(Thread.NORM_PRIORITY - 1);
                return thread;
            });
        }
        return decodePool;
    }

    private static boolean isDecodableTexture(String fileName, Class<?> type, AssetLoaderParameters<?> parameter) {
        if (type != Texture.class || parameter != null || Gdx.app == null || Gdx.gl == null) {
            return false;
        }
        String lower = fileName.toLowerCase(Locale.ENGLISH);
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
    }

    @Override
    public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
        if (!isDecodableTexture(fileName, type, parameter)) {
            if (super.getQueuedAssets() == 0) {
                batchManagerAssets = 0;
            }
            batchManagerAssets++;
            super.load(fileName, type, parameter);
            return;
        }
        if (isLoaded(fileName, Texture.class)) {
            // Same effect as AssetManager queueing an already loaded asset.
            setReferenceCount(fileName, getReferenceCount(fileName) + 1);
            return;
        }
        PendingTexture pending = pendingTextures.get(fileName);
        if (pending != null) {
            pending.references++;
            return;
        }
        pending = new PendingTexture(fileName);
        pendingTextures.put(fileName, pending);
        batchTextures++;
        PendingTexture task = pending;
        decodePool().execute(() -> decodeOnWorker(task));
    }

    private void decodeOnWorker(PendingTexture pending) {
        if (decodingCancelled || !pending.claimed.compareAndSet(false, true)) {
            return;
        }
        try {
            decodedSlots.acquire();
            pending.slotHeld = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        decode(pending);
        if (decodingCancelled) {
            discard(pending);
            return;
        }
        decodedTextures.add(pending);
    }

    private static void decode(PendingTexture pending) {
        try {
            pending.pixmap = new Pixmap(Gdx.files.internal(pending.fileName));
        } catch (RuntimeException e) {
            pending.failed = true;
        }
    }

    private void discard(PendingTexture pending) {
        if (pending.pixmap != null) {
            pending.pixmap.dispose();
            pending.pixmap = null;
        }
        if (pending.slotHeld) {
            pending.slotHeld = false;
            decodedSlots.release();
        }
    }

    /** GL upload of one decoded texture; must run on the render thread. */
    private boolean uploadOneDecoded() {
        PendingTexture pending = decodedTextures.poll();
        if (pending == null) {
            return false;
        }
        upload(pending);
        return true;
    }

    private void upload(PendingTexture pending) {
        if (pendingTextures.get(pending.fileName) != pending) {
            discard(pending);
            return;
        }
        pendingTextures.remove(pending.fileName);
        batchTexturesDone++;
        if (pending.failed || pending.pixmap == null) {
            discard(pending);
            // Let the regular loader report the problem exactly as before.
            for (int i = 0; i < pending.references; i++) {
                batchManagerAssets++;
                super.load(pending.fileName, Texture.class, null);
            }
            return;
        }
        FileHandle file = Gdx.files.internal(pending.fileName);
        // FileTextureData keeps the texture managed, so it is reloaded from file after a GL context loss.
        Texture texture = new Texture(new FileTextureData(file, pending.pixmap, null, false));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pending.pixmap = null;
        discard(pending);
        addAsset(pending.fileName, Texture.class, texture);
        if (pending.references > 1) {
            setReferenceCount(pending.fileName, pending.references);
        }
    }

    @Override
    protected synchronized <T> void addAsset(String fileName, Class<T> type, T asset) {
        if (isLoaded(fileName, type)) {
            T existing = get(fileName, type);
            if (existing != asset) {
                // A regular load of the same file finished too; keep one copy and merge the references.
                if (asset instanceof com.badlogic.gdx.utils.Disposable) {
                    ((com.badlogic.gdx.utils.Disposable) asset).dispose();
                }
                setReferenceCount(fileName, getReferenceCount(fileName) + 1);
                return;
            }
        }
        super.addAsset(fileName, type, asset);
    }

    @Override
    public synchronized boolean update() {
        uploadOneDecoded();
        boolean managerDone = super.update();
        boolean done = managerDone && pendingTextures.isEmpty();
        if (done) {
            batchTextures = 0;
            batchTexturesDone = 0;
            batchManagerAssets = 0;
        }
        return done;
    }

    @Override
    public void finishLoadingAsset(String fileName) {
        System.out.println("Finish loading called for: " + fileName);
        PendingTexture pending;
        synchronized (this) {
            pending = pendingTextures.get(fileName);
            if (pending != null && pending.claimed.compareAndSet(false, true)) {
                // Needed right now: decode here instead of waiting behind the pool queue.
                decode(pending);
                upload(pending);
            }
        }
        super.finishLoadingAsset(fileName);
    }

    @Override
    public synchronized float getProgress() {
        int total = batchTextures + batchManagerAssets;
        if (total == 0) {
            return super.getProgress();
        }
        float managerDone = batchManagerAssets * super.getProgress();
        return Math.min(1f, (batchTexturesDone + managerDone) / total);
    }

    @Override
    public synchronized int getQueuedAssets() {
        return super.getQueuedAssets() + pendingTextures.size();
    }

    @Override
    public synchronized void unload(String fileName) {
        PendingTexture pending = pendingTextures.get(fileName);
        if (pending != null && !isLoaded(fileName)) {
            if (--pending.references <= 0) {
                pendingTextures.remove(fileName);
                batchTexturesDone++;
            }
            return;
        }
        super.unload(fileName);
    }

    private void cancelDecoding() {
        decodingCancelled = true;
        PendingTexture pending;
        while ((pending = decodedTextures.poll()) != null) {
            discard(pending);
        }
    }

    /**
     * Queues everything the menu and first game frames need, so the preloader can pump it with a
     * visible percentage instead of the first frame blocking on it.
     */
    public static void queueStartupAssets() {
        CustomAssetManager manager = get();
        manager.load(SPLASH_TITLE, Texture.class);
        manager.load(SPLASH, Texture.class);
        // Main menu and HUD widgets that would otherwise be loaded synchronously when the menu opens.
        for (String path : new String[]{"skin/ancient-terror/square_normal.png", "skin/ancient-terror/square_pressed.png",
                "icon/fast_forward_up.png", "icon/fast_forward_down.png", "icon/menu.png", "token/health.png",
                "token/sanity.png", "token/clue.png", "token/focus.png", "token/ticket_blank.png", "no_ads.png",
                "flags/flags.png"}) {
            if (!manager.isLoaded(path)) {
                manager.load(path, Texture.class);
            }
        }
        if (!manager.isLoaded("skin/ancient-terror/buttons.atlas")) {
            manager.load("skin/ancient-terror/buttons.atlas", TextureAtlas.class);
        }
        manager.queueTextures1();
        for (InvestigatorId investigatorId : InvestigatorId.values()) {
            manager.load("investigator/" + investigatorId.name() + ".png", Texture.class);
        }
        manager.queueTextures2();
    }

    /** Advances queued loading for at most {@code millis}; returns the current batch progress (0..1). */
    public static float pumpLoading(int millis) {
        return get().update(millis) ? 1f : get().getProgress();
    }

    public static boolean isAssetLoaded(String path) {
        return instance != null && instance.isLoaded(path);
    }

    private boolean textures1Queued;
    private boolean textures2Queued;

    public static void loadTextures1() {
        get().progressSubject = PublishSubject.create();
        get().queueTextures1();
        for (InvestigatorId investigatorId : InvestigatorId.values()) {
            getTextureAsync("investigator/" + investigatorId.name() + ".png").subscribe();
        }
    }

    private synchronized void queueTextures1() {
        if (textures1Queued) {
            return;
        }
        textures1Queued = true;
        // Queue small audio buffers during the loading screen, not while typing.
        for (int i = 1; i <= 6; i++) {
            String path = "sounds/typewriter_key_" + i + ".wav";
            load(path, Sound.class);
            soundPaths.add(path);
        }
        for (String cue : new String[]{"space", "button_stamp", "paper_out"}) {
            String path = "sounds/typewriter_" + cue + ".wav";
            load(path, Sound.class);
            soundPaths.add(path);
        }
        for (String path : new String[]{TRAVEL_DEPART_SOUND, TRAVEL_ARRIVE_SOUND, INVESTIGATOR_HIGHLIGHT_SOUND,
                TOKEN_GAIN_SOUND, TOKEN_LOSS_SOUND, TOKEN_LEAVE_SOUND, TOKEN_LAND_SOUND}) {
            load(path, Sound.class);
            soundPaths.add(path);
        }
        load(TICK, Texture.class);
        load("background/gray.jpg", Texture.class);
        load("background/pure_white.png", Texture.class);
    }

    public static void loadTextures2() {
        get().progressSubject = PublishSubject.create();
        Schedulers.io().createWorker().schedule(() -> get().queueTextures2());
    }

    private synchronized void queueTextures2() {
        if (textures2Queued) {
            return;
        }
        textures2Queued = true;
        initCustomAssetManager();
    }

    public static void setCardAssetLoadedCallback(Consumer<String> cardAssetLoadedCallback) {
        get().cardAssetLoadedCallback = cardAssetLoadedCallback;
    }

    public static void setCardArtifactLoadedCallback(Consumer<String> cardArtifactLoadedCallback) {
        get().cardArtifactLoadedCallback = cardArtifactLoadedCallback;
    }

    public static void setCardSpellLoadedCallback(Consumer<String> cardSpellLoadedCallback) {
        get().cardSpellLoadedCallback = cardSpellLoadedCallback;
    }

    public static void setCardConditionLoadedCallback(Consumer<String> cardConditionLoadedCallback) {
        get().cardConditionLoadedCallback = cardConditionLoadedCallback;
    }


    public static PublishSubject<Float> getProgressSubject() {
        return get().progressSubject;
    }

    public static void loadTexturesTutorial() {
        get().load("monster/Maniac.png", Texture.class);
        get().load("icon/reckoning.png", Texture.class);
//        get().load("tutorial/chalkboard.jpg", Texture.class);
//        get().load("monster/filter.png", Texture.class);
    }

    private void initCustomAssetManager() {
        load("ancient_one/button_azathoth.jpg", Texture.class);
        load("ancient_one/button_cthulhu.jpg", Texture.class);
        load("ancient_one/button_shub_niggurath.jpg", Texture.class);
        load("ancient_one/lock.png", Texture.class);
        load("map/blank_map.png", Texture.class);
        load("omen/green.png", Texture.class);
        load("omen/omen_background.png", Texture.class);
        load(ACTION_BUTTON_ENABLED_NORMAL, Texture.class);
        load(ACTION_BUTTON_ENABLED_PRESSED, Texture.class);
        load(ACTION_BUTTON_ENABLED_CHECKED, Texture.class);
        load(ACTION_BUTTON_DISABLED_NORMAL, Texture.class);
        load(ACTION_BUTTON_DISABLED_CHECKED, Texture.class);
        load("omen/circle.png", Texture.class);
        load("omen/circle_disabled.png", Texture.class);
        load("omen/blue.png", Texture.class);
        load("omen/red.png", Texture.class);
        load("clock/clock.png", Texture.class);
        load("clock/minute_hand.png", Texture.class);
        load("clock/knob.png", Texture.class);
        load("clock/hour_hand.png", Texture.class);
        load("effect/noise_1.png", Texture.class);
        load("effect/noise_2.png", Texture.class);
        load("effect/noise_3.png", Texture.class);
        load("effect/noise_4.png", Texture.class);
//        load("map/map_alternative.png", Texture.class);
        load("hud_button/investigator.png", Texture.class);
        load("hud_button/reserve.png", Texture.class);
        load("hud_button/rumor.png", Texture.class);
        load("hud_button/discard.png", Texture.class);
        load("hud_button/mystery.png", Texture.class);
        load("map/space_background.jpg", Texture.class);
        load("hud_button/ancient_one.png", Texture.class);
        load("investigator/border.png", Texture.class);
        load("gate/gate_border.png", Texture.class);
        for (int i = 201; i <= 264; i++) {
            load("gate/portal_7/portal_" + i + ".png", Texture.class);
        }
        load("token/compass.png", Texture.class);
        load("reserve_label.png", Texture.class);
        // load("ancient_one/AZATHOTH.png", Texture.class);
//        load("ancient_one_label.png", Texture.class);
        load("valuelabel.png", Texture.class);
//        load("monster_sheet.png", Texture.class);
        load("discard_label.png", Texture.class);
        load("mystery_background.png", Texture.class);
        load("map/redpin.png", Texture.class);
        load("phase/action_phase.png", Texture.class);
        load(ACTION_TRAVEL, Texture.class);
        load(ACTION_REST, Texture.class);
        load(ACTION_FOCUS, Texture.class);
        load(ACTION_ACQUIRE_ASSETS, Texture.class);
        load(ACTION_TICKET, Texture.class);
        load(ACTION_TRADE, Texture.class);
        load(ACTION_SKIP, Texture.class);
        load("icon/squareButton_up.png", Texture.class);
        load("icon/squareButton_down.png", Texture.class);

        for (int i = 0; i <= 48; i++) {
            load("investigator/puzzle/" + String.format(Locale.ENGLISH, "%03d", i) + ".png", Texture.class);
        }

    }

    public final static String SELECT_ENCOUNTER_LABEL = "select_encounter_label.png";

    public final static String PASSPORT = "passport.png";

    public final static String MS_BOTTOM_LEFT = "ms_bottom_left.png";
    public final static String MS_BOTTOM_RIGHT = "ms_bottom_right.png";
    public final static String MS_TOP_LEFT = "ms_top_left.png";
    public final static String MS_TOP_RIGHT = "ms_top_right.png";

    public final static String MM_BOTTOM = "mm_bottom.png";
    public final static String MM_LEFT = "mm_left.png";
    public final static String MM_RIGHT = "mm_right.png";

    public final static String LIGHT = "icon/light.png";
    public final static String PLUS_CHECKED = "icon/plus_checked.png";
    public final static String PLUS_NORMAL = "icon/plus_normal.png";

    public final static String BIG_CITY = "encounter/big_city.png";
    public final static String CITY = "encounter/CITY.png";
    public final static String SEA = "encounter/SEA.png";
    public final static String WILDERNESS = "encounter/WILDERNESS.png";
    public final static String REDPIN = "encounter/redpin.png";

    public final static String ENCOUNTER_CITY = "encounter/background/City.jpg";
    public final static String ENCOUNTER_SEA = "encounter/background/Sea.jpg";
    public final static String ENCOUNTER_WILDERNESS = "encounter/background/Wilderness.jpg";

    public final static String ENCOUNTER_COMBAT = "encounter/background/combat.jpg";
    public final static String ENCOUNTER_DETAINED = "encounter/background/detained.jpg";

    public final static String RESEARCH_CITY = "encounter/background/Research_City.jpg";
    public final static String RESEARCH_SEA = "encounter/background/Research_Sea.jpg";
    public final static String RESEARCH_WILDERNESS = "encounter/background/Research_Wilderness.jpg";

    public final static String HORROR = "combat/horror.png";
    public final static String DAMAGE = "combat/damage.png";
    public final static String VERSUS = "combat/versus.png";

    public final static String DICE_SHEET = "dice_sheet.png";

    public final static String SPECIAL_CARD_CLUE = "card/special/clue.jpg";
    public final static String SPECIAL_CARD_SHIP = "card/special/ship.jpg";
    public final static String SPECIAL_CARD_TRAIN = "card/special/train.jpg";

    public final static String BLACK_SQUARE = "black_square.png";

    public final static String HUD_INVESTIGATOR = "hud_button/investigator.png";
    public final static String HUD_ANCIENT_ONE = "hud_button/ancient_one.png";
    public final static String HUD_DISCARD = "hud_button/discard.png";
    public final static String HUD_MYSTERY = "hud_button/mystery.png";
    public final static String HUD_RESERVE = "hud_button/reserve.png";
    public final static String HUD_RUMOR = "hud_button/rumor.png";

    public final static String ACTION_TRAVEL = "action_button/travel.png";
    public final static String ACTION_REST = "action_button/rest.png";
    public final static String ACTION_FOCUS = "action_button/focus.png";
    public final static String ACTION_ACQUIRE_ASSETS = "action_button/acquire_assets.png";
    public final static String ACTION_TICKET = "action_button/ticket.png";
    public final static String ACTION_TRADE = "action_button/trade.png";
    public final static String ACTION_SKIP = "action_button/skip.png";

    public final static String GATE = "gate/gate.png";
    public final static String RESERVE_BACKGROUND = "reserve/background.png";
    public final static String DISCARD_LABEL = "discard_label.png";
    public final static String ANCIENT_ONE_LABEL = "ancient_one_label.png";
    public final static String ANCIENT_ONE_AZATHOTH_LABEL = "ancient_one/label_azathoth.png";
    public final static String ANCIENT_ONE_LABEL_SK = "ancient_one_label_sk.png";
    public final static String VALUE_LABEL = "valuelabel.png";
    public final static String MONSTER_SHEET = "monster_sheet.png";
    public final static String ANCIENT_ONE_AZATHOTH = "ancient_one/AZATHOTH.png";

    public final static String HOURGLASS = "hourglass.png";
    public final static String NOISE_1 = "effect/noise_1.png";
    public final static String NOISE_2 = "effect/noise_2.png";
    public final static String NOISE_3 = "effect/noise_3.png";
    public final static String NOISE_4 = "effect/noise_4.png";
    public final static String FAST_FORWARD_UP = "icon/fast_forward_up.png";
    public final static String FAST_FORWARD_DiSABLED = "icon/fast_forward_disabled.png";
    public final static String FAST_FORWARD_DOWN = "icon/fast_forward_down.png";
    public final static String MYSTERY_BACKGROUND = "mystery_background.png";
    public final static String PHASE_ACTION = "phase/action_phase.png";
    public final static String PHASE_ACTION_SK = "phase/action_phase_sk.png";
    public final static String PHASE_ENCOUNTER = "phase/encounter_phase.png";
    public final static String PHASE_ENCOUNTER_SK = "phase/encounter_phase_sk.png";
    public final static String PHASE_MYTHOS = "phase/mythos_phase.png";
    public final static String PHASE_MYTHOS_SK = "phase/mythos_phase_sk.png";
    public final static String PHASE_NEW_ROUND = "phase/new_round.png";
    public final static String SPLASH = "background/splash.jpg";
    public final static String SPLASH_TITLE = "background/title.png";
    public final static String BLANK_MAP = "map/blank_map.png";
    public final static String ASTEROID = "map/asteroid.png";
    public final static String MAP = "map/new_map.jpg";
    public final static String RED_PIN = "map/redpin.png";
    public final static String STORM = "map/storm.png";
    public final static String SPACE = "map/space_background.jpg";
    public final static String LOST_IN_TIME_AND_SPACE = "encounter/background/lost_in_time_and_space.jpg";
    public final static String OTHER_WORLD_BACKGROUND = "encounter/background/other_world.jpg";
    public final static String RECKONING = "icon/reckoning.png";
    public final static String TICK = "icon/tick.png";
    public final static String TRAVEL_DEPART_SOUND = "sounds/travel_depart.wav";
    public final static String TRAVEL_ARRIVE_SOUND = "sounds/travel_arrive.wav";
    // "maximize_006" from Kenney Interface Sounds (www.kenney.nl), CC0.
    public final static String INVESTIGATOR_HIGHLIGHT_SOUND = "sounds/investigator_highlight.ogg";
    // Shared by Health and Sanity.
    public final static String TOKEN_GAIN_SOUND = "sounds/health_gain.wav";
    public final static String TOKEN_LOSS_SOUND = "sounds/health_loss.wav";
    public final static String TOKEN_LEAVE_SOUND = "sounds/token_leave.wav";
    public final static String TOKEN_LAND_SOUND = "sounds/token_land.wav";
    public final static String LOCATION_BUTTON_UP = "map/button_2.png";
    public final static String LOCATION_BUTTON_DOWN = "map/button_3.png";
    public final static String CLUE_TOKEN = "token/clue.png";
    public final static String CLUE_TOKEN_LEFT = "token/clue_left.png";
    public final static String CLUE_TOKEN_RIGHT = "token/clue_right.png";
    public final static String FOCUS_TOKEN = "token/focus.png";
    public final static String FOCUS_TOKEN_LEFT = "token/focus_left.png";
    public final static String FOCUS_TOKEN_RIGHT = "token/focus_right.png";
    public final static String COMPASS = "token/compass.png";
    public final static String INVESTIGATOR_BORDER = "investigator/border.png";
    public final static String MONSTER_BORDER = "monster/epic/epic_border.png";
    public final static String WHITE_BACKGROUND = "background/white.jpg";
    public final static String GRAY_BACKGROUND = "background/gray.jpg";
    public final static String PURE_WHITE_BACKGROUND = "background/pure_white.png";
    public final static String CLOCK = "clock/clock.png";
    public final static String CLOCK_HOUR_HAND = "clock/hour_hand.png";
    public final static String CLOCK_MINUTE_HAND = "clock/minute_hand.png";
    public final static String CLOCK_KNOB = "clock/knob.png";
    public final static String OMEN_CIRCLE = "omen/circle.png";
    public final static String OMEN_CIRCLE_DISABLED = "omen/circle_disabled.png";
    public final static String OMEN_BLUE = "omen/blue.png";
    public final static String OMEN_RED = "omen/red.png";
    public final static String OMEN_GREEN = "omen/green.png";
    public final static String OMEN_BACKGROUND = "omen/omen_background.png";
    public final static String TICKET_BLANK = "token/ticket_blank.png";
    public final static String TICKET_TRAIN_DOWN = "token/train_ticket_down.png";
    public final static String TICKET_SHIP_DOWN = "token/ship_ticket_down.png";
    public final static String TICKET_TRAIN_LEFT = "token/train_ticket_left.png";
    public final static String TICKET_TRAIN_RIGHT = "token/train_ticket_right.png";
    public final static String TICKET_SHIP_LEFT = "token/ship_ticket_left.png";
    public final static String TICKET_SHIP_RIGHT = "token/ship_ticket_right.png";
    public final static String HEALTH_ICON = "token/health.png";
    public final static String EMPTY_HEALTH_ICON = "token/empty_health.png";
    public final static String HEALTH_ICON_LEFT = "token/health_left.png";
    public final static String HEALTH_ICON_RIGHT = "token/health_right.png";
    public final static String SANITY_ICON = "token/sanity.png";
    public final static String EMPTY_SANITY_ICON = "token/empty_sanity.png";
    public final static String SANITY_ICON_LEFT = "token/sanity_left.png";
    public final static String SANITY_ICON_RIGHT = "token/sanity_right.png";
    public final static String SQUARE_BUTTON_UP = "icon/squareButton_up.png";
    public final static String SQUARE_BUTTON_DOWN = "icon/squareButton_down.png";
    public final static String DOWN_UP = "icon/down_up.png";
    public final static String CARD_TEMPLATE = "card/card_template.png";
    public final static String NEW_CARD_TEMPLATE = "card/new_card_template.png";
    public final static String PICTURE_FILTER = "card/filter.png";
    public final static String CARD_TEMPLATE_MASK_LEFT = "card/card_template_mask_left.png";
    public final static String CARD_TEMPLATE_MASK_RIGHT = "card/card_template_mask_right.png";
    public final static String CARD_RIBBON = "card/ribbon.png";
    public final static String ACQUIRE_ASSETS_REMAINING_BG = "card/circle.png";
    public final static String ASSET_CARD_BACKGROUND = "card/asset_background.jpg";
    public final static String CONDITION_CARD_BACKGROUND = "card/condition_background.jpg";
    public final static String ARTIFACT_CARD_BACKGROUND = "card/artifact_background.jpg";
    public final static String SPELL_CARD_BACKGROUND = "card/spell_background.jpg";
    public final static String TOKEN_CARD_BACKGROUND = "card/token_background.jpg";
    public final static String DISABLED_CARD = "card/disabled.png";
    public final static String ANCIENT_ONE_DIALOG_BACKGROUND = "ancient_one/azathoth-ending-background.png";
    public final static String NEW_FONT_LIBRE_BASKERVILLE = "new_font/Libre Baskerville/hiero.fnt";
    public final static String NEW_FONT_CINZEL = "new_font/Cinzel/hiero.fnt";
    public final static String NEW_FONT_SOURCE_SERIF_4 = "new_font/Source Serif 4/hiero.fnt";
    public final static String NEW_FONT_SPECIAL_ELITE = "new_font/Special_Elite/hiero.fnt";
    private final static String SKIN = "skin/sgx/skin/sgx-ui.json";

    public final static String ACTION_BUTTON_ENABLED_NORMAL = "action_button/normal.png";
    public final static String ACTION_BUTTON_ENABLED_PRESSED = "action_button/pressed.png";
    public final static String ACTION_BUTTON_ENABLED_CHECKED = "action_button/checked.png";

    public final static String ACTION_BUTTON_DISABLED_NORMAL = "action_button/disabled.png";
    public final static String ACTION_BUTTON_DISABLED_PRESSED = "action_button/disabled_pressed.png";
    public final static String ACTION_BUTTON_DISABLED_CHECKED = "action_button/disabled_checked.png";

    public final static String ACTION_BUTTON_CARD_MASK_SHADOW = "action_button/card_mask_shadow.png";
    public final static String ACTION_BUTTON_CARD_MASK = "action_button/card_mask.png";


    private static CustomAssetManager instance;
    private static long soundGeneration;
    private final java.util.Set<String> soundPaths = new java.util.HashSet<>();

    public static long getSoundGeneration() {
        return soundGeneration;
    }

    /** Nonblocking lookup: unavailable audio stays silent rather than stalling a frame. */
    public static Sound getLoadedSound(String path) {
        return instance != null && instance.isLoaded(path, Sound.class)
                ? instance.get(path, Sound.class) : null;
    }

    public static Sound getSound(String path) {
        CustomAssetManager manager = get();
        if (!manager.isLoaded(path, Sound.class)) {
            manager.load(path, Sound.class);
            manager.finishLoadingAsset(path);
        }
        manager.soundPaths.add(path);
        return manager.get(path, Sound.class);
    }

    private static CustomAssetManager get() {
        if (instance == null) {
            instance = new CustomAssetManager();
        }
        return instance;
    }

    public static void nullifyInstance() {
        soundGeneration++;
        if (instance != null) {
            instance.cancelDecoding();
            instance.disposeSizedFonts();
            for (String path : instance.soundPaths) {
                if (instance.isLoaded(path)) instance.unload(path);
            }
            instance.soundPaths.clear();
        }
        instance = null;
    }

    public static void loadAssets() {
        if (get().getProgress() >= 1) {
            getProgressSubject().onCompleted();
            return;
        }
        get().update(20);
        getProgressSubject().onNext(get().getProgress());
        if (get().getProgress() == 1f) {
            getProgressSubject().onCompleted();
        }
    }


    public static float staticGetProgress() {
        return get().getProgress();
    }

    public static Single<Texture> getTextureAsync(String id) {
        String resolvedId = resolveLocalizedTextureId(id);
        if (get().isLoaded(resolvedId)) {
            return Single.just(get().get(resolvedId, Texture.class));
        }
        Single<Texture> textureSingle = Single.create(onSub -> {

            synchronized (get()) {
                if (!get().isLoaded(resolvedId)) {
                    get().load(resolvedId, Texture.class);
                }
            }
            if (Gdx.app == null) {
                get().finishLoadingAsset(resolvedId);
                completeTextureRequest(resolvedId, onSub);
                return;
            }
            requestTextureAsyncOnRenderThread(resolvedId, onSub);
        });
        return textureSingle.subscribeOn(Schedulers.io());
    }

    public static Texture getTexture(String id) {
        String resolvedId = resolveLocalizedTextureId(id);
        if (get().isLoaded(resolvedId)) {
            return processTextureForUse(resolvedId, get().get(resolvedId));
        } else {
            get().load(resolvedId, Texture.class);
            get().finishLoadingAsset(resolvedId);
            Texture texture = getTexture(resolvedId);
            return texture;
        }
    }

    private static String resolveLocalizedTextureId(String id) {
        if (!"sk".equalsIgnoreCase(UiText.getLanguage())) {
            return id;
        }
        String localizedId = null;
        if (ANCIENT_ONE_LABEL.equals(id)) {
            localizedId = ANCIENT_ONE_LABEL_SK;
        } else if (PHASE_ACTION.equals(id)) {
            localizedId = PHASE_ACTION_SK;
        } else if (PHASE_ENCOUNTER.equals(id)) {
            localizedId = PHASE_ENCOUNTER_SK;
        } else if (PHASE_MYTHOS.equals(id)) {
            localizedId = PHASE_MYTHOS_SK;
        }
        if (localizedId != null && Gdx.files != null && Gdx.files.internal(localizedId).exists()) {
            return localizedId;
        }
        return id;
    }

    private static void requestTextureAsyncOnRenderThread(String id, rx.SingleSubscriber<? super Texture> onSub) {
        Gdx.app.postRunnable(() -> {
            if (onSub.isUnsubscribed()) {
                return;
            }
            if (get().isLoaded(id)) {
                completeTextureRequest(id, onSub);
                return;
            }
            get().pumpOncePerFrame();
            requestTextureAsyncOnRenderThread(id, onSub);
        });
    }

    /** Many pending async requests share one small loading slice per frame instead of freezing it. */
    private void pumpOncePerFrame() {
        long frame = Gdx.graphics.getFrameId();
        if (frame != lastPumpedFrame) {
            lastPumpedFrame = frame;
            update(ASYNC_PUMP_MS);
        }
    }

    private static void completeTextureRequest(String id, rx.SingleSubscriber<? super Texture> onSub) {
        if (onSub.isUnsubscribed()) {
            return;
        }
        Texture texture = processTextureForUse(id, get().get(id));
        onSub.onSuccess(texture);
    }

    private static Texture processTextureForUse(String id, Texture texture) {
        if (texture.getMinFilter() != Texture.TextureFilter.Linear && texture.getMagFilter() != Texture.TextureFilter.Linear) {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        Consumer<String> callback = null;
        if (id.startsWith("card/asset/")) {
            callback = get().cardAssetLoadedCallback;
        } else if (id.startsWith("card/artifact/")) {
            callback = get().cardArtifactLoadedCallback;
        } else if (id.startsWith("card/spell/")) {
            callback = get().cardSpellLoadedCallback;
        } else if (id.startsWith("card/condition/")) {
            callback = get().cardConditionLoadedCallback;
        }
        if (callback != null) {
            callback.accept(id);
        }
        return texture;
    }

    public static TextureRegion getTextureRegion(String id) {
        return new TextureRegion(getTexture(id));
    }

    public static TextureRegionDrawable getTextureRegionDrawable(String id) {
        return new TextureRegionDrawable(getTextureRegion(id));
    }

    public static Skin getSkin() {
        if (!get().isLoaded(SKIN)) {
            ObjectMap<String, Object> fonts = new ObjectMap<>();
            fonts.put("font", getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 24));
            fonts.put("small", getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 18));
            fonts.put("medium", getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 22));
            fonts.put("title", getBitmapFontNew(NEW_FONT_SOURCE_SERIF_4, 40));
            get().load(SKIN, Skin.class, new SkinLoader.SkinParameter(fonts));
            get().finishLoadingAsset(SKIN);
        }
        return get().get(SKIN, Skin.class);
    }

    public static BitmapFont getBitmapFontNew(String fontId) {
        BitmapFont font = commonLoad(fontId, BitmapFont.class);
        prepareFont(fontId, font);
        return font;
    }

    /** Uses independent metrics so compact controls do not resize shared card fonts. */
    public static BitmapFont getBitmapFontNew(String fontId, int size) {
        return getBitmapFontNew(fontId, size, 1f);
    }

    /** Keeps compact caption leading independent from other users of the same font size. */
    public static BitmapFont getBitmapFontNew(String fontId, int size, float lineHeightScale) {
        String key = fontId + ":" + size + ":" + lineHeightScale;
        BitmapFont font = get().sizedFonts.get(key);
        if (font == null) {
            BitmapFont source = getBitmapFontNew(fontId);
            BitmapFont.BitmapFontData data = new BitmapFont.BitmapFontData(Gdx.files.internal(fontId), false);
            Array<TextureRegion> pages = new Array<>();
            for (int i = 0; i < data.imagePaths.length; i++) {
                pages.add(source.getRegions().get(i));
            }
            font = new BitmapFont(data, pages, false);
            font.setOwnsTexture(false);
            prepareFont(fontId, font);
            // All bundled new fonts are exported at 64 px.
            BitmapFontSizing.resize(font, size);
            font.getData().lineHeight *= lineHeightScale;
            get().sizedFonts.put(key, font);
        }
        return font;
    }

    private static void prepareFont(String fontId, BitmapFont font) {
        if (Objects.equals(fontId, NEW_FONT_SOURCE_SERIF_4) || Objects.equals(fontId, NEW_FONT_SPECIAL_ELITE)) {
            FontGlyphEnricher.enrich(font);
        }
        if (Objects.equals(fontId, NEW_FONT_SPECIAL_ELITE)) {
            font.getData().markupEnabled = true;
        }
        for (TextureRegion page : font.getRegions()) {
            page.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }

    private void disposeSizedFonts() {
        for (BitmapFont font : sizedFonts.values()) {
            font.dispose();
        }
        sizedFonts.clear();
    }

    public static NinePatch createMenuDialogPatch() {
        TextureAtlas atlas = commonLoad("skin/ancient-terror/dialog.atlas", TextureAtlas.class);
        return atlas.createPatch("ancient-terror-dialog");
    }

    public static NinePatch createMenuButtonPatch(boolean pressed) {
        TextureAtlas atlas = commonLoad("skin/ancient-terror/buttons.atlas", TextureAtlas.class);
        return atlas.createPatch(pressed ? "button-pressed" : "button-normal");
    }

    private static <T> T commonLoad(String id, Class<T> clazz) {
        if (get().isLoaded(id)) {
            return get().get(id);
        } else {
            get().load(id, clazz);
            get().finishLoadingAsset(id);
            return commonLoad(id, clazz);
        }
    }


    public static Texture getNonEpicMonsterTexture(String monsterId) {
        String fileName = monsterId.substring(0, monsterId.length() - "Monster".length());
        return getTexture("monster/" + fileName + ".png");
    }

    public static Texture getEpicMonsterTexture(String monsterId) {
        String fileName = monsterId.substring(0, monsterId.length() - "Monster".length());
        return getTexture("monster/epic/" + fileName + ".png");
    }

    public static Texture getInvestigatorTexture(InvestigatorId investigatorId) {
        return getTexture("investigator/" + investigatorId.name() + ".png");
    }

    public static TextureRegionDrawable getInvestigatorDrawable(InvestigatorId investigatorId) {
        return getTextureRegionDrawable("investigator/" + investigatorId.name() + ".png");
    }

    public static Array<TextureRegion> getGateAnimation() {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 264; i >= 201; i--) {
            Texture texture = getTexture("gate/portal_7/portal_" + i + ".png");
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            frames.add(new TextureRegion(texture));
        }
        return frames;
    }

    public static Array<TextureRegion> getNoiseAnimation() {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 4; i >= 1; i--) {
            Texture texture = getTexture("effect/noise_" + i + ".png");
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            frames.add(new TextureRegion(texture));
        }
        return frames;
    }

    public static Array<TextureRegion> getStormAnimation() {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 24; i >= 0; i--) {
            Texture texture = getTexture("map/storm/" + String.format(Locale.ENGLISH, "%03d", i) + ".png");
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            frames.add(new TextureRegion(texture));
        }
        return frames;
    }

}
