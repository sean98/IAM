package Databases;

import android.content.Context;
import android.util.Log;

import java.util.List;

import Databases.Local.LocalSQLITEDao;
import Databases.SAP.SapMSSQL;
import Databases.util.DbTask;
import Models.util.Address;
import Models.Cards.Customer;
import Models.Cards.CustomerParams;
import Models.Employees.Employee;
import Models.Company.Item;
import Models.Company.SalesMan;
import Models.util.SapLocation;

public class CachedDao implements ICustomerDao, ICompanyVariablesDao {
    public static Context ApplicationContext;
    private Context context;
    private SapMSSQL onlineDB;
    private LocalSQLITEDao localDB;
    private boolean forceOnlineAlways;
    private boolean forceOnlineTemp;

    public CachedDao(){
        if(ApplicationContext == null)
            throw new RuntimeException
                    ("ApplicationContext must be initialized before creating a CachedDao");
        this.context = ApplicationContext;
        localDB = new LocalSQLITEDao(context);
        onlineDB =  new SapMSSQL();

    }

    public CachedDao(Context context){
        this.context = context;
        onlineDB =  new SapMSSQL();
        localDB = new LocalSQLITEDao(this.context);
    }

    public CachedDao forceOnline(){
        this.forceOnlineTemp = true;
        return this;
    }

    public CachedDao setForceOnlineAlways(boolean forceOnlineAlways){
        this.forceOnlineAlways = forceOnlineAlways;
        return this;
    }

    public boolean isForceOnlineAlways(){return forceOnlineAlways;}

    public boolean isForceOnlineTemp(){return  forceOnlineTemp;}

    @Override
    public DbTask<Customer> getCustomer(String cid) {
        return new DbTask<>("CachedDb getCustomer,"+cid,()-> {
            Customer cachedCustomer = null;
            if (!forceOnlineAlways && !forceOnlineTemp) {
                cachedCustomer = localDB.getCustomer(cid).call();
                forceOnlineTemp = false;
            }
            if (cachedCustomer == null) {
                cachedCustomer = this.getCustomer(cid).call();
                if (cachedCustomer != null) {
                    if (localDB.getCustomer(cid) != null)
                        localDB.addCustomer(cachedCustomer).call();
                    else
                        localDB.updateCustomer(cachedCustomer).call();
                }
            }
            return cachedCustomer;
        });

    }

    @Override
    public DbTask<Customer> updateCustomer(Customer customer) {
        return new DbTask<>("CachedDb updateCustomer,"+customer.getCid(),()-> {
            //always force online
            forceOnlineTemp = false;
            if (onlineDB.updateCustomer(customer).call() != null) {
                Customer c = localDB.updateCustomer(customer).call();
                customer.copyOf(c);
                return customer;
            }
            return null;
        });
    }

    @Override
    public DbTask<Customer> addCustomer(Customer customer) {
        return new DbTask<>("CachedDb addCustomer",()-> {
            //always force online
            forceOnlineTemp = false;
            Customer addedCustomer = onlineDB.addCustomer(customer).call();
            if (addedCustomer != null) {
                addedCustomer = localDB.addCustomer(addedCustomer).call();
                customer.copyOf(addedCustomer);
            }
            return addedCustomer;
        });
    }

    @Override
    public DbTask<List<Customer>> searchCustomers(CustomerParams params)  {
        return new DbTask<>("CachedDb searchCustomers",()-> {
            List<Customer> cachedList = null;
            if (!forceOnlineAlways && !forceOnlineTemp) {
                Log.i("Cached searchCustomers","Local");
                cachedList = localDB.searchCustomers(params).call();
                forceOnlineTemp = false;
            }
            if ((cachedList == null || cachedList.isEmpty())) {
                Log.i("Cached searchCustomers","Online");
                cachedList = onlineDB.searchCustomers(new CustomerParams()).call();
                localDB.SaveCustomers(cachedList).execute();
            }
            return cachedList;
        });
    }

    @Override
    public DbTask<List<String>> getCustomerGroups(){
        return new DbTask<>("CachedDb getCustomerGroups",()-> {
            List<String> cachedGroups = null;
            if (!forceOnlineAlways && !forceOnlineTemp) {
                cachedGroups = localDB.getCustomerGroups().call();
                forceOnlineTemp = false;
            }

            if (cachedGroups == null || cachedGroups.isEmpty()) {
                cachedGroups = onlineDB.getCustomerGroups().call();
                if (cachedGroups != null && !cachedGroups.isEmpty()) {
                    localDB.SaveCardGroups(cachedGroups).call();
                }
            }
            return cachedGroups;
        });
    }

    @Override
    public DbTask<List<Item>> getActiveItems(){
        return new DbTask<>("CachedDb getActiveItems",()-> {
            List<Item> cachedItems = null;
            if (!forceOnlineAlways && !forceOnlineTemp) {
                cachedItems = localDB.getActiveItems().call();
                forceOnlineTemp = false;
            }
            if (cachedItems == null || cachedItems.isEmpty()) {
                cachedItems = onlineDB.getActiveItems().call();
                if (cachedItems != null && !cachedItems.isEmpty()) {
                    localDB.SaveItems(cachedItems).execute();
                }
            }
            return cachedItems;
        });
    }

