package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sean98.iam.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import Models.CalendarEvents.CalendarEvent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CalendarEventAdapter extends RecyclerView.Adapter<CalendarEventAdapter.CalendarEventVH> {
    List<CalendarEvent> calendarEvents;

    private OnEventClickedListener onEventClickedListener;

    public void setOnEventClickedListener(OnEventClickedListener onEventClickedListener) {
        this.onEventClickedListener = onEventClickedListener;
    }

    public interface OnEventClickedListener{
        void onEventClicked(CalendarEvent event);
    }

    public CalendarEventAdapter() {
        calendarEvents = new ArrayList<>();
    }

    public void updateDataSet(List<CalendarEvent> calendarObjects) {
        this.calendarEvents = calendarObjects;
    }

    @NonNull
    @Override
    public CalendarEventVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_calendar_event, parent, false);
        return new CalendarEventVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarEventVH holder, int position) {
        CalendarEvent event = calendarEvents.get(position);
        holder.bindWith(event);
        holder.getView().setOnClickListener((v)->{
            if(onEventClickedListener!=null)
                onEventClickedListener.onEventClicked(event);
        });


    }

    @Override
    public int getItemCount() {
        return calendarEvents.size();
    }

    public static class CalendarEventVH extends RecyclerView.ViewHolder {
        private static final  SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        TextView time, title;
        CircleImageView type_img;
        View view;
        public CalendarEventVH(@NonNull View view) {
            super(view);
            this.view = view;
            time = view.findViewById(R.id.time_text_view);
            title = view.findViewById(R.id.title_text_view);
            type_img = view.findViewById(R.id.type_img);
        }

        public View getView(){
            return this.view;
        }

        public void bindWith(CalendarEvent co) {
            time.setText(timeFormat.format(co.getDateTime().getTime()));
            title.setText(co.getTitle());
//            type_img.setImageDrawable(new ColorDrawable(co.getTypeColor()));
//            type_img.setCircleBackgroundColor(co.getTypeColor());
        }
    }
}
