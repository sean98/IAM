package com.example.sean98.iam.Calenadar;

public class ExampleCalendarObject implements CalendarObject {
    private String title;
    private String time;
    private int color;

    public ExampleCalendarObject(String title, String time, int color) {
        this.title = title;
        this.time = time;
        this.color = color;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getStratTime() {
        return time;
    }

    @Override
    public int getTypeColor() {
        return color;
    }
}
