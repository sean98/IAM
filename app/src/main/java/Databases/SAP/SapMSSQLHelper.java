package Databases.SAP;

abstract class SapMSSQLHelper {

    class SalesmenEntry{
        static final String OSLP = "OSLP";//sales men information table

        //sql column's names - OSLP table
        static final String SLP_NAME = "SlpName";
        static final String SLP_Mobile = "Mobil";
        static final String SLP_FAX = "Fax";
        static final String SLP_EMAIL = "Email";
        static final String SLP_CODE = "SlpCode";
        static final String SLP_ACTIVE = "Active";//"Y" = active, "N" = NoActive


        static final String selectSalesmanQuery = "Select S."+SLP_NAME+",S."+SLP_CODE+",S."+SLP_Mobile+
                ",S."+SLP_EMAIL +",S."+SLP_FAX+
                ",S."+SLP_ACTIVE + " from "+OSLP+" as S ";

    }

    class CustomerEntry {
        //sql table's names
        static final String OCRD = "OCRD";//card's main table
        static final String OCRG = "OCRG";//card's customerGroups table
        //sql column's names - OCRD table
        static final String CARDCODE = "CardCode";
        static final String CARDNAME = "CardName";
        static final String CMPPRIVATE = "CmpPrivate"; // 'C' = company, 'I' =
        static final String LICTRADNUM = "LicTradNum";
        //Contact's attributes
        static final String PHONE1 = "Phone1";
        static final String PHONE2 = "Phone2";
        static final String CELLULAR = "Cellular";
        static final String EMAIL = "E_Mail";
        static final String FAX = "Fax";
        //default billing Address info
        static final String BILL_TO_DEF = "BillToDef";//default billing address
        static final String BILL_COUNTRY = "Country";
        static final String BILL_CITY = "City";
        static final String BILL_ADDRESS = "Address";
        static final String BILL_STREETNO = "StreetNo";
        static final String BILL_ZIPCODE = "ZipCode";
        static final String BILL_BLOCK = "Block";
        static final String BILL_BUILDING = "Building";
        //default shipping Address info
        static final String SHIP_TO_DEF = "ShipToDef";//default shipping address
        static final String SHIP_COUNTRY = "MailCounty";
        static final String SHIP_CITY = "MailCity";
        static final String SHIP_ADDRESS = "MailAddres";
        static final String SHIP_STREETNO = "MailStrNo";
        static final String SHIP_ZIPCODE = "MailZipCod";
        static final String SHIP_BLOCK = "MailBlock";
        static final String SHIP_BUILDING = "MailBuildi";
        //other attributes
        static final String CARDTYPE = "CardType";//'C' = customer, S = supplier ,L = lea
        static final String BALANCE = "Balance";
        static final String VALID_FOR = "validFor"; //'Y' = Valid , 'N' = Frozen
        static final String FROZEN_FOR = "frozenFor";//'Y' = Frozen , 'N' = Valid
        static final String FREE_TEXT = "Free_Text";
        static final String UPDATE_DATE = "UpdateDate";
        static final String CREATE_DATE = "CreateDate";
        static final String LOCATION = "GlblLocNum";
        //sql column's names - OCRG table
        static final String GROUPNAME = "GroupName";
        static final String GROUPCODE = "GroupCode";


        static final String SLP_CODE = "SlpCode";


