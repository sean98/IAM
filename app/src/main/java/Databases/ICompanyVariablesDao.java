package Databases;

import java.util.List;

import Databases.util.DbTask;
import Models.util.Address;
import Models.Employees.Employee;
import Models.Company.Item;
import Models.Company.SalesMan;

public interface ICompanyVariablesDao {
    DbTask<String> getCompanyName();
    DbTask<Address> getCompanyAddress();
    DbTask<String> getSystemCurrency();
    DbTask<List<Employee>> getEmployees();
    DbTask<List<SalesMan>> getSalesmen();
    DbTask<List<String>> getCustomerGroups();
    DbTask<List<Item>> getActiveItems();
    DbTask<Boolean> updateEmployeePicture(Employee employee);

}
