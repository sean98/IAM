package com.example.sean98.iam.Calenadar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sean98.iam.Calenadar.Events.CreateEventFragment;
import com.example.sean98.iam.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import Adapters.CalendarEventAdapter;
import Databases.Local.AndroidCalendarDao;
import Databases.ICalendarEventDao;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import calendar.CalendarActivity;

public class CalendarPageFragment extends Fragment {

    private CalendarEventAdapter eventAdapter;
    private CalendarActivity calendar;

    public CalendarPageFragment() {
        //Empty Constructor
    }

    public static CalendarPageFragment newInstance() {
        return new CalendarPageFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventAdapter = new CalendarEventAdapter();
        eventAdapter.setOnEventClickedListener(event ->
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameContainer,
                            CreateEventFragment.newInstance(event))
                    .addToBackStack(null)
                    .commit());


        calendar = CalendarActivity.newInstance();
        getChildFragmentManager().beginTransaction()
                .add(R.id.frame_layout, calendar)
                .commit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        RecyclerView recyclerList = view.findViewById(R.id.items_list);
        CoordinatorLayout screen = view.findViewById(R.id.frameContainer);
        FloatingActionButton addEventFab = view.findViewById(R.id.add_event);
        addEventFab.setOnClickListener(view1 ->
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, CreateEventFragment.newInstance())
                    .addToBackStack(null)
                    .commit());


        recyclerList.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        recyclerList.setLayoutManager(lm);
        recyclerList.setAdapter(eventAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerList.getContext(),
                lm.getOrientation());
        recyclerList.addItemDecoration(dividerItemDecoration);

        calendar.setOnDatePickedListener((date, events) -> {
            eventAdapter.updateDataSet(events);
            getActivity().runOnUiThread(eventAdapter::notifyDataSetChanged);
        });

        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            ICalendarEventDao calendarEventDao = new AndroidCalendarDao(getContext());
                            calendarEventDao.getCalendarEvents().addOnSuccessListener(eventList->
                                    calendar.updateEventList(eventList))
                                    .execute();
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
                                .setNegativeButton(R.string.cancel, (dialogInterface, i) ->
                                        token.cancelPermissionRequest())
                                .create()
                                .show();
                    }
                })
                .onSameThread()
                .check();

        return view;
    }
}
