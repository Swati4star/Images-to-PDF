package swati4star.createpdf.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM History order by mId desc")
    List<History> getAllHistory();

    @Insert
    void insertAll(History... histories);

    @Query("Delete from History")
    void deleteHistory();

    @Query("select * from history where operation_type IN(:types) order by mId desc")
    List<History> getHistoryByOperationType(String[] types);

    @Dao
    public interface HistoryDao {
        @Query("SELECT COUNT(*) > 0 FROM history WHERE operationType = :operationType")
        boolean existsOperationType(String operationType);
    }

}