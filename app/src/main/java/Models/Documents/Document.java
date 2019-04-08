package Models.Documents;

import com.example.sean98.iam.LoginActivity;
import com.example.sean98.iam.R;

import java.io.Serializable;
import java.util.Date;

import Models.Cards.Customer;
import Models.Employees.Employee;
import Models.Company.SalesMan;

public class Document  implements Serializable {
    public enum Type {
        Invoice(LoginActivity.applicationContext.getString(R.string.invoice)) ,// ObjectType sapInvoice = 13
        Order(LoginActivity.applicationContext.getString(R.string.order)),
        RevocationInvoice(LoginActivity.applicationContext.getString(R.string.credit_invoice)),
        AdvancePayment(LoginActivity.applicationContext.getString(R.string.advance_payment)),
        SaleQuotation(LoginActivity.applicationContext.getString(R.string.sale_quotation)),
        ShippingCet(LoginActivity.applicationContext.getString(R.string.shipping_cert));

        private final String name;
        Type(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }


    public Document(Integer sn) {
        this.sn = sn;
    }

    private boolean isClosed;
    private boolean isCanceled;

    private Integer sn;
    private String parallelSn;
    private Date updateDate;
    private Date docDate;
    private Date docDueDate;
    private Date docTaxDate;

    private Customer customer;
    private String licTradNum;
    private String cardName;
    private String address;

    private String currency;
    private float vatPercent;
    private float vatSum;

    private float discountPercent;
    private float discountSum;
    private float total;
    private float chargedSum; //can be charged by revocation invoice

    private float paidSum;

    private SalesMan salesMan;
    private Employee owner;
    private String comments;

    private Type type;
    
    public Document setParallelSn(String parallelSn) {
        this.parallelSn = parallelSn; return this; 
    }

    public Document setUpdateDate(Date updateDate) {
        this.updateDate = updateDate; return this;
    }

    public Document setDocDate(Date docDate) {
        this.docDate = docDate; return this;
    }

    public Document setDocDueDate(Date docDueDate) {
        this.docDueDate = docDueDate; return this;
    }

    public Document setDocTaxDate(Date docTaxDate) {
        this.docTaxDate = docTaxDate; return this;
    }

    public Document setCustomer(Customer customer) {
        this.customer = customer; return this;
    }

    public Document setLicTradNum(String licTradNum) {
        this.licTradNum = licTradNum; return this;
    }

    public Document setCardName(String cardName) {
        this.cardName = cardName; return this;
    }

    public Document setAddress(String address) {
        this.address = address; return this;
    }

    public Document setCurrency(String currency) {
        this.currency = currency; return this;
    }

    public Document setVatPercent(float vatPercent) {
        this.vatPercent = vatPercent; return this;
    }

    public Document setVatSum(float vatSum) {
        this.vatSum = vatSum; return this;
    }

    public Document setDiscountPercent(float discountPercent) {
        this.discountPercent = discountPercent; return this;
    }

    public Document setDiscountSum(float discountSum) {
        this.discountSum = discountSum; return this;
    }

    public Document setTotal(float total) {
        this.total = total; return this;
    }

    public Document setChargedSum(float chargedSum) {
        this.chargedSum = chargedSum; return this;
    }

    public Document setSalesMan(SalesMan salesMan) {
        this.salesMan = salesMan; return this;
    }

    public Document setOwner(Employee owner) {
        this.owner = owner; return this;
    }

    public Document setComments(String comments) {
        this.comments = comments; return this;
    }

    public Document setIsClosed(boolean isClosed) {
        this.isClosed = isClosed; return this;
    }
    public Document setIsCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled; return this;
    }


    public Document setType(Type type) {
        this.type = type; return this;
    }

    public Document setPaidSum(float paidSum){
        this.paidSum = paidSum;
        return this;
    }




    public Type getType(){return type;};

    public boolean isClosed() {
        return isClosed;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public Integer getSn() {
        return sn;
    }

    public String getParallelSn() {
        return parallelSn;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public Date getDocDate() {
        return docDate;
    }

    public Date getDocDueDate() {
        return docDueDate;
    }

    public Date getDocTaxDate() {
        return docTaxDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getLicTradNum() {
        return licTradNum;
    }

    public String getCardName() {
        return cardName;
    }

    public String getAddress() {
        return address;
    }

    public String getCurrency() {
        return currency;
    }

    public float getVatPercent() {
        return vatPercent;
    }

    public float getVatSum() {
        return vatSum;
    }

    public float getDiscountPercent() {
        return discountPercent;
    }

    public float getDiscountSum() {
        return discountSum;
    }

    public float getTotal() {
        return total;
    }

    public float getChargedSum() {
        return chargedSum;
    }

    public SalesMan getSalesMan() {
        return salesMan;
    }

    public Employee getOwner() {
        return owner;
    }

    public String getComments() {
        return comments;
    }

    public float getPaidSum() {
        return paidSum;
    }
}
