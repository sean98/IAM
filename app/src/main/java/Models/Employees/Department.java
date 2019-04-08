package Models.Employees;

import java.io.Serializable;

public class Department implements Serializable {
    private int code;
    private String name;
    private String description;
    public Department(int code, String name, String desciption) {
        this.code = code;
        this.name = name;
        this.description = desciption;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }




}
