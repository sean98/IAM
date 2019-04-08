package Models.Company;

import com.example.sean98.iam.LoginActivity;
import com.example.sean98.iam.R;

import java.io.Serializable;

public class Item implements Serializable {
    public enum Status {
        Active(LoginActivity.applicationContext.getString(R.string.activate)) ,
        Frozen(LoginActivity.applicationContext.getString(R.string.frozen));

        private final String name;
        Status(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    private String code;
    private Status status;
    private String description;
    private float defaultPrice;
    private String defaultCurrency;
    private String comments;
    private String details;
    private float defaultHeight;
    private float defaultWidth;
    private float defaultLength;
    private String picPath;
    private String freeText;




    public Item(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public float getDefaultPrice() {
        return defaultPrice;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public String getComments() {
        return comments;
    }

    public String getDetails() {
        return details;
    }

    public float getDefaultHeight() {
        return defaultHeight;
    }

    public float getDefaultWidth() {
        return defaultWidth;
    }

    public float getDefaultLength() {
        return defaultLength;
    }

    public String getPicPath() {
        return picPath;
    }

    public String getFreeText() {
        return freeText;
    }


    public Item setStatus(Status status) {
        this.status = status; return this;
    }

    public Item setDescription(String description) {
        this.description = description; return this;
    }

    public Item setDefaultPrice(float defaultPrice) {
        this.defaultPrice = defaultPrice; return this;
    }

    public Item setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency; return this;
    }

    public Item setComments(String comments) {
        this.comments = comments; return this;
    }

    public Item setDetails(String details) {
        this.details = details; return this;
    }

    public Item setDefaultHeight(float defaultHeight) {
        this.defaultHeight = defaultHeight; return this;
    }

    public Item setDefaultWidth(float defaultWidth) {
        this.defaultWidth = defaultWidth; return this;
    }

    public Item setDefaultLength(float defaultLength) {
        this.defaultLength = defaultLength; return this;
    }

    public Item setPicPath(String picPath) {
        this.picPath = picPath; return this;
    }

    public Item setFreeText(String freeText) {
        this.freeText = freeText; return this;
    }
}
