package Models.Company;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class SalesMan implements Serializable {

    public enum Status {Active,NoActive}
    private String name;
    private int code;
    private String mobile;
    private String email;
    private Status status;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof  SalesMan) {
            SalesMan tmp = (SalesMan)obj;
            boolean result = true;
            if (tmp.name!=null)
                result &= tmp.name.equals(name);
            if (tmp.mobile!=null)
                result &= tmp.mobile.equals(mobile);
            if (tmp.email!=null)
                result &= tmp.email.equals(email);
            return result && tmp.code==code && tmp.status==status;
        }
        return false;
    }

    public SalesMan(int code, String name, String mobile, String email, Status status){
        this.code = code;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.status = status;
    }



    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return name;
    }
}
