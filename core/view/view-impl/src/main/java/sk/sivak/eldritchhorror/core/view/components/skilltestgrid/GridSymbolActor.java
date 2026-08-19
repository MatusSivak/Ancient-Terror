package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GridSymbolActor extends Image {
    private SymbolType symbolType;

    public GridSymbolActor(GridTestAssets assets, SymbolType symbolType) {
        super(new TextureRegionDrawable(assets.getSymbolRegion(symbolType)));
        this.symbolType = symbolType;
    }

    public void setSymbolType(GridTestAssets assets, SymbolType symbolType) {
        this.symbolType = symbolType;
        setDrawable(new TextureRegionDrawable(assets.getSymbolRegion(symbolType)));
    }

    public SymbolType getSymbolType() {
        return symbolType;
    }
}
