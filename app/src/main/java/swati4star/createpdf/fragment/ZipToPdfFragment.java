package swati4star.createpdf.fragment;

import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import swati4star.createpdf.R;
import swati4star.createpdf.databinding.FragmentZipToPdfBinding;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.ResultUtils;
import swati4star.createpdf.util.ZipToPdf;

public class ZipToPdfFragment extends Fragment {
    private static final int INTENT_REQUEST_PICK_FILE_CODE = 10;
    private String mPath;
    private Activity mActivity;
    private FragmentZipToPdfBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentZipToPdfBinding.inflate(inflater, container, false);
        View rootView = mBinding.getRoot();
        mActivity = getActivity();

        mBinding.zipToPdf.setOnClickListener(v -> {
            // Pre conversion tasks
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.selectFile.blockTouch();
            mBinding.zipToPdf.blockTouch();

            // do the task!
            ZipToPdf.getInstance().convertZipToPDF(mPath, mActivity);

            //conversion done
            mBinding.progressBar.setVisibility(View.GONE);
            mBinding.selectFile.unblockTouch();
            mBinding.zipToPdf.unblockTouch();
        });

        mBinding.selectFile.setOnClickListener(v -> {
            PermissionsUtils.getInstance().checkStoragePermissionAndProceed(getContext(), this::chooseFile);
        });

        return rootView;
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
                mBinding.zipToPdf.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsUtils.getInstance().handleRequestPermissionsResult(mActivity, grantResults,
                requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::chooseFile);
    }
}
