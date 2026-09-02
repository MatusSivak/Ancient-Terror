package sk.sivak.eldritchhorror.core.view.assetmanager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import java8.features.function.Consumer;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.view.font.FontGlyphEnricher;
import sk.sivak.eldritchhorror.core.view.utils.UiText;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author msivak
 */
public class CustomAssetManager extends AssetManager {

    private PublishSubject<Float> progressSubject;

    private Consumer<String> cardAssetLoadedCallback;
    private Consumer<String> cardConditionLoadedCallback;
    private Consumer<String> cardSpellLoadedCallback;
    private Consumer<String> cardArtifactLoadedCallback;
    private final Map<String, BitmapFont> runtimeFonts = new ConcurrentHashMap<>();
    private boolean runtimeSkinFontsPatched;
    private static final String REQUIRED_SK_GLYPHS = "ÁÄČĎÉÍĹĽŇÓÔŔŠŤÚÝŽáäčďéíĺľňóôŕšťúýž";
    private static final String RUNTIME_FONT_CHARS = buildRuntimeFontChars();

    public CustomAssetManager() {

    }

    public static void loadTextures1() {
        get().progressSubject = PublishSubject.create();
        get().load(TICK, Texture.class);
        get().load("skills.png", Texture.class);
        get().load("skills_labels.png", Texture.class);
        get().load("background/gray.jpg", Texture.class);
        get().load("background/pure_white.png", Texture.class);
        if (!isRuntimeFontGenerationEnabled()) {
            get().load("font/BlackChancery/BlackChancery.fnt", BitmapFont.class);
            get().load("font/minya/minya.fnt", BitmapFont.class);
        }

        for (InvestigatorId investigatorId : InvestigatorId.values()) {
            getTextureAsync("investigator/" + investigatorId.name() + ".png").subscribe();
            getTextureAsync("investigator/ICON_" + investigatorId.name() + ".png").subscribe();
        }
    }

