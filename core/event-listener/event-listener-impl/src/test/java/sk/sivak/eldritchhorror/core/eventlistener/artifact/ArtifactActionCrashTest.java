package sk.sivak.eldritchhorror.core.eventlistener.artifact;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import org.junit.After;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.constants.artifact.*;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.ServicePlatform;
import sk.sivak.eldritchhorror.core.eventqueue.EventQueueImpl;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.CollectAvailableActionsData;
import sk.sivak.eldritchhorror.core.model.*;
import static org.junit.Assert.*;

public class ArtifactActionCrashTest {
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void everyArtifactListenerAcceptsItsRegisteredCardType() throws Exception {
        for (ArtifactId id : ArtifactId.values()) {
            Class<?> listener = Class.forName(id.getArtifactListenerClassName());
            ParameterizedType parent = (ParameterizedType) listener.getGenericSuperclass();
            Class<?> accepted = (Class<?>) parent.getActualTypeArguments()[0];
            assertTrue(id + " listener uses the wrong card type",
                    accepted.isAssignableFrom(Class.forName(id.getArtifactClassName())));
        }
    }

    @Test public void brainCaseBuildsActionMenuAfterGainingAndReloading() {
        EventQueueImpl events = new EventQueueImpl();
        ServicePlatform.get().setEventQueue(events);
        InvestigatorRead owner = (InvestigatorRead) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{InvestigatorRead.class}, (p,m,a) -> false);
        ServicePlatform.get().setInvestigators((InvestigatorsRead) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{InvestigatorsRead.class}, (p,m,a) -> {
                    if (m.getName().equals("getActiveInvestigatorId")) return InvestigatorId.THE_BOUNTY_HUNTER;
                    if (m.getName().equals("getOnBoardInvestigators")) return Arrays.asList(owner, owner);
                    return owner;
                }));
        MiGoBrainCaseArtifact card = new MiGoBrainCaseArtifact();
        for (boolean reload : new boolean[]{false,true}) {
            MiGoBrainCaseListener listener = new MiGoBrainCaseListener();
            if (reload) listener.justLoadWithOwner(InvestigatorId.THE_BOUNTY_HUNTER, card);
            else listener.registerWithOwner(InvestigatorId.THE_BOUNTY_HUNTER, card);
            CollectAvailableActionsData actions = new CollectAvailableActionsData();
            events.fireBeforeEvent(BeforeAfterEvent.FIND_ACTIONS, actions);
            assertEquals(1, actions.getActionPhaseActions().size());
            assertEquals(sk.sivak.eldritchhorror.core.eventlistener.action.AbstractActionPhaseListener
                    .cardActionText(card.getDescription()), actions.getActionPhaseActions().get(0).getGeneralDescription());
            assertFalse(actions.getActionPhaseActions().get(0).isDisabled());
            listener.unregister();
        }
    }
}
