import java.sql.*;

public class Account {
    public static final String TYPE_INTEREST = "Interest-Checking";
    public static final String TYPE_STUDENT  = "Student-Checking";
    public static final String TYPE_SAVING   = "Savings";
    public static final String TYPE_POCKET   = "Pocket";

    public int accountId;
    public int accountLinkId;
    public String bankBranch;
    public String type;
    public double balance;
    public double interest;
    public boolean active;

    public Account(int accountId, String bankBranch, String type, double balance, double interest, boolean active) {
        this.accountId = accountId;
        this.accountLinkId = -1;
        this.bankBranch = bankBranch;
        this.type = type;
        this.balance = balance;
        this.interest = interest;
        this.active = active;
    }

    public Account(int accountId, int accountLinked, String bankBranch, String type, double balance, boolean active) {
        this.accountId = accountId;
        this.accountLinkId = accountLinked;
        this.bankBranch = bankBranch;
        this.type = type;
        this.balance = balance;
        this.active = active;
        this.interest = getInterest(type);
    }

    public Account(int accountId, String bankBranch, String type, double balance, boolean active) {
        this(accountId, -1, bankBranch, type, balance, active);
    }

    public double getInterest(String type) {
        if (type.equals(TYPE_STUDENT) || type.equals(TYPE_POCKET))
            return 0;
        else if (type.equals(TYPE_INTEREST))
            return 0.055;
        else if (type.equals(TYPE_SAVING))
            return 0.075;
        else
            return -1;
    }

    public Account(ResultSet result) throws SQLException {
        this(
            result.getInt("account_id"),
            result.getString("bank_branch").trim(),
            result.getString("account_type").trim(),
            result.getFloat("balance"),
            result.getFloat("interest_rate"),
            result.getInt("active") == 1
        );
    }

    public String toString() {
        return "Account #" + accountId + " (" + type + ") under " + bankBranch
                + " with interest " + Math.round(interest * 10000.0) / 100.0 + "% and balance $" + Math.round(balance * 100.0) / 100.0
                + " is" + (active ? "" : " not") + " active.";
    }
}