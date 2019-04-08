package Models.util;

import android.location.Location;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Address implements Serializable {
    private String name;
    private String country;
    private String city;
    private String street;
    private String streetNum;
    private String zipCode;
    private String block;
    private String apartment;
    private SapLocation location;

    public Address(String name, String country, String city, String block, String street, String streetNum,
                   String apartment, String zipCode ,SapLocation location) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.street = street;
        this.streetNum = streetNum;
        this.zipCode = zipCode;
        this.block = block;
        this.apartment = apartment;
        this.location = location;

    }
    public Address(){};

    //getters
    public SapLocation getLocation(){
        return location;
    }

    public String getApartment() {
        return apartment;
    }

    public String getName() {
        return name;
    }

    public String getBlock(){
        return block;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getStreetNum() {
        return streetNum;
    }

    public String getZipCode() {
        return zipCode;
    }

    @NonNull
    @Override
    public String toString() {
        return (name!=null?name + " ":"") + (country!=null?country + " ":"") + (city!=null?city+" ":"")
                + (block!=null?block + " ":"") + (street!=null?street + " ":"")
                + (streetNum!=null?streetNum + " ":"") + (apartment !=null? apartment+" " :"")
                + (zipCode!=null?zipCode + " ":"");
    }

    //setters
    public Address setCountry(String country) {
        this.country = country;
        return this;
    }

    public Address setLocation(Location location){
        if (location!=null)
            this.location = new SapLocation(location);
        return this;
    }

    public Address setLocation(SapLocation location){
        this.location = location;
        return this;
    }

    public Address setApartment(String apartment) {
        this.apartment = apartment;
        return this;
    }

    public Address setCity(String city) {
        this.city = city;
        return this;
    }

    public Address setStreet(String street) {
        this.street = street;
        return this;
    }

    public Address setStreetNum(String streetNum) {
        this.streetNum = streetNum;
        return this;
    }

    public Address setZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public Address setBlock(String block) {
        this.block = block;
        return this;
    }

    public String toStringFormat(String format) {
        return format
                .replace("N", name!=null?name:"")
                .replace("C", country!=null?country:"")
                .replace("c", city!=null?city:"")
                .replace("s", street!=null?street:"")
                .replace("n", streetNum!=null?streetNum:"")
                .replace("z", zipCode!=null?zipCode:"")
                .replace("b", block!=null?block:"")
                .replace("a", apartment!=null?apartment:"");
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Address){
            return this.toString().equals(((Address)obj).toString());
        }
        return false;
    }
}
