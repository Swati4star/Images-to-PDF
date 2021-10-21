package swati4star.createpdf.fragment.texttopdf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.morphingbutton.MorphingButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.interfaces.Enhancer;
import swati4star.createpdf.interfaces.OnItemClickListener;
import swati4star.createpdf.interfaces.OnTextToPdfInterface;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.model.TextToPDFOptions;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.DialogUtils;
import swati4star.createpdf.util.DirectoryUtils;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.MorphButtonUtility;
import swati4star.createpdf.util.PageSizeUtils;
import swati4star.createpdf.util.PermissionsUtils;
import swati4star.createpdf.util.StringUtils;
import swati4star.createpdf.util.TextToPDFUtils;
import swati4star.createpdf.util.TextToPdfAsync;

import static android.app.Activity.RESULT_OK;
import static swati4star.createpdf.util.Constants.REQUEST_CODE_FOR_WRITE_PERMISSION;
import static swati4star.createpdf.util.Constants.WRITE_PERMISSIONS;

public class TextToPdfFragment extends Fragment implements OnItemClickListener,
        OnTextToPdfInterface, TextToPdfContract.View {

    private Activity mActivity;
    private FileUtils mFileUtils;
    private DirectoryUtils mDirectoryUtils;

    private final int mFileSelectCode = 0;
    private Uri mTextFileUri = null;
    private String mFileExtension;
    private int mButtonClicked = 0;
    private MaterialDialog mMaterialDialog;
    private String mFileNameWithType = null;

    @BindView(R.id.enhancement_options_recycle_view_text)
    RecyclerView mTextEnhancementOptionsRecycleView;
    @BindView(R.id.selectFile)
    MorphingButton mSelectFile;
    @BindView(R.id.createtextpdf)
    MorphingButton mCreateTextPdf;

    private EnhancementOptionsAdapter mTextEnhancementOptionsAdapter;
    private MorphButtonUtility mMorphButtonUtility;
    private String mPath;
    private List<Enhancer> mEnhancerList;
    private TextToPDFOptions.Builder mBuilder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_text_to_pdf, container, false);

        mMorphButtonUtility = new MorphButtonUtility(mActivity);
        ButterKnife.bind(this, rootView);

        mBuilder = new TextToPDFOptions.Builder(mActivity);
        addEnhancements();
        showEnhancementOptions();
        mMorphButtonUtility.morphToGrey(mCreateTextPdf, mMorphButtonUtility.integer());
        mCreateTextPdf.setEnabled(false);
        return rootView;
    }

    private void addEnhancements() {
        mEnhancerList = new ArrayList<>();
        for (final Enhancers enhancer: Enhancers.values()) {
            mEnhancerList.add(enhancer.getEnhancer(mActivity, this, mBuilder));
        }
    }

    /**
     * Function to show the enhancement options.
     */
    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 2);
        mTextEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        List<EnhancementOptionsEntity> optionsEntityist = new ArrayList<>();
        for (Enhancer enhancer: mEnhancerList) {
            optionsEntityist.add(enhancer.getEnhancementOptionsEntity());
        }
        mTextEnhancementOptionsAdapter = new EnhancementOptionsAdapter(this, optionsEntityist);
        mTextEnhancementOptionsRecycleView.setAdapter(mTextEnhancementOptionsAdapter);
    }

    @Override
    public void onItemClick(int position) {
        final Enhancer enhancer = mEnhancerList.get(position);
        enhancer.enhance();
    }

    @OnClick(R.id.createtextpdf)
    public void openCreateTextPdf() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), mFileNameWithType,
                        (dialog, input) -> {
                            if (StringUtils.getInstance().isEmpty(input)) {
                                StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_name_not_blank);
                            } else {
                                final String inputName = input.toString();
                                if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
                                    createPdf(inputName);
                                } else {
                                    MaterialDialog.Builder builder = DialogUtils.getInstance()
                                            .createOverwriteDialog(mActivity);
                                    builder.onPositive((dialog12, which) -> createPdf(inputName))
                                            .onNegative((dialog1, which) -> openCreateTextPdf())
                                            .show();
                                }
                            }
                        })
                .show();
    }

    /**
     * function to create PDF
     *
     * @param mFilename name of file to be created.
     */
    private void createPdf(String mFilename) {
        mPath = mDirectoryUtils.getOrCreatePdfDirectory().getPath();
        mPath = mPath + "/" + mFilename + mActivity.getString(R.string.pdf_ext);
        TextToPDFOptions options = mBuilder.setFileName(mFilename)
                .setPageSize(PageSizeUtils.mPageSize)
                .setInFileUri(mTextFileUri)
                .build();
        TextToPDFUtils fileUtil = new TextToPDFUtils(mActivity);
        new TextToPdfAsync(fileUtil, options, mFileExtension,
                TextToPdfFragment.this).execute();
    }

    /**
     * Create a file picker to get text file.
     */
    @OnClick(R.id.selectFile)
    public void selectTextFile() {
        if (PermissionsUtils.getInstance().checkRuntimePermissions(this, WRITE_PERMISSIONS)) {
            selectFile();
        } else {
            getRuntimePermissions();
        }
    }

    private void selectFile() {
        if (mButtonClicked == 0) {
            Uri uri = Uri.parse(Environment.getRootDirectory() + "/");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setDataAndType(uri, "*/*");
            String[] mimeTypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/msword", getString(R.string.text_type)};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(
                        Intent.createChooser(intent, String.valueOf(R.string.select_file)),
                        mFileSelectCode);
            } catch (android.content.ActivityNotFoundException ex) {
                StringUtils.getInstance().showSnackbar(mActivity, R.string.install_file_manager);
            }
            mButtonClicked = 1;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mButtonClicked = 0;
        if (requestCode == mFileSelectCode) {
            if (resultCode == RESULT_OK) {
                mTextFileUri = data.getData();
                StringUtils.getInstance().showSnackbar(mActivity, R.string.text_file_selected);
                String fileName = mFileUtils.getFileName(mTextFileUri);
                if (fileName != null) {
                    if (fileName.endsWith(Constants.textExtension))
                        mFileExtension = Constants.textExtension;
                    else if (fileName.endsWith(Constants.docxExtension))
                        mFileExtension = Constants.docxExtension;
                    else if (fileName.endsWith(Constants.docExtension))
                        mFileExtension = Constants.docExtension;
                    else {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.extension_not_supported);
                        return;
                    }
                }
                mFileNameWithType = mFileUtils.stripExtension(fileName) + getString(R.string.pdf_suffix);
                mSelectFile.setText(getString(R.string.text_file_name) + fileName);
                mCreateTextPdf.setEnabled(true);
                mMorphButtonUtility.morphToSquare(mCreateTextPdf, mMorphButtonUtility.integer());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            mActivity = getActivity();
        }

        mFileUtils = new FileUtils(mActivity);
        mDirectoryUtils = new DirectoryUtils(mActivity);
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
                requestCode, REQUEST_CODE_FOR_WRITE_PERMISSION, this::selectFile);
    }

    @Override
    public void onPDFCreationStarted() {
        mMaterialDialog = DialogUtils.getInstance().createAnimationDialog(mActivity);
        mMaterialDialog.show();
    }

    @Override
    public void onPDFCreated(boolean success) {
        if (mMaterialDialog != null && mMaterialDialog.isShowing())
            mMaterialDialog.dismiss();
        if (!success) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.error_pdf_not_created);
            mMorphButtonUtility.morphToGrey(mCreateTextPdf, mMorphButtonUtility.integer());
            mCreateTextPdf.setEnabled(false);
            mTextFileUri = null;
            mButtonClicked = 0;
            return;
        }
        StringUtils.getInstance().getSnackbarwithAction(mActivity, R.string.snackbar_pdfCreated)
                .setAction(R.string.snackbar_viewAction,
                        v -> mFileUtils.openFile(mPath, FileUtils.FileType.e_PDF)).show();
        mSelectFile.setText(R.string.select_text_file);
        mMorphButtonUtility.morphToGrey(mCreateTextPdf, mMorphButtonUtility.integer());
        mCreateTextPdf.setEnabled(false);
        mTextFileUri = null;
        mButtonClicked = 0;
        mBuilder = new TextToPDFOptions.Builder(mActivity);
    }

    @Override
    public void updateView() {
        mTextEnhancementOptionsAdapter.notifyDataSetChanged();
    }
}
