package calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sean98.iam.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import Models.CalendarEvents.CalendarEvent;

class HwAdapter extends BaseAdapter {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Activity context;

    private java.util.Calendar month;
    private GregorianCalendar pmonth;
    private int firstDay;
    private String curentDateString;
    private DateFormat df;

    public List<String> day_string;
    private String gridvalue;

    private List<CalendarEvent> calendarEvents;

    public void setCalendarEvents(List<CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
    }


    HwAdapter(Activity context, GregorianCalendar monthCalendar) {
        calendarEvents = new ArrayList<>();
        day_string = new ArrayList<>();
        Locale.setDefault(Locale.US);
        month = monthCalendar;
        GregorianCalendar selectedDate = (GregorianCalendar) monthCalendar.clone();
        this.context = context;
        month.set(GregorianCalendar.DAY_OF_MONTH, 1);

        df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();

    }

    public int getCount() {
        return day_string.size();
    }

    public String getItem(int position) {
        return day_string.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView dayView;
        if (convertView == null) { // if it's not recycled
            // attributes
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.cal_item, null);
        }


        dayView = v.findViewById(R.id.date);
        String[] separatedTime = day_string.get(position).split("-");

        gridvalue = separatedTime[2].replaceFirst("^0*", "");
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDay)) {
            dayView.setTextColor(Color.parseColor("#A9A9A9"));
            dayView.setClickable(false);
            dayView.setFocusable(false);
            dayView.setEnabled(false);
        } else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
            dayView.setTextColor(Color.parseColor("#A9A9A9"));
            dayView.setClickable(false);
            dayView.setFocusable(false);
            dayView.setEnabled(false);
        } else {
            // setting curent month's days in blue color.
            dayView.setTextColor(Color.parseColor("#696969"));
        }

        if (day_string.get(position).equals(curentDateString)) {
            v.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            v.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        dayView.setText(gridvalue);
        v.setBackgroundResource(android.R.color.transparent);
        setEventView(v, position,dayView);

        return v;
    }

    void refreshDays() {
        // clear items
        day_string.clear();
        Locale.setDefault(Locale.US);
        pmonth = (GregorianCalendar) month.clone();
        // month start day. ie; sun, mon, etc
        firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
        // finding number of weeks in current month.
        int maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridview.
        int mnthlength = maxWeeknumber * 7;
        int maxP = getMaxP();
        int calMaxP = maxP - (firstDay - 1);

        GregorianCalendar pmonthmaxset = (GregorianCalendar) pmonth.clone();

        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);


        for (int n = 0; n < mnthlength; n++) {

            String itemvalue = df.format(pmonthmaxset.getTime());
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
            day_string.add(itemvalue);

        }
    }

    private int getMaxP() {
        int maxP;
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }




    private void setEventView(View v,int pos,TextView txt){
        int len = calendarEvents.size();
        for (int i = 0; i < len; i++) {
            CalendarEvent cal_obj=calendarEvents.get(i);
            String date=timeFormat.format(cal_obj.getDateTime().getTime());
            int len1=day_string.size();
            if (len1>pos) {

                if (day_string.get(pos).equals(date)) {
                    if (((Integer.parseInt(gridvalue) <= 1) || (pos >= firstDay))
                            && ((Integer.parseInt(gridvalue) >= 7) || (pos <= 28))) {
                        v.setBackgroundColor(Color.parseColor("#343434"));
                        v.setBackgroundResource(R.drawable.event_circle);
                        txt.setTextColor(Color.parseColor("#696969"));
                    }
                    else
                        v.setBackgroundResource(android.R.color.transparent);
                }
            }}
    }

    public  List<CalendarEvent> getEventsAt(String date){
        List<CalendarEvent> result = new ArrayList<>();
        for (CalendarEvent event : calendarEvents){
            if ((timeFormat.format(event.getDateTime().getTime())).equals(date))
                result.add(event);
        }
        return result;
    }
}

