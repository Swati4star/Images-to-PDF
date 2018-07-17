package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import swati4star.createpdf.R;
import swati4star.createpdf.adapter.EnhancementOptionsAdapter;
import swati4star.createpdf.util.EnhancementOptionsEntity;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PDFUtils;
import swati4star.createpdf.util.StringUtils;

import static android.app.Activity.RESULT_OK;

public class TextToPdfFragment extends Fragment implements EnhancementOptionsAdapter.OnItemClickListner  {

    private final int mFileSelectCode = 0;
    private final ArrayList<EnhancementOptionsEntity> mTextEnhancementOptionsEntityArrayList = new ArrayList<>();
    @BindView(R.id.tv_file_name)
    TextView mTextView;
    private Activity mActivity;
    private Uri mTextFileUri = null;
    private int mFontSize;
    @BindView(R.id.enhancement_options_recycle_view_text)
    RecyclerView mTextEnhancementOptionsRecycleView;
    private EnhancementOptionsAdapter mTextEnhancementOptionsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_text_to_pdf, container, false);
        ButterKnife.bind(this, rootview);
        showEnhancementOptions();
        Button selectButton = rootview.findViewById(R.id.selectFile);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTextFile();
                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                        R.string.text_file_selected,
                        Snackbar.LENGTH_LONG).show();
            }
        });
        Button createButton = rootview.findViewById(R.id.createtextpdf);
        createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openCreateTextPdf();
            }
        });
        return rootview;
    }
    public List<EnhancementOptionsEntity> getEnhancementOptions() {
        mTextEnhancementOptionsEntityArrayList.clear();

        mTextEnhancementOptionsEntityArrayList.add(
                new EnhancementOptionsEntity(getResources().getDrawable(R.drawable.ic_font_black_24dp),
                        getResources().getString(R.string.edit_font_size)));
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
                .title(R.string.edit_font_size)
                .content(R.string.enter_font_size)
                .input(getString(R.string.example_font), null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        try {
                            mFontSize = Integer.parseInt(String.valueOf(input));
                            if (mFontSize > 1000 || mFontSize < 0) {
                                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                        R.string.invalid_entry,
                                        Snackbar.LENGTH_LONG).show();
                            } else {
                                showFontSize();
                                Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                                        R.string.font_size_changed,
                                        Snackbar.LENGTH_LONG).show();
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

    public void openCreateTextPdf() {
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
                            String mFilename = input.toString();
                            String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                                    mActivity.getString(R.string.pdf_dir);
                            mPath = mPath + mFilename + mActivity.getString(R.string.pdf_ext);
                            try {
                                PDFUtils fileUtil = new PDFUtils(mActivity);
                                fileUtil.createPdf(mTextFileUri, mFilename, mFontSize);
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
                    }
                })
                .show();
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
                    String fileName = new File(mTextFileUri.getPath()).getName();
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

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
