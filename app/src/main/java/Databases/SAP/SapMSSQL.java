package Databases.SAP;

import android.util.Log;

import com.example.sean98.iam.LocaleApplication;
import com.example.sean98.iam.R;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Databases.Exceptions.DatabaseException;
import Databases.ICompanyVariablesDao;
import Databases.ICustomerDao;
import Databases.IDocumentsDao;
import Databases.util.DbTask;
import Databases.util.NamedParamStatement;
import Models.Cards.Customer;
import Models.Cards.CustomerParams;
import Models.Company.SalesMan;
import Models.Documents.Document;
import Models.Documents.FinanceRecord;
import Models.Documents.Item;
import Models.Employees.Department;
import Models.Employees.Employee;
import Models.Employees.JobPosition;
import Models.util.Address;
import Models.util.SapLocation;
import Databases.SAP.SapMSSQLHelper.*;

//


public class SapMSSQL implements ICustomerDao, IDocumentsDao, ICompanyVariablesDao {
    //connection information
    private static final String driver = LocaleApplication.applicationContext.getString(R.string.driver);
    private static final String dbHostName = LocaleApplication.applicationContext.getString(R.string.dbHostName);
    private static final String dbName = LocaleApplication.applicationContext.getString(R.string.dbName);
    private static final String dbPort = LocaleApplication.applicationContext.getString(R.string.dbPort);
    private static final String dbUserName = LocaleApplication.applicationContext.getString(R.string.dbUserName);
    private static final String dbPassword = LocaleApplication.applicationContext.getString(R.string.dbPassword);
    static final String connectionUrl = "jdbc:jtds:sqlserver://" + dbHostName + ":"+dbPort+
            ";databaseName=" + dbName +";user=" + dbUserName + ";password=" + dbPassword + ";"
            +"sendStringParametersAsUnicode=true";

    static final int TIME_OUT_SEC = 45;

