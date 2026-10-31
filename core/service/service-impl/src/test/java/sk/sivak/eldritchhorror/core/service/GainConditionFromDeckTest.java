package sk.sivak.eldritchhorror.core.service;

import org.junit.After;
import org.junit.Test;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.action.impl.investigator.GainConditionFromDeckAction;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventlistener.condition.AbstractConditionListener;
import sk.sivak.eldritchhorror.core.eventlistener.provider.ConditionListenerProvider;
import sk.sivak.eldritchhorror.core.eventtype.data.GainConditionFromDeckData;
import sk.sivak.eldritchhorror.core.model.ConditionsDeckWrite;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

public class GainConditionFromDeckTest {
    @After public void cleanup() { ServicePlatform.nullifyInstance(); }

    @Test public void conditionBecomingUnavailableBeforeExecutionDoesNotDrawOrRegister() {
        ConditionsDeckWrite deck = mock(ConditionsDeckWrite.class);
        ConditionListenerProvider listeners = mock(ConditionListenerProvider.class);
        ServicePlatform.get().setConditionsDeck(deck);
        ServicePlatform.get().setConditionListenerProvider(listeners);
        GainConditionFromDeckData data = new GainConditionFromDeckData(InvestigatorId.THE_MUSICIAN, ConditionId.CURSED);
        GainConditionFromDeckAction action = new GainConditionFromDeckAction();
        action.setInput(data);
        // Both duplicate ownership (the report) and an exhausted deck make this false.
        when(deck.canGetConditionId(data.getInvestigatorId(), data.getConditionId())).thenReturn(false);
        assertSame(data, action.execute().toBlocking().value());
        verify(deck, never()).gainCondition(any(ConditionId.class), any(InvestigatorId.class));
        verifyNoInteractions(listeners);
    }

    @Test @SuppressWarnings({"rawtypes", "unchecked"})
    public void availableConditionIsDrawnAndRegisteredExactlyOnce() {
        ConditionsDeckWrite deck = mock(ConditionsDeckWrite.class);
        ConditionListenerProvider listeners = mock(ConditionListenerProvider.class);
        AbstractConditionListener listener = mock(AbstractConditionListener.class);
        ConditionInfo condition = mock(ConditionInfo.class);
        ServicePlatform.get().setConditionsDeck(deck);
        ServicePlatform.get().setConditionListenerProvider(listeners);
        GainConditionFromDeckData data = new GainConditionFromDeckData(InvestigatorId.THE_VIOLINIST, ConditionId.CURSED);
        when(deck.canGetConditionId(data.getInvestigatorId(), data.getConditionId())).thenReturn(true);
        when(deck.gainCondition(data.getConditionId(), data.getInvestigatorId())).thenReturn(condition);
        when(listeners.getConditionListener(condition)).thenReturn(listener);
        GainConditionFromDeckAction action = new GainConditionFromDeckAction(); action.setInput(data);
        assertSame(data, action.execute().toBlocking().value());
        verify(deck).gainCondition(data.getConditionId(), data.getInvestigatorId());
        verify(listener).register(condition, data.getInvestigatorId());
    }
}
