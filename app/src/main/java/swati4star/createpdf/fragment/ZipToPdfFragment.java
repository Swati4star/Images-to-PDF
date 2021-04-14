package swati4star.createpdf.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dd.morphingbutton.MorphingButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.ResultUtils;
import swati4star.createpdf.util.ZipToPdf;

import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;

public class ZipToPdfFragment extends Fragment {
    private static final int INTENT_REQUEST_PICK_FILE_CODE = 10;
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
        View rootView = inflater.inflate(R.layout.fragment_zip_to_pdf, container, false);
        ButterKnife.bind(this, rootView);
        mActivity = getActivity();
        return rootView;
    }

    @OnClick(R.id.selectFile)
    public void showFileChooser() {
        if (isStoragePermissionGranted()) {
            chooseFile();
        } else {
            getRuntimePermissions();
        }
    }

    private void chooseFile() {
        String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/";
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        Uri myUri = Uri.parse(folderPath);
        intent.setDataAndType(myUri, "application/zip");

        startActivityForResult(Intent.createChooser(intent, getString(R.string.merge_file_select)),
                INTENT_REQUEST_PICK_FILE_CODE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) throws NullPointerException {
        if (!ResultUtils.getInstance().checkResultValidity(resultCode, data))
            return;

        if (requestCode == INTENT_REQUEST_PICK_FILE_CODE) {
            mPath = RealPathUtil.getInstance().getRealPath(getContext(), data.getData());
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
        ZipToPdf.getInstance().convertZipToPDF(mPath, mActivity);

        //conversion done
        extractionProgress.setVisibility(View.GONE);
        selectFileButton.unblockTouch();
        convertButton.unblockTouch();
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 29) {
            return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }
    private void getRuntimePermissions() {
        if (Build.VERSION.SDK_INT < 29) {
            PermissionsUtils.getInstance().requestRuntimePermissions(this,
                    WRITE_PERMISSIONS,
                    REQUEST_CODE_FOR_WRITE_PERMISSION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsUtils.getInstance().handleRequestPermissionsResult(mActivity, grantResults,
                requestCode, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT, this::chooseFile);
    }
}
