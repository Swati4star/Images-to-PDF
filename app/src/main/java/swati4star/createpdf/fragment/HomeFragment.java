package swati4star.createpdf.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;
import swati4star.createpdf.util.CreatePdf;
import swati4star.createpdf.util.EnhancementOptionsEntity;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.StringUtils;


/**
 * HomeFragment fragment to start with creating PDF
 */
public class HomeFragment extends Fragment implements EnhancementOptionsAdapter.OnItemClickListner,
        OnPDFCreatedInterface {

    public static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;

    private static int mImageCounter = 0;

    private MorphButtonUtility mMorphButtonUtility;
    private Activity mActivity;
    private ArrayList<String> mImagesUri = new ArrayList<>();
    private final ArrayList<String> mTempUris = new ArrayList<>();
    private String mPath;
    private String mPassword;
    private String mQuality;
    private boolean mOpenSelectImages = false;
    SharedPreferences preferences;

    @BindView(R.id.addImages)
    MorphingButton addImages;
    @BindView(R.id.pdfCreate)
    MorphingButton mCreatePdf;
    @BindView(R.id.pdfOpen)
    MorphingButton mOpenPdf;
    @BindView(R.id.enhancement_options_recycle_view)
    RecyclerView mEnhancementOptionsRecycleView;

    private EnhancementOptionsAdapter mEnhancementOptionsAdapter;
    private final ArrayList<EnhancementOptionsEntity> mEnhancementOptionsEntityArrayList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);

        preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);

        mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
        mOpenPdf.setVisibility(View.GONE);

        // Get runtime permissions if build version >= Android M
        getRuntimePermissions(false);

        showEnhancementOptions();

        return root;
    }

    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        mEnhancementOptionsAdapter = new EnhancementOptionsAdapter(this, getEnhancementOptions());
        mEnhancementOptionsRecycleView.setAdapter(mEnhancementOptionsAdapter);
    }

    // Adding Images to PDF
    @OnClick(R.id.addImages)
    void startAddingImages() {
        if (getRuntimePermissions(true))
            selectImages();
    }

    // Create Pdf of selected images
    @SuppressWarnings("unchecked")
    @OnClick({R.id.pdfCreate})
    void createPdf() {
        if (mImagesUri.size() == 0) {
            if (mTempUris.size() == 0) {
                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                        R.string.snackbar_no_images,
                        Snackbar.LENGTH_LONG).show();
                return;
            } else
                mImagesUri = (ArrayList<String>) mTempUris.clone();
        }

        if (mImagesUri.size() < mTempUris.size()) {
            for (int i = mImagesUri.size(); i < mTempUris.size(); i++) {
                mImagesUri.add(mTempUris.get(i));
            }
        }

        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (StringUtils.isEmpty(input)) {
                            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                    R.string.snackbar_name_not_blank,
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            String filename = input.toString();

                            new CreatePdf(mActivity, mImagesUri, filename, mPassword, mQuality,
                                    HomeFragment.this).execute();
                        }
                    }
                })
                .show();
    }

    @OnClick(R.id.pdfOpen)
    void openPdf() {
        File file = new File(mPath);
        Intent target = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(mActivity, "com.swati4star.shareFile", file);

        target.setDataAndType(uri, getString(R.string.pdf_type));
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, getString(R.string.open_file));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                    R.string.snackbar_no_pdf_app,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Called after user is asked to grant permissions
     *
     * @param requestCode  REQUEST Code for opening permissions
     * @param permissions  permissions asked to user
     * @param grantResults bool array indicating if permission is granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mOpenSelectImages)
                        selectImages();
                    Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                            R.string.snackbar_permissions_given,
                            Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                            R.string.snackbar_insufficient_permissions,
                            Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Called after ImagePickerActivity is called
     *
     * @param requestCode REQUEST Code for opening ImagePickerActivity
     * @param resultCode  result code of the process
     * @param data        Data of the image selected
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == INTENT_REQUEST_GET_IMAGES) {

            mTempUris.clear();
            ArrayList<Uri> imageUris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            for (Uri uri : imageUris)
                mTempUris.add(uri.getPath());
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                    R.string.snackbar_images_added,
                    Snackbar.LENGTH_LONG).show();
            mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
            mOpenPdf.setVisibility(View.GONE);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Uri resultUri = result.getUri();
                    mImagesUri.add(resultUri.getPath());
                    Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                            R.string.snackbar_imagecropped,
                            Snackbar.LENGTH_LONG).show();
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE:
                    Exception error = result.getError();
                    Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                            R.string.snackbar_error_getCropped,
                            Snackbar.LENGTH_LONG).show();
                    error.printStackTrace();
                default:
                    mImagesUri.add(mTempUris.get(mImageCounter));
            }
            mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
            mImageCounter++;
            next();
        }
    }

    public List<EnhancementOptionsEntity> getEnhancementOptions() {
        mEnhancementOptionsEntityArrayList.clear();

        mEnhancementOptionsEntityArrayList.add(
                new EnhancementOptionsEntity(getResources().getDrawable(R.drawable.baseline_enhanced_encryption_24),
                        getResources().getString(R.string.password_protect_pdf_text)));

        mEnhancementOptionsEntityArrayList.add(
                new EnhancementOptionsEntity(getResources().getDrawable(R.drawable.baseline_crop_rotate_24),
                        getResources().getString(R.string.edit_images_text)));

        mEnhancementOptionsEntityArrayList.add(
                new EnhancementOptionsEntity(getResources().getDrawable(R.drawable.pdf_compress),
                        getString(R.string.compress_image) + " " +
                                preferences.getInt("DefaultCompression", 30) + "%)"));
        return mEnhancementOptionsEntityArrayList;
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                passwordProtectPDF();
                break;
            case 1:
                cropImages();
                break;
            case 2:
                compressImage();
                break;
            default:
                break;
        }
    }

    private void compressImage()  {

        if (mTempUris.size() == 0) {
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                    R.string.snackbar_no_images,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        String title = getString(R.string.compress_image) + " " + preferences.getInt("DefaultCompression", 30) + "%)";

        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(title)
                .customView(R.layout.compress_image_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        final EditText passwordInput = dialog.getCustomView().findViewById(R.id.quality);
                        final CheckBox cbSetDefault = dialog.getCustomView().findViewById(R.id.cbSetDefault);

                        int check;
                        try {
                            check = Integer.parseInt(String.valueOf(passwordInput.getText()));
                            if (check > 100 || check < 0) {
                                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                        R.string.invalid_quality,
                                        Snackbar.LENGTH_LONG).show();
                            } else {
                                mQuality = String.valueOf(check);
                                if (cbSetDefault.isChecked()) {
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putInt("DefaultCompression", check);
                                    editor.apply();
                                }
                                showCompression();
                            }
                        } catch (NumberFormatException e) {
                            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                    R.string.invalid_quality,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).build();

        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        final EditText passwordInput = dialog.getCustomView().findViewById(R.id.quality);
        passwordInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                    }
                });
        dialog.show();
        positiveAction.setEnabled(false);
    }

    private void passwordProtectPDF() {
        if (mTempUris.size() == 0) {
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                    R.string.snackbar_no_images,
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.set_password)
                .customView(R.layout.custom_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .neutralText(R.string.remove_dialog)
                .build();

        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        final View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);
        final EditText passwordInput = dialog.getCustomView().findViewById(R.id.password);
        passwordInput.setText(mPassword);
        passwordInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                        if (StringUtils.isEmpty(input)) {
                            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                    R.string.snackbar_password_cannot_be_blank,
                                    Snackbar.LENGTH_LONG).show();
                        } else {
                            mPassword = input.toString();
                            onPasswordAdded();
                        }
                    }
                });
        if (StringUtils.isNotEmpty(mPassword)) {
            neutralAction.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mPassword = null;
                    onPasswordRemoved();
                    dialog.dismiss();
                    Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                            R.string.password_remove,
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }
        dialog.show();
        positiveAction.setEnabled(false);
    }

    private void showCompression() {
        mEnhancementOptionsEntityArrayList.get(2)
                .setName(mQuality + "% Compressed");
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    private void onPasswordAdded() {
        mEnhancementOptionsEntityArrayList.get(0)
                .setImage(getResources().getDrawable(R.drawable.baseline_done_24));
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    private void onPasswordRemoved() {
        mEnhancementOptionsEntityArrayList.get(0)
                .setImage(getResources().getDrawable(R.drawable.baseline_enhanced_encryption_24));
        mEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        mImagesUri.clear();
        mTempUris.clear();
        mImageCounter = 0;
        mPassword = null;
        if (success) {
            mOpenPdf.setVisibility(View.VISIBLE);
            mMorphButtonUtility.morphToSuccess(mCreatePdf);
            mPath = path;
        }
    }

    void cropImages() {
        if (mTempUris.size() == 0) {
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                    R.string.snackbar_no_images,
                    Snackbar.LENGTH_LONG).show();
            return;
        }
        next();
    }

    private void next() {
        if (mImageCounter != mTempUris.size() && mImageCounter < mTempUris.size()) {
            CropImage.activity(Uri.fromFile(new File(mTempUris.get(mImageCounter))))
                    .setActivityMenuIconColor(mMorphButtonUtility.color(R.color.colorPrimary))
                    .setInitialCropWindowPaddingRatio(0)
                    .setAllowRotation(true)
                    .setActivityTitle(getString(R.string.cropImage_activityTitle) + (mImageCounter + 1))
                    .start(mActivity, this);
        }
    }

    boolean getRuntimePermissions(boolean openImagesActivity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)) {
                mOpenSelectImages = openImagesActivity; // if We want next activity to open after getting permissions
                requestPermissions( new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
                return false;
            }
        }
        return true;
    }


    /**
     * Opens ImagePickerActivity to select Images
     */
    private void selectImages() {

        Config config = new Config();
        config.setToolbarTitleRes(R.string.image_picker_activity_toolbar_title);
        ImagePickerActivity.setConfig(config);

        Intent intent = new Intent(mActivity, ImagePickerActivity.class);

        //add to intent the URIs of the already selected images
        //first they are converted to Uri objects
        ArrayList<Uri> uris = new ArrayList<>(mTempUris.size());
        for (String stringUri : mTempUris) {
            uris.add(Uri.fromFile(new File(stringUri)));
        }
        // add them to the intent
        intent.putExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, uris);

        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }
}
