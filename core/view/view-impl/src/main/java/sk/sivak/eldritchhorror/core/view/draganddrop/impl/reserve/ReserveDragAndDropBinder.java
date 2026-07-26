package sk.sivak.eldritchhorror.core.view.draganddrop.impl.reserve;

import com.badlogic.gdx.scenes.scene2d.Stage;
import sk.sivak.eldritchhorror.core.constants.asset.AssetInfo;
import sk.sivak.eldritchhorror.core.view.components.card.CardTemplate;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.DragAndDropBinder;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.SourceTargetGroup;
import sk.sivak.eldritchhorror.core.view.draganddrop.impl.TargetActorChangedListener;

import java.util.List;

public class ReserveDragAndDropBinder extends DragAndDropBinder {

    private int valueLimit;

    public ReserveDragAndDropBinder(int valueLimit, Stage stage) {
        super(stage, new ReserveTargetActorChangedListener());
        this.valueLimit = valueLimit;
    }

    public ReserveTargetActorChangedListener getReserveTargetActorChangedListener() {
        return (ReserveTargetActorChangedListener) listener;
    }

    @Override
    public void init(CardTemplate... cardTemplates) {
        super.init(cardTemplates);
        ((ReserveTargetActorChangedListener) listener).setSourceTargetGroup(((ReserveSourceTargetGroup) sourceTargetGroup));
        ((ReserveTargetActorChangedListener) listener).setValueLimit(valueLimit);
        ((ReserveSourceTargetGroup) sourceTargetGroup).initValueLimit(valueLimit);
        ((ReserveTargetActorChangedListener) listener).setDragAndDrop(customDragAndDrop);
        ((ReserveTargetActorChangedListener) listener).init();
    }

    @Override
    protected SourceTargetGroup createSourceTargetGroup() {
        return new ReserveSourceTargetGroup();
    }

    public List<AssetInfo> getSelectedAssets() {
        return ((ReserveTargetActorChangedListener) listener).getSelectedAssets();
    }

    public void takeBankLoan() {
        this.valueLimit += 2;
        ((ReserveTargetActorChangedListener) listener).setValueLimit(valueLimit);
        ((ReserveTargetActorChangedListener) listener).init();
    }

}
