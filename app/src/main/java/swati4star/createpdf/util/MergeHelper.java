package swati4star.createpdf.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.afollestad.materialdialogs.MaterialDialog;

import swati4star.createpdf.R;
import swati4star.createpdf.adapter.ViewFilesAdapter;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.MergeFilesListener;

import static swati4star.createpdf.util.Constants.MASTER_PWD_STRING;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.appName;

public class MergeHelper implements MergeFilesListener {
    private MaterialDialog mMaterialDialog;
    private final Activity mActivity;
    private final FileUtils mFileUtils;
    private final boolean mPasswordProtected = false;
    private String mPassword;
    private final String mHomePath;
    private final Context mContext;
    private final ViewFilesAdapter mViewFilesAdapter;
    private final SharedPreferences mSharedPrefs;

    public MergeHelper(Activity activity, ViewFilesAdapter viewFilesAdapter) {
        mActivity = activity;
        mFileUtils = new FileUtils(mActivity);
        mHomePath = PreferenceManager.getDefaultSharedPreferences(mActivity)
                .getString(STORAGE_LOCATION,
                        StringUtils.getInstance().getDefaultStorageLocation());
        mContext = mActivity;
        mViewFilesAdapter = viewFilesAdapter;
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
    }

    public void mergeFiles() {
        String[] pdfpaths = mViewFilesAdapter.getSelectedFilePath().toArray(new String[0]);
        String masterpwd = mSharedPrefs.getString(MASTER_PWD_STRING, appName);
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(mContext.getResources().getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.getInstance().isEmpty(input)) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                    } else {
                        if (!mFileUtils.isFileExist(input + mContext.getResources().getString(R.string.pdf_ext))) {
                            new MergePdf(input.toString(), mHomePath, mPasswordProtected,
                                    mPassword, this, masterpwd).execute(pdfpaths);
                        } else {
                            MaterialDialog.Builder builder = DialogUtils.getInstance().createOverwriteDialog(mActivity);
                            builder.onPositive((dialog12, which) -> new MergePdf(input.toString(),
                                    mHomePath, mPasswordProtected, mPassword,
                                    this, masterpwd).execute(pdfpaths))
                                    .onNegative((dialog1, which) -> mergeFiles()).show();
                        }
                    }
                })
                .show();
    }
    @Override
    public void resetValues(boolean isPDFMerged, String path) {
        mMaterialDialog.dismiss();
        if (isPDFMerged) {
            StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.pdf_merged)
                    .setAction(R.string.snackbar_viewAction, v ->
                            mFileUtils.openFile(path, FileUtils.FileType.e_PDF)).show();
            new DatabaseHelper(mActivity).insertRecord(path,
                    mActivity.getString(R.string.created));
        } else
            StringUtils.getInstance().showSnackbar(mActivity, R.string.file_access_error);
        mViewFilesAdapter.updateDataset();
    }

    @Override
    public void mergeStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }
}
