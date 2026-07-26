package sk.sivak.eldritchhorror.core.action.impl.doomomen;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.action.DirectEventAction;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.eventtype.DirectEvent;

public class OmenChangedEventAction extends AbstractDirectEventAction<OmenInfo> {

    public OmenChangedEventAction() {
        super(DirectEvent.OMEN_CHANGED);
    }
}
