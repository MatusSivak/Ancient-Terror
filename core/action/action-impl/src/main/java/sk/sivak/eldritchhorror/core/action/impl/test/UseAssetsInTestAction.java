package sk.sivak.eldritchhorror.core.action.impl.test;

import rx.Single;
import sk.sivak.eldritchhorror.core.action.Action;
import sk.sivak.eldritchhorror.core.constants.test.UsableAsset;
import sk.sivak.eldritchhorror.core.eventtype.data.test.TestData;

import static java8.features.util.IterableUtils.forEach;

/**
 * @author msivak
 */
public class UseAssetsInTestAction implements Action<TestData, TestData> {

    private TestData input;

    @Override
    public Single<TestData> execute() {
        return Single.create(onSub -> {
            forEach(input.getSelectedUsableAssets(), UsableAsset::runOnUseAction);
            onSub.onSuccess(input);
        });
    }

    @Override
    public void setInput(TestData input) {
        this.input = input;
    }
}
