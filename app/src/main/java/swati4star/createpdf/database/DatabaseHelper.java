package swati4star.createpdf.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.util.Date;

public class DatabaseHelper {
    private final Context mContext;

    public DatabaseHelper(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * To insert record in the database
     * @param filePath path of the file
     * @param operationType operation performed on file
     */
    public void insertRecord(String filePath, String operationType) {
        new Insert().execute(new History(filePath,  new Date().toString(), operationType));
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