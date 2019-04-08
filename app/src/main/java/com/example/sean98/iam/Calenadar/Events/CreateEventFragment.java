package com.example.sean98.iam.Calenadar.Events;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sean98.iam.Dialogs.SetAddressDialog;
import com.example.sean98.iam.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import Databases.Local.AndroidCalendarDao;
import Databases.ICalendarEventDao;
import Models.CalendarEvents.CalendarEvent;
import Models.Cards.Customer;
import Models.Company.SystemVariables;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;



public class CreateEventFragment extends Fragment  {
    private static final String CUSTOMER_KEY = "CUSTOMER_KEY";
    private static final String CALENDAR_EVENT_KEY = "CALENDAR_EVENT_KEY";

    public enum Type{Add,Update}

    private final int DEFAULT_DELTA_HOURS = 1 ;
    private final int DEFAULT_DELTA_MINUTES = 0 ;
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private Type type = Type.Add;
    private CoordinatorLayout screen;
    private TextInputEditText dateTextField;
    private TextInputEditText startTimeTextField;
    private TextInputEditText durationTimeTextField;
    private TextInputEditText locationTextField;
    private TextInputEditText notesTextField;
    private TextInputEditText titleTextField;
    private MaterialButton addEventButton;
    private CalendarEvent calendarEvent = new CalendarEvent();

    private Customer customer;
    public CreateEventFragment(){
        this.calendarEvent.setDateTime(java.util.Calendar.getInstance());
        this.calendarEvent.setDuration(new CalendarEvent.Duration().
                ofHours(DEFAULT_DELTA_HOURS).
                ofMinutes(DEFAULT_DELTA_MINUTES));
    }

    public static CreateEventFragment newInstance() {
        return new CreateEventFragment();
    }
    public static CreateEventFragment newInstance(Customer customer) {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        args.putSerializable(CUSTOMER_KEY,customer);
        fragment.setArguments(args);
        return fragment;
    }

    public static CreateEventFragment newInstance(CalendarEvent calendarEvent) {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        args.putSerializable(CALENDAR_EVENT_KEY,calendarEvent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if(getArguments().containsKey(CUSTOMER_KEY))
                customer = (Customer)getArguments().getSerializable(CUSTOMER_KEY);
            if(getArguments().containsKey(CALENDAR_EVENT_KEY)){
                calendarEvent = (CalendarEvent) getArguments().getSerializable(CALENDAR_EVENT_KEY);
                type = Type.Update;
            }
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create_event_fragment, container, false);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener((view, keyCode, keyEvent) -> {
             if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction()==KeyEvent.ACTION_UP) {
                 if(type == Type.Add){
                     new AlertDialog.Builder(getContext())
                             .setMessage(R.string.leave_without_save)
                             .setCancelable(false)
                             .setPositiveButton(android.R.string.yes, (dialogInterface, i1) -> {
                                 dialogInterface.dismiss();
                                 getActivity().getSupportFragmentManager().popBackStack();
                             }).setNegativeButton(android.R.string.no, (dialogInterface, i1) -> dialogInterface.dismiss())
                             .create().show();
                     return true;
                 }
                 else {
                     getActivity().getSupportFragmentManager().popBackStack();
                 }
            }
            return false;
        });
        dateTextField = v.findViewById(R.id.dateTextField);
        startTimeTextField = v.findViewById(R.id.startTimeTextField);
        durationTimeTextField = v.findViewById(R.id.durationTimeTextField);
        locationTextField = v.findViewById(R.id.locationTextField);
        addEventButton = v.findViewById(R.id.add_button);
        titleTextField = v.findViewById(R.id.titleTextField);
        notesTextField = v.findViewById(R.id.notesTextField);
        screen = v.findViewById(R.id.screen);

        startTimeTextField.setOnClickListener(onTimeFieldClicked);
        durationTimeTextField.setOnClickListener(onDurationFieldClicked);
        locationTextField.setOnClickListener(onLocationSetClicked);
        dateTextField.setOnClickListener(onDateFieldClicked);

