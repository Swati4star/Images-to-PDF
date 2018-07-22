package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.model.TextToPDFOptions;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.EnhancementOptionsEntity;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PDFUtils;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;

public class TextToPdfFragment extends Fragment implements EnhancementOptionsAdapter.OnItemClickListner {

    private final int mFileSelectCode = 0;
    private final ArrayList<EnhancementOptionsEntity> mTextEnhancementOptionsEntityArrayList = new ArrayList<>();
    @BindView(R.id.tv_file_name)
    TextView mTextView;
    private Activity mActivity;
    private Uri mTextFileUri = null;
    private String mFontTitle;
    @BindView(R.id.enhancement_options_recycle_view_text)
    RecyclerView mTextEnhancementOptionsRecycleView;
    private EnhancementOptionsAdapter mTextEnhancementOptionsAdapter;
    private int mFontSize = 0;
    private SharedPreferences mSharedPreferences;
    private FileUtils mFileUtils;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_text_to_pdf, container, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mFontTitle = String.format(getString(R.string.edit_font_size),
                mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE));
        ButterKnife.bind(this, rootview);
        showEnhancementOptions();
        Button selectButton = rootview.findViewById(R.id.selectFile);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTextFile();
            }
        });
        Button createButton = rootview.findViewById(R.id.createtextpdf);
        createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openCreateTextPdf();
            }
        });
        mFileUtils = new FileUtils(mActivity);
        return rootview;
    }

    private List<EnhancementOptionsEntity> getEnhancementOptions() {
        mTextEnhancementOptionsEntityArrayList.clear();

        mTextEnhancementOptionsEntityArrayList.add(
                new EnhancementOptionsEntity(getResources().getDrawable(R.drawable.ic_font_black_24dp),
                        mFontTitle));
        return mTextEnhancementOptionsEntityArrayList;
    }

    /**
     * Function to show the enhancement options.
     */
    private void showEnhancementOptions() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mTextEnhancementOptionsRecycleView.setLayoutManager(mGridLayoutManager);
        mTextEnhancementOptionsAdapter = new EnhancementOptionsAdapter(this, getEnhancementOptions());
        mTextEnhancementOptionsRecycleView.setAdapter(mTextEnhancementOptionsAdapter);
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                editFontSize();
                break;
        }
    }

    /**
     * Function to take the font size of pdf as user input
     */
    private void editFontSize() {
        new MaterialDialog.Builder(mActivity)
                .title(mFontTitle)
                .customView(R.layout.dialog_font_size, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        final EditText fontInput = dialog.getCustomView().findViewById(R.id.fontInput);
                        final CheckBox cbSetDefault = dialog.getCustomView().findViewById(R.id.cbSetFontDefault);
                        try {
                            int check = Integer.parseInt(String.valueOf(fontInput.getText()));
                            if (check > 1000 || check < 0) {
                                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                        R.string.invalid_entry,
                                        Snackbar.LENGTH_LONG).show();
                            } else {
                                mFontSize = check;
                                showFontSize();
                                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                        R.string.font_size_changed,
                                        Snackbar.LENGTH_LONG).show();
                                if (cbSetDefault.isChecked()) {
                                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                                    editor.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, mFontSize);
                                    editor.apply();
                                    mFontTitle = String.format(getString(R.string.edit_font_size),
                                            mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT,
                                                    Constants.DEFAULT_FONT_SIZE));
                                }
                            }
                        } catch (NumberFormatException e) {
                            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                    R.string.invalid_entry,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                })
                .show();
    }

    /**
     * Displays font size in UI
     */
    private void showFontSize() {
        mTextEnhancementOptionsEntityArrayList.get(0)
                .setName(String.format(getString(R.string.font_size), String.valueOf(mFontSize)));
        mTextEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    private void openCreateTextPdf() {
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
                            final String mFilename = input.toString();
                            FileUtils utils = new FileUtils(mActivity);
                            if (!utils.isFileExist(mFilename + getString(R.string.pdf_ext))) {
                                createPdf(mFilename);
                            } else {
                                new MaterialDialog.Builder(mActivity)
                                        .title(R.string.warning)
                                        .content(R.string.overwrite_message)
                                        .positiveText(android.R.string.ok)
                                        .negativeText(android.R.string.cancel)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog,
                                                                @NonNull DialogAction which) {
                                                createPdf(mFilename);

                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog,
                                                                @NonNull DialogAction which) {
                                                openCreateTextPdf();
                                            }
                                        })
                                        .show();
                            }
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
        String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                mActivity.getString(R.string.pdf_dir);
        mPath = mPath + mFilename + mActivity.getString(R.string.pdf_ext);
        try {
            PDFUtils fileUtil = new PDFUtils(mActivity);
            if (mFontSize == 0) {
                mFontSize = mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE);
            }
            fileUtil.createPdf(new TextToPDFOptions(mFilename, mTextFileUri, mFontSize));
            final String finalMPath = mPath;
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content)
                    , R.string.snackbar_pdfCreated
                    , Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_viewAction, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FileUtils fileUtils = new FileUtils(mActivity);
                            fileUtils.openFile(finalMPath);
                        }
                    }).show();
            mTextView.setVisibility(View.GONE);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Create a file picker to get text file.
     */
    private void selectTextFile() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(getString(R.string.text_type));
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, String.valueOf(R.string.select_file)),
                    mFileSelectCode);
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                    R.string.install_file_manager,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case mFileSelectCode:
                if (resultCode == RESULT_OK) {
                    mTextFileUri = data.getData();
                    Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                            R.string.text_file_selected,
                            Snackbar.LENGTH_LONG).show();
                    String fileName = mFileUtils.getFileName(mTextFileUri);
                    fileName = getString(R.string.text_file_name) + fileName;
                    mTextView.setText(fileName);
                    mTextView.setVisibility(View.VISIBLE);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

}
