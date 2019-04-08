package Models.Documents;

import com.example.sean98.iam.LoginActivity;
import com.example.sean98.iam.R;

import java.io.Serializable;

public class Item implements Serializable {

    public enum Status {
        Open(LoginActivity.applicationContext.getString(R.string.open)) ,
        Close(LoginActivity.applicationContext.getString(R.string.close));


        private final String name;
        Status(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    public Item(int number) {
        this.number = number;
    }

    private int  number;
    private String code;
    private Status status;
    private String description;
    private float quantity;
    private float price;
    private float totalPrice;
    private String currency;
    private String comments;
    private String details;
    private float height;
    private float width;
    private float length;
    private Integer units;

    public Item setCode(String code) {
        this.code = code; return this;
    }

    public Item setStatus(Status status) {
        this.status = status; return this;
    }

    public Item setDescription(String description) {
        this.description = description; return this;
    }

    public Item setQuantity(float quantity) {
        this.quantity = quantity; return this;
    }

    public Item setPrice(float price) {
        this.price = price; return this;
    }

    public Item setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice; return this;
    }

    public Item setCurrency(String currency) {
        this.currency = currency; return this;
    }

    public Item setComments(String comments) {
        this.comments = comments; return this;
    }

    public Item setDetails(String details) {
        this.details = details; return this;
    }

    public Item setHeight(float height) {
        this.height = height; return this;
    }

    public Item setWidth(float width) {
        this.width = width; return this;
    }

    public Item setLength(float length) {
        this.length = length; return this;
    }

    public Item setUnits(Integer units) {
        this.units = units; return this;
    }


    public int getNumber() {
        return number;
    }

    public String getCode() {
        return code;
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public float getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public String getComments() {
        return comments;
    }

    public String getDetails() {
        return details;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
    }

    public float getUnits() {
        return units;
    }
}
