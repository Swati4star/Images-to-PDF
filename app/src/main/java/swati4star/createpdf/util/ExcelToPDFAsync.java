package swati4star.createpdf.util;

import android.os.AsyncTask;

import com.aspose.cells.FileFormatType;
import com.aspose.cells.Workbook;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;

public class ExcelToPDFAsync extends AsyncTask<Void, Void, Void> {
    private final OnPDFCreatedInterface mOnPDFCreatedInterface;
    private boolean mSuccess;
    private String mPath;
    private String mDestPath;

    /**
     * This public constructor is responsible for initializing the path of actual file,
     * the destination path and the onPDFCreatedInterface instance.
     * @param parentPath
     * @param destPath
     * @param onPDFCreated
     */
    public ExcelToPDFAsync(String parentPath, String destPath,
                           OnPDFCreatedInterface onPDFCreated) {
        mPath = parentPath;
        mDestPath = destPath;
        this.mOnPDFCreatedInterface = onPDFCreated;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSuccess = true;
        mOnPDFCreatedInterface.onPDFCreationStarted();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            final Workbook workbook = new Workbook(mPath);
            workbook.save(mDestPath, FileFormatType.PDF);
        } catch (Exception e) {
            e.printStackTrace();
            mSuccess = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mOnPDFCreatedInterface.onPDFCreated(mSuccess, mPath);
    }
}
