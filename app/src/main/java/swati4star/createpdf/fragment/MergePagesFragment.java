package swati4star.createpdf.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.snackbar.Snackbar;

import swati4star.createpdf.databinding.FragmentMergePagesBinding;
import swati4star.createpdf.util.MergePdfPages;

public class MergePagesFragment extends Fragment {

    private FragmentMergePagesBinding mBinding;
    private ActivityResultLauncher<String[]> mPicker;
    private Uri mSelectedUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentMergePagesBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        setupPicker(root);
        setupButtons(root);

        return root;
    }

    private void setupPicker(View root) {

        mPicker = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {

                    if (uri == null) {
                        Snackbar.make(root, "No file selected", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    mSelectedUri = uri;
                    mBinding.filepath.setText(getFileName(uri));
                }
        );
    }

    private void setupButtons(View root) {

        mBinding.selectFileBtn.setOnClickListener(v ->
                mPicker.launch(new String[]{"application/pdf"}));

        mBinding.mergeFileBtn.setOnClickListener(v -> startMerge(root));
    }

    private void startMerge(View root) {

        if (mSelectedUri == null) {
            Snackbar.make(root, "Select a PDF first", Snackbar.LENGTH_SHORT).show();
            return;
        }

        String pages = mBinding.pagesinput.getText().toString().trim();

        if (pages.isEmpty()) {
            Snackbar.make(root, "Enter pages like 1,3-5", Snackbar.LENGTH_SHORT).show();
            return;
        }

        new MaterialDialog.Builder(requireContext())
                .title("Create PDF")
                .content("Enter file name")
                .input("", "", (dialog, input) -> {

                    String fileName = input.toString().trim();

                    if (fileName.isEmpty()) {
                        Snackbar.make(root, "Invalid file name", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    new MergePdfPages(
                            requireContext(),
                            mSelectedUri,
                            pages,
                            fileName,
                            uri -> {

                                if (uri == null) {
                                    Snackbar.make(root, "Save failed", Snackbar.LENGTH_SHORT).show();
                                    return;
                                }

                                openPdf(uri);
                            }
                    ).execute();

                })
                .show();
    }

    private void openPdf(Uri uri) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Open PDF"));

        } catch (Exception e) {
            Snackbar.make(mBinding.getRoot(),
                    "No app found to open PDF",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {

        try (Cursor cursor = requireContext()
                .getContentResolver()
                .query(uri, null, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {

                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                if (index >= 0) {
                    return cursor.getString(index);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "document.pdf";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}