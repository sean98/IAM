package Databases.Local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import androidx.annotation.Nullable;


class localSQLiteDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    static final int DATABASE_VERSION = 6;
    static final String DATABASE_NAME = "IAMdb.db";


    public localSQLiteDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CustomerEntry.SQL_CREATE_ENTRIES);
        db.execSQL(SalesmenEntry.SQL_CREATE_ENTRIES);
        db.execSQL(CustomersGroupEntry.SQL_CREATE_ENTRIES);
        db.execSQL(EmployeeEntry.SQL_CREATE_ENTRIES);
        db.execSQL(ItemEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(CustomerEntry.SQL_DELETE_ENTRIES);
        db.execSQL(SalesmenEntry.SQL_DELETE_ENTRIES);
        db.execSQL(CustomersGroupEntry.SQL_DELETE_ENTRIES);
        db.execSQL(EmployeeEntry.SQL_DELETE_ENTRIES);
        db.execSQL(ItemEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }



    static class CustomerEntry implements BaseColumns {
        static final String TABLE_NAME =                     "Customers";
        static final String COLUMN_NAME_CID =                "cid";
        static final String COLUMN_NAME_NAME =               "name";
        static final String COLUMN_NAME_LICTRADNUM =         "licTradNum";
        static final String COLUMN_NAME_GROUP =              "customerGroup";
        static final String COLUMN_NAME_TYPE =               "type";
        static final String COLUMN_NAME_STATUS =             "status";
        static final String COLUMN_NAME_PHONE1 =             "phone1";
        static final String COLUMN_NAME_PHONE2 =             "phone2";
        static final String COLUMN_NAME_CELLULAR =           "cellular";
        static final String COLUMN_NAME_EMAIL =              "email";
        static final String COLUMN_NAME_FAX =                "fax";
        static final String COLUMN_NAME_LOCATION =           "location";
        static final String COLUMN_NAME_BALANCE =            "balance";
        static final String COLUMN_NAME_SALESMAN =           "salesman";
        static final String COLUMN_NAME_COMMENTS =           "comments";

        //default billing Address info
        static final String COLUMN_NAME_BILL_TO_DEF =        "BillToDef";//default billing address
        static final String COLUMN_NAME_BILL_COUNTRY =       "Country";
        static final String COLUMN_NAME_BILL_CITY =          "City";
        static final String COLUMN_NAME_BILL_ADDRESS =       "Address";
        static final String COLUMN_NAME_BILL_STREETNO =      "StreetNo";
        static final String COLUMN_NAME_BILL_ZIPCODE =       "ZipCode";
        static final String COLUMN_NAME_BILL_BLOCK =         "Block";
        static final String COLUMN_NAME_BILL_BUILDING =      "Building";
        static final String COLUMN_NAME_BILL_LOCATION =      "BLocation";

        //default shipping Address info
        static final String COLUMN_NAME_SHIP_TO_DEF =        "ShipToDef";//default shipping address
        static final String COLUMN_NAME_SHIP_COUNTRY =       "MailCounty";
        static final String COLUMN_NAME_SHIP_CITY =          "MailCity";
        static final String COLUMN_NAME_SHIP_ADDRESS =       "MailAddres";
        static final String COLUMN_NAME_SHIP_STREETNO =      "MailStrNo";
        static final String COLUMN_NAME_SHIP_ZIPCODE =       "MailZipCod";
        static final String COLUMN_NAME_SHIP_BLOCK =         "MailBlock";
        static final String COLUMN_NAME_SHIP_BUILDING =      "MailBuildi";
        static final String COLUMN_NAME_SHIP_LOCATION =      "MLocation";




        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_CID+ " TEXT PRIMARY KEY,"+
                        COLUMN_NAME_NAME+ " TEXT,"+
                        COLUMN_NAME_LICTRADNUM+ " TEXT,"+
                        COLUMN_NAME_GROUP+ " TEXT,"+
                        COLUMN_NAME_TYPE+ " TEXT,"+
                        COLUMN_NAME_STATUS+ " TEXT,"+
                        COLUMN_NAME_PHONE1+ " TEXT,"+
                        COLUMN_NAME_PHONE2+ " TEXT,"+
                        COLUMN_NAME_CELLULAR+ " TEXT,"+
                        COLUMN_NAME_EMAIL+ " TEXT,"+
                        COLUMN_NAME_FAX+ " TEXT,"+
                        COLUMN_NAME_BALANCE+ " REAL,"+
                        COLUMN_NAME_SALESMAN+ " TEXT,"+
                        COLUMN_NAME_COMMENTS +  " TEXT,"+
                        COLUMN_NAME_LOCATION+ " TEXT,"+
                        COLUMN_NAME_SHIP_TO_DEF+ " TEXT,"+
                        COLUMN_NAME_SHIP_COUNTRY+ " TEXT,"+
                        COLUMN_NAME_SHIP_CITY+ " TEXT,"+
                        COLUMN_NAME_SHIP_ADDRESS+ " TEXT,"+
                        COLUMN_NAME_SHIP_STREETNO+ " TEXT,"+
                        COLUMN_NAME_SHIP_ZIPCODE+ " TEXT,"+
                        COLUMN_NAME_SHIP_BLOCK+ " TEXT,"+
                        COLUMN_NAME_SHIP_BUILDING+ " TEXT,"+
                        COLUMN_NAME_BILL_TO_DEF + " TEXT,"+
                        COLUMN_NAME_BILL_COUNTRY + " TEXT,"+
                        COLUMN_NAME_BILL_CITY + " TEXT,"+
                        COLUMN_NAME_BILL_ADDRESS + " TEXT,"+
                        COLUMN_NAME_BILL_STREETNO + " TEXT,"+
                        COLUMN_NAME_BILL_ZIPCODE + " TEXT,"+
                        COLUMN_NAME_BILL_BLOCK + " TEXT,"+
                        COLUMN_NAME_SHIP_LOCATION+ " TEXT,"+
                        COLUMN_NAME_BILL_LOCATION+ " TEXT,"+
                        COLUMN_NAME_BILL_BUILDING + " TEXT)";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + CustomerEntry.TABLE_NAME;


    }

    class SalesmenEntry {
        static final String TABLE_NAME = "Salsman";
        static final String COLUMN_NAME_SLP_CODE =   "SlpCode";
        static final String COLUMN_NAME_SLP_NAME =   "SlpName";
        static final String COLUMN_NAME_SLP_Mobile = "Mobil";
        static final String COLUMN_NAME_SLP_EMAIL =  "Email";
       // public static final String COLUMN_NAME_SLP_FAX =  "FAX";
       static final String COLUMN_NAME_SLP_ACTIVE = "Active";


        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + SalesmenEntry.TABLE_NAME + " (" +
                    COLUMN_NAME_SLP_CODE + " INTEGER PRIMARY KEY,"+
                    COLUMN_NAME_SLP_NAME + " TEXT,"+
                    COLUMN_NAME_SLP_Mobile + " TEXT,"+
                    COLUMN_NAME_SLP_EMAIL + " TEXT,"+
                    //COLUMN_NAME_SLP_FAX + " TEXT,"+
                    COLUMN_NAME_SLP_ACTIVE + " TEXT)";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + SalesmenEntry.TABLE_NAME;

    }

    class CustomersGroupEntry {
        static final String TABLE_NAME = "Groups";
        static final String COLUMN_NAME_NAME =   "SlpCode";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_NAME + " TEXT PRIMARY KEY)";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    class EmployeeEntry{
        static final String TABLE_NAME =                "Employees";
        static final String COLUMN_NAME_SN =            "sn";
        static final String COLUMN_NAME_FIRST_NAME =    "firstName";
        static final String COLUMN_NAME_MIDDLE_NAME =   "middleName";
        static final String COLUMN_NAME_LAST_NAME =     "lastName";
        static final String COLUMN_NAME_IS_ACTIVE =     "isActive";
        static final String COLUMN_NAME_GNEDER =        "gender";
        static final String COLUMN_NAME_BIRTHDAT_DATE = "birthday";
        static final String COLUMN_NAME_ID =            "id";
        static final String COLUMN_NAME_JOB_TITLE =     "jobTitle";
        static final String COLUMN_NAME_DPT_CODE =      "departmentCode";
        static final String COLUMN_NAME_DPT_NAME =      "departmentName";
        static final String COLUMN_NAME_DPT_DEDC =      "departmentDescription";
        static final String COLUMN_NAME_POS_CODE =      "positionCode";
        static final String COLUMN_NAME_POS_NAME =      "positionName";
        static final String COLUMN_NAME_POS_DESC =      "positionDesc";
        static final String COLUMN_NAME_MANAGER_SN =     "managerSn";
        static final String COLUMN_NAME_SLP_CODE =       "salesManCode";
        static final String COLUMN_NAME_HPHONE =         "homePhone";
        static final String COLUMN_NAME_WPHONE=          "officePhone";
        static final String COLUMN_NAME_WCELL =          "workCellular";
        static final String COLUMN_NAME_FAX =            "fax";
        static final String COLUMN_NAME_WEMAIL =         "email";
        static final String COLUMN_NAME_HOME_COUNTRY =   "HCounty";
        static final String COLUMN_NAME_HOME_CITY =      "HCity";
        static final String COLUMN_NAME_HOME_BLOCK =     "HBlock";
        static final String COLUMN_NAME_HOME_STREET =    "Hstreet";
        static final String COLUMN_NAME_HOME_STREETNO =  "HstreetNo";
        static final String COLUMN_NAME_HOME_APARTMENT = "Hapartment";
        static final String COLUMN_NAME_HOME_ZIPCODE =   "HZipCode";
        static final String COLUMN_NAME_WORK_COUNTRY =   "WCounty";
        static final String COLUMN_NAME_WORK_CITY =      "WCity";
        static final String COLUMN_NAME_WORK_BLOCK =     "WBlock";
        static final String COLUMN_NAME_WORK_STREET =    "Wstreet";
        static final String COLUMN_NAME_WORK_STREETNO =  "WstreetNo";
        static final String COLUMN_NAME_WORK_APARTMENT = "Wapartment";
        static final String COLUMN_NAME_WORK_ZIPCODE =   "WZipCode";
        static final String COLUMN_NAME_PICTURE_PATH =   "picture";


        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_SN+" INTEGER PRIMARY KEY,"+
                        COLUMN_NAME_FIRST_NAME+" TEXT,"+
                        COLUMN_NAME_MIDDLE_NAME+" TEXT,"+
                        COLUMN_NAME_LAST_NAME+" TEXT,"+
                        COLUMN_NAME_IS_ACTIVE+" INTEGER,"+
                        COLUMN_NAME_GNEDER+" TEXT,"+
                        COLUMN_NAME_BIRTHDAT_DATE+" TEXT,"+
                        COLUMN_NAME_ID+" TEXT,"+
                        COLUMN_NAME_JOB_TITLE+" TEXT,"+
                        COLUMN_NAME_DPT_CODE+" TEXT,"+
                        COLUMN_NAME_DPT_NAME+" TEXT,"+
                        COLUMN_NAME_DPT_DEDC+" TEXT,"+
                        COLUMN_NAME_POS_CODE +" INTEGER,"+
                        COLUMN_NAME_POS_NAME+" TEXT,"+
                        COLUMN_NAME_POS_DESC+" TEXT,"+
                        COLUMN_NAME_MANAGER_SN+" TEXT,"+
                        COLUMN_NAME_SLP_CODE+" TEXT,"+
                        COLUMN_NAME_HPHONE+" TEXT,"+
                        COLUMN_NAME_WPHONE+" TEXT,"+
                        COLUMN_NAME_WCELL+" TEXT,"+
                        COLUMN_NAME_FAX+" TEXT,"+
                        COLUMN_NAME_WEMAIL+" TEXT,"+
                        COLUMN_NAME_HOME_COUNTRY+" TEXT,"+
                        COLUMN_NAME_HOME_CITY+" TEXT,"+
                        COLUMN_NAME_HOME_BLOCK+" TEXT,"+
                        COLUMN_NAME_HOME_STREET+" TEXT,"+
                        COLUMN_NAME_HOME_STREETNO+" TEXT,"+
                        COLUMN_NAME_HOME_APARTMENT+" TEXT,"+
                        COLUMN_NAME_HOME_ZIPCODE+" TEXT,"+
                        COLUMN_NAME_WORK_COUNTRY+" TEXT,"+
                        COLUMN_NAME_WORK_CITY+" TEXT,"+
                        COLUMN_NAME_WORK_BLOCK+" TEXT,"+
                        COLUMN_NAME_WORK_STREET+" TEXT,"+
                        COLUMN_NAME_WORK_STREETNO+" TEXT,"+
                        COLUMN_NAME_WORK_APARTMENT+" TEXT,"+
                        COLUMN_NAME_WORK_ZIPCODE+" TEXT,"+
                        COLUMN_NAME_PICTURE_PATH+" TEXT)";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    class ItemEntry {
        static final String TABLE_NAME = "Items";
        static final String COLUMN_NAME_CODE =       "code";
        static final String COLUMN_NAME_STATUS =     "status";
        static final String COLUMN_NAME_DESC =       "description";
        static final String COLUMN_NAME_DPRICE =     "defaultPrice";
        static final String COLUMN_NAME_DCURRENCY =  "defaultCurrency";
        static final String COLUMN_NAME_COMMENTS =   "comments";
        static final String COLUMN_NAME_DETAILS =    "details";
        static final String COLUMN_NAME_DH =         "defaultHeight";
        static final String COLUMN_NAME_DW =         "defaultWidth";
        static final String COLUMN_NAME_DL =         "defaultLength";
        static final String COLUMN_NAME_PIC_PATH =   "picPath";
        static final String COLUMN_NAME_FREE_TEXT =  "freeText";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_CODE +" TEXT PRIMARY KEY,"+
                        COLUMN_NAME_STATUS +" TEXT,"+
                        COLUMN_NAME_DESC +" TEXT,"+
                        COLUMN_NAME_DPRICE +" TEXT,"+
                        COLUMN_NAME_DCURRENCY +" TEXT,"+
                        COLUMN_NAME_COMMENTS +" TEXT,"+
                        COLUMN_NAME_DETAILS +" TEXT,"+
                        COLUMN_NAME_DH +" TEXT,"+
                        COLUMN_NAME_DW +" TEXT,"+
                        COLUMN_NAME_DL +" TEXT,"+
                        COLUMN_NAME_PIC_PATH +" TEXT,"+
                        COLUMN_NAME_FREE_TEXT +" TEXT)";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }



}
