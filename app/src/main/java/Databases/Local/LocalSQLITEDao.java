package Databases.Local;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import Databases.util.DbTask;
import Databases.ICompanyVariablesDao;
import Databases.ICustomerDao;
import Models.util.Address;
import Models.Cards.Customer;
import Models.Cards.CustomerParams;
import Models.Employees.Department;
import Models.Employees.Employee;
import Models.Employees.JobPosition;
import Models.Company.Item;
import Models.Company.SalesMan;
import Models.util.SapLocation;
import androidx.annotation.NonNull;
import Databases.Local.localSQLiteDbHelper.*;
public class LocalSQLITEDao implements ICustomerDao, ICompanyVariablesDao {

    SQLiteDatabase db;
    localSQLiteDbHelper dbHelper;
    Context context;
    static Map<Integer,SalesMan> salesManMap = null;//getSalesmanMap()
    static Set<String> customerGroups = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public LocalSQLITEDao(@NonNull Context context){
        this.context = context;
        dbHelper = new localSQLiteDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public DbTask<Customer> getCustomer(String cid) {
        return new DbTask<>("LocalDb getCustomer",()->{
            Cursor cursor = db.query(
                    CustomerEntry.TABLE_NAME,
                    null,//get all columns
                    CustomerEntry.COLUMN_NAME_CID+" = ?",
                    new String[]{cid},
                    null,
                    null,
                    null);
            Customer customer = null;
            if(cursor.moveToNext()){
                customer = setCustomer(cursor,db);
            }
            return customer;
        });

    }

    @Override
    public  DbTask<Customer> updateCustomer(Customer customer) {
        return new DbTask<>("LocalDb updateCustomer"+","+customer.getCid(),()-> {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(CustomerEntry.COLUMN_NAME_CID,customer.getCid());
            values.put(CustomerEntry.COLUMN_NAME_NAME,customer.getName() );
            values.put(CustomerEntry.COLUMN_NAME_LICTRADNUM,customer.getLicTradNum() );
            values.put(CustomerEntry.COLUMN_NAME_GROUP,customer.getGroup() );
            values.put(CustomerEntry.COLUMN_NAME_TYPE,customer.getType().toString() );
            values.put(CustomerEntry.COLUMN_NAME_STATUS,customer.getStatus().toString() );
            values.put(CustomerEntry.COLUMN_NAME_PHONE1,customer.getPhone1() );
            values.put(CustomerEntry.COLUMN_NAME_PHONE2,customer.getPhone2() );
            values.put(CustomerEntry.COLUMN_NAME_CELLULAR,customer.getCellular() );
            values.put(CustomerEntry.COLUMN_NAME_EMAIL,customer.getEmail() );
            values.put(CustomerEntry.COLUMN_NAME_COMMENTS,customer.getComments() );
            values.put(CustomerEntry.COLUMN_NAME_FAX,customer.getFax() );
            values.put(CustomerEntry.COLUMN_NAME_LOCATION,customer.getLocation()!=null?convertToString(customer.getLocation()):null );
            values.put(CustomerEntry.COLUMN_NAME_BALANCE,customer.getBalance() );
            values.put(CustomerEntry.COLUMN_NAME_SALESMAN,customer.getSalesman().getCode() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_TO_DEF,customer.getBillingAddress().getName() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_COUNTRY,customer.getBillingAddress().getCountry() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_CITY,customer.getBillingAddress().getCity() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_ADDRESS,customer.getBillingAddress().getStreet() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_STREETNO,customer.getBillingAddress().getStreetNum() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_ZIPCODE,customer.getBillingAddress().getZipCode() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_BLOCK,customer.getBillingAddress().getBlock() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_BUILDING,customer.getBillingAddress().getApartment() );
            values.put(CustomerEntry.COLUMN_NAME_SHIP_TO_DEF,customer.getShippingAddress().getName() );
            values.put(CustomerEntry.COLUMN_NAME_SHIP_COUNTRY,customer.getShippingAddress().getCountry() );
            values.put(CustomerEntry.COLUMN_NAME_SHIP_CITY,customer.getShippingAddress().getCity() );
            values.put(CustomerEntry.COLUMN_NAME_SHIP_ADDRESS,customer.getShippingAddress().getStreet());
            values.put(CustomerEntry.COLUMN_NAME_SHIP_STREETNO,customer.getShippingAddress().getStreetNum() );
            values.put(CustomerEntry.COLUMN_NAME_SHIP_ZIPCODE,customer.getShippingAddress().getZipCode() );
            values.put(CustomerEntry.COLUMN_NAME_SHIP_BLOCK,customer.getShippingAddress().getBlock());
            values.put(CustomerEntry.COLUMN_NAME_SHIP_BUILDING,customer.getShippingAddress().getApartment());
            int count = db.update(
                    CustomerEntry.TABLE_NAME,
                    values,
                    CustomerEntry.COLUMN_NAME_CID+" = ? ",
                    new String[]{customer.getCid()});

            return count==1?customer:null;
        });
    }

    @Override
    public DbTask<Customer> addCustomer(Customer customer) {
        return new DbTask<>("LocalDb addCustomer",()->{
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(CustomerEntry.COLUMN_NAME_CID,customer.getCid());
            values.put(CustomerEntry.COLUMN_NAME_NAME,customer.getName() );
            values.put(CustomerEntry.COLUMN_NAME_LICTRADNUM,customer.getLicTradNum() );
            values.put(CustomerEntry.COLUMN_NAME_GROUP,customer.getGroup() );
            values.put(CustomerEntry.COLUMN_NAME_TYPE,customer.getType().toString() );
            values.put(CustomerEntry.COLUMN_NAME_STATUS,customer.getStatus().toString() );
            values.put(CustomerEntry.COLUMN_NAME_PHONE1,customer.getPhone1() );
            values.put(CustomerEntry.COLUMN_NAME_PHONE2,customer.getPhone2() );
            values.put(CustomerEntry.COLUMN_NAME_CELLULAR,customer.getCellular() );
            values.put(CustomerEntry.COLUMN_NAME_EMAIL,customer.getEmail() );
            values.put(CustomerEntry.COLUMN_NAME_FAX,customer.getFax() );
            values.put(CustomerEntry.COLUMN_NAME_LOCATION,customer.getLocation()!=null?
                    convertToString(customer.getLocation()):null );
            values.put(CustomerEntry.COLUMN_NAME_COMMENTS,customer.getComments());
            values.put(CustomerEntry.COLUMN_NAME_BALANCE,customer.getBalance());
            values.put(CustomerEntry.COLUMN_NAME_SALESMAN,customer.getSalesman().getCode() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_TO_DEF,customer.getBillingAddress().getName() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_COUNTRY,customer.getBillingAddress().getCountry() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_CITY,customer.getBillingAddress().getCity() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_ADDRESS,customer.getBillingAddress().getStreet() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_STREETNO,customer.getBillingAddress().getStreetNum() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_ZIPCODE,customer.getBillingAddress().getZipCode() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_BLOCK,customer.getBillingAddress().getBlock() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_BUILDING,customer.getBillingAddress().getApartment() );
            values.put(CustomerEntry.COLUMN_NAME_BILL_LOCATION,customer.getBillingAddress().getLocation()!=null?
                    convertToString(customer.getBillingAddress().getLocation()):null);

            values.put(CustomerEntry.COLUMN_NAME_SHIP_TO_DEF,customer.getShippingAddress().getName() );
            values.put(CustomerEntry.COLUMN_NAME_SHIP_COUNTRY,customer.getShippingAddress().getCountry() );
            values.put(CustomerEntry.COLUMN_NAME_SHIP_CITY,customer.getShippingAddress().getCity() );
            values.put(CustomerEntry.COLUMN_NAME_SHIP_ADDRESS,customer.getShippingAddress().getStreet());
            values.put(CustomerEntry.COLUMN_NAME_SHIP_STREETNO,customer.getShippingAddress().getStreetNum() );
            values.put(CustomerEntry.COLUMN_NAME_SHIP_ZIPCODE,customer.getShippingAddress().getZipCode() );
            values.put(CustomerEntry.COLUMN_NAME_SHIP_BLOCK,customer.getShippingAddress().getBlock());
            values.put(CustomerEntry.COLUMN_NAME_SHIP_BUILDING,customer.getShippingAddress().getApartment());
            values.put(CustomerEntry.COLUMN_NAME_SHIP_LOCATION,customer.getShippingAddress().getLocation()!=null?
                    convertToString(customer.getShippingAddress().getLocation()):null);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(CustomerEntry.TABLE_NAME, null, values);
            return newRowId>0?customer:null;

        });
    }

    @Override
    public DbTask<List<Customer>> searchCustomers(CustomerParams params){
        return new DbTask<>("LocalDb searchCustomers",()->{
            ArrayList<String> paramsValues = new ArrayList<>();
            String sqlQuery = CustomerEntry.COLUMN_NAME_CID+ " IS NOT Null ";
            //check cid filter
            if(params.cid != null){
                paramsValues.add(params.cid);
                sqlQuery += "and (" + CustomerEntry.COLUMN_NAME_CID + " = ? ) ";
            }

            //check balance filter
            if(params.balanceMoreThen != null){
                paramsValues.add(params.balanceMoreThen+"");
                sqlQuery += "and ("+CustomerEntry.COLUMN_NAME_BALANCE + " > ? ) ";
            }

            //check types filters
            if (!params.types.isEmpty()) {
                sqlQuery+= "and (";
                for (int i = 0; i < params.types.size(); i++) {
                    sqlQuery += CustomerEntry.COLUMN_NAME_TYPE + " = ? ";
                    if (i < params.types.size() - 1)
                        sqlQuery += " or ";
                    paramsValues.add(params.types.get(i).toString());
                }
                sqlQuery += ") ";
            }

            //check customerGroups filters
            if (!params.groups.isEmpty()) {
                sqlQuery+= "and (";
                for (int i = 0; i < params.groups.size(); i++) {
                    sqlQuery += CustomerEntry.COLUMN_NAME_GROUP + " = ? ";
                    if (i < params.groups.size() - 1)
                        sqlQuery += " or ";
                    paramsValues.add(params.groups.get(i));
                }
                sqlQuery += ") ";
            }

            //check salesmen filters
            if(!params.salesmen.isEmpty()){
                sqlQuery+= "and (";
                for (int i = 0; i < params.salesmen.size(); i++) {
                    sqlQuery += CustomerEntry.COLUMN_NAME_SALESMAN + " = ? ";
                    if (i < params.salesmen.size() - 1)
                        sqlQuery += " or ";
                    paramsValues.add(params.salesmen.get(i).getCode()+"");
                }
                sqlQuery += ") ";
            }

            Cursor cursor = db.query(
                    CustomerEntry.TABLE_NAME,
                    null,//get all columns
                    sqlQuery,
                    paramsValues.toArray(new String[0]),
                    null,
                    null,
                    null);
            List<Customer> customers = new ArrayList<>();
            while (cursor.moveToNext()){
                customers.add(setCustomer(cursor,db));
            }
            return customers;
        });

    }

    @Override
    public DbTask<List<String>> getCustomerGroups(){
        return new DbTask<>("LocalDb getCustomerGroups",()-> new ArrayList<>(getCustomerGroups(db)));
    }


    public  DbTask<Integer> SaveItems(List<Item> items){
        return new DbTask<>("LocalDb SaveItems",()-> {
            db.delete(ItemEntry.TABLE_NAME, null, null);
            int added = 0;
            for (Item i : items) {
                if (saveItem(i))
                    added++;
            }
            return added;
        });
    }
    private boolean saveItem(Item item){
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_NAME_CODE ,item.getCode());
        values.put(ItemEntry.COLUMN_NAME_STATUS ,item.getStatus()== Item.Status.Active?"A":"U");
        values.put(ItemEntry.COLUMN_NAME_DESC ,item.getDescription());
        values.put(ItemEntry.COLUMN_NAME_DPRICE ,item.getDefaultPrice());
        values.put(ItemEntry.COLUMN_NAME_DCURRENCY ,item.getDefaultCurrency());
        values.put(ItemEntry.COLUMN_NAME_DH ,item.getDefaultHeight());
        values.put(ItemEntry.COLUMN_NAME_DW ,item.getDefaultWidth());
        values.put(ItemEntry.COLUMN_NAME_DL ,item.getDefaultLength());
        values.put(ItemEntry.COLUMN_NAME_COMMENTS ,item.getComments());
        values.put(ItemEntry.COLUMN_NAME_DETAILS ,item.getDetails());
        values.put(ItemEntry.COLUMN_NAME_PIC_PATH ,item.getPicPath());
        values.put(ItemEntry.COLUMN_NAME_FREE_TEXT ,item.getFreeText());
        long newRowId = db.insert(ItemEntry.TABLE_NAME, null, values);
        return newRowId>0;
    }

