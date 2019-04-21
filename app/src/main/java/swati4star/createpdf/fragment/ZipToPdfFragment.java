package swati4star.createpdf.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dd.morphingbutton.MorphingButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.ZipToPdf;

import static swati4star.createpdf.util.ResultUtils.checkResultValidity;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class ZipToPdfFragment extends Fragment {
    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 145;
    private String mPath;
    private Activity mActivity;
    private boolean mPermissionGranted = false;

    @BindView(R.id.selectFile)
    MorphingButton selectFileButton;
    @BindView(R.id.zip_to_pdf)
    MorphingButton convertButton;
    @BindView(R.id.progressBar)
    ProgressBar extractionProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_zip_to_pdf, container, false);
        ButterKnife.bind(this, rootview);
        mActivity = getActivity();
        mPermissionGranted = isPermissionGranted();
        return rootview;
    }

    @OnClick(R.id.selectFile)
    public void showFileChooser() {
        if (!mPermissionGranted) {
            getRuntimePermissions();
            return;
        }
        String folderPath = Environment.getExternalStorageDirectory() + "/";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Uri myUri = Uri.parse(folderPath);
        intent.setDataAndType(myUri, "application/zip");

        startActivityForResult(Intent.createChooser(intent, getString(R.string.merge_file_select)),
                INTENT_REQUEST_PICKFILE_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) throws NullPointerException {
        if (!checkResultValidity(resultCode, data))
            return;

        if (requestCode == INTENT_REQUEST_PICKFILE_CODE) {
            mPath = RealPathUtil.getRealPath(getContext(), data.getData());
            if (mPath != null) {
                convertButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.zip_to_pdf)
    public void convertZipToPdf() {

        // Pre conversion tasks
        extractionProgress.setVisibility(View.VISIBLE);
        selectFileButton.blockTouch();
        convertButton.blockTouch();

        // do the task!
        ZipToPdf.convertZipToPDF(mPath, mActivity);

        //conversion done
        extractionProgress.setVisibility(View.GONE);
        selectFileButton.unblockTouch();
        convertButton.unblockTouch();
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length < 1)
            return;
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = true;
                    showFileChooser();
                } else
                    showSnackbar(mActivity, R.string.snackbar_insufficient_permissions);
            }
        }
    }
}
