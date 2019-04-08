package Models.Employees;

import java.io.Serializable;
import java.util.Date;

import Models.util.Address;
import Models.Company.SalesMan;

public class Employee implements Serializable {
    public Employee(int sn) {
        this.sn = sn;
    }

    public enum Gender {Male,Female}
    private int sn;
    private boolean isActive;
    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private Date birthday;
    private String id;

    private String jobTitle;
    private Department department;
    private JobPosition position;
    private Employee manager;
    private SalesMan salesMan;

    private Address homeAddress;
    private Address workAddress;

    private String homePhone;
    private String officePhone;
    private String workCellular;
    private String fax;
    private String email;

    private String picPath;

    public Employee setPicPath(String picPath) {
        this.picPath = picPath;
        return this;
    }

    public Employee setActive(boolean active) {
        isActive = active;
        return this;
    }

    public Employee setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public Employee setMiddleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    public Employee setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Employee setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Employee setBirthday(Date birthday) {
        this.birthday = birthday;
        return this;
    }

    public Employee setId(String id) {
        this.id = id;
        return this;
    }

    public Employee setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }

    public Employee setDepartment(Department department) {
        this.department = department;
        return this;
    }

    public Employee setPosition(JobPosition position) {
        this.position = position;
        return this;
    }

    public Employee setManager(Employee manager) {
        this.manager = manager;
        return this;
    }

    public Employee setSalesMan(SalesMan salesMan) {
        this.salesMan = salesMan;
        return this;
    }

    public Employee setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
        return this;
    }

    public Employee setWorkAddress(Address workAddress) {
        this.workAddress = workAddress;
        return this;
    }

    public Employee setHomePhone(String homePhone) {
        this.homePhone = homePhone;
        return this;
    }

    public Employee setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
        return this;
    }

    public Employee setWorkCellular(String workCellular) {
        this.workCellular = workCellular;
        return this;
    }

    public Employee setFax(String fax) {
        this.fax = fax;
        return this;
    }

    public Employee setEmail(String email) {
        this.email = email;
        return this;
    }

    public int getSn() {
        return sn;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getId() {
        return id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public Department getDepartment() {
        return department;
    }

    public JobPosition getPosition() {
        return position;
    }

    public Employee getManager() {
        return manager;
    }

    public SalesMan getSalesMan() {
        return salesMan;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public Address getWorkAddress() {
        return workAddress;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public String getWorkCellular() {
        return workCellular;
    }

    public String getFax() {
        return fax;
    }

    public String getEmail() {
        return email;
    }

    public String getName(){
        return getFirstName()+" "+getMiddleName()!=null?getMiddleName()+" ":getLastName();
    }

    public String getPicPath() {
        return picPath;
    }
}