        static final String sqlSelectCustomerQ =
                "SELECT " +
                " C." + CARDCODE + ",C." + CARDNAME + ",C." + CARDTYPE + ",C." + GROUPCODE + ",G." + GROUPNAME +
                ",C." + CMPPRIVATE + ",C." + LICTRADNUM + ",C." + BALANCE +
                ",C." + PHONE1 + ",C." + PHONE2 + ",C." + CELLULAR + ",C." + FAX + ",C." + EMAIL +
                ",C." + BILL_COUNTRY + ",C." + BILL_CITY + ",C." + BILL_ADDRESS + ",C." + BILL_STREETNO + ",C." + BILL_ZIPCODE +
                ",C." + BILL_TO_DEF + ",C." + BILL_BLOCK + ",C." + BILL_BUILDING +
                ",C." + SHIP_COUNTRY + ",C." + SHIP_CITY + ",C." + SHIP_ADDRESS + ",C." + SHIP_STREETNO + ",C." + SHIP_ZIPCODE +
                ",C." + SHIP_TO_DEF + ",C." + SHIP_BLOCK + ",C." + SHIP_BUILDING +
                ",C." + VALID_FOR + ",C." + FROZEN_FOR + " " +
                ",C." + SLP_CODE + ",C." + LOCATION + ",C." + FREE_TEXT + " " +
                "from " + OCRD + " as C," + OCRG + " as G where G." + GROUPCODE + " = C." + GROUPCODE + " and " +
                "(" + CARDTYPE + "= 'C' or " + CARDTYPE + "= 'L') and " +
                CARDNAME + " is not Null and  LEN(LTRIM(RTRIM(" + CARDNAME + "))) > 0 ";



        static final String updateCustomerQuery =
                "UPDATE "+OCRD+" SET "+
                CARDNAME+"= @cardName@,"+GROUPCODE+"= @groupCode@,"+
                CMPPRIVATE+"= @cmPrivate@,"+LICTRADNUM+"= @licTradNum@,"+
                PHONE1+"= @phone1@,"+PHONE2+"= @phone2@,"+CELLULAR+"= @cellular@,"+
                FAX+"= @fax@,"+ EMAIL+"= @email@," +SLP_CODE+"= @slpCode@," +
                BILL_TO_DEF+"=@billToDef@,"+BILL_COUNTRY+"=@billCountry@,"+BILL_CITY+"=@billCity@,"+
                BILL_ADDRESS+"=@billAddr@,"+BILL_STREETNO+"=@billStreetNO@,"+BILL_ZIPCODE+"=@billZip@,"+
                BILL_BLOCK+"=@billBlock@,"+BILL_BUILDING+"=@billBuilding@,"+
                SHIP_TO_DEF+"=@shipToDef@,"+SHIP_COUNTRY+"=@shipCountry@,"+SHIP_CITY+"=@shipCity@,"+
                SHIP_ADDRESS+"=@shipAddr@,"+SHIP_STREETNO+"=@shipStreetNO@,"+SHIP_ZIPCODE+"=@shipZip@,"+
                SHIP_BLOCK+"=@shipBlock@,"+SHIP_BUILDING+"=@shipBuilding@,"+LOCATION+"=@location@,"+
                VALID_FOR+"= @validFor@,"+FROZEN_FOR+"= @frozenFor@,"+
                FREE_TEXT+"=@freeText@,"+UPDATE_DATE+"= GetDate() "+
                "Where "+CARDCODE+"= @cardCode@";



        static final String selectGroupsQuery = "Select G."+GROUPNAME+",G."+GROUPCODE+
                    " from "+OCRG+" as G ";


        static final String updateLocationQuery = "UPDATE "+OCRD+" SET "+LOCATION+"=@location@,"+
                UPDATE_DATE+"= GetDate() Where "+CARDCODE+"=@cardCode@ ";

        class AddressEntry{
            static final String CRD1 = "CRD1";//address's information table


            //sql column's names - CRD1 table
            static final String ADDRESS_CODE = "Address";
            static final String ADDRESS_STEEET = "Street";
            static final String ADDRESS_STREETNO = "StreetNo";
            static final String ADDRESS_BLOCK = "Block";
            static final String ADDRESS_ZIPCODE = "ZipCode";
            static final String ADDRESS_CITY = "City";
            //TODO make other counties available
            static final String ADDRESS_COUNTRY = "Country";//IL = Israel
            static final String ADDRESS_TYPE = "AdresType";//'B' billing, 'S' shipping
            static final String ADDRESS_LINE_NUM = "LineNum";
            static final String ADDRESS_BUILDING = "Building";



