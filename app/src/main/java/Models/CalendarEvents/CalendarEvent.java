package Models.CalendarEvents;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Models.Cards.Customer;
import Models.Documents.Document;
import Models.Employees.Employee;
import Models.util.Address;
import androidx.annotation.NonNull;

public class CalendarEvent implements Serializable {
    enum Status{Open,Close,Canceled}
    private long sn;
    private Employee owner;
    private List<Attendee> attendees = new ArrayList<>();

    private Calendar dateTime;
    private Duration duration;
    private Address address;

    private Customer customer;
    private Document document;

    private String type;
    private String subject;

    private String title;
    private String details;

    public CalendarEvent(){}
    public CalendarEvent(long sn) {
        this.sn = sn;
    }

    public long getSn() {
        return sn;
    }

    public Employee getOwner() {
        return owner;
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public Address getAddress() {
        return address;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Document getDocument() {
        return document;
    }

    public String getType() {
        return type;
    }

    public String getSubject() {
        return subject;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public Calendar getEndDateTime(){
        Calendar endDateTime = Calendar.getInstance();
        endDateTime.setTimeInMillis(dateTime.getTimeInMillis());
        endDateTime.add(Calendar.HOUR,duration.getHours());
        endDateTime.add(Calendar.MINUTE,duration.getMinutes());
        endDateTime.add(Calendar.DAY_OF_YEAR,duration.getDays());
        return endDateTime;
    }

    public List<Attendee> getAttendees(){
        return new ArrayList<>(attendees);
    }
    public int addAttendee(Attendee attendee){
        attendees.add(attendee);
        return attendees.size();
    }

    public int removeAttendee(Attendee attendee){
        attendees.remove(attendee);
        return attendees.size();
    }



    public void setOwner(Employee owner) {
        this.owner = owner;
    }

    public void setDateTime(Calendar dateTime) {
        this.dateTime = dateTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public static class Attendee implements Serializable{
        String name;
        String email;
        String phone;


        public Attendee(String name, String email, String phone) {
            this.name = name;
            this.email = email;
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }
    }

    public static class Duration implements Serializable{

        long minutes;
        public Duration(){}
        public Duration(long millis){
            this.minutes = millis/60000;
        }

        public long getTimeInMillis(){
            return minutes*60000;
        }

        public Duration ofMinutes(int minutes){
            this.minutes +=minutes;
            return this;
        }
        public Duration ofHours(int hours){
            return ofMinutes(hours*60);
        }
        public Duration ofDays(int days){
            return ofHours(days*24);
        }

        public int getMinutes() {
            return(int) minutes%60;
        }

        public int getHours() {
            return (int)(minutes/60)%24;
        }

        public int getDays() {
            return (int)(minutes/60)/24;
        }

        @NonNull
        @Override
        public String toString() {
            return toString("d, H:m");
        }

        public String toString(String format){
           return  format.
                   replace("HH",getHours()<10?"0"+getHours():getHours()+"").
                   replace("mm",getMinutes()<10?"0"+getMinutes():getMinutes()+"").
                   replace("dd",getDays()<10?"0"+getDays():getDays()+"").
                   replace("H",getHours()+"").
                   replace("m",getMinutes()+"").
                   replace("d",getDays()+"");
        }
    }
}
