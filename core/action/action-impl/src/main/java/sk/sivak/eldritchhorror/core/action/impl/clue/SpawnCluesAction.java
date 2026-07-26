package sk.sivak.eldritchhorror.core.action.impl.clue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class SpawnCluesAction extends AbstractHookableAction<Integer, List<ClueInfo>> {

    private static final Logger logger = LogManager.getLogger(SpawnCluesAction.class);
    private final boolean quick;
    private Iterator<ClueInfo> iterator;
    private List<ClueInfo> output = new LinkedList<>();

    public SpawnCluesAction(boolean quick) {
        super(BeforeAfterEvent.SPAWN_CLUES);
        this.quick = quick;
    }

    @Override
    protected void onExecute(SingleSubscriber<? super List<ClueInfo>> ss) {
        for (int i = 0; i < input; i++) {
            output.add(ServicePlatform.get().getCluePool().spawnClue());
        }

        if (quick) {
            for (ClueInfo clueInfo : output) {
                ServicePlatform.get().getGameController().confirmClueLocation(
                        clueInfo.getSpawnLocationId(), clueInfo.getCurrentLocationId()).subscribe();
            }
            ss.onSuccess(output);
            return;
        }
        iterator = output.iterator();
        confirmSingleClue(ss);
    }

    private void confirmSingleClue(SingleSubscriber<? super List<ClueInfo>> ss) {
        if (iterator.hasNext()) {
            ClueInfo next = iterator.next();
            if (next == null) {
                ss.onSuccess(output);
                return;
            }
            ServicePlatform.get().getGameController().confirmClueLocation(next.getSpawnLocationId(), next.getCurrentLocationId()).subscribe(() -> {
                confirmSingleClue(ss);
            });
        } else {
            ss.onSuccess(output);
        }

    }
}
