package Models.Cards;

import android.location.Location;

import com.example.sean98.iam.LocaleApplication;
import com.example.sean98.iam.LoginActivity;
import com.example.sean98.iam.R;

import java.io.Serializable;

import Models.Company.SalesMan;
import Models.util.Address;
import Models.util.SapLocation;

public class Customer implements Serializable {
    public enum Type {
        Private(LocaleApplication.applicationContext.getString(R.string.private_cust)) ,
        Company(LocaleApplication.applicationContext.getString(R.string.company));

        private final String name;
        Type(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
    public enum Status {
        Valid(LocaleApplication.applicationContext.getString(R.string.valid)) ,
        Frozen(LocaleApplication.applicationContext.getString(R.string.frozen));

        private final String name;
        Status(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }


    //private attributes//
    //customer's information
    private String cid;
    private String name;
    private String licTradNum;
    private String group;
    private Type type;
    private Status status;
    //contact's information
    private String phone1;
    private String phone2;
    private String cellular;
    private String email;
    private String fax;
    //location's information
    private Address billingAddress;
    private Address shippingAddress;
    private SapLocation location;//GPS default location
    //finance
    private float balance;
    //other
    private String comments;
    private SalesMan salesman;

    //constructors
    public Customer(String cid ,float balance ,Address billingAddress,Address shippingAddress){
        this.cid = cid;
        this.balance = balance;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
    }
    public Customer(){ //unsaved customer, cid = null
        billingAddress = new Address();
        shippingAddress = new Address();

    }


    //setters
    public Customer setStatus(Status status) {
        this.status = status;
        return this;
    }
    public Customer setLicTradNum(String licTradNum) {
        this.licTradNum = licTradNum;
        return this;
    }
    public Customer setGroup(String group) {
        this.group = group;
        return this;
    }

    public Customer setType(Type type) {
        this.type = type;
        return this;
    }

    public Customer setPhone1(String phone1) {
        this.phone1 = phone1;
        return this;
    }

    public Customer setPhone2(String phone2) {
        this.phone2 = phone2;
        return this;
    }

    public Customer setCellular(String cellular) {
        this.cellular = cellular;
        return this;
    }

    public Customer setEmail(String email) {
        this.email = email;
        return this;
    }

    public Customer setFax(String fax) {
        this.fax = fax;
        return this;
    }


    public Customer setName(String name){
        this.name = name;
        return this;
    }

    public Customer setComments(String comments){
        this.comments = comments;
        return this;
    }

    public Customer setSalesman(SalesMan salesman){
        this.salesman = salesman;
        return this;
    }

    public Customer setLocation(Location location){
        this.location= new SapLocation(location);
        return this;
    }

    public Customer setLocation(SapLocation location) {
        if (location!=null)
            this.location = location;
        return this;
    }


    public String getCid() {
        return cid;
    }

    public SalesMan getSalesman(){
        return salesman;
    }

    public String getName() {
        return name;
    }

    public String getLicTradNum() {
        return licTradNum;
    }

    public String getGroup() {
        return group;
    }

    public Type getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getCellular() {
        return cellular;
    }

    public String getEmail() {
        return email;
    }

    public String getFax() {
        return fax;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public float getBalance() {
        return balance;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public SapLocation getLocation(){
        return location;
    }

    public String getComments(){
        return comments;
    }

   public void copyOf(Customer customer) {
       cid = customer.cid;
       name = customer.name;
       licTradNum = customer.licTradNum;
       group = customer.group;
       type = customer.type;
       status = customer.status;
       phone1 = customer.phone1;
       phone2 = customer.phone2;
       cellular = customer.cellular;
       email = customer.email;
       fax = customer.fax;
       location = customer.location;
       balance = customer.balance;
       comments = customer.comments;
       salesman = customer.salesman;
       billingAddress = customer.billingAddress;
       shippingAddress = customer.shippingAddress;
   }


}
