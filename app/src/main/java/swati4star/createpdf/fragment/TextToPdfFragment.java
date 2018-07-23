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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.model.EnhancementOptionsEntity;
import swati4star.createpdf.model.TextToPDFOptions;
import swati4star.createpdf.util.Constants;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PDFUtils;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;

public class TextToPdfFragment extends Fragment implements EnhancementOptionsAdapter.OnItemClickListner {

    private Activity mActivity;
    private FileUtils mFileUtils;

    private final int mFileSelectCode = 0;
    private Uri mTextFileUri = null;
    private String mFontTitle;
    private int mFontSize = 0;

    @BindView(R.id.enhancement_options_recycle_view_text)
    RecyclerView mTextEnhancementOptionsRecycleView;
    @BindView(R.id.tv_file_name)
    TextView mTextView;

    private final ArrayList<EnhancementOptionsEntity> mTextEnhancementOptionsEntityArrayList = new ArrayList<>();
    private EnhancementOptionsAdapter mTextEnhancementOptionsAdapter;
    private SharedPreferences mSharedPreferences;
    private Font.FontFamily mFontFamily;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_text_to_pdf, container, false);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mFontTitle = String.format(getString(R.string.edit_font_size),
                mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, Constants.DEFAULT_FONT_SIZE));
        mFontFamily = Font.FontFamily.valueOf(mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY));
        ButterKnife.bind(this, rootview);
        showEnhancementOptions();
        return rootview;
    }

    private List<EnhancementOptionsEntity> getEnhancementOptions() {
        mTextEnhancementOptionsEntityArrayList.clear();
        mTextEnhancementOptionsEntityArrayList.add(
                new EnhancementOptionsEntity(getResources().getDrawable(R.drawable.ic_font_black_24dp),
                        mFontTitle));
        mTextEnhancementOptionsEntityArrayList.add(
                new EnhancementOptionsEntity(getResources().getDrawable(R.drawable.ic_font_family_24dp),
                        String.format(getString(R.string.default_font_family_text), mFontFamily.name())));

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
            case 1:
                changeFontFamily();
        }
    }

    private void changeFontFamily() {
        String fontFamilyDefault = mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT,
                Constants.DEFAULT_FONT_FAMILY);
        int ordinal = Font.FontFamily.valueOf(fontFamilyDefault).ordinal();
        MaterialDialog materialDialog = new MaterialDialog.Builder(mActivity)
                .title(String.format(getString(R.string.default_font_family_text), fontFamilyDefault))
                .customView(R.layout.dialog_font_family, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String fontFamily;
                        View view = dialog.getCustomView();
                        RadioGroup radioGroup = view.findViewById(R.id.radio_group_font_family);
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton = view.findViewById(selectedId);
                        fontFamily = radioButton.getText().toString();
                        mFontFamily = Font.FontFamily.valueOf(fontFamily);
                        final CheckBox cbSetDefault = view.findViewById(R.id.cbSetDefault);

                        if (cbSetDefault.isChecked()) {
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString(Constants.DEFAULT_FONT_FAMILY_TEXT, fontFamily);
                            editor.apply();
                        }
                        showFontFamily();
                    }
                })
                .build();
        RadioGroup radioGroup = materialDialog.getCustomView().findViewById(R.id.radio_group_font_family);
        RadioButton rb = (RadioButton) radioGroup.getChildAt(ordinal);
        rb.setChecked(true);
        materialDialog.show();
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
                                showSnackbar(R.string.invalid_entry);
                            } else {
                                mFontSize = check;
                                showFontSize();
                                showSnackbar(R.string.font_size_changed);
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
                            showSnackbar(R.string.invalid_entry);
                        }
                    }
                })
                .show();
    }

    /**
     * Displays font family in UI
     */
    private void showFontFamily() {
        mTextEnhancementOptionsEntityArrayList.get(1)
                .setName(getString(R.string.font_family_text) + mFontFamily.name());
        mTextEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    /**
     * Displays font size in UI
     */
    private void showFontSize() {
        mTextEnhancementOptionsEntityArrayList.get(0)
                .setName(String.format(getString(R.string.font_size), String.valueOf(mFontSize)));
        mTextEnhancementOptionsAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.createtextpdf)
    public void openCreateTextPdf() {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.creating_pdf)
                .content(R.string.enter_file_name)
                .input(getString(R.string.example), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (StringUtils.isEmpty(input)) {
                            showSnackbar(R.string.snackbar_name_not_blank);
                        } else {
                            final String inputName = input.toString();
                            if (!mFileUtils.isFileExist(inputName + getString(R.string.pdf_ext))) {
                                createPdf(inputName);
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
                                                createPdf(inputName);
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
            fileUtil.createPdf(new TextToPDFOptions(mFilename, mTextFileUri, mFontSize, mFontFamily));
            final String finalMPath = mPath;
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content)
                    , R.string.snackbar_pdfCreated
                    , Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_viewAction, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFileUtils.openFile(finalMPath);
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
    @OnClick(R.id.selectFile)
    public void selectTextFile() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(getString(R.string.text_type));
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, String.valueOf(R.string.select_file)),
                    mFileSelectCode);
        } catch (android.content.ActivityNotFoundException ex) {
            showSnackbar(R.string.install_file_manager);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case mFileSelectCode:
                if (resultCode == RESULT_OK) {
                    mTextFileUri = data.getData();
                    showSnackbar(R.string.text_file_selected);
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
        mFileUtils = new FileUtils(mActivity);
    }

    private void showSnackbar(int resID) {
        Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                resID,
                Snackbar.LENGTH_LONG).show();
    }
}
