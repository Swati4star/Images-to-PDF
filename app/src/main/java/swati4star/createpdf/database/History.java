package swati4star.createpdf.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class History {
    @PrimaryKey(autoGenerate = true)
    private int mId;
    @ColumnInfo(name = "file_name")
    private String mFileName;

    @ColumnInfo(name = "date")
    private String mDate;

    @ColumnInfo(name = "operation_type")
    private String mOperationType;

    public History(String fileName, String date, String operationType) {
        this.mFileName = fileName;
        this.mDate = date;
        this.mOperationType = operationType;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getOperationType() {
        return mOperationType;
    }

    public void setOperationType(String operationType) {
        this.mOperationType = operationType;
    }
}
