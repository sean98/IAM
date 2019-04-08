package com.example.sean98.iam;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sean98.iam.Calenadar.CalendarPageFragment;
import com.example.sean98.iam.Catalogs.CatalogListFragment;
import com.example.sean98.iam.Customers.CustomerScreenManger;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import Databases.CachedDao;
import Databases.ICompanyVariablesDao;
import Databases.SAP.SapMSSQL;
import Models.Cards.Customer;
import Models.Cards.CustomerParams;
import Models.Employees.Employee;
import Models.util.SapLocation;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class MainActivity extends LocaleActivity {

    public static final String BUNDLE_TAG = "BUNDLE_TAG", EMPLOYEE_TAG = "EMPLOYEE_TAG";
    private static final int CUSTOMER_REQUEST = 0;
    private static final int IMAGE_REQUEST = 1;

    private Employee employee;
    private ImageView image;

    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private Toolbar toolbar;

    private Fragment activeFragment = null;
    private NavigationView navigationView;
    private Activity activity;

    private FirebaseStorage storage;
    private StorageReference riversRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(getApplicationContext());
        storage = FirebaseStorage.getInstance();

        setContentView(R.layout.activity_main);
        activity = this;
        fragmentManager = getSupportFragmentManager();

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        Configuration config = getResources().getConfiguration();
        if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            toolbar.setCollapseIcon(getRotateDrawable(BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_arrow_left_white_24dp)));
        }

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationItemSelectedListener());

        Bundle args = getIntent().getBundleExtra(BUNDLE_TAG);
        if (args != null) {
            employee = (Employee) args.getSerializable(EMPLOYEE_TAG);
            setNavigationHeader(navigationView.getHeaderView(0), employee);
        }

        new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0) {
            //This makes the home button to open drawer
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure the request was successful
        if (resultCode == RESULT_OK) {
            // Check which request we're responding to
            switch (requestCode) {
                case CUSTOMER_REQUEST:
                    activeFragment = fragmentManager.findFragmentById(R.id.fragment_placeholder);
                    if (activeFragment instanceof CustomerScreenManger)
                        if (data.getBooleanExtra(CustomerActivity.RESULT_KEY, false))
                            ((CustomerScreenManger) activeFragment).notifyCacheChanged();
                    break;
                case IMAGE_REQUEST:
                    Uri file = data.getData();
                    if (file == null)
                        return;

                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.
                                getBitmap(this.getContentResolver(), file);
                        double maxSize = 450;
                        double scale = maxSize / Math.max(bitmap.getHeight(), bitmap.getWidth());
                        bitmap = getResizedBitmap(bitmap, (int) (scale * bitmap.getWidth()),
                                (int) (scale * bitmap.getHeight()));
                        image.setImageBitmap(bitmap);

                        StorageReference storageRef = storage
                                .getReferenceFromUrl(getString(R.string.google_storage_bucket));//"gs://iam-ltd.appspot.com");
                        String[] tmp = file.getPath().split("/");
                        riversRef = storageRef.child("employees/" + tmp[tmp.length - 1]);
                        Log.d("file", file.getPath());

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] byteData = baos.toByteArray();
                        riversRef.putBytes(byteData)
                                // Register observers to listen for when the download is done
                                // or if it fails
                                .addOnFailureListener(exception ->
                                        Toast.makeText(getApplicationContext(),
                                                R.string.image_fail_upload,
                                                Toast.LENGTH_SHORT)
                                                .show())
                                .addOnSuccessListener(taskSnapshot -> {
                                    riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        Log.i("profilePic", "onActivityResult: " + uri);
                                        employee.setPicPath(uri.toString());
                                        ICompanyVariablesDao dao = new CachedDao();
                                        dao.updateEmployeePicture(employee)
                                                .addOnFailureListener(exception ->
                                                        runOnUiThread(() ->
                                                                Toast.makeText(getApplicationContext(),
                                                                        R.string.image_fail_upload,
                                                                        Toast.LENGTH_SHORT)
                                                                        .show()))
                                                .addOnSuccessListener(aBoolean ->
                                                        runOnUiThread(() ->
                                                                Toast.makeText(getApplicationContext(),
                                                                        R.string.image_succ_upload,
                                                                        Toast.LENGTH_SHORT)
                                                                        .show()))
                                                .execute();
                                    });
                                });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
            return;
        }
        if (activeFragment == null) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.back_to_exit, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            super.onBackPressed();
            activeFragment = fragmentManager.findFragmentById(R.id.fragment_placeholder);
            if (activeFragment == null) {
                for (int i = 0; i < navigationView.getMenu().size(); i++)
                    navigationView.getMenu().getItem(i).setChecked(false);
                toolbar.getMenu().clear();
            }
        }
    }

    private void setNavigationHeader(View view, @NonNull Employee employee) {
        image = view.findViewById(R.id.nav_profile_image);
        view.setOnClickListener(view1 -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, IMAGE_REQUEST);
        });

        Log.i("profilePic", "setNavigationHeader: " + employee.getPicPath());
        if (employee.getPicPath() != null) {
            CircularProgressDrawable cpb = new CircularProgressDrawable(this);
            cpb.setStrokeWidth(5);
            cpb.setCenterRadius(30);
            cpb.setBackgroundColor(Color.WHITE);
            cpb.start();
            Glide.with(this)
                    .load(employee.getPicPath())
                    .centerCrop()
                    .placeholder(cpb)
                    .error(android.R.drawable.stat_notify_error)
                    .into(image);


            new CachedDao().refreshEmployees().addOnSuccessListener(employees -> {
                for (Employee e : employees) {
                    if (e.getSn() == employee.getSn()  ) {
                        if (e.getPicPath().equals(employee.getPicPath()))
                            return;
                        if (activity != null && e.getPicPath() != null)
                            activity.runOnUiThread(() -> {
                                if (e.getPicPath() != null) {
                                    Glide.with(this)
                                            .load(e.getPicPath())
                                            .centerCrop()
                                            .placeholder(cpb)
                                            .error(android.R.drawable.stat_notify_error)
                                            .into(image);
                                }
                            });
                    }
                }
            }).execute();
        }

        TextView name = view.findViewById(R.id.nav_name);
        String text = employee.getFirstName() + " " + employee.getLastName();
        name.setText(text);
    }

    private class NavigationItemSelectedListener
            implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            if (!navigationView.getMenu().findItem(menuItem.getItemId()).isChecked()) {
                Fragment newFragmnet = null;
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                toolbar.getMenu().clear();
                switch (menuItem.getItemId()) {
                    case R.id.nav_customers:
                        CustomerScreenManger cm = CustomerScreenManger.getInstance();
                        cm.onCustomerClickedListener = (View view, Customer c) -> {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(CustomerActivity.CUSTOMER_KEY, c);
                            Intent intent = new Intent(MainActivity.this, CustomerActivity.class);
                            intent.putExtra(CustomerActivity.BUNDLE_KEY, bundle);
                            if (view != null) {
                                Pair[] pairs = new Pair[5];
                                pairs[0] = new Pair<View, String>(view.findViewById(R.id.type_image), "trans_image");
                                pairs[1] = new Pair<View, String>(view.findViewById(R.id.name_title_text), "trans_name");
                                pairs[2] = new Pair<View, String>(view.findViewById(R.id.group_text), "trans_group");
                                pairs[3] = new Pair<View, String>(view.findViewById(R.id.city_text), "trans_city");
                                pairs[4] = new Pair<View, String>(view.findViewById(R.id.cardView), "trans_card");

                                ActivityOptions options;
                                options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                                startActivityForResult(intent, CUSTOMER_REQUEST, options.toBundle());
                            } else
                                startActivityForResult(intent, CUSTOMER_REQUEST);
                        };
                        newFragmnet = cm;
                        break;
                    case R.id.nav_recent:
                        newFragmnet = CalendarPageFragment.newInstance();
                        break;
                    case R.id.nav_logout: {
                        SharedPreferences sharedPref = PreferenceManager
                                .getDefaultSharedPreferences(getApplicationContext());
                        sharedPref.edit()
                                .putInt(LoginFragment.SN_TAG, -1)
                                .apply();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(LoginActivity.BUNDLE_TAG, new Bundle());
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    case R.id.nav_catalog:
                        newFragmnet = CatalogListFragment.newInstance();
                        break;
                    case R.id.nav_language:
                        newFragmnet = LanguageFragment.newInstance();
                        break;
                }
                Log.i("fragmentManager", "onCreate: " + fragmentManager.getBackStackEntryCount());
                if (fragmentManager.getBackStackEntryCount() > 0)
                    fragmentManager.popBackStack();
                activeFragment = newFragmnet;
                transaction
                        .replace(R.id.fragment_placeholder, activeFragment)
                        .addToBackStack(null)
                        .commit();
            }
            // set item as selected to persist highlight
            menuItem.setChecked(true);
            // close drawer when item is tapped
            drawerLayout.closeDrawers();
            return true;
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }


    Drawable getRotateDrawable(final Bitmap b) {
        final BitmapDrawable drawable = new BitmapDrawable(getResources(), b) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate(180, b.getWidth() / 2, b.getHeight() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
        return drawable;
    }

    private void updateAllCustomers() {
        SapMSSQL db = new SapMSSQL();
        db.searchCustomers(new CustomerParams())
        .addOnSuccessListener(customers -> {

            for (Customer c : customers) {
                try {
                    Geocoder coder = new Geocoder(this);
                    String adrStr = c.getBillingAddress().toStringFormat("C, c, s n");
                    List<Address> addressList = coder.getFromLocationName(adrStr, 1);
                    if (addressList != null && addressList.size() > 0) {
                        double lat = addressList.get(0).getLatitude();
                        double lng = addressList.get(0).getLongitude();
                        c.setLocation(new SapLocation(lat, lng));
                            c.getBillingAddress().setLocation(new SapLocation(lat, lng));
                    }
                    db.updateCustomer(c).call();

                    Log.i("updateAllCustomers", "updateAllCustomers: "
                            + customers.indexOf(c) + " : " + c.getLocation());
                } catch (
                        Exception e) {
                    e.printStackTrace();
                } // end catch
            }
        }).execute();
    }
}
