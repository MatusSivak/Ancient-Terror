package sk.sivak.eldritchhorror.core.view.components.skilltestgrid;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GridSymbolActor extends Image {
    public GridSymbolActor(GridTestAssets assets, SymbolType symbolType) {
        super(new TextureRegionDrawable(assets.getSymbolRegion(symbolType)));
    }

    public void setSymbolType(GridTestAssets assets, SymbolType symbolType) {
        setDrawable(new TextureRegionDrawable(assets.getSymbolRegion(symbolType)));
    }
}