            static final String updateAddress =
                    "IF exists (SELECT 1 from "+CRD1+" Where "+CARDCODE+"= @cardCode@ and "+
                    ADDRESS_CODE+" = @addressCode@ and "+ADDRESS_TYPE+"= @addressType@ )"+
                            "BEGIN UPDATE "+CRD1+" SET "+ADDRESS_CITY+"=@city@,"+
                    ADDRESS_STEEET+"=@street@,"+ADDRESS_STREETNO+"=@streetNo@,"+ADDRESS_ZIPCODE+"=@zipCode@,"+
                    ADDRESS_BLOCK+"=@block@,"+LICTRADNUM+"=@licTradNum@,"+ADDRESS_TYPE+"=@addressType@,"+
                    ADDRESS_BUILDING+"=@building@,"+
                    LOCATION+"=@location@ "+
                    "Where "+CARDCODE+"=@cardCode@ and "+ADDRESS_TYPE+"=@addressType@ and "+ADDRESS_CODE+"=@addressCode@ END ELSE "+
                    "BEGIN INSERT into "+CRD1+" ("+ADDRESS_CODE+","+CARDCODE+","+ADDRESS_LINE_NUM+","+ADDRESS_CITY+","+
                    ADDRESS_STEEET+","+ADDRESS_STREETNO+","+ADDRESS_ZIPCODE+","+ADDRESS_BLOCK+","+
                    LICTRADNUM+","+ADDRESS_TYPE+","+ADDRESS_BUILDING+","+LOCATION+") "+
                    "Values (@addressCode@,@cardCode@,(SELECT Max("+ADDRESS_LINE_NUM+")+1 from "+CRD1+
                    " where "+CARDCODE+" =@cardCode@),"+
                    "@city@,@street@,@streetNo@,@zipCode@,@block@,@licTradNum@,@addressType@,@building@,@location@) END";
        }

    }

    class ItemEntry {
        static final String ITEMS_TABLE2 = "ITM1";
        static final String ITEMS_TABLE = "OITM";
        static final String ITMS_CODE = "itemCode";
        static final String ITMS_NAME = "itemName";
        static final String ITMS_VALID = "validFor";
        static final String ITMS_DFT_PRICE = "Price";
        static final String ITMS_DFT_CURR = "Currency";
        static final String ITMS_DFT_H = "SHeight1";
        static final String ITMS_DFT_W = "SWidth1";
        static final String ITMS_DFT_L = "SLength1";
        static final String ITMS_PIC_PATH = "PicturName";
        static final String ITMS_FREE_TEXT = "UserText";
        static final String ITMS_FOR_SELL = "SellItem";
        static final String ITMS_PRICE_TYPE = "PriceList"; //=2 fo selling price

        static final String selectActiveItemsQuery =
                "SELECT T1." + ITMS_CODE + "," +
                ITMS_NAME + "," + ITMS_VALID + "," + ITMS_DFT_PRICE + "," +
                ITMS_DFT_CURR + "," + ITMS_DFT_H + "," + ITMS_DFT_W + "," + ITMS_DFT_L + "," + ITMS_FOR_SELL + "," +
                ITMS_PIC_PATH + "," + ITMS_FREE_TEXT + " from " + ITEMS_TABLE + " as T1 " +
                "Inner Join " + ITEMS_TABLE2 + " as T2 on T1." + ITMS_CODE + "=T2." + ITMS_CODE +
                " where " + ITMS_VALID + " = 'Y'  and " + ITMS_FOR_SELL + " = 'Y'  and " +
                ITMS_PRICE_TYPE + "=2 ";

    }

    class DocumentEntry {
        static final  String DOC_ORDER_TABLE = "ORDR";
        static final  String DOC_INVOICE_TABLE = "OINV";
        static final  String DOC_PRICE_OFFER_TABLE = "OQUT";
        static final  String DOC_REVOKED_INVOICE_TABLE = "ORIN";
        static final  String DOC_SHIPPIN_CERT_TABLE = "ODLN";
        static final  String DOC_ADVANCE_PAYMENT_TABLE = "ODPI";


