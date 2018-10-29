package swati4star.createpdf.util;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.WhatsNewAdapter;

public class WhatsNewUtils {

    /**
     * Display news
     *
     * @param context - current context
     */
    public static void displayDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.fragment_whats_new);
        ListView lv = dialog.findViewById(R.id.whatsNewListView);
        TextView title = dialog.findViewById(R.id.title);
        Button continueButton = dialog.findViewById(R.id.continueButton);
        continueButton.setText(R.string.whatsnew_continue);
        title.setText(R.string.whatsnew_title);
        ArrayList<String> titleList = null;
        ArrayList<String> contentList = null;
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(context));
            JSONArray data = obj.getJSONArray("data");
            titleList = new ArrayList<String>();
            contentList = new ArrayList<String>();

            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                String newTitle = jsonObject.getString("title");
                String newContent = jsonObject.getString("content");
                titleList.add(newTitle);
                contentList.add(newContent);
            }
            WhatsNewAdapter whatsNewAdapter = new WhatsNewAdapter(context, titleList, contentList);
            lv.setAdapter(whatsNewAdapter);
            dialog.show();
            continueButton.setOnClickListener(view -> dialog.dismiss());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load data from json file
     *
     * @param context - current context
     * @return - json
     */
    private static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("whatsnew.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
