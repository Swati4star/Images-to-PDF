package swati4star.createpdf.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHelper {
    private Context mContext;

    public DatabaseHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void insertRecord(String fileName, String operationType) {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        new Insert().execute(new History(fileName, date, operationType));

    }

    @SuppressLint("StaticFieldLeak")
    private class Insert extends AsyncTask<History, Void, Void> {

        @Override
        protected Void doInBackground(History... histories) {
            AppDatabase db = AppDatabase.getDatabase(mContext.getApplicationContext());
            db.historyDao().insertAll(histories);
            return null;
        }
    }
}
