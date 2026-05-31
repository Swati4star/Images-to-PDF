package swati4star.createpdf.fragment;

import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;
import static swati4star.createpdf.util.Constants.appName;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import swati4star.createpdf.R;
import swati4star.createpdf.activity.MainActivity;
import swati4star.createpdf.adapter.HistoryAdapter;
import swati4star.createpdf.database.AppDatabase;
import swati4star.createpdf.database.History;
import swati4star.createpdf.databinding.FragmentHistoryBinding;
import swati4star.createpdf.util.BackgroundExecutor;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnClickListener {
    private Activity mActivity;
    private List<History> mHistoryList;
    private HistoryAdapter mHistoryAdapter;
    private boolean[] mFilterOptionState;
    FragmentHistoryBinding mBinding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();
        mFilterOptionState = new boolean[getResources().getStringArray(R.array.filter_options_history).length];
        Arrays.fill(mFilterOptionState, Boolean.TRUE); //by default all options should be selected
        // by default all operations should be shown, so pass empty array
        loadHistory(new String[0]);
        getRuntimePermissions();
        mBinding.getStarted.setOnClickListener(v -> {
            Fragment fragment = new HomeFragment();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            mActivity.setTitle(appName);
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setNavigationViewSelection(R.id.nav_home);
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history_fragment, menu);
        MenuItem searchItem = menu.findItem(R.id.actionSearchHistory);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_history));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchHistory(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchHistory(newText);
                return true;
            }
        });
    }

    private void searchHistory(String query) {
        if (query.isEmpty()) {
            loadHistory(new String[0]);
            return;
        }
        BackgroundExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(mActivity.getApplicationContext());
            mHistoryList = db.historyDao().searchHistory("%" + query + "%");
            BackgroundExecutor.postToMainThread(() -> {
                if (mHistoryList != null && !mHistoryList.isEmpty()) {
                    mBinding.emptyStatusView.setVisibility(View.GONE);
                    if (mHistoryAdapter == null) {
                        mHistoryAdapter = new HistoryAdapter(mActivity, mHistoryList, HistoryFragment.this);
                        mBinding.historyRecyclerView.setAdapter(mHistoryAdapter);
                    } else {
                        mHistoryAdapter.setData(mHistoryList);
                    }
                } else {
                    if (mHistoryAdapter != null) {
                        mHistoryAdapter.setData(new ArrayList<>());
                    }
                    mBinding.emptyStatusView.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionDeleteHistory:
                deleteHistory();
                break;
            case R.id.actionFilterHistory:
                openFilterDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        String[] options = getResources().getStringArray(R.array.filter_options_history);

        builder.setMultiChoiceItems(options, mFilterOptionState, (dialogInterface, index, isChecked) ->
                mFilterOptionState[index] = isChecked);

        builder.setTitle(getString(R.string.title_filter_history_dialog));

        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            ArrayList<String> selectedOptions = new ArrayList<>();
            for (int j = 0; j < mFilterOptionState.length; j++) {
                if (mFilterOptionState[j]) { //only apply those operations whose state is true i.e selected checkbox
                    selectedOptions.add(options[j]);
                }
            }
            loadHistory(selectedOptions.toArray(new String[0]));
        });

        builder.setNeutralButton(getString(R.string.select_all), (dialogInterface, i) -> {
            Arrays.fill(mFilterOptionState, Boolean.TRUE); //reset state
            loadHistory(new String[0]);
        });
        builder.create().show();
    }

    private void deleteHistory() {
        MaterialDialog.Builder builder = DialogUtils.getInstance().createWarningDialog(mActivity,
                R.string.delete_history_message);
        builder.onPositive((dialog2, which) -> BackgroundExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(mActivity.getApplicationContext());
            db.historyDao().deleteHistory();
            BackgroundExecutor.postToMainThread(() -> {
                if (mHistoryAdapter != null) {
                    mHistoryAdapter.deleteHistory();
                }
                mBinding.emptyStatusView.setVisibility(View.VISIBLE);
            });
        })).show();
    }

    private void loadHistory(String[] filters) {
        BackgroundExecutor.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(mActivity.getApplicationContext());
            if (filters.length == 0) {
                mHistoryList = db.historyDao().getAllHistory();
            } else {
                mHistoryList = db.historyDao().getHistoryByOperationType(filters);
            }
            BackgroundExecutor.postToMainThread(() -> {
                if (mHistoryList != null && !mHistoryList.isEmpty()) {
                    mBinding.emptyStatusView.setVisibility(View.GONE);
                    mHistoryAdapter = new HistoryAdapter(mActivity, mHistoryList, HistoryFragment.this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
                    mBinding.historyRecyclerView.setLayoutManager(mLayoutManager);
                    mBinding.historyRecyclerView.setAdapter(mHistoryAdapter);
                    mBinding.historyRecyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
                } else {
                    mBinding.emptyStatusView.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    @Override
    public void onItemClick(String path) {
        FileUtils fileUtils = new FileUtils(mActivity);
        File file = new File(path);
        if (file.exists()) {
            fileUtils.openFile(path, FileUtils.FileType.e_PDF);
        } else {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.pdf_does_not_exist_message);
        }
    }

    /***
     * check runtime permissions for storage and camera
     ***/
    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                WRITE_PERMISSIONS,
                REQUEST_CODE_FOR_WRITE_PERMISSION);
    }
}