        dateTextField.setText(dateFormat.format(calendarEvent.getDateTime().getTime()));
        startTimeTextField.setText(timeFormat.format(calendarEvent.getDateTime().getTime()));
        durationTimeTextField.setText(calendarEvent.getDuration().toString("HH:mm"));

        titleTextField.setText(calendarEvent.getTitle());
        notesTextField.setText(calendarEvent.getDetails());

        if(calendarEvent.getAddress()!=null)
            locationTextField.setText(calendarEvent.getAddress().toStringFormat("c , s n"));

        if(type == Type.Add)
            addEventButton.setText(getResources().getText(R.string.add));
        else{
            addEventButton.setText(getResources().getText(R.string.update));
            addEventButton.setVisibility(View.GONE);//TODO create update method
        }

        addEventButton.setOnClickListener((viewB)-> {
            if (type == Type.Add)
                onAddClicked(viewB);
                });

        if(customer!=null){
            titleTextField.setText(customer.getName());
            calendarEvent.setAddress(customer.getBillingAddress());
            locationTextField.setText(customer.getBillingAddress().toStringFormat("c , s n"));
        }
        return v;
    }


    private DatePickerDialog.OnDateSetListener mDateSetListener = (datePicker,year,month,day)->{
        calendarEvent.getDateTime().set(year,month,day);
        dateTextField.setText(dateFormat.format(calendarEvent.getDateTime().getTime()));
    };
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =(timePicker,hour,minute)->{
        calendarEvent.getDateTime().set(java.util.Calendar.MINUTE,minute);
        calendarEvent.getDateTime().set(java.util.Calendar.HOUR_OF_DAY,hour);
        startTimeTextField.setText(timeFormat.format(calendarEvent.getDateTime().getTime()));
    };
    private TimePickerDialog.OnTimeSetListener mDurationSetListener =(timePicker,hour,minute)->{
        this.calendarEvent.setDuration(new CalendarEvent.Duration().
                ofHours(hour).
                ofMinutes(minute));
        durationTimeTextField.setText(calendarEvent.getDuration().toString("HH:mm"));
    };



    private void onAddClicked(View view){
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            calendarEvent.setTitle(titleTextField.getText().toString());
                            calendarEvent.setDetails(notesTextField.getText().toString());
                            CalendarEvent.Attendee owner = new CalendarEvent.Attendee(
                                    SystemVariables.ActiveUser.getName(),
                                    SystemVariables.ActiveUser.getEmail(),
                                    SystemVariables.ActiveUser.getWorkCellular()
                            );
                            calendarEvent.addAttendee(owner);
                            if(customer!= null){
                                CalendarEvent.Attendee customerA = new CalendarEvent.Attendee(
                                        customer.getName(),
                                        null,
                                        null
                                );
                                calendarEvent.addAttendee(customerA);

                            }
                            //calendarEvent.addAttendee(new CalendarEvent.Attendee("Lior","Lior.itzhak@gmail.com",null));
                            //calendarEvent.addAttendee(new CalendarEvent.Attendee("Sean","Sean98Goldfarb@gmail.com",null));
                            calendarEvent.setOwner(SystemVariables.ActiveUser);

                            ICalendarEventDao androidCalendar = new AndroidCalendarDao(getContext());

                            androidCalendar.pushCalendarEvent(calendarEvent).addOnSuccessListener(id->{
                                if(getActivity()!=null)
                                    getActivity().runOnUiThread(()->{
                                            Toast.makeText(getActivity(),R.string.event_added_to_calendar,Toast.LENGTH_SHORT)
                                                    .show();
                                            getActivity().onBackPressed();
                                    });
                            }).execute();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Snackbar.make(screen, R.string.no_calendar, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.activate, view -> {
                                        Intent intent = new Intent(Settings
                                                .ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getActivity()
                                                .getPackageName(), null);
                                        intent.setData(uri);
                                        startActivityForResult(intent, 101);
                                    })
                                    .setActionTextColor(Color.YELLOW)
                                    .show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                                   PermissionToken token) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.calendar_request_title)
                                .setMessage(R.string.calendar_request_msg)
                                .setIcon(R.drawable.calendar_edit)
                                .setPositiveButton(R.string.ok, (dialogInterface, i) ->
                                        token.continuePermissionRequest())
                                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                                    token.cancelPermissionRequest();
                                })
                                .create()
                                .show();
                    }
                })
                .onSameThread()
                .check();
    }



    public View.OnClickListener onDateFieldClicked = (view)->{
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int year = cal.get(java.util.Calendar.YEAR);
        int month = cal.get(java.util.Calendar.MONTH);
        int day = cal.get(java.util.Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                R.style.DialogTheme,mDateSetListener,year,month,day);
        dialog.show();
    };

    private View.OnClickListener onTimeFieldClicked = (view)->{
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int hour = cal.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = cal.get(java.util.Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(getActivity(),
                R.style.DialogTheme,mTimeSetListener,hour,minute, DateFormat.is24HourFormat(getActivity()));
        dialog.show();
    };

    private View.OnClickListener onDurationFieldClicked = (view)->{
        TimePickerDialog dialog = new TimePickerDialog(getActivity(),
                R.style.DialogTheme,mDurationSetListener,DEFAULT_DELTA_HOURS,DEFAULT_DELTA_MINUTES,
                true);

        dialog.show();
    };

    private View.OnClickListener onLocationSetClicked =(view)->{
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if (prev != null)
            ft.remove(prev);
        ft.addToBackStack(null);

        // Create and show the dialog.
        SetAddressDialog dialog;
        if (calendarEvent.getAddress()!=null)
            dialog= SetAddressDialog.newInstance(calendarEvent.getAddress());
        else
            dialog = SetAddressDialog.newInstance();
        dialog.setOnAddressSetListener((address)-> {
           this.calendarEvent.setAddress(address);
           locationTextField.setText(address.toStringFormat("c , s n"));
        });
        dialog.show(ft, "setAddress_dialog");
    };













    //Open google calendar in google's activity
    private void addEventToCalendar(){
        Calendar beginTime = calendarEvent.getDateTime();
        Calendar endTime = java.util.Calendar.getInstance();
        endTime.setTimeInMillis(calendarEvent.getDateTime().getTimeInMillis());
        endTime.add(Calendar.HOUR,calendarEvent.getDuration().getHours());
        endTime.add(Calendar.MINUTE,calendarEvent.getDuration().getMinutes());
        long endMillis = endTime.getTimeInMillis();
        long startMillis = beginTime.getTimeInMillis();

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.DISPLAY_COLOR,Color.BLUE);
        intent.putExtra(CalendarContract.Events.TITLE, titleTextField.getText().toString());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, notesTextField.getText().toString());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, locationTextField.getText().toString());
        intent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);

        intent.putExtra(CalendarContract.Events.ALLOWED_ATTENDEE_TYPES ,0 );
        intent.putExtra(CalendarContract.Attendees.ATTENDEE_NAME, "Trevor");
        intent.putExtra(CalendarContract.Attendees.ATTENDEE_EMAIL, "trevor@example.com");
        intent.putExtra(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP,
                CalendarContract.Attendees.RELATIONSHIP_NONE);
        intent.putExtra(CalendarContract.Attendees.ATTENDEE_TYPE, CalendarContract.Attendees.TYPE_OPTIONAL);
        intent.putExtra(CalendarContract.Attendees.ATTENDEE_STATUS,CalendarContract.Attendees.ATTENDEE_STATUS_INVITED);

        intent.putExtra(CalendarContract.Events._ID,10);

        startActivity(intent);


// ... do something with event ID
//
//
    }
}
