package Databases;
import Databases.util.DbTask;
import Models.Cards.Customer;
import Models.Cards.CustomerParams;
import Models.Company.SalesMan;
import Models.util.SapLocation;

import java.util.List;

public interface ICustomerDao {
     DbTask<Customer> getCustomer(String cid);
     DbTask<Customer> updateCustomer(Customer customer);
     DbTask<Customer> addCustomer(Customer customer);
     DbTask<List<Customer>> searchCustomers(CustomerParams params);
     DbTask<List<String>> getCustomerGroups();
     DbTask<List<SalesMan>> getSalesmen();
     DbTask<SapLocation> updateLocation(Customer customer);
}