    @Override
    public DbTask<Boolean> updateEmployeePicture(Employee employee) {
        return new DbTask<>("CachedDb updateEmployeePicture"+employee.getSn(),()-> {
            //always force online
            forceOnlineTemp = false;
            boolean ret = onlineDB.updateEmployeePicture(employee).call();
            localDB.updateEmployeePicture(employee).execute();
            return ret;
        });
    }

    @Override
    public  DbTask<String> getCompanyName()  {
        return new DbTask<>("CachedDb getCompanyName",()-> {
            String cName = null;
            if (!forceOnlineAlways && !forceOnlineTemp) {
                cName = localDB.getCompanyName().call();
                forceOnlineTemp = false;
            }
            if (cName == null) {
                cName = onlineDB.getCompanyName().call();
                localDB.SaveCompanyName(cName).call();
            }
            return cName;
        });
    }

    @Override
    public DbTask<Address> getCompanyAddress() {
        return new DbTask<>("CachedDb getCompanyAddress",()-> {
            Address cAddress = null;
            if (!forceOnlineAlways && !forceOnlineTemp) {
                cAddress = localDB.getCompanyAddress().call();
                forceOnlineTemp = false;
            }
            if (cAddress == null) {
                cAddress = onlineDB.getCompanyAddress().call();
                localDB.SaveCompanyAddress(cAddress).call();
            }
            return cAddress;
        });
    }

    @Override
    public  DbTask<String> getSystemCurrency() {
        return new DbTask<>("CachedDb getSystemCurrency",()-> {
            String cSystemCurrency = null;
            if (!forceOnlineAlways && !forceOnlineTemp) {
                cSystemCurrency = localDB.getSystemCurrency().call();
                forceOnlineTemp = false;
            }
            if (cSystemCurrency == null) {
                cSystemCurrency = onlineDB.getSystemCurrency().call();
                localDB.SaveCompanyName(cSystemCurrency).call();
            }
            return cSystemCurrency;
        });
    }

    @Override
    public  DbTask<List<Employee>> getEmployees(){
        return new DbTask<>("CachedDb getEmployees",()-> {
            List<Employee> cachedEmployees = null;
            if (!forceOnlineAlways && !forceOnlineTemp) {
                cachedEmployees = localDB.getEmployees().call();
                forceOnlineTemp = false;
            }
            if (cachedEmployees == null || cachedEmployees.isEmpty()) {
                cachedEmployees = onlineDB.getEmployees().call();
                if (cachedEmployees != null && !cachedEmployees.isEmpty()) {
                    localDB.SaveEmployees(cachedEmployees).call();
                }
            }
            return cachedEmployees;
        });
    }

    @Override
    public DbTask<List<SalesMan>> getSalesmen() {
        return new DbTask<>("CachedDb getSalesmen",()-> {
            List<SalesMan> cachedSalesmen = null;
            if (!forceOnlineAlways && !forceOnlineTemp) {
                cachedSalesmen = localDB.getSalesmen().call();
                forceOnlineTemp = false;
            }
            if (cachedSalesmen == null || cachedSalesmen.isEmpty()) {
                cachedSalesmen = onlineDB.getSalesmen().call();
                if (cachedSalesmen != null && !cachedSalesmen.isEmpty()) {
                    localDB.SaveSalesmen(cachedSalesmen).call();
                }
            }
            return cachedSalesmen;
        });
    }

    @Override
    public  DbTask<SapLocation> updateLocation(Customer customer){
        return new DbTask<>("CachedDb updateLocation",()-> {
            //always forced online
            forceOnlineTemp = false;
            if (onlineDB.updateLocation(customer).call() != null)
                return localDB.updateLocation(customer).call();
            return null;
        });
    }

    public DbTask<List<Employee>> refreshEmployees(){
        return new DbTask<List<Employee>>("CachedDb refreshEmployees",()-> {
            Log.i("Cached ","refreshEmployees");
            List<Employee> emps = onlineDB.getEmployees().call();
            localDB.SaveEmployees(emps).execute();
            //always forced online
            forceOnlineTemp = false;
            return emps;
        });
    }

    public DbTask<Boolean> refreshCache(){
        return new DbTask<>("CachedDb refreshCache",()-> {
            Log.i("Cached ","refreshCache");

            localDB.SaveCompanyName(onlineDB.getCompanyName().call()).execute();
            localDB.SaveCompanyAddress(onlineDB.getCompanyAddress().call()).execute();
            localDB.SaveSystemCurrency(onlineDB.getSystemCurrency().call()).execute();

            localDB.SaveCardGroups(onlineDB.getCustomerGroups().call()).execute();
            localDB.SaveSalesmen(onlineDB.getSalesmen().call()).execute();
            onlineDB.searchCustomers(new CustomerParams()).
                    addOnSuccessListener((customers ->
                            localDB.SaveCustomers(customers).execute()))
                    .execute();


            localDB.SaveItems(onlineDB.getActiveItems().call()).execute();
            localDB.SaveEmployees(onlineDB.getEmployees().call()).execute();
            //always forced online
            forceOnlineTemp = false;
            return true;
        });
    }
}
