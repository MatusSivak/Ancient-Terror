package sk.sivak.eldritchhorror.core.eventlistener.mythos;

import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;

public class YellowMythosCard11 implements MythosCardEventListener{
    @Override
    public void execute() {
        ServicePlatform.get().getDoomOmenService().selectNewOmen();
    }
}
