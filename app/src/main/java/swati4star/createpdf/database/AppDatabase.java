package swati4star.createpdf.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import swati4star.createpdf.util.Constants;

@Database(entities = {History.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, Constants.DATABASE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract HistoryDao historyDao();
}