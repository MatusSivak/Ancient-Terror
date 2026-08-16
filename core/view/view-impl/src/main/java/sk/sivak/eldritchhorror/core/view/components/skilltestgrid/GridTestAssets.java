package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;

import java.util.EnumMap;
import java.util.Map;

public class GridTestAssets {
    private static final String SLOT_SYMBOL_ONE = "slot/symbol/1.png";
    private static final String SLOT_SYMBOL_TWO = "slot/symbol/2.png";
    private static final String SLOT_SYMBOL_THREE = "slot/symbol/3.png";
    private static final String SLOT_SYMBOL_FIVE = "slot/symbol/5.png";
    private static final String SLOT_SYMBOL_SIX = "slot/symbol/6.png";
    private static final String SLOT_GRID_BOTTOM = "slot/decorative/bottom.png";
    private static final String SLOT_GRID_OVERLAY = "slot/decorative/overlay.png";
    private static final String SLOT_EXPLOSION_OVERLAY = "slot/animation/explosion.png";
    private static final String SLOT_IMPLOSION_OVERLAY = "slot/animation/implosion.png";
    private static final String SLOT_SPAWN_ANIMATION = "slot/animation/spawn.png";
    private static final int IMPLOSION_COLS = 4;
    private static final int IMPLOSION_ROWS = 4;
    private static final int DICE_SHEET_CELL = 46;

    private final TextureRegion boardBackground;
    private final TextureRegion overlayRegion;
    private final Map<SymbolType, TextureRegion> symbolRegions;
    private final Map<SymbolType, Array<TextureRegion>> implosionFramesPerSymbol;
    private final Array<TextureRegion> explosionOverlayFrames;
    private final Array<TextureRegion> implosionOverlayFrames;
    private final Array<TextureRegion> spawnFrames;

    public GridTestAssets() {
        boardBackground = CustomAssetManager.getTextureRegion(SLOT_GRID_BOTTOM);
        overlayRegion = exists(SLOT_GRID_OVERLAY) ? CustomAssetManager.getTextureRegion(SLOT_GRID_OVERLAY) : null;

        symbolRegions = new EnumMap<>(SymbolType.class);
        symbolRegions.put(SymbolType.ONE, resolveSymbolRegion(SLOT_SYMBOL_ONE, 0, 4));
        symbolRegions.put(SymbolType.TWO, resolveSymbolRegion(SLOT_SYMBOL_TWO, 4, 4));
        symbolRegions.put(SymbolType.THREE, resolveSymbolRegion(SLOT_SYMBOL_THREE, 0, 8));
        symbolRegions.put(SymbolType.FIVE, resolveSymbolRegion(SLOT_SYMBOL_FIVE, 12, 4));
        symbolRegions.put(SymbolType.SIX, resolveSymbolRegion(SLOT_SYMBOL_SIX, 8, 4));

        implosionFramesPerSymbol = new EnumMap<>(SymbolType.class);
        implosionFramesPerSymbol.put(SymbolType.ONE,   loadImplosionSheet("slot/animation/1.png"));
        implosionFramesPerSymbol.put(SymbolType.TWO,   loadImplosionSheet("slot/animation/2.png"));
        implosionFramesPerSymbol.put(SymbolType.THREE, loadImplosionSheet("slot/animation/3.png"));
        implosionFramesPerSymbol.put(SymbolType.FIVE,  loadImplosionSheet("slot/animation/5.png"));
        implosionFramesPerSymbol.put(SymbolType.SIX,   loadImplosionSheet("slot/animation/6.png"));

        explosionOverlayFrames = loadImplosionSheet(SLOT_EXPLOSION_OVERLAY);
        implosionOverlayFrames = loadImplosionSheet(SLOT_IMPLOSION_OVERLAY);
        spawnFrames = loadImplosionSheet(SLOT_SPAWN_ANIMATION);
    }

    public TextureRegion getBoardBackground() {
        return boardBackground;
    }

    public TextureRegion getOverlayRegion() {
        return overlayRegion;
    }

    public TextureRegion getSymbolRegion(SymbolType type) {
        TextureRegion region = symbolRegions.get(type);
        if (region == null) {
            throw new IllegalArgumentException("Missing symbol region for type " + type);
        }
        return region;
    }

    /** Shared 16-frame array for the given symbol; do not modify or dispose. */
    public Array<TextureRegion> getImplosionFrames(SymbolType type) {
        Array<TextureRegion> frames = implosionFramesPerSymbol.get(type);
        return frames != null ? frames : new Array<>(0);
    }

    /** Shared 16-frame explosion overlay array; do not modify or dispose. */
    public Array<TextureRegion> getExplosionOverlayFrames() {
        return explosionOverlayFrames;
    }

    /** Shared 16-frame implosion overlay array; do not modify or dispose. */
    public Array<TextureRegion> getImplosionOverlayFrames() {
        return implosionOverlayFrames;
    }

    /** Shared 16-frame spawn array; do not modify or dispose. */
    public Array<TextureRegion> getSpawnFrames() {
        return spawnFrames;
    }

    private Array<TextureRegion> loadImplosionSheet(String path) {
        if (!exists(path)) {
            return new Array<>(0);
        }
        Texture sheet = CustomAssetManager.getTexture(path);
        int frameWidth = sheet.getWidth() / IMPLOSION_COLS;
        int frameHeight = sheet.getHeight() / IMPLOSION_ROWS;
        Array<TextureRegion> frames = new Array<>(IMPLOSION_COLS * IMPLOSION_ROWS);
        for (int row = 0; row < IMPLOSION_ROWS; row++) {
            for (int col = 0; col < IMPLOSION_COLS; col++) {
                frames.add(new TextureRegion(sheet, col * frameWidth, row * frameHeight, frameWidth, frameHeight));
            }
        }
        return frames;
    }

    private TextureRegion resolveSymbolRegion(String preferredPath, int diceGridX, int diceGridY) {
        if (exists(preferredPath)) {
            return CustomAssetManager.getTextureRegion(preferredPath);
        }
        Texture diceSheet = CustomAssetManager.getTexture(CustomAssetManager.DICE_SHEET);
        return new TextureRegion(diceSheet, diceGridX * DICE_SHEET_CELL, diceGridY * DICE_SHEET_CELL, DICE_SHEET_CELL, DICE_SHEET_CELL);
    }

    private boolean exists(String path) {
        return Gdx.files != null && Gdx.files.internal(path).exists();
    }
}
