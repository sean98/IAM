package Models.Cards;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Models.Company.SalesMan;

public class CustomerParams implements Serializable {
    public String cid;
    public List<String> names = new ArrayList<>();
    public List<Customer.Type> types = new ArrayList<>();
    public List<Customer.Status>  statuses = new ArrayList<>();
    public List<String> groups = new ArrayList<>();
    public List<SalesMan> salesmen = new ArrayList<>();
    public Float balanceMoreThen = null;
    public CustomerParams() {}

    public CustomerParams(CustomerParams copy) {
        cid = (cid==null?null:String.valueOf(copy.cid));
        names = new ArrayList<>(copy.names);
        types = new ArrayList<>(copy.types);
        statuses = new ArrayList<>(copy.statuses);
        groups = new ArrayList<>(copy.groups);
        salesmen = new ArrayList<>(copy.salesmen);
        balanceMoreThen = copy.balanceMoreThen;
    }

    public CustomerParams BalanceMoreThen(float balance){
        this.balanceMoreThen = balance;
        return this;
    }
    public CustomerParams Cid(String cid){
        this.cid = cid;
        return this;
    }

    public CustomerParams Group(String group){
        groups.add(group);
        return this;
    }

    public CustomerParams Name(String name){
        names.add(name);
        return this;
    }

    public CustomerParams Type(Customer.Type type){
        types.add(type);
        return this;
    }

    public CustomerParams Status(Customer.Status status){
        statuses.add(status);
        return this;
    }

    public CustomerParams Salesman(SalesMan salesMan){
        salesmen.add(salesMan);
        return this;
    }

}