        static final  String DOC_SN = "DocNum";
        static final  String DOC_PARALLEL_SN = "NumAtCard";//	BP Reference No.
        static final  String DOC_TYPE = "ObjType";//13 = A/R Invoice
        static final  String IS_CANCELED = "CANCELED"; //Y / N
        static final  String DOC_STATUS = "DocStatus"; //C=Closed, O=Active
        static final  String DOC_DATE = "DocDate";
        static final  String DOC_DUE_DATE = "DocDueDate";
        static final  String DOC_TAX_DATE = "TaxDate";
        static final  String DOC_CARD_CODE = "CardCode";//Customer/Vendor Code
        static final  String DOC_CARD_NAME = "CardName";//Customer/Vendor Name
        static final  String DOC_ADDRESS = "Address";//	Bill to
        static final  String DOC_CURRENCY = "DocCur";//Document Currency
        static final  String DOC_VAT_SUM = "VatSum";//Total Tax
        static final  String DOC_VAT_PERCENT = "VatPercent";//Tax Rate
        static final  String DOC_DISCOUNT_PERCENT = "DiscPrcnt";//Discount % for Document
        static final  String DOC_DISCOUNT_SUM = "DiscSum";//Total Discount
        static final  String DOC_TOTAL = "DocTotal";//Total Money
        static final  String DOC_PAID_SUM = "PaidSum";//Total PAid Money

        static final  String DOC_CHARGED_SUM = "PaidToDate";//
        static final  String DOC_LICTRADNUM = "LicTradNum";//Licensed Dealer No.
        static final  String DOC_UPDATE_DATE = "UpdateDate";//
        static final  String DOC_COMMENTS = "Comments";//
        static final  String DOC_SALESMAN_CODE = "SlpCode";//
        static final  String DOC_OWNER_CODE = "OwnerCode";//
        static final  String DOC_ENTRY = "DocEntry";


        static final String selectDocumentQuery ="SELECT "+DOC_SN+","+DOC_TYPE+","+
                IS_CANCELED+","+DOC_STATUS+","+DOC_LICTRADNUM+","+DOC_PARALLEL_SN+","+
                DOC_DATE+","+DOC_DUE_DATE+","+DOC_TAX_DATE+","+DOC_CARD_CODE+","+
                DOC_UPDATE_DATE +","+
                DOC_CARD_NAME+","+DOC_ADDRESS+","+DOC_CURRENCY+","+DOC_VAT_SUM+","+
                DOC_VAT_PERCENT +","+ DOC_DISCOUNT_PERCENT +","+DOC_DISCOUNT_SUM+","+DOC_PAID_SUM+
                ","+DOC_TOTAL+","+
                DOC_CHARGED_SUM+","+DOC_COMMENTS+","+DOC_SALESMAN_CODE+","+DOC_OWNER_CODE +
                " from ";


        class ItemEntry{
            static final  String ITEM_ORDER_TABLE = "RDR1";
            static final  String ITEM_INVOICE_TABLE = "INV1";
            static final  String ITEM_PRICE_OFFER_TABLE = "QUT1";
            static final  String ITEM_REVOKED_INVOICE_TABLE = "RIN1";
            static final  String ITEM_SHIPPIN_CERT_TABLE = "DLN1";
            static final  String ITEM_ADVANCE_PAYMENT_TABLE = "DPI1";

            static final String ITM_NUM = "LineNum";
            static final String ITM_CODE = "ItemCode";
            static final String ITM_STATUS = "LineStatus";
            static final String ITM_DESCRIPTION = "Dscription";
            static final String ITM_QUANTITY = "Quantity";
            static final String ITM_PRICE = "Price";
            static final String ITM_TOTAL_PRICE = "LineTotal";
            static final String ITM_CURRENCY = "Currency";
            static final String ITM_COMMENTS = "FreeTxt";
            static final String ITM_DETAILS = "Text";
            static final String ITM_HEIGHT = "Height1";
            static final String ITM_WIDTH = "Width1";
            static final String ITM_LENGTH = "Length1";
            static final String ITM_UNITS = "U_roll";
            static final String ITM_DOC_ENTRY = "DocEntry";
            static final String TABLE_NAME = "@TABLE_NAME@";

