package swati4star.createpdf.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.ImageEditor;
import swati4star.createpdf.activity.PreviewActivity;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.model.ImageToPDFOptions;
import swati4star.createpdf.util.CreatePdf;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PageSizeUtils;
import swati4star.createpdf.util.StringUtils;

import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;
import static swati4star.createpdf.util.Constants.IMAGE_EDITOR_KEY;
import static swati4star.createpdf.util.Constants.PREVIEW_IMAGES;
import static swati4star.createpdf.util.Constants.RESULT;
import static swati4star.createpdf.util.ImageEnhancementOptionsUtils.getEnhancementOptions;


/**
 * ImageToPdfFragment fragment to start with creating PDF
 */
public class ImageToPdfFragment extends Fragment implements EnhancementOptionsAdapter.OnItemClickListner,
        OnPDFCreatedInterface {

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private static final int INTENT_REQUEST_APPLY_FILTER = 10;
    private static final int INTENT_REQUEST_PREVIEW_IMAGE = 11;

    private static int mImageCounter = 0;
    private ArrayList<EnhancementOptionsEntity> mEnhancementOptionsEntityArrayList = new ArrayList<>();
    @BindView(R.id.addImages)
    MorphingButton addImages;
    @BindView(R.id.pdfCreate)
    MorphingButton mCreatePdf;
    @BindView(R.id.pdfOpen)
    MorphingButton mOpenPdf;
    @BindView(R.id.enhancement_options_recycle_view)
    RecyclerView mEnhancementOptionsRecycleView;
    @BindView(R.id.tvNoOfImages)
    TextView mNoOfImages;
    private MorphButtonUtility mMorphButtonUtility;
    private Activity mActivity;
    private ArrayList<String> mImagesUri = new ArrayList<>();
    private final ArrayList<String> mTempUris = new ArrayList<>();
    private String mPath;
    private String mPassword;
    private String mQuality;
    private Rectangle mPageSize = PageSize.A4;
    private boolean mOpenSelectImages = false;
    private SharedPreferences mSharedPreferences;
    private EnhancementOptionsAdapter mEnhancementOptionsAdapter;
    private boolean mPasswordProtected = false;
    private FileUtils mFileUtils;
    private int mButtonClicked = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mOpenPdf.setVisibility(View.GONE);

        // Get runtime permissions if build version >= Android M
        getRuntimePermissions(false);

        showEnhancementOptions();

        mFileUtils = new FileUtils(mActivity);

        // Check for the images received
        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<Parcelable> uris = bundle.getParcelableArrayList(getString(R.string.bundleKey));
            for (Parcelable p : uris) {
                Uri uri = (Uri) p;
                if (mFileUtils.getUriRealPath(uri) == null) {
                    showSnackbar(R.string.whatsappToast);
                } else {
                    mTempUris.add(mFileUtils.getUriRealPath(uri));
                    if (mTempUris.size() > 0) {
                        mNoOfImages.setText(String.format(mActivity.getResources()
                                .getString(R.string.images_selected), mTempUris.size()));
                        mNoOfImages.setVisibility(View.VISIBLE);
                    } else {
                        mNoOfImages.setVisibility(View.GONE);
                    }
                    showSnackbar(R.string.successToast);
                }
            }
        } else {
            mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
            mCreatePdf.setEnabled(false);
        }

        return root;
    }

    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        mEnhancementOptionsEntityArrayList = getEnhancementOptions(mActivity);
        mEnhancementOptionsAdapter = new EnhancementOptionsAdapter(this, mEnhancementOptionsEntityArrayList);
        mEnhancementOptionsRecycleView.setAdapter(mEnhancementOptionsAdapter);
    }

    /**
     * Adding Images to PDF
     */
    @OnClick(R.id.addImages)
    void startAddingImages() {
        if (mButtonClicked == 0) {
            if (getRuntimePermissions(true))
                selectImages();
            mButtonClicked = 1;
        }
    }

    private void filterImages() {
        if (mTempUris.size() == 0)
            showSnackbar(R.string.snackbar_no_images);
        else {
            // Apply filters
            try {
                Intent intent = new Intent(getContext(), ImageEditor.class);
                intent.putStringArrayListExtra(IMAGE_EDITOR_KEY, mTempUris);
                startActivityForResult(intent, INTENT_REQUEST_APPLY_FILTER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Create Pdf of selected images
    @SuppressWarnings("unchecked")
    @OnClick({R.id.pdfCreate})
    void createPdf() {
        if (mImagesUri.size() == 0) {
            if (mTempUris.size() == 0) {
                showSnackbar(R.string.snackbar_no_images);
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
                .input(getString(R.string.example), null, (dialog, input) -> {
                    if (StringUtils.isEmpty(input)) {
                        showSnackbar(R.string.snackbar_name_not_blank);
                    } else {
                        final String filename = input.toString();
                        FileUtils utils = new FileUtils(mActivity);
                        if (!utils.isFileExist(filename + getString(R.string.pdf_ext))) {
                            new CreatePdf(mActivity, new ImageToPDFOptions(filename, mPageSize,
                                    mPasswordProtected, mPassword, mQuality, mImagesUri),
                                    ImageToPdfFragment.this).execute();
                        } else {
                            new MaterialDialog.Builder(mActivity)
                                    .title(R.string.warning)
                                    .content(R.string.overwrite_message)
                                    .positiveText(android.R.string.ok)
                                    .negativeText(android.R.string.cancel)
                                    .onPositive((dialog12, which) -> new CreatePdf(
                                            mActivity, new ImageToPDFOptions(filename, mPageSize,
                                            mPasswordProtected, mPassword, mQuality, mImagesUri),
                                            ImageToPdfFragment.this).execute())
                                    .onNegative((dialog1, which) -> createPdf())
                                    .show();
                        }
                    }
                })
                .show();
    }

    @OnClick(R.id.pdfOpen)
    void openPdf() {
        mFileUtils.openFile(mPath);
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
                    showSnackbar(R.string.snackbar_permissions_given);
                } else
                    showSnackbar(R.string.snackbar_insufficient_permissions);
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
        mButtonClicked = 0;
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case INTENT_REQUEST_GET_IMAGES:

                mTempUris.clear();
                ArrayList<Uri> imageUris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
                for (Uri uri : imageUris)
                    mTempUris.add(uri.getPath());
                if (imageUris.size() > 0) {
                    mNoOfImages.setText(String.format(mActivity.getResources()
                            .getString(R.string.images_selected), imageUris.size()));
                    mNoOfImages.setVisibility(View.VISIBLE);
                    showSnackbar(R.string.snackbar_images_added);
                    mCreatePdf.setEnabled(true);
                } else {
                    mNoOfImages.setVisibility(View.GONE);
                }
                mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
                mOpenPdf.setVisibility(View.GONE);

                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:

                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Uri resultUri = result.getUri();
                        mImagesUri.add(resultUri.getPath());
                        showSnackbar(R.string.snackbar_imagecropped);
                        break;
                    case CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE:
                        Exception error = result.getError();
                        showSnackbar(R.string.snackbar_error_getCropped);
                        error.printStackTrace();
                    default:
                        mImagesUri.add(mTempUris.get(mImageCounter));
                }
                mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
                mImageCounter++;
                next();
                break;
            case INTENT_REQUEST_APPLY_FILTER:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        try {
                            mImagesUri.clear();
                            mTempUris.clear();
                            ArrayList<String> mFilterUris = data.getStringArrayListExtra(RESULT);
                            int size = mFilterUris.size() - 1;
                            for (int k = 0; k <= size; k++) {
                                mTempUris.add(mFilterUris.get(k));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
                break;

            case INTENT_REQUEST_PREVIEW_IMAGE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        try {
                            mImagesUri.clear();
                            mTempUris.clear();
                            ArrayList<String> uris = data.getStringArrayListExtra(RESULT);
                            mImagesUri = uris;
                            mTempUris.addAll(uris);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
                break;
        }
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
            case 3:
                filterImages();
                break;
            case 4:
                setPageSize();
                break;
            case 5:
                previewPDF();
                break;
            default:
                break;
        }
    }

    private void previewPDF() {
        if (mImagesUri.size() == 0) {
            if (mTempUris.size() == 0) {
                showSnackbar(R.string.snackbar_no_images);
                return;
            } else
                mImagesUri = (ArrayList<String>) mTempUris.clone();
        }

        if (mImagesUri.size() < mTempUris.size()) {
            for (int i = mImagesUri.size(); i < mTempUris.size(); i++) {
                mImagesUri.add(mTempUris.get(i));
            }
        }

        Intent intent = new Intent(mActivity, PreviewActivity.class);
        intent.putExtra(PREVIEW_IMAGES, mImagesUri);
        startActivityForResult(intent, INTENT_REQUEST_PREVIEW_IMAGE);
    }

    private void setPageSize() {
        if (mTempUris.size() == 0) {
            showSnackbar(R.string.snackbar_no_images);
            return;
        }

        new MaterialDialog.Builder(mActivity)
                .title(R.string.set_page_size_text)
                .customView(R.layout.set_page_size_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog1, which) -> {
                    View view = dialog1.getCustomView();
                    RadioGroup radioGroup = view.findViewById(R.id.radio_group_page_size);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    Spinner spinnerA = view.findViewById(R.id.spinner_page_size_a0_a10);
                    Spinner spinnerB = view.findViewById(R.id.spinner_page_size_b0_b10);
                    PageSizeUtils utils = new PageSizeUtils();
                    mPageSize = utils.getPageSize(selectedId, spinnerA.getSelectedItem().toString(),
                            spinnerB.getSelectedItem().toString());
                }).show();
    }

    private void compressImage() {

        if (mTempUris.size() == 0) {
            showSnackbar(R.string.snackbar_no_images);
            return;
        }

        String title = getString(R.string.compress_image) + " " +
                mSharedPreferences.getInt(DEFAULT_COMPRESSION, 30) + "%)";

        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(title)
                .customView(R.layout.compress_image_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive((dialog1, which) -> {
                    final EditText qualityInput = dialog1.getCustomView().findViewById(R.id.quality);
                    final CheckBox cbSetDefault = dialog1.getCustomView().findViewById(R.id.cbSetDefault);

                    int check;
                    try {
                        check = Integer.parseInt(String.valueOf(qualityInput.getText()));
                        if (check > 100 || check < 0) {
                            showSnackbar(R.string.invalid_entry);
                        } else {
                            mQuality = String.valueOf(check);
                            if (cbSetDefault.isChecked()) {
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putInt(DEFAULT_COMPRESSION, check);
                                editor.apply();
                            }
                            showCompression();
                        }
                    } catch (NumberFormatException e) {
                        showSnackbar(R.string.invalid_entry);
                    }
                }).build();

        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        final EditText qualityValueInput = dialog.getCustomView().findViewById(R.id.quality);
        qualityValueInput.addTextChangedListener(
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
            showSnackbar(R.string.snackbar_no_images);
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
                            showSnackbar(R.string.snackbar_password_cannot_be_blank);
                        } else {
                            mPassword = input.toString();
                            mPasswordProtected = true;
                            onPasswordAdded();
                        }
                    }
                });
        if (StringUtils.isNotEmpty(mPassword)) {
            neutralAction.setOnClickListener(v -> {
                mPassword = null;
                onPasswordRemoved();
                mPasswordProtected = false;
                dialog.dismiss();
                showSnackbar(R.string.password_remove);
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
        mNoOfImages.setVisibility(View.GONE);
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
            showSnackbar(R.string.snackbar_no_images);
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

    private boolean getRuntimePermissions(boolean openImagesActivity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)) {
                mOpenSelectImages = openImagesActivity; // if We want next activity to open after getting permissions
                requestPermissions(new String[]{
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

    private void showSnackbar(int resID) {
        Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                resID, Snackbar.LENGTH_LONG).show();
    }

}
