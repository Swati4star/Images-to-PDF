package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.theartofdev.edmodo.cropper.CropImage;
import com.zhihu.matisse.Matisse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.CropImageActivity;
import swati4star.createpdf.activity.ImageEditor;
import swati4star.createpdf.activity.PreviewActivity;
import swati4star.createpdf.activity.RearrangeImages;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.OnItemClickListener;
import swati4star.createpdf.interfaces.OnPDFCreatedInterface;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.model.ImageToPDFOptions;
import swati4star.createpdf.model.Watermark;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.CreatePdf;
import swati4star.createpdf.util.DefaultTextWatcher;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.ImageEnhancementOptionsUtils;
import swati4star.createpdf.util.ImageUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PageSizeUtils;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.SharedPreferencesUtil;
import swati4star.createpdf.util.StringUtils;

import static swati4star.createpdf.util.Constants.DEFAULT_BORDER_WIDTH;
import static swati4star.createpdf.util.Constants.DEFAULT_COMPRESSION;
import static swati4star.createpdf.util.Constants.DEFAULT_IMAGE_BORDER_TEXT;
import static swati4star.createpdf.util.Constants.DEFAULT_IMAGE_SCALE_TYPE_TEXT;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_COLOR;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_SIZE;
import static swati4star.createpdf.util.Constants.DEFAULT_PAGE_SIZE_TEXT;
import static swati4star.createpdf.util.Constants.DEFAULT_QUALITY_VALUE;
import static swati4star.createpdf.util.Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
import static swati4star.createpdf.util.Constants.MASTER_PWD_STRING;
import static swati4star.createpdf.util.Constants.OPEN_SELECT_IMAGES;
import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.RESULT;
import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;
import static swati4star.createpdf.util.Constants.appName;
import static swati4star.createpdf.util.WatermarkUtils.getStyleNameFromFont;
import static swati4star.createpdf.util.WatermarkUtils.getStyleValueFromName;

/**
 * ImageToPdfFragment fragment to start with creating PDF
 */
