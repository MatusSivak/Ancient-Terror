package sk.sivak.eldritchhorror.core.action.impl.action.investigator;

import java8.features.stream.Stream;
import java8.features.util.IterableUtils;
import rx.SingleSubscriber;
import sk.sivak.eldritchhorror.core.action.AbstractHookableAction;
import sk.sivak.eldritchhorror.core.action.ServicePlatform;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionId;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.eventtype.BeforeAfterEvent;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.InvestigatorRestriction;
import sk.sivak.eldritchhorror.core.model.InvestigatorRead;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author msivak
 */
public class SelectInvestigatorAction extends AbstractHookableAction<InvestigatorRestriction, InvestigatorId> {

    public SelectInvestigatorAction() {
        super(BeforeAfterEvent.SELECT_INVESTIGATOR);
    }

    @Override
    protected void onExecute(SingleSubscriber<? super InvestigatorId> ss) {
        List<? extends InvestigatorRead> selectedInvestigators = ServicePlatform.get().getInvestigators().getOnBoardInvestigators();
        List<InvestigatorId> list = new LinkedList<>();

        if (input.getAllowedInvestigators().length == 0 && input.getDisabledInvestigators().length == 0) {
            list.addAll(Stream.map(selectedInvestigators, si -> si.getInfo().getInvestigatorId()));
        } else if (input.getAllowedInvestigators().length == 0 && input.getDisabledInvestigators().length != 0) {
            list.addAll(Stream.map(selectedInvestigators, si -> si.getInfo().getInvestigatorId()));
            List<InvestigatorId> disabledInvestigators = Arrays.asList(input.getDisabledInvestigators());
            IterableUtils.removeIf(list, disabledInvestigators::contains);
        } else if (input.getAllowedInvestigators().length != 0 && input.getDisabledInvestigators().length == 0) {
            list.addAll(Arrays.asList(input.getAllowedInvestigators()));
        } else {
            list.addAll(Arrays.asList(input.getAllowedInvestigators()));
            List<InvestigatorId> disabledInvestigators = Arrays.asList(input.getDisabledInvestigators());
            IterableUtils.removeIf(list, disabledInvestigators::contains);
        }

        if (!input.canBeDelayedOrDetained()) {
            Iterator<InvestigatorId> iterator = list.iterator();
            while (iterator.hasNext()) {
                InvestigatorId investigatorId = iterator.next();
                boolean isDelayed = ServicePlatform.get().getInvestigators().getInvestigator(investigatorId).isDelayed();
                if (isDelayed) {
                    iterator.remove();
                    continue;
                }
                boolean isDetained = ServicePlatform.get().getConditionsDeck().hasCondition(investigatorId, ConditionId.DETAINED);
                if (isDetained) {
                    iterator.remove();
                }
            }
        }
        if (list.isEmpty()) {
            ss.onSuccess(ServicePlatform.get().getInvestigators().getActiveInvestigatorId());
            return;
        }
        ServicePlatform.get().getGameController().selectInvestigator(list, input.getTitle()).subscribe(ss::onSuccess);
    }
}
