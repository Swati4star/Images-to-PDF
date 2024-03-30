package swati4star.createpdf.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class History {
    @PrimaryKey(autoGenerate = true)
    private int mId;
    @ColumnInfo(name = "file_path")
    private String mFilePath;

    @ColumnInfo(name = "date")
    private String mDate;

    @ColumnInfo(name = "operation_type")
    private String mOperationType;

    public History(@NonNull String filePath, @NonNull String date, @NonNull String operationType) {
        this.mFilePath = filePath;
        this.mDate = date;
        this.mOperationType = operationType;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    @NonNull
    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(@NonNull String fileName) {
        this.mFilePath = fileName;
    }

    @NonNull
    public String getDate() {
        return mDate;
    }

    public void setDate(@NonNull String date) {
        this.mDate = date;
    }

    @NonNull
    public String getOperationType() {
        return mOperationType;
    }

    public void setOperationType(@NonNull String operationType) {
        this.mOperationType = operationType;
    }
}
