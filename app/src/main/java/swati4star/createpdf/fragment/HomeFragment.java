package swati4star.createpdf.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.adapter.ViewFilesAdapter;
import swati4star.createpdf.util.EnhancementOptionsEntity;
import swati4star.createpdf.util.StringUtils;

import static java.util.Collections.singletonList;


/**
 * HomeFragment fragment to start with creating PDF
 */
public class HomeFragment extends Fragment implements EnhancementOptionsAdapter.OnItemClickListner {

    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private static int mImageCounter = 0;
    private Activity mActivity;
    private ArrayList<String> mImagesUri = new ArrayList<>();
    private ArrayList<String> mTempUris = new ArrayList<>();
    private String mPath;
    private String mFilename;
    private String mPassword;
    private String mQuality;
    private View mPositiveAction;
    private View mNeutralAction;
    private EditText mPasswordInput;
    private boolean mOpenSelectImages = false;
    @BindView(R.id.addImages)
    MorphingButton addImages;
    @BindView(R.id.pdfCreate)
    MorphingButton mCreatePdf;
    @BindView(R.id.pdfOpen)
    MorphingButton mOpenPdf;
    @BindView(R.id.enhancement_options_recycle_view)
    RecyclerView mEnhancementOptionsRecycleView;
    private int mMorphCounter1 = 1;
    private EnhancementOptionsAdapter mEnhancementOptionsAdapter;
    private ArrayList<EnhancementOptionsEntity> mEnhancementOptionsEntityArrayList = new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);

        morphToSquare(mCreatePdf, integer());
        mOpenPdf.setVisibility(View.GONE);

        // Get runtime permissions if build version >= Android M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)) {
                mOpenSelectImages = false; // We don't want next activity to open after getting permissions
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
            }
        }

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
        // Check if permissions are granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                mOpenSelectImages = true; // We want next activity to open after getting permissions
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
            } else {
                selectImages();
            }
        } else {
            selectImages();
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
                    .setActivityMenuIconColor(color(R.color.colorPrimary))
                    .setInitialCropWindowPaddingRatio(0)
                    .setAllowRotation(true)
                    .setActivityTitle(getString(R.string.cropImage_activityTitle) + (mImageCounter + 1))
                    .start(mActivity, this);
        }
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
                            mFilename = input.toString();

                            new CreatingPdf().execute();

                            if (mMorphCounter1 == 0)
                                mMorphCounter1++;
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
     * Opens ImagePickerActivity to select Images
     */
    private void selectImages() {
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
            morphToSquare(mCreatePdf, integer());
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
            morphToSquare(mCreatePdf, integer());
            mImageCounter++;
            next();
        }
    }

    /**
     * Converts morph button ot square shape
     *
     * @param btnMorph the button to be converted
     * @param duration time period of transition
     */
    private void morphToSquare(final MorphingButton btnMorph, int duration) {
        MorphingButton.Params square = MorphingButton.Params.create()
                .duration(duration)
                .cornerRadius(dimen(R.dimen.mb_corner_radius_2))
                .width(dimen(R.dimen.mb_width_328))
                .height(dimen(R.dimen.mb_height_48))
                .color(color(R.color.mb_blue))
                .colorPressed(color(R.color.mb_blue_dark))
                .text(getString(R.string.mb_button));
        btnMorph.morph(square);
    }

    /**
     * Converts morph button into success shape
     *
     * @param btnMorph the button to be converted
     */
    private void morphToSuccess(final MorphingButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(integer())
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.mb_green))
                .colorPressed(color(R.color.mb_green_dark))
                .icon(R.drawable.ic_done);
        btnMorph.morph(circle);
    }

    private int integer() {
        return getResources().getInteger(R.integer.mb_animation);
    }

    private int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    private int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    /**
     * An async task that converts selected images to Pdf
     */
    @SuppressLint("StaticFieldLeak")
    class CreatingPdf extends AsyncTask<String, String, String> {

        // Progress dialog
        MaterialDialog dialog;
        boolean success;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            final MaterialDialog.Builder builder = new MaterialDialog.Builder(mActivity)
                    .title(R.string.please_wait)
                    .content(R.string.populating_list)
                    .cancelable(false)
                    .progress(true, 0);
            dialog = builder.build();
            success = true;
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            mPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    HomeFragment.this.getString(R.string.pdf_dir);

            File folder = new File(mPath);
            if (!folder.exists()) {
                success = folder.mkdir();
                if (!success) {
                    return null;
                }
            }

            mPath = mPath + mFilename + HomeFragment.this.getString(R.string.pdf_ext);

            Log.v("stage 1", "store the pdf in sd card");

            Document document = new Document(PageSize.A4, 38, 38, 50, 38);

            Log.v("stage 2", "Document Created");

            Rectangle documentRect = document.getPageSize();

            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(mPath));

                Log.v("Stage 3", "Pdf writer");

                if (StringUtils.isNotEmpty(mPassword)) {
                    writer.setEncryption(mPassword.getBytes(),
                            getString(R.string.app_name).getBytes(),
                            PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY,
                            PdfWriter.ENCRYPTION_AES_128);

                    Log.v("Stage 3.1", "Set Encryption");
                }

                document.open();

                Log.v("Stage 4", "Document opened");

                for (int i = 0; i < mImagesUri.size(); i++) {
                    int quality = 30;

                    if (StringUtils.isNotEmpty(mQuality)) {
                        quality = Integer.parseInt(mQuality);
                    }
                    Image image = Image.getInstance(mImagesUri.get(i));
                    image.setCompressionLevel(100-quality);

                    Log.v("Stage 6", "Image path adding");

                    image.setAbsolutePosition(
                            (documentRect.getWidth() - image.getScaledWidth()) / 2,
                            (documentRect.getHeight() - image.getScaledHeight()) / 2);
                    Log.v("Stage 7", "Image Alignments");

                    image.setBorder(Image.BOX);

                    image.setBorderWidth(15);

                    document.add(image);

                    document.newPage();
                }

                Log.v("Stage 8", "Image adding");

                document.close();

                Log.v("Stage 7", "Document Closed" + mPath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            document.close();
            resetValues();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();

            if (!success) {
                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                        R.string.snackbar_folder_not_created,
                        Snackbar.LENGTH_LONG).show();
                return;
            }

            mOpenPdf.setVisibility(View.VISIBLE);
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content)
                    , R.string.snackbar_pdfCreated
                    , Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_viewAction, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<File> list = new ArrayList<>(singletonList(new File(mPath)));
                            ViewFilesAdapter filesAdapter = new ViewFilesAdapter(mActivity, list, null);
                            filesAdapter.openFile(mPath);
                        }
                    }).show();
            morphToSuccess(mCreatePdf);
        }
    }

    private void resetValues() {
        mImagesUri.clear();
        mTempUris.clear();
        mImageCounter = 0;
        mPassword = null;
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
                        getString(R.string.compress_image)));
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

        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.compress_image)
                .customView(R.layout.compress_image_dialog, true)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .build();

        mPositiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        mPasswordInput = dialog.getCustomView().findViewById(R.id.quality);
        mPasswordInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mPositiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable input) {
                        int check;
                        try {
                            check = Integer.parseInt(String.valueOf(input));
                            if (check > 100 || check < 0) {
                                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                        R.string.invalid_quality,
                                        Snackbar.LENGTH_LONG).show();
                            } else {
                                mQuality = String.valueOf(check);
                                showCompression();
                            }
                        } catch (NumberFormatException e) {
                                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                        R.string.invalid_quality,
                                        Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
        dialog.show();
        mPositiveAction.setEnabled(false);
    }

    private void showCompression() {
        mEnhancementOptionsEntityArrayList.get(2)
                .setName(mQuality + "% Compressed");
        mEnhancementOptionsAdapter.notifyDataSetChanged();
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

        mPositiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        mNeutralAction = dialog.getActionButton(DialogAction.NEUTRAL);
        mPasswordInput = dialog.getCustomView().findViewById(R.id.password);
        mPasswordInput.setText(mPassword);
        mPasswordInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mPositiveAction.setEnabled(s.toString().trim().length() > 0);
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

            mNeutralAction.setOnClickListener(new View.OnClickListener() {
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
        mPositiveAction.setEnabled(false);
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
}
