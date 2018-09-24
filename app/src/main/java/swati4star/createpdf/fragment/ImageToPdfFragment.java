package swati4star.createpdf.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.GrayscaleTransformation;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.ImageEditor;
import swati4star.createpdf.activity.PreviewActivity;
import swati4star.createpdf.activity.RearrangeImages;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.OnItemClickListner;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.model.ImageToPDFOptions;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.CreatePdf;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PageSizeUtils;
import swati4star.createpdf.util.StringUtils;

import static swati4star.createpdf.util.Constants.AUTHORITY_APP;
import static swati4star.createpdf.util.Constants.DEFAULT_BORDER_WIDTH;
import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;
import static swati4star.createpdf.util.Constants.DEFAULT_IMAGE_BORDER_TEXT;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_SIZE;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_SIZE_TEXT;
import static swati4star.createpdf.util.Constants.DEFAULT_QUALITY_VALUE;
import static swati4star.createpdf.util.Constants.OPEN_SELECT_IMAGES;
import static swati4star.createpdf.util.Constants.RESULT;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.DialogUtils.createAnimationDialog;
import static swati4star.createpdf.util.DialogUtils.createCustomDialog;
import static swati4star.createpdf.util.DialogUtils.createCustomDialogWithoutContent;
import static swati4star.createpdf.util.DialogUtils.createOverwriteDialog;
import static swati4star.createpdf.util.ImageEnhancementOptionsUtils.getEnhancementOptions;
import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;
import static swati4star.createpdf.util.StringUtils.getSnackbarwithAction;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

/**
 * ImageToPdfFragment fragment to start with creating PDF
 */
