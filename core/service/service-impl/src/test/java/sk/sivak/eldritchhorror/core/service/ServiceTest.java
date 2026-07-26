package sk.sivak.eldritchhorror.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sk.sivak.eldritchhorror.core.action.provider.ActionProvider;
import sk.sivak.eldritchhorror.core.commandqueue.CommandQueue;

/**
 * @author msivak
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceTest {

    private ServiceImpl service;

    @Mock
    private ActionProvider actionProvider;

    @Mock
    private CommandQueue commandQueue;

    @Test
    public void simpleTest() {

    }
}
