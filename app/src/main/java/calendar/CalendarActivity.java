package calendar;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.sean98.iam.LocaleApplication;
import com.example.sean98.iam.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import Models.CalendarEvents.CalendarEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CalendarActivity extends Fragment {
    private GregorianCalendar cal_month;
    private HwAdapter hwAdapter;
    private TextView tv_month;
    private OnDatePickedListener onDatePickedListener;
    private GridView gridview;
    private View lastView;
    private Drawable lastBackRound;
    private ColorStateList lastColor;
    private List<CalendarEvent> calendarEvents;

    public void setOnDatePickedListener(OnDatePickedListener onDatePickedListener) {
        this.onDatePickedListener = onDatePickedListener;
    }

    public static CalendarActivity newInstance() {
        return new CalendarActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
        hwAdapter = new HwAdapter(getActivity(), cal_month);
        if (calendarEvents!=null)
            hwAdapter.setCalendarEvents(calendarEvents);
    }

    public void updateEventList(List<CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
        if (hwAdapter != null) {
            hwAdapter.setCalendarEvents(calendarEvents);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    hwAdapter.notifyDataSetChanged();
                    checkCurrentDate();
                });
            }
        }
    }

    private void setNextMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMaximum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) + 1), cal_month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH,
                    cal_month.get(GregorianCalendar.MONTH) + 1);
        }
    }

    private void setPreviousMonth() {
        if (cal_month.get(GregorianCalendar.MONTH) == cal_month.getActualMinimum(GregorianCalendar.MONTH)) {
            cal_month.set((cal_month.get(GregorianCalendar.YEAR) - 1), cal_month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            cal_month.set(GregorianCalendar.MONTH, cal_month.get(GregorianCalendar.MONTH) - 1);
        }
    }

    private void refreshCalendar() {
        hwAdapter.refreshDays();
        tv_month.setText(String.format(LocaleApplication.applicationLocale,"%tB ",cal_month)
                + cal_month.get(Calendar.YEAR));
        hwAdapter.notifyDataSetChanged();
        checkCurrentDate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_calendar, container, false);
        ImageButton previous = view.findViewById(R.id.ib_prev);
        ImageButton next = view.findViewById(R.id.Ib_next);
        gridview = view.findViewById(R.id.gv_calendar);

        Configuration config = getResources().getConfiguration();
        if(config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            next.setScaleX(-1);
            previous.setScaleX(-1);
        }

        tv_month = view.findViewById(R.id.tv_month);
        refreshCalendar();

        previous.setOnClickListener(v -> {
            setPreviousMonth();
            refreshCalendar();
        });


        next.setOnClickListener(v -> {
            setNextMonth();
            refreshCalendar();
        });

        gridview.setAdapter(hwAdapter);
        gridview.setOnItemClickListener((parent, v, position, id) -> {
            TextView tv;
            if (lastView!=null) {
                lastView.setBackground(lastBackRound);
                tv = lastView.findViewById(R.id.date);
                tv.setTextColor(lastColor);
            }
            lastView = v;
            lastBackRound = v.getBackground();
            tv = lastView.findViewById(R.id.date);
            lastColor = tv.getTextColors();
            tv.setTextColor(Color.WHITE);
            v.setBackgroundResource(R.drawable.date_picker);

            HwAdapter adapter = ((HwAdapter) parent.getAdapter());
            String selectedGridDate = adapter.getItem(position);
            List<CalendarEvent> events = adapter.getEventsAt(selectedGridDate);
            if (onDatePickedListener!=null)
                onDatePickedListener.onDatePicked(selectedGridDate, events);
        });
        return view;
    }

    private void checkCurrentDate() {
        if (gridview == null)
            return;
        String today = (new SimpleDateFormat("yyyy-MM-dd"))
                .format(Calendar.getInstance().getTime());
        boolean check = false;
        for (int i = 0; i < gridview.getCount(); i++) {
            if (gridview.getItemAtPosition(i).equals(today)) {
                final int position = i;
                View view = gridview.getChildAt(position);
                if (getActivity() == null || view == lastView)
                    return;
                check = true;
                gridview.post(() -> {
                    lastView = gridview.getChildAt(position);
                    lastBackRound = lastView.getBackground();
                    TextView tv = lastView.findViewById(R.id.date);
                    lastView.setBackgroundResource(R.drawable.date_picker);
                    lastColor = tv.getTextColors();
                    tv.setTextColor(Color.WHITE);
                    String selectedGridDate = hwAdapter.getItem(position);
                    List<CalendarEvent> events = hwAdapter.getEventsAt(selectedGridDate);
                    if (onDatePickedListener != null)
                        onDatePickedListener.onDatePicked(selectedGridDate, events);
                });
                break;
            }
        }
        if (!check)
            onDatePickedListener.onDatePicked("", new ArrayList<>());
    }

    public interface OnDatePickedListener {
        void onDatePicked(String date, List<CalendarEvent> event);
    }
}
