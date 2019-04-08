package Models.Company;

import android.util.Log;

import java.util.List;

import Databases.CachedDao;
import Databases.ICompanyVariablesDao;
import Models.util.Address;
import Models.Employees.Employee;

public abstract class SystemVariables {
    private static String companyName;
    private static Address companyAddress;
    private static String systemCurrency;
    private static List<Employee> employees;
    private static List<SalesMan> salesMEN;
    private static List<String> groups;
    private static List<Item> items;
    public static Employee ActiveUser;


    public static String GetCompanyName() {
        if(companyName == null) {
            ICompanyVariablesDao db = new CachedDao();
            try {
                companyName = db.getCompanyName().call();
            } catch (Exception e) {
                Log.e("Exception", "getCompanyName: "+e.getMessage());
                e.printStackTrace();
            }
        }
        return companyName;
    }

    public static Address GetCompanyAddress() {
        if(companyAddress == null){
            ICompanyVariablesDao db = new CachedDao();
            try {
                companyAddress = db.getCompanyAddress().call();
            } catch (Exception e) {
                Log.e("Exception", "getCompanyAddress: " +e.getMessage());
                e.printStackTrace();
            }
        }
        return companyAddress;
    }

    public static String GetSystemCurrency() {
        if(systemCurrency == null){
            ICompanyVariablesDao db = new CachedDao();
            try {
                systemCurrency = db.getSystemCurrency().call();
            } catch (Exception e) {
                Log.e("Exception", "getSystemCurrency: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return systemCurrency!=null?systemCurrency:"";
    }

    public static List<Employee> GetEmployees(){
        if(employees == null){
            ICompanyVariablesDao db = new CachedDao();
            try {
                employees = db.getEmployees().call();
            } catch (Exception e) {
                Log.e("Exception", "getEmployees: "+e.getMessage());
                e.printStackTrace();
            }
        }
        return employees;
    }

    public static List<SalesMan> GetSalesmen(){
        if(salesMEN == null){
            ICompanyVariablesDao db = new CachedDao();
            try {
                salesMEN = db.getSalesmen().call();
            } catch (Exception e) {
                Log.e("Exception", "getSalesmen: "+e.getMessage());
                e.printStackTrace();
            }
        }
        return salesMEN;

    }

    public static List<String> getCardsGroups(){
        if(groups == null){
            ICompanyVariablesDao db = new CachedDao();
            try {
                groups = db.getCustomerGroups().call();
            } catch (Exception e) {
                Log.e("Exception", "getCardsGroups: "+e.getMessage());
                e.printStackTrace();
            }
        }
        return groups;

    }

    public static List<Item> getActiveItems(){
        if(items == null){
            ICompanyVariablesDao db = new CachedDao();
            try {
                items = db.getActiveItems().call();
            } catch (Exception e) {
                Log.e("Exception", "getActiveItems:"+e.getMessage());
                e.printStackTrace();
            }
        }
        return items;

    }
}
