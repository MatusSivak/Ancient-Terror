package sk.sivak.eldritchhorror.core.action.impl.card;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.AbstractDirectEventAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.data.spell.ResolvedSpellData;

import static sk.sivak.eldritchhorror.core.eventtype.DirectEvent.SPELL_RESOLVED;

public class FireSpellResolvedEventAction extends AbstractDirectEventAction<ResolvedSpellData> {

    private final ResolvedSpellData resolvedSpellData;

    public FireSpellResolvedEventAction(ResolvedSpellData resolvedSpellData) {
        super(null);
        this.resolvedSpellData = resolvedSpellData;
    }

    @Override
    public Single<ResolvedSpellData> execute() {
        return Single.<ResolvedSpellData>create(onSub -> {
            ServicePlatform.get().getEventQueue().fireDirectEvent(SPELL_RESOLVED, resolvedSpellData);
            onSub.onSuccess(resolvedSpellData);
        }).cache();
    }
}
