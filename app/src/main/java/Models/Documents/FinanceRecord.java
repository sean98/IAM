package Models.Documents;

import com.example.sean98.iam.LoginActivity;
import com.example.sean98.iam.MainActivity;
import com.example.sean98.iam.R;

import java.io.Serializable;
import java.util.Date;

public class FinanceRecord implements Serializable {

    public enum Type {
        Invoice(LoginActivity.applicationContext.getString(R.string.invoice)) ,
        Receipt(LoginActivity.applicationContext.getString(R.string.receipt)) ,
        CreditInvoice(LoginActivity.applicationContext.getString(R.string.credit_invoice)) ,
        Other(LoginActivity.applicationContext.getString(R.string.other));

        private final String name;
        Type(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
    private String cardCode;
    private String baseDocSN;
    private String base2DocSN;
    private Date refDate;
    private float debt;
    private float balanceDue;
    private String memo;
    private Type type;

    public FinanceRecord(String cardCode, String baseDocSN, String base2DocSN, Date refDate,
                         float debt, float balanceDue,
                         String memo, Type type) {
        this.cardCode = cardCode;
        this.baseDocSN = baseDocSN;
        this.base2DocSN = base2DocSN;
        this.refDate = refDate;
        this.debt = debt;
        this.balanceDue = balanceDue;
        this.memo = memo;
        this.type = type;
    }

    public String getCardCode() {
        return cardCode;
    }

    public String getBaseDocSN() {
        return baseDocSN;
    }

    public String getBase2DocSN() {
        return base2DocSN;
    }

    public Date getRefDate() {
        return refDate;
    }

    public float getDebt() {
        return debt;
    }

    public float getBalanceDue() {
        return balanceDue;
    }

    public String getMemo() {
        return memo;
    }

    public Type getType() {
        return type;
    }
}
