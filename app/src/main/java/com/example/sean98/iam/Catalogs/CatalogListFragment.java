package com.example.sean98.iam.Catalogs;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sean98.iam.R;

import Adapters.CatalogItemAdapter;
import Databases.CachedDao;
import Databases.Exceptions.DatabaseException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import permmisionModels.PermissionMethods;

public class CatalogListFragment extends Fragment {

    public static CatalogListFragment newInstance() {
        return new CatalogListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog_list, container, false);
        RecyclerView catalog_list = view.findViewById(R.id.catalog_list);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);

        CatalogItemAdapter adapter = new CatalogItemAdapter();
        catalog_list.setAdapter(adapter);
        int numberOfColumns = 2;
        catalog_list.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        adapter.setOnItemChosenListener(item ->
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.catalog_container, CatalogItemPageFragment.newInstance(item))
                        .addToBackStack(null)
                        .commit());

        CachedDao db = new CachedDao();

        refreshLayout.setOnRefreshListener(() ->
                db.forceOnline().getActiveItems().addOnSuccessListener(items -> {
                    adapter.updateDataSet(items);
                    if (getActivity() != null)
                        getActivity().runOnUiThread(adapter::notifyDataSetChanged);
                }).addOnFailureListener(de -> {
                    String msg;
                    if (de.getType() == DatabaseException.Type.TimeOut
                            && !PermissionMethods.isInternetAvailable())
                        msg = getString(R.string.no_internet_access);
                    else
                        msg = getString(R.string.no_access_to_server);
                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), msg,
                                Toast.LENGTH_SHORT).show());
                }).addOnPostExecuteListener(o -> refreshLayout.setRefreshing(false)).execute());

        refreshLayout.setRefreshing(true);
        db.getActiveItems()
                .addOnFailureListener(de -> {
                    String msg;
                    if (de.getType() == DatabaseException.Type.TimeOut
                            && !PermissionMethods.isInternetAvailable())
                        msg = getString(R.string.no_internet_access);
                    else
                        msg = getString(R.string.no_access_to_server);
                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), msg,
                                Toast.LENGTH_SHORT).show());
                })
                .addOnSuccessListener((items) -> {
                    adapter.updateDataSet(items);
                    if (getActivity() != null)
                        getActivity().runOnUiThread(adapter::notifyDataSetChanged);
                }).addOnPostExecuteListener(o -> refreshLayout.setRefreshing(false))
                .execute();
        return view;
    }
}
