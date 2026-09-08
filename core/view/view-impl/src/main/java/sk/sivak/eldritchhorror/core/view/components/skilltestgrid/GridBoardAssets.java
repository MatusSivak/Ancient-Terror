package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public interface GridBoardAssets {
    TextureRegion getBoardBackground();
    TextureRegion getOverlayRegion();
    TextureRegion getWhitePixel();
    TextureRegion getSymbolRegion(SymbolType type);
    Array<TextureRegion> getImplosionFrames(SymbolType type);
    Array<TextureRegion> getExplosionOverlayFrames();
    Array<TextureRegion> getImplosionOverlayFrames();
    Array<TextureRegion> getSpawnFrames();
    Array<TextureRegion> getHighlightFrames();
}
