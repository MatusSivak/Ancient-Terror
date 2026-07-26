package sk.sivak.eldritchhorror.core.eventtype.data.token;

import rx.functions.Action;
import rx.functions.Action0;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SpendData {
    private Map<TokenType, Integer> tokenAmountMap = new HashMap<>();
    private boolean silent;
    private boolean hasEnough;
    private Action0 payAction;
    private Action0 revertAction;
    private List<Action0> postRevertActions = new LinkedList<>();

    public void setPayAction(Action0 payAction) {
        this.payAction = payAction;
    }

    public void pay() {
        if (payAction != null) {
            payAction.call();
        }
    }

    public Map<TokenType, Integer> getTokenAmountMap() {
        return tokenAmountMap;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public boolean hasEnough() {
        return hasEnough;
    }

    public void setHasEnough(boolean hasEnough) {
        this.hasEnough = hasEnough;
    }

    @Override
    public String toString() {
        return "SpendData{" +
                "tokenAmountMap=" + tokenAmountMap +
                ", silent=" + silent +
                ", hasEnough=" + hasEnough +
                ", payAction=" + payAction +
                '}';
    }

    public void setRevertAction(Action0 revertAction) {
        this.revertAction = revertAction;
    }

    public void revert() {
        if (revertAction != null) {
            revertAction.call();
        }
        for (Action0 postRevertAction : postRevertActions) {
            postRevertAction.call();
        }
    }

    public void addPostRevertAction(Action0 postRevertAction) {
        postRevertActions.add(postRevertAction);
    }
}
