package swati4star.createpdf.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.HistoryAdapter;
import swati4star.createpdf.database.AppDatabase;
import swati4star.createpdf.database.History;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

public class HistoryFragment extends Fragment {

    @BindView(R.id.emptyStatusView)
    ConstraintLayout mEmptyStatusLayout;
    @BindView(R.id.historyRecyclerView)
    RecyclerView mHistoryRecyclerView;
    private Activity mActivity;
    private List<History> mHistoryList;
    private HistoryAdapter mHistoryAdapter;


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

        new LoadHistory(mActivity).execute();
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteHistory() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.warning)
                .content(R.string.delete_history_message)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        new DeleteHistory().execute();
                    }
                })
                .show();
    }

    @OnClick(R.id.getStarted)
    public void loadHome() {
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadHistory extends AsyncTask<Void, Void, Void> {
        private final Context mContext;

        LoadHistory(Context mContext) {
            this.mContext = mContext;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase db = AppDatabase.getDatabase(mActivity.getApplicationContext());
            mHistoryList = db.historyDao().getAllHistory();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mHistoryList != null && !mHistoryList.isEmpty()) {
                mEmptyStatusLayout.setVisibility(View.GONE);
                mHistoryAdapter = new HistoryAdapter(mActivity, mHistoryList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                mHistoryRecyclerView.setLayoutManager(mLayoutManager);
                mHistoryRecyclerView.setAdapter(mHistoryAdapter);
                mHistoryRecyclerView.addItemDecoration(new ViewFilesDividerItemDecoration(mContext));
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
}