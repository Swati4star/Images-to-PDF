package swati4star.createpdf.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM History order by mId desc")
    List<History> getAllHistory();

    @Insert
    void insertAll(History... histories);

    @Query("Delete from History")
    void deleteHistory();
}