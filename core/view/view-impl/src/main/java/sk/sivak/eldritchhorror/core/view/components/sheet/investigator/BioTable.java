package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.widget.VisTable;
import sk.sivak.eldritchhorror.core.view.assetmanager.CustomAssetManager;
import java.util.Locale;
import static sk.sivak.eldritchhorror.core.view.components.sheet.investigator.CharacterSheetWidgets.*;
import static sk.sivak.eldritchhorror.core.view.utils.UiText.get;

public class BioTable extends VisTable {
    private final Image photo = new Image();
    private final Label nameValue = text("", 20, INK);
    private final Label professionValue = text("", 16, SECONDARY_INK);

    public BioTable() {
        photo.setScaling(Scaling.fit);
        nameValue.setWrap(true);
        professionValue.setWrap(true);
        add(photo).width(150).height(146).padBottom(8).row();
        add(nameValue).growX().minWidth(0).padBottom(3).row();
        add(professionValue).growX().minWidth(0);
    }

    public void init(BioTableData data) {
        nameValue.setText(data.getName());
        String key = "investigator.profession." + data.getInvestigatorId().name().toLowerCase(Locale.ENGLISH);
        String localized = get(key);
        professionValue.setText(("!" + key + "!").equals(localized) ? data.getInvestigatorId().toString() : localized);
        photo.setDrawable(CustomAssetManager.getInvestigatorDrawable(data.getInvestigatorId()));
    }
}