public class ImageToPdfFragment extends Fragment implements OnItemClickListener,
        OnPDFCreatedInterface {

    private static final int INTENT_REQUEST_APPLY_FILTER = 10;
    private static final int INTENT_REQUEST_PREVIEW_IMAGE = 11;
    private static final int INTENT_REQUEST_REARRANGE_IMAGE = 12;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;

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
    public static ArrayList<String> mImagesUri = new ArrayList<>();
    private static final ArrayList<String> mUnarrangedImagesUri = new ArrayList<>();
    private String mPath;
    private SharedPreferences mSharedPreferences;
    private FileUtils mFileUtils;
    private PageSizeUtils mPageSizeUtils;
    private int mPageColor;
    private boolean mIsButtonAlreadyClicked = false;
    private ImageToPDFOptions mPdfOptions;
    private MaterialDialog mMaterialDialog;
    private String mHomePath;
    private int mMarginTop = 50;
    private int mMarginBottom = 38;
    private int mMarginLeft = 50;
    private int mMarginRight = 38;
    private String mPageNumStyle;
    private int mChoseId;


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
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_ITP,
                DEFAULT_PAGE_COLOR);
        mHomePath = mSharedPreferences.getString(STORAGE_LOCATION,
                StringUtils.getInstance().getDefaultStorageLocation());

        // Get default values & show enhancement options
        resetValues();

        // Check for the images received
        checkForImagesInBundle();

        if (mImagesUri.size() > 0) {
            mNoOfImages.setText(String.format(mActivity.getResources()
                    .getString(R.string.images_selected), mImagesUri.size()));
            mNoOfImages.setVisibility(View.VISIBLE);
            mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
            mCreatePdf.setEnabled(true);
            StringUtils.getInstance().showSnackbar(mActivity, R.string.successToast);
        } else {
            mNoOfImages.setVisibility(View.GONE);
            mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
        }

        return root;
    }

    /**
     * Adds images (if any) received in the bundle
     */
    private void checkForImagesInBundle() {
        Bundle bundle = getArguments();
        if (bundle == null)
            return;
        if (bundle.getBoolean(OPEN_SELECT_IMAGES))
            startAddingImages();
        ArrayList<Parcelable> uris = bundle.getParcelableArrayList(getString(R.string.bundleKey));
        if (uris == null)
            return;
        for (Parcelable p : uris) {
            String uriRealPath = mFileUtils.getUriRealPath((Uri) p);
            if (uriRealPath == null) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.whatsappToast);
            } else {
                mImagesUri.add(uriRealPath);
            }
        }
    }

    /**
     * Shows enhancement options
     */
    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 2);
        mEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        ImageEnhancementOptionsUtils imageEnhancementOptionsUtilsInstance = ImageEnhancementOptionsUtils.getInstance();
        ArrayList<EnhancementOptionsEntity> list = imageEnhancementOptionsUtilsInstance.getEnhancementOptions(mActivity,
                mPdfOptions);
        EnhancementOptionsAdapter adapter =
                new EnhancementOptionsAdapter(this, list);
        mEnhancementOptionsRecycleView.setAdapter(adapter);
    }

    /**
     * Adding Images to PDF
     */
    @OnClick(R.id.addImages)
    void startAddingImages() {
        if (!mIsButtonAlreadyClicked) {
            if (PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
                selectImages();
                mIsButtonAlreadyClicked = true;
            } else {
                getRuntimePermissions();
            }
        }
    }

    /**
     * Create Pdf of selected images
     */
    @OnClick(R.id.pdfCreate)
    void pdfCreateClicked() {
        createPdf(false);
    }

    /**
     * Opens the dialog to select a save name
     */
    private void createPdf(boolean isGrayScale) {
        String preFillName = mFileUtils.getLastFileName(mImagesUri);
        String ext = getString(R.string.pdf_ext);
        mFileUtils.openSaveDialog(preFillName, ext, filename -> save(isGrayScale, filename));
    }

    /**
     * Saves the PDF
     * @param isGrayScale if the images should be converted to grayscale before
     * @param filename the filename to save to
     */
    private void save(boolean isGrayScale, String filename) {
        mPdfOptions.setImagesUri(mImagesUri);
        mPdfOptions.setPageSize(PageSizeUtils.mPageSize);
        mPdfOptions.setImageScaleType(ImageUtils.getInstance().mImageScaleType);
        mPdfOptions.setPageNumStyle(mPageNumStyle);
        mPdfOptions.setMasterPwd(mSharedPreferences.getString(MASTER_PWD_STRING, appName));
        mPdfOptions.setPageColor(mPageColor);
        mPdfOptions.setOutFileName(filename);
        if (isGrayScale)
            saveImagesInGrayScale();
        new CreatePdf(mPdfOptions, mHomePath, ImageToPdfFragment.this).execute();
    }

    @OnClick(R.id.pdfOpen)
    void openPdf() {
        mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF);
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
        PermissionsUtils.getInstance().handleRequestPermissionsResult(mActivity, grantResults,
                requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::selectImages);
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
        mIsButtonAlreadyClicked = false;
        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        switch (requestCode) {
            case INTENT_REQUEST_GET_IMAGES:
                mImagesUri.clear();
                mUnarrangedImagesUri.clear();
                mImagesUri.addAll(Matisse.obtainPathResult(data));
                mUnarrangedImagesUri.addAll(mImagesUri);
                if (mImagesUri.size() > 0) {
                    mNoOfImages.setText(String.format(mActivity.getResources()
                            .getString(R.string.images_selected), mImagesUri.size()));
                    mNoOfImages.setVisibility(View.VISIBLE);
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_images_added);
                    mCreatePdf.setEnabled(true);
                    mCreatePdf.unblockTouch();
                }
                mMorphButtonUtility.morphToSquare(mCreatePdf, mMorphButtonUtility.integer());
                mOpenPdf.setVisibility(View.GONE);
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                HashMap<Integer, Uri> croppedImageUris =
                        (HashMap) data.getSerializableExtra(CropImage.CROP_IMAGE_EXTRA_RESULT);

                for (int i = 0; i < mImagesUri.size(); i++) {
                    if (croppedImageUris.get(i) != null) {
                        mImagesUri.set(i, croppedImageUris.get(i).getPath());
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_imagecropped);
                    }
                }
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
                if (!mUnarrangedImagesUri.equals(mImagesUri) && mImagesUri.size() > 0) {
                    mNoOfImages.setText(String.format(mActivity.getResources()
                            .getString(R.string.images_selected), mImagesUri.size()));
                    StringUtils.getInstance().showSnackbar(mActivity, R.string.images_rearranged);
                    mUnarrangedImagesUri.clear();
                    mUnarrangedImagesUri.addAll(mImagesUri);
                }
                if (mImagesUri.size() == 0) {
                    mNoOfImages.setVisibility(View.GONE);
                    mMorphButtonUtility.morphToGrey(mCreatePdf, mMorphButtonUtility.integer());
                    mCreatePdf.setEnabled(false);
                }
                break;
        }
    }

    @Override
    public void onItemClick(int position) {

        if (mImagesUri.size() == 0) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_no_images);
            return;
        }
        switch (position) {
            case 0:
                passwordProtectPDF();
                break;
            case 1:
                cropImage();
                break;
            case 2:
                compressImage();
                break;
            case 3:
                startActivityForResult(ImageEditor.getStartIntent(mActivity, mImagesUri),
                        INTENT_REQUEST_APPLY_FILTER);
                break;
            case 4:
                mPageSizeUtils.showPageSizeDialog(false);
                break;
            case 5:
                ImageUtils.getInstance().showImageScaleTypeDialog(mActivity, false);
                break;
            case 6:
                startActivityForResult(PreviewActivity.getStartIntent(mActivity, mImagesUri),
                        INTENT_REQUEST_PREVIEW_IMAGE);
                break;
            case 7:
                addBorder();
                break;
            case 8:
                startActivityForResult(RearrangeImages.getStartIntent(mActivity, mImagesUri),
                        INTENT_REQUEST_REARRANGE_IMAGE);
                break;
            case 9:
                createPdf(true);
                break;
            case 10:
                addMargins();
                break;
            case 11:
                addPageNumbers();
                break;
            case 12:
                addWatermark();
                break;
            case 13:
                setPageColor();
                break;
        }
    }


    /**
     * Saves Images with gray scale filter
     */
    private void saveImagesInGrayScale() {
        ArrayList<String> tempImageUri = new ArrayList<>();
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/PDFfilter");
            dir.mkdirs();

            int size = mImagesUri.size();
            for (int i = 0; i < size; i++) {
                String fileName = String.format(getString(R.string.filter_file_name),
                        String.valueOf(System.currentTimeMillis()), i + "_grayscale");
                File outFile = new File(dir, fileName);

                File f = new File(mImagesUri.get(i));
                FileInputStream fis = new FileInputStream(f);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                Bitmap grayScaleBitmap = ImageUtils.getInstance().toGrayscale(bitmap);

                outFile.createNewFile();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile), 1024 * 8);
                grayScaleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.close(); // Includes flushing the stream and closing the FileOutputStream
                tempImageUri.add(outFile.getAbsolutePath());
            }
            mImagesUri.clear();
            mImagesUri.addAll(tempImageUri);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addBorder() {
        DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.border)
                .customView(R.layout.dialog_border_image, true)
                .onPositive((dialog1, which) -> {
                    View view = dialog1.getCustomView();
                    final EditText input = view.findViewById(R.id.border_width);
                    int value = 0;
                    try {
                        value = Integer.parseInt(String.valueOf(input.getText()));
                        if (value > 200 || value < 0) {
                            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
                        } else {
                            mPdfOptions.setBorderWidth(value);
                            showEnhancementOptions();
                        }
                    } catch (NumberFormatException e) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
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
        DialogUtils.getInstance().createCustomDialogWithoutContent(mActivity, R.string.compression_image_edit)
                .customView(R.layout.compress_image_dialog, true)
                .onPositive((dialog1, which) -> {
                    final EditText qualityInput = dialog1.getCustomView().findViewById(R.id.quality);
                    final CheckBox cbSetDefault = dialog1.getCustomView().findViewById(R.id.cbSetDefault);
                    int check;
                    try {
                        check = Integer.parseInt(String.valueOf(qualityInput.getText()));
                        if (check > 100 || check < 0) {
                            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
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
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
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
                new DefaultTextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }
                });

        positiveAction.setOnClickListener(v -> {
            if (StringUtils.getInstance().isEmpty(passwordInput.getText())) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_password_cannot_be_blank);
            } else {
                mPdfOptions.setPassword(passwordInput.getText().toString());
                mPdfOptions.setPasswordProtected(true);
                showEnhancementOptions();
                dialog.dismiss();
            }
        });

        if (StringUtils.getInstance().isNotEmpty(mPdfOptions.getPassword())) {
            neutralAction.setOnClickListener(v -> {
                mPdfOptions.setPassword(null);
                mPdfOptions.setPasswordProtected(false);
                showEnhancementOptions();
                dialog.dismiss();
                StringUtils.getInstance().showSnackbar(mActivity, R.string.password_remove);
            });
        }
        dialog.show();
        positiveAction.setEnabled(false);
    }

    private void addWatermark() {
        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.add_watermark)
                .customView(R.layout.add_watermark_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .neutralText(R.string.remove_dialog)
                .build();

        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        final View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);

        final Watermark watermark = new Watermark();

        final EditText watermarkTextInput = dialog.getCustomView().findViewById(R.id.watermarkText);
        final EditText angleInput = dialog.getCustomView().findViewById(R.id.watermarkAngle);
        final ColorPickerView colorPickerInput = dialog.getCustomView().findViewById(R.id.watermarkColor);
        final EditText fontSizeInput = dialog.getCustomView().findViewById(R.id.watermarkFontSize);
        final Spinner fontFamilyInput = dialog.getCustomView().findViewById(R.id.watermarkFontFamily);
        final Spinner styleInput = dialog.getCustomView().findViewById(R.id.watermarkStyle);

        ArrayAdapter<Font.FontFamily> fontFamilyAdapter = new ArrayAdapter<>(mActivity,
                android.R.layout.simple_spinner_dropdown_item, Font.FontFamily.values());
        fontFamilyInput.setAdapter(fontFamilyAdapter);

        ArrayAdapter<String> styleAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item,
                mActivity.getResources().getStringArray(R.array.fontStyles));
        styleInput.setAdapter(styleAdapter);


        if (mPdfOptions.isWatermarkAdded()) {
            watermarkTextInput.setText(mPdfOptions.getWatermark().getWatermarkText());
            angleInput.setText(String.valueOf(mPdfOptions.getWatermark().getRotationAngle()));
            fontSizeInput.setText(String.valueOf(mPdfOptions.getWatermark().getTextSize()));
            BaseColor color = this.mPdfOptions.getWatermark().getTextColor();
            //color.getRGB() returns an ARGB color
            colorPickerInput.setColor(color.getRGB());

            fontFamilyInput.setSelection(fontFamilyAdapter.getPosition(mPdfOptions.getWatermark().getFontFamily()));
            styleInput.setSelection(styleAdapter.getPosition(
                    getStyleNameFromFont(mPdfOptions.getWatermark().getFontStyle())));
        } else {
            angleInput.setText("0");
            fontSizeInput.setText("50");
        }
        watermarkTextInput.addTextChangedListener(
                new DefaultTextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                        if (StringUtils.getInstance().isEmpty(input)) {
                            StringUtils.getInstance().
                                    showSnackbar(mActivity, R.string.snackbar_watermark_cannot_be_blank);
                        } else {
                            watermark.setWatermarkText(input.toString());
                            showEnhancementOptions();
                        }
                    }
                });

        neutralAction.setEnabled(this.mPdfOptions.isWatermarkAdded());
        positiveAction.setEnabled(this.mPdfOptions.isWatermarkAdded());

        neutralAction.setOnClickListener(v -> {
            mPdfOptions.setWatermarkAdded(false);
            showEnhancementOptions();
            dialog.dismiss();
            StringUtils.getInstance().showSnackbar(mActivity, R.string.watermark_remove);
        });

        positiveAction.setOnClickListener(v -> {
            watermark.setWatermarkText(watermarkTextInput.getText().toString());
            watermark.setFontFamily(((Font.FontFamily) fontFamilyInput.getSelectedItem()));
            watermark.setFontStyle(getStyleValueFromName(((String) styleInput.getSelectedItem())));

            watermark.setRotationAngle(StringUtils.getInstance().parseIntOrDefault(angleInput.getText(), 0));

            watermark.setTextSize(StringUtils.getInstance().parseIntOrDefault(fontSizeInput.getText(), 50));

            watermark.setTextColor((new BaseColor(
                    Color.red(colorPickerInput.getColor()),
                    Color.green(colorPickerInput.getColor()),
                    Color.blue(colorPickerInput.getColor()),
                    Color.alpha(colorPickerInput.getColor())
            )));
            mPdfOptions.setWatermark(watermark);
            mPdfOptions.setWatermarkAdded(true);
            showEnhancementOptions();
            dialog.dismiss();
            StringUtils.getInstance().showSnackbar(mActivity, R.string.watermark_added);
        });

        dialog.show();
    }

    private void setPageColor() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.page_color)
                .customView(R.layout.dialog_color_chooser, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> {
                    View view = dialog.getCustomView();
                    ColorPickerView colorPickerView = view.findViewById(R.id.color_picker);
                    CheckBox defaultCheckbox = view.findViewById(R.id.set_default);
                    mPageColor = colorPickerView.getColor();
                    if (defaultCheckbox.isChecked()) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putInt(Constants.DEFAULT_PAGE_COLOR_ITP, mPageColor);
                        editor.apply();
                    }
                })
                .build();
        ColorPickerView colorPickerView = materialDialog.getCustomView().findViewById(R.id.color_picker);
        colorPickerView.setColor(mPageColor);
        materialDialog.show();
    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success, String path) {
        if (mMaterialDialog != null && mMaterialDialog.isShowing())
            mMaterialDialog.dismiss();

        if (!success) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_folder_not_created);
            return;
        }
        new DatabaseHelper(mActivity).insertRecord(path, mActivity.getString(R.string.created));
        StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction,
                        v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
        mOpenPdf.setVisibility(View.VISIBLE);
        mMorphButtonUtility.morphToSuccess(mCreatePdf);
        mCreatePdf.blockTouch();
        mPath = path;
        resetValues();
    }

    private void cropImage() {
        Intent intent = new Intent(mActivity, CropImageActivity.class);
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void getRuntimePermissions() {
        PermissionsUtils.getInstance().requestRuntimePermissions(this,
                    WRITE_PERMISSIONS,
                    REQUEST_CODE_FOR_WRITE_PERMISSION);
    }

    /**
     * Opens Matisse activity to select Images
     */
    private void selectImages() {
        ImageUtils.selectImages(this, INTENT_REQUEST_GET_IMAGES);
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
        mPdfOptions.setWatermarkAdded(false);
        mImagesUri.clear();
        showEnhancementOptions();
        mNoOfImages.setVisibility(View.GONE);
        ImageUtils.getInstance().mImageScaleType = mSharedPreferences.getString(DEFAULT_IMAGE_SCALE_TYPE_TEXT,
                IMAGE_SCALE_TYPE_ASPECT_RATIO);
        mPdfOptions.setMargins(0, 0, 0, 0);
        mPageNumStyle = mSharedPreferences.getString (Constants.PREF_PAGE_STYLE, null);
        mPageColor = mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_ITP,
                DEFAULT_PAGE_COLOR);
    }

    private void addMargins() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.add_margins)
                .customView(R.layout.add_margins_dialog, false)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(((dialog, which) -> {
                    View view = dialog.getCustomView();
                    EditText top = view.findViewById(R.id.topMarginEditText);
                    EditText bottom = view.findViewById(R.id.bottomMarginEditText);
                    EditText right = view.findViewById(R.id.rightMarginEditText);
                    EditText left = view.findViewById(R.id.leftMarginEditText);

                    mMarginTop = StringUtils.getInstance().parseIntOrDefault(top.getText(), 0);
                    mMarginBottom = StringUtils.getInstance().parseIntOrDefault(bottom.getText(), 0);
                    mMarginRight = StringUtils.getInstance().parseIntOrDefault(right.getText(), 0);
                    mMarginLeft = StringUtils.getInstance().parseIntOrDefault(left.getText(), 0);

                    mPdfOptions.setMargins(mMarginTop, mMarginBottom, mMarginRight, mMarginLeft);
                })).build();
        materialDialog.show();
    }


    private void addPageNumbers() {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        mPageNumStyle = mSharedPreferences.getString (Constants.PREF_PAGE_STYLE, null);
        mChoseId = mSharedPreferences.getInt (Constants.PREF_PAGE_STYLE_ID, -1);

        RelativeLayout dialogLayout = (RelativeLayout) getLayoutInflater ()
                .inflate (R.layout.add_pgnum_dialog, null);

        RadioButton rbOpt1 = dialogLayout.findViewById(R.id.page_num_opt1);
        RadioButton rbOpt2 = dialogLayout.findViewById(R.id.page_num_opt2);
        RadioButton rbOpt3 = dialogLayout.findViewById(R.id.page_num_opt3);
        RadioGroup rg = dialogLayout.findViewById(R.id.radioGroup);
        CheckBox cbDefault = dialogLayout.findViewById (R.id.set_as_default);

        if (mChoseId > 0) {
            cbDefault.setChecked (true);
            rg.clearCheck ();
            rg.check (mChoseId);
        }

        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.choose_page_number_style)
                .customView(dialogLayout, false)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .neutralText(R.string.remove_dialog)
                .onPositive(((dialog, which) -> {

                    int checkedRadioButtonId = rg.getCheckedRadioButtonId ();
                    mChoseId = checkedRadioButtonId;
                    if (checkedRadioButtonId == rbOpt1.getId ()) {
                        mPageNumStyle = Constants.PG_NUM_STYLE_PAGE_X_OF_N;
                    } else if (checkedRadioButtonId == rbOpt2.getId ()) {
                        mPageNumStyle = Constants.PG_NUM_STYLE_X_OF_N;
                    } else if (checkedRadioButtonId == rbOpt3.getId ()) {
                        mPageNumStyle = Constants.PG_NUM_STYLE_X;
                    }
                    if (cbDefault.isChecked ()) {
                        SharedPreferencesUtil.getInstance().setDefaultPageNumStyle(editor, mPageNumStyle, mChoseId);
                    } else {
                        SharedPreferencesUtil.getInstance().clearDefaultPageNumStyle(editor);
                    }
                }))
                .onNeutral((((dialog, which) -> mPageNumStyle = null)))
                .build();
        materialDialog.show();
    }
}