            static final String selectItemsQuery =
                    "SELECT " + ITM_NUM + "," + ITM_CODE + "," + ITM_STATUS + "," + ITM_DESCRIPTION + "," +
                            ITM_QUANTITY + "," + ITM_PRICE + "," + ITM_TOTAL_PRICE + "," + ITM_CURRENCY + "," +
                            ITM_COMMENTS + "," + ITM_DETAILS + "," + ITM_HEIGHT + "," + ITM_WIDTH + "," +
                            ITM_LENGTH + "," + ITM_LENGTH + "," + ITM_UNITS + "," + ITM_DOC_ENTRY +
                            " from ";
        }

    }
    
    
    class EmployeeEntry {
        static final String OHEM = "OHEM";//human resources table

        static final String EMP_SN = "empID";
        static final String EMP_ID = "govID";
        static final String EMP_IS_ACTIVE = "Active";
        static final String EMP_FIRST_NAME = "firstName";
        static final String EMP_MIDDLE_NAME = "middleName";
        static final String EMP_LAST_NAME = "lastName";
        static final String EMP_GENDER = "sex";
        static final String EMP_BIRTHDAY = "birthDate";
        //Address
        static final String EMP_HOME_COUNTRY = "homeCountr";
        static final String EMP_HOME_CITY = "homeCity";
        static final String EMP_HOME_STREET = "homeStreet";
        static final String EMP_HOME_STREETNO = "StreetNoH";
        static final String EMP_HOME_ZIPCODE = "homeZip";
        static final String EMP_HOME_BLOCK = "homeBlock";
        static final String EMP_HOME_BUILDING = "HomeBuild";

        static final String EMP_WORK_COUNTRY = "workCountr";
        static final String EMP_WORK_CITY = "workCity";
        static final String EMP_WORK_STREET = "workStreet";
        static final String EMP_WORK_STREETNO = "StreetNoW";
        static final String EMP_WORK_ZIPCODE = "workZip";
        static final String EMP_WORK_BLOCK = "workBlock";
        static final String EMP_WORK_BUILDING = "WorkBuild";

        static final String EMP_OFFICE_PHONE = "officeTel";
        static final String EMP_HOME_PHONE = "homeTel";
        static final String EMP_CELLULAR = "mobile";
        static final String EMP_FAX = "fax";
        static final String EMP_EMAIL = "email";

        //job
        static final String EMP_JOB_TITLE = "jobTitle";
        static final String EMP_DEPARTMENT_CODE = "dept";
        static final String EMP_POSITION_CODE = "position";
        static final String EMP_MANAGER_SN = "manager";
        static final String EMP_SALEMAN_CODE = "salesPrson";


        static final String OHPS = "OHPS";//job positions table
        static final String POS_CODE = "posID";
        static final String POS_NAME = "name";
        static final String POS_DESCRIPTION = "descriptio";
        static final String POS_IS_LOCK = "LocFields";

        static final String OUDP = "OUDP";//job department table
        static final String DEPT_CODE = "Code";
        static final String DEPT_NAME = "Name";
        static final String DEPT_DESCRIPTION = "Remarks";

        static final String EMP_PIC_PATH = "picture";


        static final String selectDepartments = "Select "+DEPT_CODE+","+DEPT_NAME+","+
                DEPT_DESCRIPTION+" from "+OUDP+" ";

        static final String selectJobPositions = "Select "+POS_CODE+","+POS_NAME+","+
                POS_DESCRIPTION+","+POS_IS_LOCK+" from "+OHPS+" ";




