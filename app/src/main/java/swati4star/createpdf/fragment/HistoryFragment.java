package swati4star.createpdf.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.MainActivity;
import swati4star.createpdf.adapter.HistoryAdapter;
import swati4star.createpdf.database.AppDatabase;
import swati4star.createpdf.database.History;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.FileUtils;

import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;
import static swati4star.createpdf.util.Constants.appName;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnClickListener {

    @BindView(R.id.emptyStatusView)
    ConstraintLayout mEmptyStatusLayout;
    @BindView(R.id.historyRecyclerView)
    RecyclerView mHistoryRecyclerView;
    private Activity mActivity;
    private List<History> mHistoryList;
    private HistoryAdapter mHistoryAdapter;
    private boolean[] mFilterOptionState;

    @Override
    public void onAttach(Context context) {
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
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, root);

        mFilterOptionState = new boolean[getResources().getStringArray(R.array.filter_options_history).length];
        Arrays.fill(mFilterOptionState, Boolean.TRUE); //by default all options should be selected
        // by default all operations should be shown, so pass empty array
        new LoadHistory(mActivity).execute(new String[0]);
        getRuntimePermissions();
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_history_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            new LoadHistory(mActivity).execute(selectedOptions.toArray(new String[0]));
        });

        builder.setNeutralButton(getString(R.string.select_all), (dialogInterface, i) -> {
            Arrays.fill(mFilterOptionState, Boolean.TRUE); //reset state 
            new LoadHistory(mActivity).execute(new String[0]);
        });
        builder.create().show();
    }

    private void deleteHistory() {
        MaterialDialog.Builder builder = DialogUtils.getInstance().createWarningDialog(mActivity,
                R.string.delete_history_message);
        builder.onPositive((dialog2, which) -> new DeleteHistory().execute())
                .show();
    }

    @OnClick(R.id.getStarted)
    public void loadHome() {
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        mActivity.setTitle(appName);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setNavigationViewSelection(R.id.nav_home);
        }
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

    @SuppressLint("StaticFieldLeak")
    private class LoadHistory extends AsyncTask<String[], Void, Void> {
        private final Context mContext;

        LoadHistory(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Void doInBackground(String[]... args) {
            AppDatabase db = AppDatabase.getDatabase(mActivity.getApplicationContext());
            if (args[0].length == 0) {
                mHistoryList = db.historyDao().getAllHistory();
            } else {
                String[] filters = args[0];
                mHistoryList = db.historyDao().getHistoryByOperationType(filters);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mHistoryList != null && !mHistoryList.isEmpty()) {
                mEmptyStatusLayout.setVisibility(View.GONE);
                mHistoryAdapter = new HistoryAdapter(mActivity, mHistoryList, HistoryFragment.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                mHistoryRecyclerView.setLayoutManager(mLayoutManager);
                mHistoryRecyclerView.setAdapter(mHistoryAdapter);
                mHistoryRecyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(mContext));
            } else {
                mEmptyStatusLayout.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(aVoid);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteHistory extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase db = AppDatabase.getDatabase(mActivity.getApplicationContext());
            db.historyDao().deleteHistory();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mHistoryAdapter != null) {
                mHistoryAdapter.deleteHistory();
            }
            mEmptyStatusLayout.setVisibility(View.VISIBLE);
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