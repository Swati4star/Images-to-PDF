package swati4star.createpdf;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewFiles extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    Activity activity;
    ArrayList<String> inFiles;
    FilesAdapter adapter;
    File[] files;
    File folder;

    ListView listView;
    SwipeRefreshLayout swipeView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_view_files, container, false);

        listView = (ListView) root.findViewById(R.id.list);
        swipeView = (SwipeRefreshLayout) root.findViewById(R.id.swipe);

        //Create/Open folder
        folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDFfiles/");
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Initialize variables
        inFiles = new ArrayList<>();
        files = folder.listFiles();
        adapter = new FilesAdapter(activity, inFiles);
        listView.setAdapter(adapter);
        swipeView.setOnRefreshListener(this);

        // Populate data into listView
        populateListView();

        return root;
    }

    /**
     * Populate data into listView
     */
    public void populateListView() {

        inFiles = new ArrayList<>();
        files = folder.listFiles();
        if (files == null)
            Toast.makeText(activity, "No PDFs right now", Toast.LENGTH_LONG).show();
        else {
            for (File file : files) {
                if (!file.isDirectory() && file.getName().endsWith(".pdf")) {
                        inFiles.add(file.getPath());
                        Log.v("adding", file.getName());

                }
            }

        }
        Log.v("done", "adding");
        adapter = new FilesAdapter(activity, inFiles);
        listView.setAdapter(adapter);

    }

    @Override
    public void onRefresh() {

        Log.v("refresh", "refreshing dta");
        populateListView();
        swipeView.setRefreshing(false);
    }
}
