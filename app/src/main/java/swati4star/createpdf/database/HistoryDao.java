package swati4star.createpdf.database;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @NonNull
    @Query("SELECT * FROM History order by mId desc")
    List<History> getAllHistory();

    @Insert
    void insertAll(@NonNull History... histories);

    @Query("Delete from History")
    void deleteHistory();

    @NonNull
    @Query("select * from history where operation_type IN(:types) order by mId desc")
    List<History> getHistoryByOperationType(@NonNull String[] types);
}