    //init sql driver
    static {
        try{
            Class.forName(driver);
        } catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }

    }

    //init customer's types map
    private static final Map<Customer.Type,String> typesMap =  new HashMap<>();
    static {typesMap.put( Customer.Type.Company, "C");typesMap.put(Customer.Type.Private, "I");}

    //cache Maps - don't use directly
    private static Map<String,Integer> groupsMap = null; //getGroupMap()
    private static Map<Integer,SalesMan> salesManMap = null;//getSalesmanMap()

    private static Map<Integer, Department > departmentMap = null;
    private static Map<Integer, JobPosition> jobPositionMap = null;
    private static Map<Integer, Employee   > employeesMap = null;

    private static String CompanyNameCache = null;
    private static String SystemCurrencyCache = null;
    private static Address CompanyAddressCache = null;

    //get group's map, group's name to group key (sql number)
    private static Map<String,Integer> getGroupMap() throws DatabaseException {
        try {
            if (groupsMap == null || groupsMap.isEmpty()){
                Connection conn = DriverManager.getConnection(connectionUrl);
                NamedParamStatement st = new NamedParamStatement(conn,CustomerEntry.selectGroupsQuery);
                ResultSet result = st.setQueryTimeout(TIME_OUT_SEC).executeQuery();
                groupsMap = new HashMap<>();
                while(result.next()){
                    groupsMap.put(result.getString(CustomerEntry.GROUPNAME),
                            result.getInt(CustomerEntry.GROUPCODE));
                }
            }
        }
        catch (SQLTimeoutException se){throw getException(se);}
        catch (SQLException se){throw getException(se);}

        return groupsMap;
    }

    //get salesman list fro sql database
    private static Map<Integer,SalesMan> getSalesmanMap() throws SQLException{
        if (salesManMap == null || salesManMap.isEmpty()){
            Connection conn = DriverManager.getConnection(connectionUrl);
            NamedParamStatement st = new NamedParamStatement(conn,SalesmenEntry.selectSalesmanQuery);
            ResultSet r = st.setQueryTimeout(TIME_OUT_SEC).executeQuery();
            salesManMap = new HashMap<>();
            while(r.next()){
                SalesMan s = new SalesMan(r.getInt(SalesmenEntry.SLP_CODE),r.getString(SalesmenEntry.SLP_NAME),
                        r.getString(SalesmenEntry.SLP_Mobile),r.getString(SalesmenEntry.SLP_EMAIL),
                        r.getString(SalesmenEntry.SLP_ACTIVE)=="Y"?SalesMan.Status.Active:SalesMan.Status.NoActive);
                salesManMap.put(s.getCode(),s);
            }
        }
        return salesManMap;
    }

    private Connection conn;

    @Override
    public DbTask<Customer> getCustomer(String cid){
        return new DbTask<>("SAPdb getCustomer,"+cid,()->{
            final String sqlQuery = CustomerEntry.sqlSelectCustomerQ + "and C."+ CustomerEntry.CARDCODE+"=@cardCode@ ";
            try {
                Customer customer = null;
                if(conn == null || conn.isClosed())
                    conn = DriverManager.getConnection(connectionUrl);
                NamedParamStatement st = new NamedParamStatement(conn,sqlQuery);
                st.setString("@cardCode@", cid);
                ResultSet rs = st.executeQuery();
                if(rs.next())
                    customer = setCustomer(rs);
                rs.close();
                return  customer;
            }
            catch(SQLException se){throw getException(se);}
            //close resources
            finally{
                try{
                    if(conn!=null)
                        conn.close();
                }
                catch(SQLException se){throw getException(se);}
            }
        });
    }

    private boolean updateAddress(Address address,Customer customer,String addressType)throws DatabaseException {
        boolean rs = false;
        if(address.getName() == null)
            return true;
        try {
            if(conn == null || conn.isClosed())
                conn = DriverManager.getConnection(connectionUrl);
            NamedParamStatement st = new NamedParamStatement(conn,CustomerEntry.AddressEntry.updateAddress);
            st.setString("@cardCode@", customer.getCid());
            st.setString("@addressType@",addressType);
            st.setString("@addressCode@",address.getName());
            st.setString("@city@",address.getCity());
            st.setString("@street@",address.getStreet());
            st.setString("@block@",address.getBlock());
            st.setString("@zipCode@",address.getZipCode());
            st.setString("@country@",address.getCountry());
            st.setString("@streetNo@",address.getStreetNum());
            st.setString("@licTradNum@",customer.getLicTradNum());
            st.setString("@building@",address.getApartment());
            st.setString("@location@",convertToString(address.getLocation()));

            rs = st.execute();
        }
        catch (SQLTimeoutException se){
            Log.e("updateAddress",se.getMessage()+"");
            throw getException(se); }
        catch(SQLException se){
            Log.e("updateAddress",se.getMessage()+"");
            throw getException(se);}
        //close resources
        finally{
            try{
                if(conn!=null)
                    conn.close();
            }
            catch(SQLException se){
                throw getException(se);}
        }
        return rs;
    }

    @Override
    public DbTask<Customer> updateCustomer(Customer customer){
        return new DbTask<>("SAPdb updateCustomer,"+customer.getCid(),()-> {
            if(customer.getCid() != null) {
                try {
                    boolean rs = updateAddress(customer.getBillingAddress(), customer, "B");
                    rs &= updateAddress(customer.getShippingAddress(), customer, "S");

                    if (conn == null || conn.isClosed())
                        conn = DriverManager.getConnection(connectionUrl);
                    NamedParamStatement st = new NamedParamStatement(conn, CustomerEntry.updateCustomerQuery);
                    st.setQueryTimeout(TIME_OUT_SEC);

                    st.setString("@cardCode@", customer.getCid());
                    st.setString("@cardName@", customer.getName());
                    st.setInt("@groupCode@", getGroupMap().get(customer.getGroup()));
                    st.setString("@cmPrivate@", typesMap.get(customer.getType()));
                    st.setString("@licTradNum@", customer.getLicTradNum());
                    st.setString("@phone1@", customer.getPhone1());
                    st.setString("@phone2@", customer.getPhone2());
                    st.setString("@cellular@", customer.getCellular());
                    st.setString("@fax@", customer.getFax());
                    st.setString("@email@", customer.getEmail());
                    st.setString("@cellular@", customer.getCellular());
                    st.setString("@validFor@", customer.getStatus() == Customer.Status.Valid ? "Y" : "N");
                    st.setString("@frozenFor@", customer.getStatus() == Customer.Status.Frozen ? "Y" : "N");
                    st.setString("@freeText@", customer.getComments());
                    st.setInt("@slpCode@", customer.getSalesman().getCode());
                    st.setString("@shipToDef@", customer.getShippingAddress().getName());
                    st.setString("@shipCountry@", customer.getShippingAddress().getCountry());
                    st.setString("@shipCity@", customer.getShippingAddress().getCity());
                    st.setString("@shipAddr@", customer.getShippingAddress().getStreet());
                    st.setString("@shipStreetNO@", customer.getShippingAddress().getStreetNum());
                    st.setString("@shipZip@", customer.getShippingAddress().getZipCode());
                    st.setString("@shipBlock@", customer.getShippingAddress().getBlock());
                    st.setString("@shipBuilding@", customer.getShippingAddress().getApartment());
                    st.setString("@billToDef@", customer.getBillingAddress().getName());
                    st.setString("@billCountry@", customer.getBillingAddress().getCountry());
                    st.setString("@billCity@", customer.getBillingAddress().getCity());
                    st.setString("@billAddr@", customer.getBillingAddress().getStreet());
                    st.setString("@billStreetNO@", customer.getBillingAddress().getStreetNum());
                    st.setString("@billZip@", customer.getBillingAddress().getZipCode());
                    st.setString("@billBlock@", customer.getBillingAddress().getBlock());
                    st.setString("@billBuilding@", customer.getBillingAddress().getApartment());
                    st.setString("@location@",convertToString(customer.getLocation()));

                    rs &= st.execute();

                    // if(rs.next())
                    //customer = setCustomer(rs);
                    // rs.close();
                    return getCustomer(customer.getCid()).call();
                }
                catch (SQLTimeoutException se) {
                    throw getException(se);}
                catch (SQLException se) {
                    throw getException(se);}
                //close resources
                finally {
                    try {
                        if (conn != null)
                            conn.close();
                    } catch (SQLException se) {
                        throw getException(se);}
                }
            }
            return null;
        });
    }


    @Override
    public DbTask<Customer> addCustomer(Customer customer){
        return new DbTask<>("SAPdb addCustomer",()->{
            final String sqlSearchEmptyQuery = "select top 1 "+CustomerEntry.CARDCODE+" from "+CustomerEntry.OCRD+" where "+CustomerEntry.CARDNAME+" is Null";
            final String sqlUpdateCreateTimeQuery = "Update "+CustomerEntry.OCRD+" SET "+CustomerEntry.CREATE_DATE+"= GetDate() Where "+
                    CustomerEntry.CARDCODE+"=@cardCode@ ";
            Customer c = null;
            try {
                if(conn == null || conn.isClosed())
                    conn = DriverManager.getConnection(connectionUrl);
                PreparedStatement s =  conn.prepareStatement(sqlSearchEmptyQuery);
                s.setQueryTimeout(TIME_OUT_SEC);
                ResultSet r = s.executeQuery();
                if(r.next()){
                    String cCode = r.getString(CustomerEntry.CARDCODE);
                    c = new Customer(cCode,customer.getBalance(),
                            customer.getBillingAddress(),customer.getShippingAddress());

                    c.setName(customer.getName()).setStatus(customer.getStatus()).
                            setLicTradNum(customer.getLicTradNum()).setGroup(customer.getGroup()).
                            setType(customer.getType()).setPhone1(customer.getPhone1()).setPhone2(customer.getPhone2()).
                            setCellular(customer.getCellular()).setEmail(customer.getEmail()).setFax(customer.getFax()).
                            setSalesman(customer.getSalesman()).setComments(customer.getComments());

                    c = updateCustomer(c).call();
                    if(conn == null || conn.isClosed())
                        conn = DriverManager.getConnection(connectionUrl);

                    NamedParamStatement st = new NamedParamStatement(conn,sqlUpdateCreateTimeQuery);
                    st.setQueryTimeout(TIME_OUT_SEC);
                    st.setString("@cardCode@",cCode);
                    st.execute();
                }
                else{
                    throw new RuntimeException("NO EMPTY CUSTOMER FOUND IN SAP DB");
                }
            }
            catch(SQLException se){throw getException(se);}
            //close resources
            finally{
                try{
                    if(conn!=null)
                        conn.close();
                } catch(SQLException se){throw getException(se);}
            }
            return c;
        });
    }

    @Override
    public DbTask<List<Customer>> searchCustomers(CustomerParams params){
        return new DbTask<>("SAPdb searchCustomers",()->{
            try {
                if(conn == null || conn.isClosed())
                    conn = DriverManager.getConnection(connectionUrl);

                ArrayList paramsValues = new ArrayList();
                String sqlQuery = CustomerEntry.sqlSelectCustomerQ;

                //check cid filter
                if(params.cid != null){
                    paramsValues.add(params.cid);
                    sqlQuery += "and (" + CustomerEntry.CARDCODE + " = ? ) ";
                }

                //check balance filter
                if(params.balanceMoreThen != null){
                    paramsValues.add(params.balanceMoreThen);
                    sqlQuery += "and ("+CustomerEntry.BALANCE + " > ? ) ";
                }

                //check types filters
                if (!params.types.isEmpty()) {
                    sqlQuery+= "and (";
                    for (int i = 0; i < params.types.size(); i++) {
                        sqlQuery += CustomerEntry.CMPPRIVATE + " = ? ";
                        if (i < params.types.size() - 1)
                            sqlQuery += " or ";
                        paramsValues.add( typesMap.get(params.types.get(i)));
                    }
                    sqlQuery += ") ";
                }

                //check customerGroups filters
                if (!params.groups.isEmpty()) {
                    sqlQuery+= "and (";
                    for (int i = 0; i < params.groups.size(); i++) {
                        sqlQuery += "C."+CustomerEntry.GROUPCODE + " = ? ";
                        if (i < params.groups.size() - 1)
                            sqlQuery += " or ";
                        paramsValues.add(getGroupMap().get(params.groups.get(i)));
                    }
                    sqlQuery += ") ";
                }

                //check salesmen filters
                if(!params.salesmen.isEmpty()){
                    sqlQuery+= "and (";
                    for (int i = 0; i < params.salesmen.size(); i++) {
                        sqlQuery += "C."+CustomerEntry.SLP_CODE + " = ? ";
                        if (i < params.salesmen.size() - 1)
                            sqlQuery += " or ";
                        paramsValues.add(params.salesmen.get(i).getCode());
                    }
                    sqlQuery += ") ";
                }

                PreparedStatement st = conn.prepareStatement(sqlQuery);
                mapParams(st,paramsValues.toArray());
                ResultSet rs= st.executeQuery();
                ArrayList<Customer> customers = new ArrayList<>();
                while (rs.next())
                    customers.add(setCustomer(rs));
                rs.close();
                return customers;
            }
            catch(SQLException se){throw getException(se); }
            //close resources
            finally{
                try{
                    if(conn!=null)
                        conn.close();
                } catch(SQLException se){throw getException(se);}
            }
        });
    }
    @Override
    public DbTask<List<String>> getCustomerGroups(){
        return new DbTask<>("SAPdb getCustomerGroups",()-> new ArrayList<>(getGroupMap().keySet()));
    }

    @Override
    public DbTask<List<Models.Company.Item>> getActiveItems(){
        return new DbTask<>("SAPdb getActiveItems",()->{
            try {
                if(conn == null || conn.isClosed())
                    conn = DriverManager.getConnection(connectionUrl);
                NamedParamStatement st = new NamedParamStatement(conn,ItemEntry.selectActiveItemsQuery);
                ResultSet rs = st.executeQuery();
                List<Models.Company.Item> items = new ArrayList<>();
                while (rs.next())
                    items.add(setItem(rs));
                rs.close();
                return  items;
            }
            catch(SQLException se){throw getException(se);}
            //close resources
            finally{
                try{
                    if(conn!=null)
                        conn.close();
                }
                catch(SQLException se){throw getException(se);}
            }
        });
    }


    @Override
    public DbTask<Boolean> updateEmployeePicture(Employee employee) {
        return new DbTask<>("updateEmployeePicture "+employee.getSn(),()->{
            boolean r;
                try {
                    if(conn == null || conn.isClosed())
                        conn = DriverManager.getConnection(connectionUrl);
                    NamedParamStatement st = new NamedParamStatement(conn,EmployeeEntry.updateEmpPicQuery);
                    st.setString("@empPic@", employee.getPicPath());
                    st.setInt("@empSn@",employee.getSn());
                    r = st.execute();
                }
                catch(SQLException se){throw getException(se);}
                //close resources
                finally{
                    try{
                        if(conn!=null)
                            conn.close();
                    }
                    catch(SQLException se){throw getException(se);}
                }
            return r;

        });
    }

    @Override
    public DbTask<List<SalesMan>> getSalesmen(){
        return new DbTask<>("SAPdb getSalesmen",()->{
            try {
                return new ArrayList<>(getSalesmanMap().values());
            }catch (SQLException se ){throw getException(se);}
        });
    }

    @Override
    public DbTask<SapLocation> updateLocation(Customer customer){
        return new DbTask<>("SAPdb updateLocation,"+customer.getCid(),()->{
            boolean r = false;
            if(customer.getCid()!=null){
                try {
                    if(conn == null || conn.isClosed())
                        conn = DriverManager.getConnection(connectionUrl);

                    NamedParamStatement st = new NamedParamStatement(conn,CustomerEntry.updateLocationQuery);
                    st.setString("@cardCode@", customer.getCid());
                    st.setString("@location@",convertToString(customer.getLocation()));
                    r = st.execute();
                }
                catch(SQLException se){throw getException(se);}
                //close resources
                finally{
                    try{
                        if(conn!=null)
                            conn.close();
                    } catch(SQLException se){throw getException(se);}
                }
            }

            return r?customer.getLocation():null;
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

    private static Customer setCustomer(ResultSet sqlResult) throws SQLException{
        ResultSet rs = sqlResult;
        SalesMan s = getSalesmanMap().get(rs.getInt(CustomerEntry.SLP_CODE));

        Address billingAddress = new Address(rs.getString(CustomerEntry.BILL_TO_DEF),rs.getString(CustomerEntry.BILL_COUNTRY),
                rs.getString(CustomerEntry.BILL_CITY),rs.getString(CustomerEntry.BILL_BLOCK),rs.getString(CustomerEntry.BILL_ADDRESS),
                rs.getString(CustomerEntry.BILL_STREETNO),rs.getString(CustomerEntry.BILL_BUILDING),
                rs.getString(CustomerEntry.BILL_ZIPCODE),
                convertToLocation(rs.getString(CustomerEntry.LOCATION)));

        Address shippingAddress = new Address(rs.getString(CustomerEntry.SHIP_TO_DEF),rs.getString(CustomerEntry.SHIP_COUNTRY),
                rs.getString(CustomerEntry.SHIP_CITY),rs.getString(CustomerEntry.SHIP_BLOCK),rs.getString(CustomerEntry.SHIP_ADDRESS),
                rs.getString(CustomerEntry.SHIP_STREETNO),rs.getString(CustomerEntry.SHIP_BUILDING),
                rs.getString(CustomerEntry.SHIP_ZIPCODE),null);

        Customer c = new Customer(rs.getString(CustomerEntry.CARDCODE),rs.getBigDecimal(CustomerEntry.BALANCE).floatValue(),
                billingAddress,shippingAddress);

        c.setName(rs.getString(CustomerEntry.CARDNAME)).setLicTradNum(rs.getString(CustomerEntry.LICTRADNUM)).
                setGroup(rs.getString(CustomerEntry.GROUPNAME)).setPhone1(rs.getString(CustomerEntry.PHONE1)).
                setPhone2(rs.getString(CustomerEntry.PHONE2)).setCellular(rs.getString(CustomerEntry.CELLULAR)).
                setEmail(rs.getString(CustomerEntry.EMAIL)).setFax(rs.getString(CustomerEntry.FAX)).
                setType(rs.getString(CustomerEntry.CMPPRIVATE).charAt(0)=='C'? Customer.Type.Company:Customer.Type.Private).
                setStatus(rs.getString(CustomerEntry.VALID_FOR).charAt(0)=='Y'? Customer.Status.Valid:Customer.Status.Frozen).
                setComments(rs.getString(CustomerEntry.FREE_TEXT)).
                setSalesman(s).
                setLocation(convertToLocation(rs.getString(CustomerEntry.LOCATION)));
        return c;
    }

    private static void mapParams(PreparedStatement ps, Object... args) throws SQLException {
        int i = 1;
        for (Object arg : args) {
            if (arg instanceof Date) {
                ps.setTimestamp(i++, new Timestamp(((Date) arg).getTime()));
            } else if (arg instanceof Integer) {
                ps.setInt(i++, (Integer) arg);
            } else if (arg instanceof Long) {
                ps.setLong(i++, (Long) arg);
            } else if (arg instanceof Double) {
                ps.setDouble(i++, (Double) arg);
            } else if (arg instanceof Float) {
                ps.setFloat(i++, (Float) arg);
            } else {
                ps.setString(i++, (String) arg);
            }
        }
    }

    @Override
    public  DbTask<List<FinanceRecord>> getFinanceRecords(Customer customer){
        return new DbTask<>("SAPdb getFinanceRecords,"+customer.getCid(),()->{
            List<FinanceRecord> frList;
            try {
                if(conn == null || conn.isClosed())
                    conn = DriverManager.getConnection(connectionUrl);
                NamedParamStatement st = new NamedParamStatement(conn, FinanceRecordEntry.selectCardRecords);
                st.setString("@cardCode@", customer.getCid());
                ResultSet rs = st.setQueryTimeout(TIME_OUT_SEC).executeQuery();
                frList = new ArrayList<>();
                while(rs.next()){
                    FinanceRecord.Type type;
                    switch (rs.getInt(FinanceRecordEntry.TRANSTYPE)){
                        case 13:
                            type = FinanceRecord.Type.Invoice;
                            break;
                        case 24:
                            type = FinanceRecord.Type.Receipt;
                            break;
                        case 14:
                            type = FinanceRecord.Type.CreditInvoice;
                            break;

                        default:type = FinanceRecord.Type.Other;
                    }
                    FinanceRecord fr = new FinanceRecord(customer.getCid(),rs.getString(FinanceRecordEntry.BASEREF),
                            rs.getString(FinanceRecordEntry.REF2), rs.getDate(FinanceRecordEntry.REFDATE),
                            rs.getBigDecimal(FinanceRecordEntry.DEBIT).floatValue()
                                    -1*rs.getBigDecimal(FinanceRecordEntry.CREDIT).floatValue(),
                            rs.getBigDecimal(FinanceRecordEntry.BALDUEDEB).floatValue()
                                    -1*rs.getBigDecimal(FinanceRecordEntry.BALSCCRED).floatValue(),
                            rs.getString(FinanceRecordEntry.LINEMEMO),type);

                    frList.add(fr);
                }
                rs.close();
            }
            catch (SQLTimeoutException se) {throw getException(se);}
            catch (SQLException se){throw getException(se);}
            //close resources
            finally{
                try{
                    if(conn!=null)
                        conn.close();
                } catch(SQLException se){throw getException(se);}
            }
            return frList;
        });
    }


    private static DatabaseException getException(SQLException se){
        DatabaseException.Type type;
        switch (se.getSQLState()){
            case "08S03":
                type = DatabaseException.Type.ServerUnreachable;
                break;
            case "08S01":
                type = DatabaseException.Type.TimeOut;
                break;
            case "28000":
                type = DatabaseException.Type.LoginDetails;
                break;
            default:
                type = DatabaseException.Type.Other;
        }
        return new DatabaseException(se.getMessage(),type);
    }

    @Override
    public DbTask<String> getCompanyName(){
        return new DbTask<>("SAPdb getCompanyName",()->{
            if(CompanyNameCache == null || CompanyNameCache.isEmpty()){
                try {
                    if(conn == null || conn.isClosed())
                        conn = DriverManager.getConnection(connectionUrl);

                    ResultSet rs = conn.prepareStatement(CompanyEntry.selectName).executeQuery();
                    if(rs.next()){
                        CompanyNameCache = rs.getString(CompanyEntry.COMPANY_NAME);
                    }
                    rs.close();
                }
                catch (SQLTimeoutException se) {throw getException(se);}
                catch (SQLException se){throw getException(se);}
                //close resources
                finally{
                    try{
                        if(conn!=null)
                            conn.close();
                    } catch(SQLException se){throw getException(se);}
                }
            }

            return CompanyNameCache;
        });
    }

    @Override
    public DbTask<Address> getCompanyAddress(){
        return new DbTask<>("SAPdb getCompanyAddress",()->{
            if(CompanyAddressCache == null){
                try {
                    if(conn == null || conn.isClosed())
                        conn = DriverManager.getConnection(connectionUrl);


                    PreparedStatement st = conn.prepareStatement(CompanyEntry.selectAddress);
                    st.setQueryTimeout(TIME_OUT_SEC);
                    ResultSet rs = st.executeQuery();
                    if(rs.next()){
                        CompanyAddressCache = new Address(
                                "",
                                rs.getString(CompanyEntry.ACOMPANY_DDRESS_COUNTRY),
                                rs.getString(CompanyEntry.COMPANY_ADDRESS_CITY),
                                rs.getString(CompanyEntry.COMPANY_ADDRESS_BLOCK),
                                rs.getString(CompanyEntry.COMPANY_ADDRESS_STEEET),
                                rs.getString(CompanyEntry.COMPANY_ADDRESS_STREETNO),
                                rs.getString(CompanyEntry.COMPANY_ADDRESS_BUILDING),
                                rs.getString(CompanyEntry.COMPANY_ADDRESS_ZIPCODE),
                                convertToLocation(rs.getString(CompanyEntry.COMPANY_ADDRESS_LOCATION)));
                    }

                    rs.close();
                }
                catch (SQLTimeoutException se) {throw getException(se);}
                catch (SQLException se){throw getException(se);}
                //close resources
                finally{
                    try{
                        if(conn!=null)
                            conn.close();
                    } catch(SQLException se){throw getException(se);}
                }
            }
            return CompanyAddressCache;

        });
    }

    @Override
    public DbTask<String> getSystemCurrency(){
        return new DbTask<>("SAPdb getSystemCurrency",()->{
            if(SystemCurrencyCache == null || SystemCurrencyCache.isEmpty()){
                try {
                    if(conn == null || conn.isClosed())
                        conn = DriverManager.getConnection(connectionUrl);

                    ResultSet rs = conn.prepareStatement(CompanyEntry.selectCurrency).executeQuery();
                    if(rs.next()){
                        SystemCurrencyCache = rs.getString(CompanyEntry.SYSTEM_CURRENCY);
                    }

                    rs.close();
                }
                catch (SQLTimeoutException se) {throw getException(se);}
                catch (SQLException se){throw getException(se);}
                //close resources
                finally{
                    try{
                        if(conn!=null)
                            conn.close();
                    } catch(SQLException se){throw getException(se);}
                }
            }
            return SystemCurrencyCache;
        });
    }

    @Override
    public DbTask<List<Employee>> getEmployees(){
        return new DbTask<List<Employee>>("SAPdb getEmployees ",()->{
            if(employeesMap == null || employeesMap.isEmpty()){
                try {
                    if(conn == null || conn.isClosed())
                        conn = DriverManager.getConnection(connectionUrl);

                    if(departmentMap == null){
                        departmentMap = new HashMap<>();
                        ResultSet rs = conn.prepareStatement(EmployeeEntry.selectDepartments).executeQuery();
                        while (rs.next()){
                            Department department = new Department(rs.getInt(EmployeeEntry.DEPT_CODE),
                                    rs.getString(EmployeeEntry.DEPT_NAME),rs.getString(EmployeeEntry.DEPT_DESCRIPTION));
                            departmentMap.put(department.getCode(),department);
                        }
                        rs.close();
                    }

                    if(conn == null || conn.isClosed())
                        conn = DriverManager.getConnection(connectionUrl);
                    if(jobPositionMap == null){
                        jobPositionMap = new HashMap<>();
                        ResultSet rs = conn.prepareStatement(EmployeeEntry.selectJobPositions).executeQuery();
                        while(rs.next()){
                            JobPosition jobPosition = new JobPosition(rs.getInt(EmployeeEntry.POS_CODE),
                                    rs.getString(EmployeeEntry.POS_NAME),rs.getString(EmployeeEntry.POS_DESCRIPTION));
                            jobPositionMap.put(jobPosition.getCode(),jobPosition);
                        }
                        rs.close();
                    }


                    if(conn == null || conn.isClosed())
                        conn = DriverManager.getConnection(connectionUrl);
                    employeesMap = new HashMap<>();
                    ResultSet rs = conn.prepareStatement(EmployeeEntry.selectEmployeeQuery).executeQuery();
                    Map<Integer,Integer> mangers = new HashMap<>();
                    while(rs.next()){
                        Employee employee = setEmployee(rs);
                        employeesMap.put(employee.getSn(),employee);
                        Integer positionCode = rs.getInt(EmployeeEntry.EMP_POSITION_CODE);
                        if(positionCode!= null)
                            employee.setPosition(jobPositionMap.get(positionCode));
                        Integer departmentCode = rs.getInt(EmployeeEntry.EMP_DEPARTMENT_CODE);
                        if(departmentCode!= null)
                            employee.setDepartment(departmentMap.get(departmentCode));
                        Integer salesmanCode = rs.getInt(EmployeeEntry.EMP_SALEMAN_CODE);
                        if(salesmanCode!= null)
                            employee.setSalesMan(getSalesmanMap().get(salesmanCode));
                        mangers.put(employee.getSn(),rs.getInt(EmployeeEntry.EMP_MANAGER_SN));

                    }

                    rs.close();
                    for(Employee employee :employeesMap.values() ){
                        Integer mangerSn =mangers.get(employee.getSn());
                        if(mangerSn!=null)
                            employee.setManager(employeesMap.get(mangerSn));
                    }
                }
                catch (SQLTimeoutException se) {throw getException(se);}
                catch (SQLException se){throw getException(se);}
                //close resources
                finally{
                    try{
                        if(conn!=null)
                            conn.close();
                    } catch(SQLException se){throw getException(se);}
                }
            }
            return new ArrayList(employeesMap.values());
        });
    }

    static private Employee setEmployee(ResultSet rs) throws SQLException{
        Address homeAdd = setEmpAddress(rs,EmployeeEntry.EMP_HOME_COUNTRY,
                EmployeeEntry.EMP_HOME_CITY, EmployeeEntry.EMP_HOME_BLOCK,
                EmployeeEntry.EMP_HOME_STREET,EmployeeEntry.EMP_HOME_STREETNO,
                EmployeeEntry.EMP_HOME_BUILDING,EmployeeEntry.EMP_HOME_ZIPCODE);
        Address workAdd = setEmpAddress(rs,EmployeeEntry.EMP_WORK_COUNTRY,
                EmployeeEntry.EMP_WORK_CITY,EmployeeEntry.EMP_WORK_BLOCK,
                EmployeeEntry.EMP_WORK_STREET,EmployeeEntry.EMP_WORK_STREETNO,
                EmployeeEntry.EMP_WORK_BUILDING,EmployeeEntry.EMP_WORK_ZIPCODE);

        Employee e = new Employee(rs.getInt(EmployeeEntry.EMP_SN));
        e.setFirstName(rs.getString(EmployeeEntry.EMP_FIRST_NAME)).
                setMiddleName(rs.getString(EmployeeEntry.EMP_MIDDLE_NAME)).
                setLastName(rs.getString(EmployeeEntry.EMP_LAST_NAME)).
                setActive(rs.getString(EmployeeEntry.EMP_IS_ACTIVE).charAt(0)=='Y').
                setGender((rs.getString(EmployeeEntry.EMP_GENDER).charAt(0)=='M'? Employee.Gender.Male: Employee.Gender.Female)).
                setBirthday(rs.getDate(EmployeeEntry.EMP_BIRTHDAY)).setOfficePhone(rs.getString(EmployeeEntry.EMP_OFFICE_PHONE)).
                setHomePhone(rs.getString(EmployeeEntry.EMP_HOME_PHONE)).setWorkCellular(rs.getString(EmployeeEntry.EMP_CELLULAR)).
                setFax(rs.getString(EmployeeEntry.EMP_FAX)).setEmail(rs.getString(EmployeeEntry.EMP_EMAIL)).
                setJobTitle(rs.getString(EmployeeEntry.EMP_JOB_TITLE)).
                setPicPath(rs.getString(EmployeeEntry.EMP_PIC_PATH)).
                setHomeAddress(homeAdd).
                setWorkAddress(workAdd);
        return e;
    }

    static private Address setEmpAddress(ResultSet rs,String countryCode,
                                      String cityCode,String blockCode,String streetCode,
                                      String streetNumberCode,String apartmentCode,
                                      String zipcodeCode) throws SQLException{
        Address a = new Address();
        a.setCountry(rs.getString(countryCode));
        a.setCity(rs.getString(cityCode));
        a.setBlock(rs.getString(blockCode));
        a.setStreet(rs.getString(streetCode));
        a.setStreetNum(rs.getString(streetNumberCode));
        a.setApartment(rs.getString(apartmentCode));
        a.setZipCode(rs.getString(zipcodeCode));

        return a;
    }



    private  List<Document> getDocs(Customer customer, Document.Type type) throws DatabaseException,Exception {
        String docTable = "";
        switch (type){
            case Invoice: docTable = DocumentEntry.DOC_INVOICE_TABLE;
                break;
            case Order: docTable = DocumentEntry.DOC_ORDER_TABLE;
                break;
            case SaleQuotation:docTable = DocumentEntry.DOC_PRICE_OFFER_TABLE;
            break;
            case RevocationInvoice:docTable = DocumentEntry.DOC_REVOKED_INVOICE_TABLE;
                break;
            case ShippingCet:docTable = DocumentEntry.DOC_SHIPPIN_CERT_TABLE;
                break;
            case AdvancePayment:docTable = DocumentEntry.DOC_ADVANCE_PAYMENT_TABLE;
                break;
        }
        String sqlSelectInvoices = DocumentEntry.selectDocumentQuery+docTable+
                " Where "+ DocumentEntry.DOC_CARD_CODE+"=@cardCode@ ";

        List<Document> documents = null;
       try {
           if(conn == null || conn.isClosed())
               conn = DriverManager.getConnection(connectionUrl);

           NamedParamStatement st = new NamedParamStatement(conn,sqlSelectInvoices);
           st.setString("@cardCode@",customer.getCid());
           ResultSet rs = st.setQueryTimeout(TIME_OUT_SEC).executeQuery();
           documents = new ArrayList<>();
           List<Integer> salesmanCodes = new ArrayList<>();
           List<Integer> ownersCodes = new ArrayList<>();

           while(rs.next()){
               Document doc = setDocument(rs,type);
               doc.setCustomer(customer);
               salesmanCodes.add(rs.getInt( DocumentEntry.DOC_SALESMAN_CODE));
               ownersCodes.add(rs.getInt( DocumentEntry.DOC_OWNER_CODE));
               documents.add(doc);
           }

           for(int i = 0 ; i < documents.size();i++){
               documents.get(i).setSalesMan(getSalesmanMap().get(salesmanCodes.get(i)));
               documents.get(i).setOwner(getEmployees().call().get(ownersCodes.get(i)));
           }
           rs.close();
       }
       catch (SQLTimeoutException se) {
           throw getException(se);}
       catch (SQLException se){
           throw getException(se);}

       //close resources
       finally{
           try{
               if(conn!=null)
                   conn.close();
           } catch(SQLException se) {
               throw getException(se);
           }

       }
    return documents;
    }

    private static Document setDocument(ResultSet rs, Document.Type type) throws SQLException{
        Document d = new Document(rs.getInt(DocumentEntry.DOC_SN));
        d.setCardName(rs.getString(DocumentEntry.DOC_CARD_NAME)).
                setParallelSn(rs.getString(DocumentEntry.DOC_PARALLEL_SN)).
                setAddress(rs.getString(DocumentEntry.DOC_ADDRESS)).
                setDocDate(rs.getDate(DocumentEntry.DOC_DATE)).
                setDocDueDate(rs.getDate(DocumentEntry.DOC_DUE_DATE)).
                setDocTaxDate(rs.getDate(DocumentEntry.DOC_TAX_DATE)).
                setVatPercent(rs.getBigDecimal(DocumentEntry.DOC_VAT_PERCENT).floatValue()).
                setVatSum(rs.getBigDecimal(DocumentEntry.DOC_VAT_SUM).floatValue()).
                setTotal(rs.getBigDecimal(DocumentEntry.DOC_TOTAL).floatValue()).
                setPaidSum(rs.getBigDecimal(DocumentEntry.DOC_PAID_SUM).floatValue()).
                setDiscountPercent(rs.getBigDecimal(DocumentEntry.DOC_DISCOUNT_PERCENT).floatValue()).
                setDiscountSum(rs.getBigDecimal(DocumentEntry.DOC_DISCOUNT_SUM).floatValue()).
                setLicTradNum(rs.getString(CustomerEntry.LICTRADNUM)).
                setComments(rs.getString(DocumentEntry.DOC_COMMENTS)).
                setUpdateDate(rs.getDate(DocumentEntry.DOC_UPDATE_DATE)).
                setCurrency(rs.getString(DocumentEntry.DOC_CURRENCY)).setType(type).
                setChargedSum(rs.getBigDecimal(DocumentEntry.DOC_CHARGED_SUM).floatValue()).
                setIsCanceled(rs.getString(DocumentEntry.IS_CANCELED).charAt(0) =='Y').
                setIsClosed(rs.getString(DocumentEntry.DOC_STATUS).charAt(0) =='C');
        return d;
    }

    private DbTask<List<Document>>  getDocuments(Customer customer, Document.Type type){
        return new DbTask<>("SAPdb getDocuments:"+type.name(),()->getDocs(customer,type));
    }

    @Override
    public DbTask<List<Document>> getInvoices(Customer customer)  {
        return getDocuments(customer, Document.Type.Invoice);
    }

    @Override
    public DbTask<List<Document>> getOrders(Customer customer){
        return getDocuments(customer, Document.Type.Order);
    }

    @Override
    public DbTask<List<Document>> getOffers(Customer customer) {
        return getDocuments(customer, Document.Type.SaleQuotation);
    }

    @Override
    public DbTask<List<Document>> getRevokedInvoices(Customer customer) {
        return getDocuments(customer, Document.Type.RevocationInvoice);
    }

    @Override
    public DbTask<List<Models.Documents.Item>> getDocumentItems(Document document){
        return new DbTask<>("getDocumentItems "+document.getSn(),()-> {
            String itemTableName = null;
            String docTableName = null;
            if(document.getType() == Document.Type.Invoice){
                docTableName = DocumentEntry.DOC_INVOICE_TABLE;
                itemTableName = DocumentEntry.ItemEntry.ITEM_INVOICE_TABLE;
            }else if(document.getType() == Document.Type.Order){
                docTableName = DocumentEntry.DOC_ORDER_TABLE;
                itemTableName = DocumentEntry.ItemEntry.ITEM_ORDER_TABLE;
            }else if(document.getType() == Document.Type.RevocationInvoice){
                docTableName = DocumentEntry.DOC_REVOKED_INVOICE_TABLE;
                itemTableName = DocumentEntry.ItemEntry.ITEM_REVOKED_INVOICE_TABLE;
            }else if(document.getType() == Document.Type.AdvancePayment){
                docTableName = DocumentEntry.DOC_ADVANCE_PAYMENT_TABLE;
                itemTableName = DocumentEntry.ItemEntry.ITEM_ADVANCE_PAYMENT_TABLE;
            }else if(document.getType() == Document.Type.SaleQuotation){
                docTableName = DocumentEntry.DOC_PRICE_OFFER_TABLE;
                itemTableName = DocumentEntry.ItemEntry.ITEM_PRICE_OFFER_TABLE;
            }else if(document.getType() == Document.Type.ShippingCet){
                docTableName = DocumentEntry.DOC_SHIPPIN_CERT_TABLE;
                itemTableName = DocumentEntry.ItemEntry.ITEM_SHIPPIN_CERT_TABLE;
            }

            String sqlQuery = DocumentEntry.ItemEntry.selectItemsQuery +itemTableName + " where "+DocumentEntry.ItemEntry.ITM_DOC_ENTRY+
                    " in (Select "+DocumentEntry.DOC_ENTRY+
                    " from "+docTableName+" where "+DocumentEntry.DOC_SN+"=@docSN@)";

            List<Models.Documents.Item> items = null;
            try {
                if(conn == null || conn.isClosed())
                    conn = DriverManager.getConnection(connectionUrl);

                NamedParamStatement st = new NamedParamStatement(conn,sqlQuery);
                st.setInt("@docSN@",document.getSn());
                ResultSet rs = st.setQueryTimeout(TIME_OUT_SEC).executeQuery();
                items = new ArrayList<>();

                while(rs.next()){
                    Item doc = setDocItem(rs);
                    items.add(doc);
                }

                rs.close();
            }
            catch (SQLTimeoutException se) {
                throw getException(se);}
            catch (SQLException se){
                throw getException(se);}
            //close resources
            finally{
                try{
                    if(conn!=null)
                        conn.close();
                } catch(SQLException se) {
                    throw getException(se);
                }
            }
            return items;
        });
    }

    private static Models.Documents.Item setDocItem(ResultSet rs) throws SQLException {
        Models.Documents.Item i = new Item(rs.getInt(DocumentEntry.ItemEntry.ITM_NUM));
        i.setCode(rs.getString(DocumentEntry.ItemEntry.ITM_CODE)).
                setStatus(rs.getString(DocumentEntry.ItemEntry.ITM_STATUS).charAt(0)=='O'
                        ? Models.Documents.Item.Status.Open: Models.Documents.Item.Status.Close).
                setDescription(rs.getString(DocumentEntry.ItemEntry.ITM_DESCRIPTION)).
                setQuantity(rs.getBigDecimal(DocumentEntry.ItemEntry.ITM_QUANTITY).floatValue()).
                setPrice(rs.getBigDecimal(DocumentEntry.ItemEntry.ITM_PRICE).floatValue()).
                setTotalPrice(rs.getBigDecimal(DocumentEntry.ItemEntry.ITM_TOTAL_PRICE).floatValue()).
                setCurrency(rs.getString(DocumentEntry.ItemEntry.ITM_CURRENCY)).
                setComments(rs.getString(DocumentEntry.ItemEntry.ITM_COMMENTS)).
                setDetails(rs.getString(DocumentEntry.ItemEntry.ITM_DETAILS)).
                setHeight(rs.getBigDecimal(DocumentEntry.ItemEntry.ITM_HEIGHT).floatValue()).
                setWidth(rs.getBigDecimal(DocumentEntry.ItemEntry.ITM_WIDTH).floatValue()).
                setLength(rs.getBigDecimal(DocumentEntry.ItemEntry.ITM_LENGTH).floatValue()).
                setUnits(rs.getInt(DocumentEntry.ItemEntry.ITM_UNITS));
        return  i;
    }

    private static Models.Company.Item setItem(ResultSet rs)throws SQLException  {
        Models.Company.Item i = new Models.Company.Item(rs.getString(ItemEntry.ITMS_CODE));
        i.setStatus(rs.getString(ItemEntry.ITMS_VALID).charAt(0)=='Y'
                ? Models.Company.Item.Status.Active : Models.Company.Item.Status.Frozen).
                setDescription(rs.getString(ItemEntry.ITMS_NAME)).
                setDefaultPrice(rs.getBigDecimal(ItemEntry.ITMS_DFT_PRICE)!=null?rs.getBigDecimal(ItemEntry.ITMS_DFT_PRICE).floatValue():0).
                setDefaultHeight(rs.getBigDecimal(ItemEntry.ITMS_DFT_H)!=null?rs.getBigDecimal(ItemEntry.ITMS_DFT_H).floatValue():0).
                setDefaultWidth(rs.getBigDecimal(ItemEntry.ITMS_DFT_W)!=null?rs.getBigDecimal(ItemEntry.ITMS_DFT_W).floatValue():0).
                setDefaultLength(rs.getBigDecimal(ItemEntry.ITMS_DFT_L)!=null?rs.getBigDecimal(ItemEntry.ITMS_DFT_L).floatValue():0).
                setDefaultCurrency(rs.getString(ItemEntry.ITMS_DFT_CURR)).
                setFreeText(rs.getString(ItemEntry.ITMS_FREE_TEXT)).
                setPicPath(rs.getString(ItemEntry.ITMS_PIC_PATH));
        return i;
    }

}






