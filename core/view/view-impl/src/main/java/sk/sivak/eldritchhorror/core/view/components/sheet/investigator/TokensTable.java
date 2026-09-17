package sk.sivak.eldritchhorror.core.view.components.sheet.investigator;

import com.kotcrab.vis.ui.widget.VisTable;
import static sk.sivak.eldritchhorror.core.view.components.sheet.investigator.CharacterSheetWidgets.*;

public class TokensTable extends VisTable {
    public void init(TokensTableData data) {
        clearChildren();
        add(createInventory(data)).growX().minWidth(0).minHeight(TOKEN_ICON_SIZE);
    }
}
