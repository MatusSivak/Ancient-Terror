package sk.sivak.eldritchhorror.core.eventtype.data.token;

public class LoseTokenData {
    private int amount;
    private boolean executed;
    private TokenType tokenType;

    public LoseTokenData(int amount, TokenType tokenType) {
        this.amount = amount;
        this.tokenType = tokenType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }
}