    public static void loadTextures2() {
        get().progressSubject = PublishSubject.create();
        Schedulers.io().createWorker().schedule(() -> get().initCustomAssetManager());
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
//        get().load("tutorial/chalkboard.png", Texture.class);
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
        if (!isRuntimeFontGenerationEnabled()) {
            load("font/Adler/Adler.fnt", BitmapFont.class);
        }
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
    public final static String RESERVE_LABEL = "reserve_label.png";
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
    public final static String SKILLS = "skills.png";
    public final static String SKILLS_LABELS = "skills_labels.png";
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
    public final static String MAP = "map/new_map.png";
    public final static String RED_PIN = "map/redpin.png";
    public final static String STORM = "map/storm.png";
    public final static String SPACE = "map/space_background.jpg";
    public final static String LOST_IN_TIME_AND_SPACE = "encounter/background/lost_in_time_and_space.jpg";
    public final static String OTHER_WORLD_BACKGROUND = "encounter/background/other_world.jpg";
    public final static String RECKONING = "icon/reckoning.png";
    public final static String TICK = "icon/tick.png";
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
    public final static String CARD_CIRCLE = "card/circle.png";
    public final static String ASSET_CARD_BACKGROUND = "card/asset_background.jpg";
    public final static String CONDITION_CARD_BACKGROUND = "card/condition_background.jpg";
    public final static String ARTIFACT_CARD_BACKGROUND = "card/artifact_background.jpg";
    public final static String SPELL_CARD_BACKGROUND = "card/spell_background.jpg";
    public final static String TOKEN_CARD_BACKGROUND = "card/token_background.jpg";
    public final static String DISABLED_CARD = "card/disabled.png";
    public final static String NEW_FONT_LIBRE_BASKERVILLE = "new_font/Libre Baskerville/hiero.fnt";
    public final static String NEW_FONT_CINZEL = "new_font/Cinzel/hiero.fnt";
    public final static String NEW_FONT_SOURCE_SERIF_4 = "new_font/Source Serif 4/hiero.fnt";
    public final static String FONT_ADLER = "font/Adler/Adler.fnt";
    public final static String FONT_TYPEWRITER = "font/typewriter/typewriter.fnt";
    public final static String FONT_GOBLIN_ONE = "font/goblinOne/goblinOne.fnt"; // card cost, monster card, remaining cost, stat value, token value
    public final static String FONT_BLACK_CHANCERY = "font/BlackChancery/BlackChancery.fnt"; // ancient one alt name, card type, card traits, it's epic?, mystery flavor, quote
    public final static String FONT_MINYA = "font/minya/minya.fnt";
    private static final String WINDOWS_FONT_COURIER = "internal:font/runtime/windows/cour.ttf";
    private static final String WINDOWS_FONT_TIMES = "internal:font/runtime/windows/times.ttf";
    private static final String WINDOWS_FONT_GEORGIA_ITALIC = "internal:font/runtime/windows/georgiai.ttf";
    private static final String[] STRICT_FONT_PATHS_ADLER = new String[]{WINDOWS_FONT_COURIER};
    private static final String[] STRICT_FONT_PATHS_MINYA = new String[]{WINDOWS_FONT_TIMES};
    private static final String[] STRICT_FONT_PATHS_BLACK_CHANCERY = new String[]{WINDOWS_FONT_GEORGIA_ITALIC};
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

    private static CustomAssetManager get() {
        if (instance == null) {
            instance = new CustomAssetManager();
        }
        return instance;
    }

    public static void nullifyInstance() {
        if (instance != null) {
            instance.disposeRuntimeFonts();
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
            return Single.just(get().get(id));
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
            get().update(5000);
            requestTextureAsyncOnRenderThread(id, onSub);
        });
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

    @Override
    public void finishLoadingAsset(String fileName) {
        System.out.println("Finish loading called for: " + fileName);
        super.finishLoadingAsset(fileName);
    }

    public static TextureRegion getTextureRegion(String id) {
        return new TextureRegion(getTexture(id));
    }

    public static TextureRegionDrawable getTextureRegionDrawable(String id) {
        return new TextureRegionDrawable(getTextureRegion(id));
    }

    public static Skin getSkin() {
        Skin skin = commonLoad(SKIN, Skin.class);
        if (isRuntimeFontGenerationEnabled()) {
            get().applyRuntimeSkinFonts(skin);
        }
        return skin;
    }

    public static BitmapFont getBitmapFontNew(String fontId) {
        BitmapFont bitmapFont = commonLoad(fontId, BitmapFont.class);
        if (Objects.equals(fontId, NEW_FONT_SOURCE_SERIF_4)) {
            FontGlyphEnricher.enrich(bitmapFont);
        }
        if (bitmapFont.getRegion().getTexture().getMinFilter() == Texture.TextureFilter.Nearest) {
            bitmapFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return bitmapFont;
    }
    public static BitmapFont getBitmapFont(String fontId) {
        if (isRuntimeFontGenerationEnabled() && isRuntimeEligibleFont(fontId)) {
            synchronized (get()) {
                BitmapFont runtimeFont = get().runtimeFonts.get(fontId);
                if (runtimeFont != null && get().fontContainsRequiredGlyphs(runtimeFont)) {
                    return runtimeFont;
                }
                if (runtimeFont != null) {
                    runtimeFont.dispose();
                    get().runtimeFonts.remove(fontId);
                }
                BitmapFont generatedFont = get().tryGenerateRuntimeFont(fontId);
                if (generatedFont != null && get().fontContainsRequiredGlyphs(generatedFont)) {
                    get().runtimeFonts.put(fontId, generatedFont);
                    return generatedFont;
                }
                if (generatedFont != null) {
                    generatedFont.dispose();
                }
                throw new GdxRuntimeException("Strict runtime font generation failed for fontId: " + fontId);
            }
        }
        BitmapFont bitmapFont = commonLoad(fontId, BitmapFont.class);
        if (bitmapFont.getRegion().getTexture().getMinFilter() == Texture.TextureFilter.Nearest) {
            bitmapFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        return bitmapFont;
    }

    private boolean fontContainsRequiredGlyphs(BitmapFont font) {
        if (font == null || font.getData() == null) {
            return false;
        }
        for (int i = 0; i < REQUIRED_SK_GLYPHS.length(); i++) {
            if (!font.getData().hasGlyph(REQUIRED_SK_GLYPHS.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isRuntimeEligibleFont(String fontId) {
        return FONT_ADLER.equals(fontId)
                || FONT_MINYA.equals(fontId)
                || FONT_BLACK_CHANCERY.equals(fontId);
    }

    private static boolean isRuntimeFontGenerationEnabled() {
        return true;
    }

    private BitmapFont tryGenerateRuntimeFont(String fontId) {
        String[] preferredPaths = resolveFontPaths(fontId);
        for (String path : preferredPaths) {
            BitmapFont generated = tryGenerateRuntimeFontFromCandidate(path, resolveFontSize(fontId));
            if (generated != null) {
                return generated;
            }
        }
        return null;
    }

    private BitmapFont tryGenerateRuntimeFontFromCandidate(String path, int size) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        try {
            FileHandle fontFile = resolveFontFile(path);
            if (fontFile == null || !fontFile.exists()) {
                return null;
            }
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
            try {
                FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
                parameter.size = size;
                parameter.characters = RUNTIME_FONT_CHARS;
                parameter.incremental = false;
                parameter.minFilter = Texture.TextureFilter.Linear;
                parameter.magFilter = Texture.TextureFilter.Linear;
                BitmapFont font = generator.generateFont(parameter);
                if (font.getRegion() != null && font.getRegion().getTexture() != null) {
                    font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                }
                return font;
            } finally {
                generator.dispose();
            }
        } catch (Exception ignored) {
            // fallback to next candidate
        }
        return null;
    }

    private void applyRuntimeSkinFonts(Skin skin) {
        if (runtimeSkinFontsPatched || skin == null) {
            return;
        }
        Map<BitmapFont, BitmapFont> fontReplacements = new IdentityHashMap<>();
        boolean replacedMainFont = replaceSkinFont(skin, "font", 24, false, fontReplacements);
        boolean replacedSmallFont = replaceSkinFont(skin, "small", 18, false, fontReplacements);
        boolean replacedMediumFont = replaceSkinFont(skin, "medium", 22, false, fontReplacements);
        boolean replacedTitleFont = replaceSkinFont(skin, "title", 40, true, fontReplacements);
        if (!replacedMainFont || !replacedSmallFont || !replacedMediumFont || !replacedTitleFont) {
            throw new GdxRuntimeException("Strict runtime skin font replacement failed.");
        }
        remapSkinResourceFonts(skin, fontReplacements);
        runtimeSkinFontsPatched = true;
    }

    private boolean replaceSkinFont(Skin skin, String skinFontName, int size, boolean serif, Map<BitmapFont, BitmapFont> fontReplacements) {
        BitmapFont original = skin.get(skinFontName, BitmapFont.class);
        BitmapFont generated = tryGenerateRuntimeFontForSkin(size, serif);
        if (generated == null || !fontContainsRequiredGlyphs(generated)) {
            if (generated != null) {
                generated.dispose();
            }
            return false;
        }
        fontReplacements.put(original, generated);
        skin.remove(skinFontName, BitmapFont.class);
        skin.add(skinFontName, generated, BitmapFont.class);
        runtimeFonts.put("skin:" + skinFontName, generated);
        return true;
    }

    private void remapSkinResourceFonts(Skin skin, Map<BitmapFont, BitmapFont> fontReplacements) {
        if (fontReplacements.isEmpty()) {
            return;
        }
        try {
            Field resourcesField = Skin.class.getDeclaredField("resources");
            resourcesField.setAccessible(true);
            ObjectMap<Class, ObjectMap<String, Object>> resources = (ObjectMap<Class, ObjectMap<String, Object>>) resourcesField.get(skin);
            for (ObjectMap<String, Object> resourceMap : resources.values()) {
                for (Object resource : resourceMap.values()) {
                    replaceBitmapFontFields(resource, fontReplacements);
                }
            }
        } catch (Exception ignored) {
            // If reflection fails, Skin will still use replaced named fonts where resolved lazily.
        }
    }

    private void replaceBitmapFontFields(Object resource, Map<BitmapFont, BitmapFont> fontReplacements) {
        if (resource == null) {
            return;
        }
        Class<?> type = resource.getClass();
        while (type != null) {
            for (Field field : type.getDeclaredFields()) {
                if (!BitmapFont.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    BitmapFont current = (BitmapFont) field.get(resource);
                    BitmapFont replacement = fontReplacements.get(current);
                    if (replacement != null && replacement != current) {
                        field.set(resource, replacement);
                    }
                } catch (Exception ignored) {
                    // keep existing font on inaccessible style fields
                }
            }
            type = type.getSuperclass();
        }
    }

    private BitmapFont tryGenerateRuntimeFontForSkin(int size, boolean serif) {
        String[] candidates = serif ? resolveFontPaths(FONT_BLACK_CHANCERY) : resolveFontPaths(FONT_ADLER);
        for (String candidate : candidates) {
            BitmapFont generated = tryGenerateRuntimeFontFromCandidate(candidate, size);
            if (generated != null) {
                return generated;
            }
        }
        return null;
    }

    private FileHandle resolveFontFile(String candidatePath) {
        String path = candidatePath.trim();
        if (path.startsWith("internal:")) {
            String internalPath = path.substring("internal:".length());
            FileHandle internal = Gdx.files.internal(internalPath);
            return internal.exists() ? internal : null;
        }
        if (path.startsWith("absolute:")) {
            String absolutePath = path.substring("absolute:".length());
            FileHandle absolute = Gdx.files.absolute(absolutePath);
            return absolute.exists() ? absolute : null;
        }
        FileHandle internal = Gdx.files.internal(path);
        if (internal.exists()) {
            return internal;
        }
        FileHandle absolute = Gdx.files.absolute(path);
        if (absolute.exists()) {
            return absolute;
        }
        return null;
    }

    private static String buildRuntimeFontChars() {
        StringBuilder chars = new StringBuilder(FreeTypeFontGenerator.DEFAULT_CHARS)
                .append(REQUIRED_SK_GLYPHS)
                .append("ěĚřŘůŮ");
        return chars.toString();
    }

    private static int resolveFontSize(String fontId) {
        if (FONT_BLACK_CHANCERY.equals(fontId)) {
            return 44;
        }
        if (FONT_MINYA.equals(fontId)) {
            return 40;
        }
        return 42;
    }

    private static String[] resolveFontPaths(String fontId) {
        if (FONT_ADLER.equals(fontId)) {
            return STRICT_FONT_PATHS_ADLER;
        }
        if (FONT_BLACK_CHANCERY.equals(fontId)) {
            return STRICT_FONT_PATHS_BLACK_CHANCERY;
        }
        if (FONT_MINYA.equals(fontId)) {
            return STRICT_FONT_PATHS_MINYA;
        }
        return STRICT_FONT_PATHS_MINYA;
    }

    private void disposeRuntimeFonts() {
        for (BitmapFont bitmapFont : runtimeFonts.values()) {
            if (bitmapFont != null) {
                bitmapFont.dispose();
            }
        }
        runtimeFonts.clear();
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