        static final String selectEmployeeQuery = "Select "+EMP_SN+","+EMP_IS_ACTIVE+","+EMP_FIRST_NAME+","+
                EMP_MIDDLE_NAME+","+ EMP_LAST_NAME+","+EMP_GENDER+","+EMP_BIRTHDAY+","+EMP_ID+"," +
                EMP_HOME_COUNTRY+","+EMP_HOME_CITY+","+EMP_HOME_STREET+","+
                EMP_HOME_STREETNO+","+EMP_HOME_ZIPCODE+","+EMP_HOME_BLOCK+","+EMP_HOME_BUILDING+","+
                EMP_WORK_COUNTRY+","+EMP_WORK_CITY+","+EMP_WORK_STREET+","+EMP_WORK_STREETNO+","+
                EMP_WORK_ZIPCODE+","+EMP_WORK_BLOCK+","+EMP_WORK_BUILDING+","+
                EMP_OFFICE_PHONE+","+EMP_HOME_PHONE+","+EMP_CELLULAR+","+EMP_FAX+","+EMP_EMAIL +","+
                EMP_JOB_TITLE+","+EMP_DEPARTMENT_CODE+","+EMP_POSITION_CODE+","+
                EMP_MANAGER_SN+","+ EMP_SALEMAN_CODE+","+EMP_PIC_PATH+" from "+OHEM+" ";

        static final String updateEmpPicQuery =  "UPDATE "+OHEM+" Set "+
                EMP_PIC_PATH+"=@empPic@ where "+EMP_SN+"=@empSn@ ";

    }


    class CompanyEntry{
        final static String OADM = "OADM";
        final static String SYSTEM_CURRENCY = "SysCurrncy";
        final static String COMPANY_NAME = "CompnyName";
        /////////
        final static String ADM1 = "ADM1";
        //sql column's names - CRD1 table
        static final String COMPANY_ADDRESS_STEEET = "Street";
        static final String COMPANY_ADDRESS_STREETNO = "StreetNo";
        static final String COMPANY_ADDRESS_BLOCK = "Block";
        static final String COMPANY_ADDRESS_ZIPCODE = "ZipCode";
        static final String COMPANY_ADDRESS_CITY = "City";
        //TODO make other counties available
        static final String ACOMPANY_DDRESS_COUNTRY = "Country";//IL = Israel
        static final String COMPANY_ADDRESS_BUILDING = "Building";
        static final String COMPANY_ADDRESS_LOCATION = "GlblLocNum";

        static final String selectAddress = "SELECT top 1 "+COMPANY_ADDRESS_STEEET+","+
                COMPANY_ADDRESS_STREETNO+","+COMPANY_ADDRESS_LOCATION+","+
                COMPANY_ADDRESS_BLOCK+","+ COMPANY_ADDRESS_ZIPCODE+","+
                COMPANY_ADDRESS_CITY+","+ ACOMPANY_DDRESS_COUNTRY+","+
                COMPANY_ADDRESS_BUILDING+" from "+ADM1+" ";

        static final String selectName = "SELECT top 1 "+COMPANY_NAME+" from "+OADM+" ";

        static final String selectCurrency =  "SELECT top 1 "+SYSTEM_CURRENCY+" from "+OADM+" ";



    }


    class FinanceRecordEntry {
        //sql table's names
        final static String OJDT = "OJDT";
        final static String JDT1 = "JDT1";

        //JDT1's columns
        final static String BALDUEDEB ="BalDueDeb";
        final static String BALSCCRED ="BalScCred";
        final static String CREDIT ="Credit";
        final static String DEBIT ="Debit";
        final static String BASEREF ="BaseRef";
        final static String REF2 ="Ref2";
        final static String LINEMEMO ="LineMemo";
        final static String TRANSTYPE ="TransType";
        final static String SHORTNAME ="ShortName";
        final static String TRANSID = "TransId";
        final static String REFDATE = "RefDate";

        final static String selectCardRecords = "SELECT J."+BALDUEDEB+",J."+BALSCCRED+",J."+CREDIT+","+
                "J."+DEBIT+",J."+BASEREF+",J."+REF2+",J."+LINEMEMO+",J."+TRANSTYPE+","+
                "J."+REFDATE+" "+
                "from "+JDT1+" as J LEFT OUTER JOIN "+OJDT+" as O "+
                "ON J."+TRANSID+"="+"O."+TRANSID+" Where J."+SHORTNAME+"=@cardCode@ ";


    }

}
