package swati4star.createpdf.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.dd.morphingbutton.MorphingButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.activity.ImagesPreviewActivity;
import swati4star.createpdf.adapter.MergeFilesAdapter;
import swati4star.createpdf.interfaces.BottomSheetPopulate;
import swati4star.createpdf.util.BottomSheetCallback;
import swati4star.createpdf.util.BottomSheetUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.ViewFilesDividerItemDecoration;

import static android.app.Activity.RESULT_OK;
import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;
import static swati4star.createpdf.util.DialogUtils.createAnimationDialog;
import static swati4star.createpdf.util.FileUriUtils.getFilePath;
import static swati4star.createpdf.util.FileUtils.getFileNameWithoutExtension;
import static swati4star.createpdf.util.FileUtils.saveImage;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

/**
 * A simple {@link Fragment} subclass.
 */
public class PdfToImageFragment extends Fragment implements BottomSheetPopulate, MergeFilesAdapter.OnClickListener {

    private static final int INTENT_REQUEST_PICKFILE_CODE = 10;
    private Activity mActivity;
    private String mPath;
    private MorphButtonUtility mMorphButtonUtility;
    private FileUtils mFileUtils;
    private BottomSheetUtils mBottomSheetUtils;

    @BindView(R.id.lottie_progress)
    LottieAnimationView mLottieProgress;
    BottomSheetBehavior mSheetBehavior;
    @BindView(R.id.bottom_sheet)
    LinearLayout mLayoutBottomSheet;
    @BindView(R.id.upArrow)
    ImageView mUpArrow;
    @BindView(R.id.selectFile)
    MorphingButton mSelectFileButton;
    @BindView(R.id.createImages)
    MorphingButton mCreateImagesButton;
    @BindView(R.id.created_images)
    RecyclerView mCreatedImages;
    @BindView(R.id.pdfToImagesText)
    TextView mCreateImagesSuccessText;
    @BindView(R.id.options)
    LinearLayout options;
    @BindView(R.id.layout)
    RelativeLayout mLayout;
    @BindView(R.id.recyclerViewFiles)
    RecyclerView mRecyclerViewFiles;
    private Uri mUri;
    private ArrayList<String> mOutputFilePaths;
    private int mImagesCount;
    private MaterialDialog mMaterialDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        mFileUtils = new FileUtils(mActivity);
        mBottomSheetUtils = new BottomSheetUtils(mActivity);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pdf_to_image, container, false);
        ButterKnife.bind(this, rootView);
        mSheetBehavior = BottomSheetBehavior.from(mLayoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetCallback(mUpArrow, isAdded()));
        mLottieProgress.setVisibility(View.VISIBLE);
        mBottomSheetUtils.populateBottomSheetWithPDFs(this);
        resetView();
        return rootView;
    }

    private void resetView() {
        mPath = null;
        mMorphButtonUtility.initializeButton(mSelectFileButton, mCreateImagesButton);
    }

    @Override
    public void onPopulate(ArrayList<String> paths) {
        if (paths == null || paths.size() == 0) {
            mLayout.setVisibility(View.GONE);
        } else {
            // Init recycler view
            mRecyclerViewFiles.setVisibility(View.VISIBLE);
            MergeFilesAdapter mergeFilesAdapter = new MergeFilesAdapter(mActivity,
                    paths, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
            mRecyclerViewFiles.setLayoutManager(mLayoutManager);
            mRecyclerViewFiles.setAdapter(mergeFilesAdapter);
            mRecyclerViewFiles.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
        }
        mLottieProgress.setVisibility(View.GONE);
    }

    @OnClick(R.id.shareImages)
    void onShareImagesClick() {
        if (mOutputFilePaths != null) {
            ArrayList<File> fileArrayList = new ArrayList<>();
            for (String path : mOutputFilePaths) {
                fileArrayList.add(new File(path));
            }
            mFileUtils.shareMultipleFiles(fileArrayList);
        }
    }

    @OnClick(R.id.viewImages)
    void onViewImagesClick() {
        mActivity.startActivity(ImagesPreviewActivity.getStartIntent(mActivity, mOutputFilePaths));
    }

    @OnClick(R.id.selectFile)
    public void showFileChooser() {
        startActivityForResult(mFileUtils.getFileChooser(),
                INTENT_REQUEST_PICKFILE_CODE);
    }

    @OnClick(R.id.createImages)
    public void createImages() {
        Log.d(PdfToImageFragment.class.getSimpleName(), "Create Images");

        mMaterialDialog = createAnimationDialog(mActivity);
        mMaterialDialog.show();

        mOutputFilePaths = new ArrayList<>();
        mImagesCount = 0;
        // Render pdf pages as bitmap
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        ParcelFileDescriptor fileDescriptor = null;
        try {
            if (mUri != null)
                fileDescriptor = mActivity.getContentResolver().openFileDescriptor(mUri, "r");
            else if (mPath != null)
                fileDescriptor = ParcelFileDescriptor.open(new File(mPath), MODE_READ_ONLY);
            if (fileDescriptor != null) {
                PdfRenderer renderer = new PdfRenderer(fileDescriptor);
                final int pageCount = renderer.getPageCount();
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = renderer.openPage(i);
                    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),
                            Bitmap.Config.ARGB_8888);
                    // say we render for showing on the screen
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    bitmaps.add(bitmap);
                    // close the page
                    page.close();

                    String filename = getFileNameWithoutExtension(mPath) +
                            "_" + Integer.toString(i + 1);
                    String path = saveImage(filename, bitmap);
                    if (path != null) {
                        mOutputFilePaths.add(path);
                        mImagesCount++;
                    }
                }

                // close the renderer
                renderer.close();
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }

        if (bitmaps.size() < 1) {
            showSnackbar(mActivity, R.string.file_access_error);
        } else {
            Log.d(PdfToImageFragment.class.getSimpleName(), mImagesCount + " images created");

            mMaterialDialog.dismiss();
            resetView();

            if (mImagesCount == 0) {
                showSnackbar(mActivity, R.string.extract_images_failed);
                return;
            }

            String text = String.format(mActivity.getString(R.string.create_images_success), mImagesCount);
            showSnackbar(mActivity, text);
            mCreateImagesSuccessText.setText(text);
            mCreateImagesSuccessText.setVisibility(View.VISIBLE);
            options.setVisibility(View.VISIBLE);

            Log.d(PdfToImageFragment.class.getSimpleName(), "Size : " + mOutputFilePaths.size());

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
            mCreateImagesSuccessText.setText(text);
            mCreatedImages.setVisibility(View.VISIBLE);
            mCreatedImages.setLayoutManager(mLayoutManager);
            mCreatedImages.addItemDecoration(new ViewFilesDividerItemDecoration(mActivity));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) throws NullPointerException {
        if (data == null || resultCode != RESULT_OK || data.getData() == null)
            return;
        if (requestCode == INTENT_REQUEST_PICKFILE_CODE) {
            mUri = data.getData();
            Log.d(PdfToImageFragment.class.getSimpleName(), "Uri is : " + mUri.toString());
            setTextAndActivateButtons(getFilePath(data.getData()));
        }
    }

    @Override
    public void onItemClick(String path) {
        mUri = null;
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setTextAndActivateButtons(path);
    }

    private void setTextAndActivateButtons(String path) {
        Log.d(PdfToImageFragment.class.getSimpleName(), "Path : " + path);
        mCreatedImages.setVisibility(View.GONE);
        options.setVisibility(View.GONE);
        mCreateImagesSuccessText.setVisibility(View.GONE);
        mPath = path;
        mMorphButtonUtility.setTextAndActivateButtons(path,
                mSelectFileButton, mCreateImagesButton);
    }
}