    @Override
    public DbTask<List<Models.Company.Item>> getActiveItems(){
        return new DbTask<>("LocalDb getActiveItems",()->{
            Cursor cursor = db.query(
                    ItemEntry.TABLE_NAME,
                    null,//get all columns
                    null,
                    null,
                    null,
                    null,
                    null);
            List<Item> items = new ArrayList<>();
            while (cursor.moveToNext()){
                items.add(setItem(cursor));
            }
            return items;
        });
    }



    @Override
    public DbTask<Boolean> updateEmployeePicture(Employee employee) {
        return new DbTask<>("LocalDb updateEmployeePicture,"+employee.getSn(),()->{
            ContentValues values = new ContentValues();
            values.put(EmployeeEntry.COLUMN_NAME_PICTURE_PATH,employee.getPicPath() );
            final  String whereQuery = EmployeeEntry.COLUMN_NAME_SN+"= ? ";
            String args[] = {employee.getSn()+""};
            long newRowId = db.update(EmployeeEntry.TABLE_NAME, values,whereQuery, args);
            return newRowId>0;
        });
    }

    private Item setItem(Cursor cursor){
        Item i = new Item(cursor.getString(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_NAME_CODE)));
        i.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_NAME_STATUS)).charAt(0)=='A'
                ? Item.Status.Active : Item.Status.Frozen).
                setDescription(cursor.getString(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_NAME_DESC))).
                setDefaultPrice(cursor.getFloat(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_NAME_DPRICE))).
                setDefaultHeight(cursor.getFloat(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_NAME_DH))).
                setDefaultWidth(cursor.getFloat(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_NAME_DW))).
                setDefaultLength(cursor.getFloat(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_NAME_DL))).
                setDefaultCurrency(cursor.getString(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_NAME_DCURRENCY))).
                setFreeText(cursor.getString(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_NAME_FREE_TEXT))).
                setPicPath(cursor.getString(cursor.getColumnIndexOrThrow(ItemEntry.COLUMN_NAME_PIC_PATH)));
        return i;
    }

    private static String COMPANY_VARS_KEY = "COMPANY_VARS_KEY";
    private static String COMPANY_CURRENCY_KEY = "COMPANY_CURRENCY_KEY";
    private static String COMPANY_NAME_KEY = "COMPANY_NAME_KEY";

    private static String COMPANY_ADDRESS_COUNTRY_KEY =     "COMPANY_ADDRESS_COUNTRY_KEY";
    private static String COMPANY_ADDRESS_CITY_KEY =        "COMPANY_ADDRESS_CITY_KEY";
    private static String COMPANY_ADDRESS_STREET_KEY =      "COMPANY_ADDRESS_STREET_KEY";
    private static String COMPANY_ADDRESS_STREETNUM_KEY =   "COMPANY_ADDRESS_STREETNUM_KEY";
    private static String COMPANY_ADDRESS_ZIPCODE_KEY =     "COMPANY_ADDRESS_ZIPCODE_KEY";
    private static String COMPANY_ADDRESS_BLOCK_KEY =       "COMPANY_ADDRESS_BLOCK_KEY";
    private static String COMPANY_ADDRESS_APARTMENT_KEY =   "COMPANY_ADDRESS_APARTMENT_KEY";
    private static String COMPANY_ADDRESS_LOCATION_KEY =    "COMPANY_ADDRESS_LOCATION_KEY";


    public  DbTask<Boolean> SaveCompanyName(String companyName){
        return new DbTask<>("LocalDb SaveCompanyName",()-> {
            SharedPreferences sharedPref = context.getSharedPreferences(COMPANY_VARS_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putString(COMPANY_NAME_KEY, companyName);
            edit.apply();
            return true;
        });
    }
    @Override
    public DbTask<String> getCompanyName(){
        return new DbTask<>("LocalDb getCompanyName",()->{
            SharedPreferences sharedPref = context.getSharedPreferences(COMPANY_VARS_KEY,Context.MODE_PRIVATE);
            return sharedPref.getString(COMPANY_NAME_KEY,null);
        });
    }

    public DbTask<Boolean> SaveCompanyAddress(Address companyAddress){
        return new DbTask<>("LocalDb SaveCompanyAddress",()-> {
            SharedPreferences sharedPref = context.getSharedPreferences(COMPANY_VARS_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putString(COMPANY_ADDRESS_COUNTRY_KEY, companyAddress.getCountry());
            edit.putString(COMPANY_ADDRESS_CITY_KEY, companyAddress.getCity());
            edit.putString(COMPANY_ADDRESS_STREET_KEY, companyAddress.getStreet());
            edit.putString(COMPANY_ADDRESS_STREETNUM_KEY, companyAddress.getStreetNum());
            edit.putString(COMPANY_ADDRESS_ZIPCODE_KEY, companyAddress.getZipCode());
            edit.putString(COMPANY_ADDRESS_BLOCK_KEY, companyAddress.getBlock());
            edit.putString(COMPANY_ADDRESS_APARTMENT_KEY, companyAddress.getApartment());
            if (companyAddress.getLocation() != null)
                edit.putString(COMPANY_ADDRESS_LOCATION_KEY, companyAddress.getLocation().toString());
            edit.apply();
            return true;
        });
    }

    @Override
    public DbTask<Address> getCompanyAddress(){
        return new DbTask<>("LocalDb getCompanyAddress",()->{
            SharedPreferences sharedPref = context.getSharedPreferences(COMPANY_VARS_KEY,Context.MODE_PRIVATE);
            String country = sharedPref.getString(COMPANY_ADDRESS_COUNTRY_KEY,null);
            String city = sharedPref.getString(COMPANY_ADDRESS_CITY_KEY,null);
            String street = sharedPref.getString( COMPANY_ADDRESS_STREET_KEY,null);
            String streetNum = sharedPref.getString(COMPANY_ADDRESS_STREETNUM_KEY,null);
            String zipCode = sharedPref.getString(COMPANY_ADDRESS_ZIPCODE_KEY,null);
            String block = sharedPref.getString(COMPANY_ADDRESS_BLOCK_KEY,null);
            String apartment = sharedPref.getString(COMPANY_ADDRESS_APARTMENT_KEY,null);
            String location = sharedPref.getString(COMPANY_ADDRESS_LOCATION_KEY,null);
            return  new Address().setCountry(country).setCity(city).
                    setBlock(block).setStreet(street).setStreetNum(streetNum).
                    setApartment(apartment).setZipCode(zipCode).
                    setLocation(location!=null?convertToLocation(location):null);
        });
    }

    public DbTask<Boolean> SaveSystemCurrency(String systemCurrency){
        return new DbTask<>("LocalDb getSystemCurrency",()-> {
            SharedPreferences sharedPref = context.getSharedPreferences(COMPANY_VARS_KEY, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putString(COMPANY_CURRENCY_KEY, systemCurrency);
            edit.apply();
            return true;
        });
    }
    @Override
    public DbTask<String> getSystemCurrency(){
        return new DbTask<>("LocalDb getSystemCurrency",()->{
            SharedPreferences sharedPref = context.getSharedPreferences(COMPANY_VARS_KEY,Context.MODE_PRIVATE);
            return sharedPref.getString(COMPANY_CURRENCY_KEY,null);
        });
    }

    public DbTask<Integer> SaveEmployees(List<Employee> employees){
        return new DbTask<>("LocalDb SaveEmployees",()-> {
            db.delete(EmployeeEntry.TABLE_NAME, null, null);
            int added = 0;
            for (Employee e : employees) {
                if (addEmployee(e))
                    added++;
            }
            return added;
        });
    }

    public boolean addEmployee(Employee employee){
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(EmployeeEntry.COLUMN_NAME_SN,employee.getSn());
        values.put(EmployeeEntry.COLUMN_NAME_FIRST_NAME,employee.getFirstName());
        values.put(EmployeeEntry.COLUMN_NAME_MIDDLE_NAME,employee.getMiddleName());
        values.put(EmployeeEntry.COLUMN_NAME_LAST_NAME,employee.getLastName());
        values.put(EmployeeEntry.COLUMN_NAME_IS_ACTIVE,employee.isActive()?"Y":"N");
        values.put(EmployeeEntry.COLUMN_NAME_GNEDER,employee.getGender()== Employee.Gender.Male?"M":"F");
        if(employee.getBirthday()!=null)
            values.put(EmployeeEntry.COLUMN_NAME_BIRTHDAT_DATE,dateFormat.format(employee.getBirthday()));
        values.put(EmployeeEntry.COLUMN_NAME_ID,employee.getId());
        values.put(EmployeeEntry.COLUMN_NAME_JOB_TITLE,employee.getJobTitle());
        if(employee.getDepartment()!=null){
            values.put(EmployeeEntry.COLUMN_NAME_DPT_CODE,employee.getDepartment().getCode());
            values.put(EmployeeEntry.COLUMN_NAME_DPT_NAME,employee.getDepartment().getName());
            values.put(EmployeeEntry.COLUMN_NAME_DPT_DEDC,employee.getDepartment().getDescription());
        }
        if(employee.getPosition()!=null){
            values.put(EmployeeEntry.COLUMN_NAME_POS_CODE,employee.getPosition().getCode());
            values.put(EmployeeEntry.COLUMN_NAME_POS_NAME,employee.getPosition().getName());
            values.put(EmployeeEntry.COLUMN_NAME_POS_DESC,employee.getPosition().getDescription());
        }

        if(employee.getManager() != null)
            values.put(EmployeeEntry.COLUMN_NAME_MANAGER_SN,employee.getManager().getSn());
        if(employee.getSalesMan() != null)
            values.put(EmployeeEntry.COLUMN_NAME_SLP_CODE,employee.getSalesMan().getCode());
        values.put(EmployeeEntry.COLUMN_NAME_HPHONE,employee.getHomePhone());
        values.put(EmployeeEntry.COLUMN_NAME_WPHONE,employee.getOfficePhone());
        values.put(EmployeeEntry.COLUMN_NAME_WCELL,employee.getWorkCellular());
        values.put(EmployeeEntry.COLUMN_NAME_FAX,employee.getFax());
        values.put(EmployeeEntry.COLUMN_NAME_WEMAIL,employee.getEmail());
        if(employee.getHomeAddress() != null){
            values.put(EmployeeEntry.COLUMN_NAME_HOME_COUNTRY,employee.getHomeAddress().getCountry());
            values.put(EmployeeEntry.COLUMN_NAME_HOME_CITY,employee.getHomeAddress().getCity());
            values.put(EmployeeEntry.COLUMN_NAME_HOME_BLOCK,employee.getHomeAddress().getBlock());
            values.put(EmployeeEntry.COLUMN_NAME_HOME_STREET,employee.getHomeAddress().getStreet());
            values.put(EmployeeEntry.COLUMN_NAME_HOME_STREETNO,employee.getHomeAddress().getStreetNum());
            values.put(EmployeeEntry.COLUMN_NAME_HOME_APARTMENT,employee.getHomeAddress().getApartment());
            values.put(EmployeeEntry.COLUMN_NAME_HOME_ZIPCODE,employee.getHomeAddress().getZipCode());
        }
        if(employee.getWorkAddress() != null) {
            values.put(EmployeeEntry.COLUMN_NAME_WORK_COUNTRY, employee.getWorkAddress().getCountry());
            values.put(EmployeeEntry.COLUMN_NAME_WORK_CITY, employee.getWorkAddress().getCountry());
            values.put(EmployeeEntry.COLUMN_NAME_WORK_BLOCK, employee.getWorkAddress().getCity());
            values.put(EmployeeEntry.COLUMN_NAME_WORK_STREET, employee.getWorkAddress().getStreet());
            values.put(EmployeeEntry.COLUMN_NAME_WORK_STREETNO, employee.getWorkAddress().getStreetNum());
            values.put(EmployeeEntry.COLUMN_NAME_WORK_APARTMENT, employee.getWorkAddress().getApartment());
            values.put(EmployeeEntry.COLUMN_NAME_WORK_ZIPCODE, employee.getWorkAddress().getZipCode());
        }
        values.put(EmployeeEntry.COLUMN_NAME_PICTURE_PATH, employee.getPicPath());

        long newRowId = db.insert(EmployeeEntry.TABLE_NAME, null, values);
        return newRowId>0;
    }

    Map<Integer, Employee> employeesMap = null;
    @Override
    public DbTask<List<Employee>> getEmployees(){
        return new DbTask<>("LocalDb getEmployees",()->{
            if(employeesMap == null || employeesMap.isEmpty()){
                Cursor cursor = db.query(
                        EmployeeEntry.TABLE_NAME,
                        null,//get all columns
                        null,
                        null,
                        null,
                        null,
                        null);
                employeesMap = new HashMap<>();
                Map<Integer,Integer> mangers = new HashMap<>();

                while (cursor.moveToNext()){
                    Employee employee = setEmployee(cursor);
                    employeesMap.put(employee.getSn(),employee);
                    mangers.put(employee.getSn(),cursor.getInt(
                            cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_MANAGER_SN)));
                }
                for(Employee employee :employeesMap.values() ){
                    Integer mangerSn =mangers.get(employee.getSn());
                    if(mangerSn!=null)
                        employee.setManager(employeesMap.get(mangerSn));
                }

            }
            return new ArrayList<>(employeesMap.values());
        });
    }

    private Employee setEmployee(Cursor cursor) {
        Address homeAdd = setEmpAddress(cursor,
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_HOME_COUNTRY),
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_HOME_CITY),
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_HOME_BLOCK),
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_HOME_STREET),
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_HOME_STREETNO),
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_HOME_APARTMENT),
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_HOME_ZIPCODE));

        Address workAdd = setEmpAddress(cursor,
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_WORK_COUNTRY),
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_WORK_CITY),
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_WORK_BLOCK),
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_WORK_STREET),
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_WORK_STREETNO),
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_WORK_APARTMENT),
                cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_WORK_ZIPCODE));


        String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_BIRTHDAT_DATE));

        Date date = null;
        if(dateTime!=null)
            try {
                date = dateFormat.parse(dateTime);
            } catch (ParseException e) {
                Log.e("Database Error", "INNER-DB Parsing ISO8601 datetime failed", e);
            }
        Department department = new Department(
                cursor.getInt(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_DPT_CODE)),
                cursor.getString( cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_DPT_NAME)),
                cursor.getString( cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_DPT_DEDC)));

        JobPosition jobPosition = new JobPosition(
                cursor.getInt(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_POS_CODE)),
                cursor.getString(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_POS_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_POS_DESC)));

        SalesMan salesMan  = getSalesmanMap(db).get(cursor.getInt(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_SLP_CODE)));

        Employee e = new Employee(cursor.getInt(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_SN)));
        e.setFirstName(cursor.getString( cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_FIRST_NAME))).
                setMiddleName(cursor.getString( cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_MIDDLE_NAME))).
                setLastName(cursor.getString( cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_LAST_NAME))).
                setActive(cursor.getString(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_IS_ACTIVE))
                        .charAt(0) =='Y').
                setGender((cursor.getString( cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_GNEDER)).
                        charAt(0)=='M'? Employee.Gender.Male: Employee.Gender.Female)).
                setBirthday(date).
                setOfficePhone(cursor.getString(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_WPHONE))).
                setHomePhone(cursor.getString(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_HPHONE))).
                setWorkCellular(cursor.getString(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_WCELL))).
                setFax(cursor.getString(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_FAX))).
                setEmail(cursor.getString(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_WEMAIL))).
                setJobTitle(cursor.getString(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_JOB_TITLE))).
                setHomeAddress(homeAdd).
                setPicPath(cursor.getString(cursor.getColumnIndexOrThrow(EmployeeEntry.COLUMN_NAME_PICTURE_PATH))).
                setWorkAddress(workAdd).
                setSalesMan(salesMan).setPosition(jobPosition).setDepartment(department);

        return e;
    }

    static private Address setEmpAddress(Cursor cursor,int countryCode,
                                         int cityCode,int blockCode,int streetCode,
                                         int streetNumberCode,int apartmentCode,
                                         int zipcodeCode) {
        Address a = new Address();
        a.setCountry(cursor.getString(countryCode));
        a.setCity(cursor.getString(cityCode));
        a.setBlock(cursor.getString(blockCode));
        a.setStreet(cursor.getString(streetCode));
        a.setStreetNum(cursor.getString(streetNumberCode));
        a.setApartment(cursor.getString(apartmentCode));
        a.setZipCode(cursor.getString(zipcodeCode));
        return a;
    }

    @Override
    public DbTask<List<SalesMan>> getSalesmen(){
        return new DbTask<>("LocalDb getSalesmen",()->
         new ArrayList<>(getSalesmanMap(db).values()));
    }


    public DbTask<Integer> SaveSalesmen(List<SalesMan> salesMEN){
        return new DbTask<>("LocalDb SaveSalesmen",()->{
            db.delete(SalesmenEntry.TABLE_NAME,null,null);
            int added = 0;
            for (SalesMan s:salesMEN) {
                if(saveSalesman(s))
                    added++;
            }
            return added;
        });
    }

    public DbTask<Integer> SaveCardGroups(List<String> groups){
        return new DbTask<>("LocalDb SaveCardGroups",()-> {
            db.delete(CustomersGroupEntry.TABLE_NAME, null, null);
            int added = 0;
            for (String s : groups) {
                if (saveGroup(s))
                    added++;
            }
            return added;
        });
    }

    public DbTask<Integer> SaveCustomers(List<Customer>customers) {
        return new DbTask<>("LocalDb SaveCustomers",()-> {
            db.delete(CustomerEntry.TABLE_NAME, null, null);
            int added = 0;
            for (Customer c : customers) {
                if (addCustomer(c).call() != null)
                    added++;
            }
            return added;
        });
    }

    private boolean saveGroup(String s) {
        ContentValues values = new ContentValues();
        values.put(CustomersGroupEntry.COLUMN_NAME_NAME ,s);
        long newRowId = db.insert(CustomersGroupEntry.TABLE_NAME, null, values);
        return newRowId>0;
    }

    private boolean saveSalesman(SalesMan salesMan){
        ContentValues values = new ContentValues();
        values.put(SalesmenEntry.COLUMN_NAME_SLP_CODE ,salesMan.getCode());
        values.put(SalesmenEntry.COLUMN_NAME_SLP_NAME ,salesMan.getName());
        values.put(SalesmenEntry.COLUMN_NAME_SLP_Mobile,salesMan.getMobile());
        values.put(SalesmenEntry.COLUMN_NAME_SLP_EMAIL ,salesMan.getEmail());
        values.put(SalesmenEntry.COLUMN_NAME_SLP_ACTIVE ,salesMan.getStatus().toString());
        long newRowId = db.insert(SalesmenEntry.TABLE_NAME, null, values);
        return newRowId>0;
    }
    @Override
    public DbTask<SapLocation> updateLocation(Customer customer){
        return new DbTask<>("LocalDb updateLocation,"+customer.getCid(),()->{
            ContentValues values = new ContentValues();
            values.put(CustomerEntry.COLUMN_NAME_LOCATION,convertToString(customer.getLocation()) );
            final  String whereQuery = CustomerEntry.COLUMN_NAME_CID+"= ? ";
            String args[] = {customer.getCid()+""};
            long newRowId = db.update(CustomerEntry.TABLE_NAME, values,whereQuery, args);
            return newRowId>0?customer.getLocation():null;
        });
    }

    private static SapLocation convertToLocation(String location){
        SapLocation loc = null;
        if(location != null ){
            String[] locStr = location.split(",");
            if (locStr.length == 2){
                try
                {
                    double latitude  = Double.parseDouble(locStr[0]);
                    double longitude  = Double.parseDouble(locStr[1]);
                    loc = new SapLocation();
                    loc.setLatitude(latitude);
                    loc.setLongitude(longitude);
                }
                catch(NumberFormatException e) {
                    //not a double
                }
            }
        }
        return loc;
    }
    private static String convertToString(SapLocation location){
        String locStr = null;
        if(location != null){
            locStr = location.getLatitude()+","+location.getLongitude();
        }
        return locStr;
    }

    private static Customer setCustomer(Cursor cursor,SQLiteDatabase db){
        Cursor cr = cursor;
        float balance = cr.getFloat(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_BALANCE));
        Address billingAddress = new Address(
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_BILL_TO_DEF)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_BILL_COUNTRY)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_BILL_CITY)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_BILL_BLOCK)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_BILL_ADDRESS)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_BILL_STREETNO)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_BILL_BUILDING)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_BILL_ZIPCODE)),
                convertToLocation(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_BILL_LOCATION))));

        Address shippingAddress = new Address(
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_SHIP_TO_DEF)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_SHIP_COUNTRY)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_SHIP_CITY)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_SHIP_BLOCK)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_SHIP_ADDRESS)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_SHIP_STREETNO)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_SHIP_BUILDING)),
                cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_SHIP_ZIPCODE)),
                convertToLocation(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_SHIP_LOCATION))));

        SalesMan s = getSalesmanMap(db).get(cr.getInt(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_SALESMAN)));

        Customer c = new Customer(cursor.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_CID)),
                        balance,billingAddress,shippingAddress);

        c.setName(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_NAME))).
                setLicTradNum(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_LICTRADNUM))).
                setGroup(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_GROUP))).
                setPhone1(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_PHONE1))).
                setPhone2(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_PHONE2))).
                setCellular(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_CELLULAR))).
                setEmail(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_EMAIL))).
                setFax(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_FAX))).
                setType(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_TYPE)).
                        equals(Customer.Type.Company.toString()) ? Customer.Type.Company:Customer.Type.Private).
                setStatus(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_STATUS)).
                        equals(Customer.Status.Valid.toString())? Customer.Status.Valid:Customer.Status.Frozen).
                setComments(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_COMMENTS))).
                setSalesman(s).
                setLocation(convertToLocation(cr.getString(cursor.getColumnIndexOrThrow(CustomerEntry.COLUMN_NAME_LOCATION))));
                return c;


    }


    private static Map<Integer,SalesMan> getSalesmanMap(SQLiteDatabase db) {
        if (salesManMap == null || salesManMap.isEmpty()) {

            Cursor cursor = db.query(
                    SalesmenEntry.TABLE_NAME,
                    null,//get all columns
                    null,
                    null,
                    null,
                    null,
                    null);
            salesManMap = new HashMap<>();
            while (cursor.moveToNext()) {
                SalesMan s = new SalesMan(
                        cursor.getInt(cursor.getColumnIndexOrThrow(SalesmenEntry.COLUMN_NAME_SLP_CODE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SalesmenEntry.COLUMN_NAME_SLP_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SalesmenEntry.COLUMN_NAME_SLP_Mobile)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SalesmenEntry.COLUMN_NAME_SLP_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SalesmenEntry.COLUMN_NAME_SLP_ACTIVE))
                                .equals(SalesMan.Status.Active.toString()) ? SalesMan.Status.Active : SalesMan.Status.NoActive);
                salesManMap.put(s.getCode(), s);
            }
        }
        return salesManMap;

    }

    private static Set<String> getCustomerGroups(SQLiteDatabase db) {
        if (customerGroups == null || customerGroups.isEmpty()){

            Cursor cursor = db.query(
                    CustomersGroupEntry.TABLE_NAME,
                    null,//get all columns
                    null,
                    null,
                    null,
                    null,
                    null);
            customerGroups = new HashSet<>();
            while(cursor.moveToNext()){
                customerGroups.add(cursor.getString(cursor.getColumnIndexOrThrow(CustomersGroupEntry.COLUMN_NAME_NAME)));
            }
        }
        return customerGroups;
    }
}
