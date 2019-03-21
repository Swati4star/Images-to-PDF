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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.MainActivity;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.RealPathUtil;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;
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
        if (data == null || resultCode != RESULT_OK || data.getData() == null)
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
        final int BUFFER_SIZE = 4096;
        extractionProgress.setVisibility(View.VISIBLE);
        selectFileButton.blockTouch();
        convertButton.blockTouch();

        BufferedOutputStream bufferedOutputStream;
        FileInputStream fileInputStream;
        ArrayList<Uri> imageUris = new ArrayList<>();
        FileUtils.makeAndClearTemp();
        String dest = Environment.getExternalStorageDirectory().toString() +
                Constants.pdfDirectory + Constants.tempDirectory;

        try {
            fileInputStream = new FileInputStream(mPath);
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
            ZipEntry zipEntry;
            int folderPrefix = 0;

            /* In case there are folders in the zip file and in those folders we have images
             *  then we have possibility that file names in different folders are same.
             *  In this case we will use folderPrefix so that all images are copied to temp
             *  for every folder encountered we will increment folderPrefix by one and append
             *  it to the the image name*/
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String zipEntryName = zipEntry.getName().toLowerCase();

                if (zipEntry.isDirectory()) {
                    folderPrefix++;
                } else if (zipEntryName.endsWith(".jpg") || zipEntryName.endsWith(".png")) {
                    String newFileName = "/" + zipEntryName;
                    int index = zipEntryName.lastIndexOf("/");
                    /*index will be -1 when image is in just inside the zip
                     * and not inside some folder*/
                    if (index != -1)
                        newFileName = zipEntryName.substring(index);
                    if (folderPrefix != 0)
                        newFileName = newFileName.replace("/", "/" + folderPrefix + "- ");
                    File newFile = new File(dest + newFileName);
                    imageUris.add(Uri.fromFile(newFile));

                    byte[] buffer = new byte[BUFFER_SIZE];
                    FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER_SIZE);
                    int count;

                    while ((count = zipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        bufferedOutputStream.write(buffer, 0, count);
                    }

                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                }
            }
            zipInputStream.close();

            if (imageUris.size() == 0) {
                StringUtils.showSnackbar(mActivity, R.string.error_no_image_in_zip);
                return;
            }
            /*once we have extracted images out of zip, now we will pass
             * image uri's and to main activity which will then be used to
             * to start images to pdf fragment*/
            ((MainActivity) getActivity()).convertImagesToPdf(imageUris);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            StringUtils.showSnackbar(mActivity, R.string.error_occurred);
        } catch (IOException e) {
            e.printStackTrace();
            StringUtils.showSnackbar(mActivity, R.string.error_occurred);
        } finally {
            extractionProgress.setVisibility(View.GONE);
            selectFileButton.unblockTouch();
            convertButton.unblockTouch();
        }
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
