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

public class ViewFiles extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    Activity ac;
    ListView g;
    SwipeRefreshLayout swipeView;
    ArrayList<String> inFiles;
    File[] files;
    Files_adapter adapter;
    File folder;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ac = (Activity) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_viewfiles, container, false);
        folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/PDFfiles/");
        //Create/Open folder
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }


        swipeView = (SwipeRefreshLayout) root.findViewById(R.id.swipe);
        inFiles = new ArrayList<String>();
        files = folder.listFiles();
        g = (ListView) root.findViewById(R.id.list);
        adapter = new Files_adapter(ac, inFiles);
        g.setAdapter(adapter);
        swipeView.setOnRefreshListener(this);

        fill_data();


        return root;

    }


    public void fill_data() {

        inFiles = new ArrayList<String>();
        files = folder.listFiles();
        if (files == null)
            Toast.makeText(ac, "No PDFs right now", Toast.LENGTH_LONG).show();
        else {
            for (File file : files) {
                if (file.isDirectory()) {
                } else {
                    if (file.getName().endsWith(".pdf")) {
                        inFiles.add(file.getPath());

                        Log.e("adding", file.getName());

                    }
                }
            }

        }
        Log.e("done","adding");
        adapter = new Files_adapter(ac, inFiles);
        g.setAdapter(adapter);

    }

    @Override
    public void onRefresh() {

        Log.e("refresh","refreshing dta");

        fill_data();
        swipeView.setRefreshing(false);
    }
}
