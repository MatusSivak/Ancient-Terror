package sk.sivak.eldritchhorror.core.action.impl.action.general;

import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.action.RestData;
import sk.sivak.eldritchhorror.core.service.TokenService;

/**
 * @author msivak
 */
public class RestActionAction extends AbstractHookableAction<RestData, RestData> {

    public RestActionAction() {
        super(BeforeAfterEvent.REST);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super RestData> ss) {

        if (!input.isHealthEnabled() && !input.isSanityEnabled()) {
            ss.onSuccess(input);
            return;
        }

        TokenService service = ServicePlatform.get().getTokenService();
        service.hold();
        if (input.getHealthGained() != 0) {
            service.gainHealth(input.getHealthGained());
            service.convertFromTo(Object.class, RestData.class, in -> input);
        }
        if (input.getSanityGained() != 0) {
            service.gainSanity(input.getSanityGained());
            service.convertFromTo(Object.class, RestData.class, in -> input);
        }
        service.release();

        if (input.getSanityGained() == 0 && input.getHealthGained() == 0) {
            ServicePlatform.get().getGameController().justRest().subscribe(() -> ss.onSuccess(input));
        } else {
            ss.onSuccess(input);
        }
    }
}