public class ImageToPdfFragment extends Fragment implements OnItemClickListner,
        OnPDFCreatedInterface {

    private static final int INTENT_REQUEST_APPLY_FILTER = 10;
    private static final int INTENT_REQUEST_PREVIEW_IMAGE = 11;
    private static final int INTENT_REQUEST_REARRANGE_IMAGE = 12;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;

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
    private String mPath;
    private boolean mOpenSelectImages = false;
    private SharedPreferences mSharedPreferences;
    private FileUtils mFileUtils;
    private PageSizeUtils mPageSizeUtils;
    private int mButtonClicked = 0;
    private static int mImageCounter;
    private ImageToPDFOptions mPdfOptions;
    private MaterialDialog mMaterialDialog;
    private String mHomePath;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_images_to_pdf, container, false);
        ButterKnife.bind(this, root);

        // Initialize variables
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mPageSizeUtils = new PageSizeUtils(mActivity);
        mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
        mHomePath = mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation());

        // Get runtime permissions if build version >= Android M
        getRuntimePermissions(false);

        // Get default values & show enhancement options
        resetValues();

        // Check for the images received
        checkForImagesInBundle();

        return root;
    }

    /**
     * Adds images (if any) received in the bundle
     */
    private void checkForImagesInBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getBoolean(OPEN_SELECT_IMAGES))
                startAddingImages();
            ArrayList<Parcelable> uris = bundle.getParcelableArrayList(getString(R.string.bundleKey));
            if (uris == null)
                return;
            for (Parcelable p : uris) {
                Uri uri = (Uri) p;
                if (mFileUtils.getUriRealPath(uri) == null) {
                    showSnackbar(mActivity, R.string.whatsappToast);
                } else {
                    mImagesUri.add(mFileUtils.getUriRealPath(uri));
                    if (mImagesUri.size() > 0) {
                        mNoOfImages.setText(String.format(mActivity.getResources()
                                .getString(R.string.images_selected), mImagesUri.size()));
                        mNoOfImages.setVisibility(View.VISIBLE);
                    } else {
                        mNoOfImages.setVisibility(View.GONE);
                    }
                    showSnackbar(mActivity, R.string.successToast);
                }
            }
        }
    }

    /**
     * Shows enhancement options
     */
    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        ArrayList<EnhancementOptionsEntity> list = getEnhancementOptions(mActivity, mPdfOptions);
        EnhancementOptionsAdapter adapter =
                new EnhancementOptionsAdapter(this, list);
        mEnhancementOptionsRecycleView.setAdapter(adapter);
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

    /**
     * Create Pdf of selected images
     */
    @OnClick({R.id.pdfCreate})
    void createPdf() {
        mPdfOptions.setImagesUri(mImagesUri);
        mPdfOptions.setPageSize(PageSizeUtils.mPageSize);
        MaterialDialog.Builder builder = createCustomDialog(mActivity,
                R.string.creating_pdf, R.string.enter_file_name);
        builder.input(getString(R.string.example), null, (dialog, input) -> {
            if (StringUtils.isEmpty(input)) {
                showSnackbar(mActivity, R.string.snackbar_name_not_blank);
            } else {
                final String filename = input.toString();
                FileUtils utils = new FileUtils(mActivity);
                if (!utils.isFileExist(filename + getString(R.string.pdf_ext))) {
                    mPdfOptions.setOutFileName(filename);
                    new CreatePdf(mPdfOptions, mHomePath,
                            ImageToPdfFragment.this).execute();
                } else {
                    MaterialDialog.Builder builder2 = createOverwriteDialog(mActivity);
                    builder2.onPositive((dialog2, which) -> {
                        mPdfOptions.setOutFileName(filename);
                        new CreatePdf(mPdfOptions, mHomePath, ImageToPdfFragment.this).execute();
                    }).onNegative((dialog1, which) -> createPdf()).show();
                }
            }
        }).show();
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

        if (grantResults.length < 1)
            return;

        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mOpenSelectImages)
                        selectImages();
                    showSnackbar(mActivity, R.string.snackbar_permissions_given);
                } else
                    showSnackbar(mActivity, R.string.snackbar_insufficient_permissions);
            }
        }
    }

    /**
     * Called after Matisse Activity is called
     *
     * @param requestCode REQUEST Code for opening Matisse Activity
     * @param resultCode  result code of the process
     * @param data        Data of the image selected
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mButtonClicked = 0;
        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        switch (requestCode) {
            case INTENT_REQUEST_GET_IMAGES:
                mImagesUri.clear();
                List<Uri> imageUris = Matisse.obtainResult(data);
                for (Uri uri : imageUris)
                    mImagesUri.add(mFileUtils.getUriRealPath(uri));
                if (imageUris.size() > 0) {
                    mNoOfImages.setText(String.format(mActivity.getResources()
                            .getString(R.string.images_selected), imageUris.size()));
                    mNoOfImages.setVisibility(View.VISIBLE);
                    showSnackbar(mActivity, R.string.snackbar_images_added);
                    mCreatePdf.setEnabled(true);
                } else {
                    mNoOfImages.setVisibility(View.GONE);
                }
                mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
                mOpenPdf.setVisibility(View.GONE);
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                if (mImagesUri.size() > mImageCounter)
                    mImagesUri.set(mImageCounter, resultUri.getPath());
                showSnackbar(mActivity, R.string.snackbar_imagecropped);
                mImageCounter++;
                cropNextImage();
                break;

            case INTENT_REQUEST_APPLY_FILTER:
                mImagesUri.clear();
                ArrayList<String> mFilterUris = data.getStringArrayListExtra(RESULT);
                int size = mFilterUris.size() - 1;
                for (int k = 0; k <= size; k++)
                    mImagesUri.add(mFilterUris.get(k));
                break;

            case INTENT_REQUEST_PREVIEW_IMAGE:
                mImagesUri = data.getStringArrayListExtra(RESULT);
                if (mImagesUri.size() > 0) {
                    mNoOfImages.setText(String.format(mActivity.getResources()
                            .getString(R.string.images_selected), mImagesUri.size()));
                } else {
                    mNoOfImages.setVisibility(View.GONE);
                    mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
                    mCreatePdf.setEnabled(false);
                }
                break;

            case INTENT_REQUEST_REARRANGE_IMAGE:
                mImagesUri = data.getStringArrayListExtra(RESULT);
                showSnackbar(mActivity, R.string.images_rearranged);
                break;
        }
    }

    @Override
    public void onItemClick(int position) {

        if (mImagesUri.size() == 0) {
            showSnackbar(mActivity, R.string.snackbar_no_images);
            return;
        }
        switch (position) {
            case 0:
                passwordProtectPDF();
                break;
            case 1:
                cropNextImage();
                break;
            case 2:
                compressImage();
                break;
            case 3:
                startActivityForResult(ImageEditor.getStartIntent(mActivity, mImagesUri),
                        INTENT_REQUEST_APPLY_FILTER);
                break;
            case 4:
                mPageSizeUtils.showPageSizeDialog(R.layout.set_page_size_dialog, false);
                break;
            case 5:
                startActivityForResult(PreviewActivity.getStartIntent(mActivity, mImagesUri),
                        INTENT_REQUEST_PREVIEW_IMAGE);
                break;
            case 6:
                addBorder();
                break;
            case 7:
                startActivityForResult(RearrangeImages.getStartIntent(mActivity, mImagesUri),
                        INTENT_REQUEST_REARRANGE_IMAGE);
                break;
            case 8:
                saveCurrentImage();
                createPdf();
                break;
        }
    }

    /**
     * Saves Current Image with grayscale filter
     */
    private void saveCurrentImage() {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/PDFfilter");
            dir.mkdirs();
            Picasso picasso = Picasso.with(getContext());
            Transformation transformation = new GrayscaleTransformation();

            for (int countElements = mImagesUri.size() - 1; countElements >= 0; countElements--) {
                String fileName = String.format(getString(R.string.filter_file_name),
                        String.valueOf(System.currentTimeMillis()), "grayscale");
                File outFile = new File(dir, fileName);
                String imagePath = outFile.getAbsolutePath();
                picasso.load(new File(mImagesUri.get(countElements)))
                        .transform(transformation)
                        .into(getTarget(imagePath));
                mImagesUri.remove(countElements);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void addBorder() {
        createCustomDialogWithoutContent(mActivity, R.string.border)
                .customView(R.layout.dialog_border_image, true)
                .onPositive((dialog1, which) -> {
                    View view = dialog1.getCustomView();
                    final EditText input = view.findViewById(R.id.border_width);
                    int value = 0;
                    try {
                        value = Integer.parseInt(String.valueOf(input.getText()));
                        if (value > 200 || value < 0) {
                            showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            mPdfOptions.setBorderWidth(value);
                            showEnhancementOptions();
                        }
                    } catch (NumberFormatException e) {
                        showSnackbar(mActivity, R.string.invalid_entry);
                    }
                    final CheckBox cbSetDefault = view.findViewById(R.id.cbSetDefault);
                    if (cbSetDefault.isChecked()) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putInt(Constants.DEFAULT_IMAGE_BORDER_TEXT, value);
                        editor.apply();
                    }
                }).build().show();
    }

    private void compressImage() {
        createCustomDialogWithoutContent(mActivity, R.string.compression_image_edit)
                .customView(R.layout.compress_image_dialog, true)
                .onPositive((dialog1, which) -> {
                    final EditText qualityInput = dialog1.getCustomView().findViewById(R.id.quality);
                    final CheckBox cbSetDefault = dialog1.getCustomView().findViewById(R.id.cbSetDefault);
                    int check;
                    try {
                        check = Integer.parseInt(String.valueOf(qualityInput.getText()));
                        if (check > 100 || check < 0) {
                            showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            mPdfOptions.setQualityString(String.valueOf(check));
                            if (cbSetDefault.isChecked()) {
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putInt(DEFAULT_COMPRESSION, check);
                                editor.apply();
                            }
                            showEnhancementOptions();
                        }
                    } catch (NumberFormatException e) {
                        showSnackbar(mActivity, R.string.invalid_entry);
                    }
                }).show();
    }

    private void passwordProtectPDF() {
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
        passwordInput.setText(mPdfOptions.getPassword());
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
                            showSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank);
                        } else {
                            mPdfOptions.setPassword(input.toString());
                            mPdfOptions.setPasswordProtected(true);
                            showEnhancementOptions();
                        }
                    }
                });
        if (StringUtils.isNotEmpty(mPdfOptions.getPassword())) {
            neutralAction.setOnClickListener(v -> {
                mPdfOptions.setPasswordProtected(false);
                showEnhancementOptions();
                dialog.dismiss();
                showSnackbar(mActivity, R.string.password_remove);
            });
        }
        dialog.show();
        positiveAction.setEnabled(false);
    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        mMaterialDialog.dismiss();
        if (!success) {
            showSnackbar(mActivity, R.string.snackbar_folder_not_created);
            return;
        }
        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction, v -> mFileUtils.openFile(mPath)).show();
        mOpenPdf.setVisibility(View.VISIBLE);
        mMorphButtonUtility.morphToSuccess(mCreatePdf);
        mPath = path;
        resetValues();
    }

    private void cropNextImage() {
        if (mImageCounter < mImagesUri.size()) {
            CropImage.activity(Uri.fromFile(new File(mImagesUri.get(mImageCounter))))
                    .setActivityMenuIconColor(mMorphButtonUtility.color(R.color.colorPrimary))
                    .setInitialCropWindowPaddingRatio(0)
                    .setAllowRotation(true)
                    .setActivityTitle(getString(R.string.cropImage_activityTitle) + (mImageCounter + 1))
                    .start(mActivity, this);
        } else {
            mImageCounter = 0;
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
     * Opens Matisse activity to select Images
     */
    private void selectImages() {
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, AUTHORITY_APP))
                .maxSelectable(1000)
                .imageEngine(new PicassoEngine())
                .forResult(INTENT_REQUEST_GET_IMAGES);
    }

    /**
     * Resets pdf creation related values & show enhancement options
     */
    private void resetValues() {
        mPdfOptions = new ImageToPDFOptions();
        mPdfOptions.setBorderWidth(mSharedPreferences.getInt(DEFAULT_IMAGE_BORDER_TEXT,
                DEFAULT_BORDER_WIDTH));
        mPdfOptions.setQualityString(
                Integer.toString(mSharedPreferences.getInt(DEFAULT_COMPRESSION,
                        DEFAULT_QUALITY_VALUE)));
        mPdfOptions.setPageSize(mSharedPreferences.getString(DEFAULT_PAGE_SIZE_TEXT,
                DEFAULT_PAGE_SIZE));
        mPdfOptions.setPasswordProtected(false);
        mImagesUri.clear();
        mImageCounter = 0;
        showEnhancementOptions();
        mNoOfImages.setVisibility(View.GONE);
    }

    /**
     * Target need to save the for picasso
     */

    private Target getTarget(final String url) {
        return new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                File file = new File(url);
                try {
                    file.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ostream.flush();
                    ostream.close();
                    mImagesUri.add(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }
}
