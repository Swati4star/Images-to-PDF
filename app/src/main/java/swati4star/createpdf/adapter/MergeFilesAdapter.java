package swati4star.createpdf.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;
import swati4star.createpdf.util.FileUtils;
import swati4star.createpdf.util.PDFUtils;

public class MergeFilesAdapter extends RecyclerView.Adapter<MergeFilesAdapter.ViewMergeFilesHolder> {

    private final ArrayList<String> mFilePaths;
    private final Activity mContext;
    private final OnClickListener mOnClickListener;
    private final PDFUtils mPDFUtils;
    private final boolean mIsMergeFragment;

    public MergeFilesAdapter(Activity mContext, ArrayList<String> mFilePaths,
                             boolean mIsMergeFragment, OnClickListener mOnClickListener) {
        this.mContext = mContext;
        this.mFilePaths = mFilePaths;
        this.mOnClickListener = mOnClickListener;
        mPDFUtils = new PDFUtils(mContext);
        this.mIsMergeFragment = mIsMergeFragment;
    }

    @NonNull
    @Override
    public ViewMergeFilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_merge_files, parent, false);
        return new MergeFilesAdapter.ViewMergeFilesHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewMergeFilesHolder holder, int position) {
        boolean isPdfFile = isPdfFile(mFilePaths.get(position));
        if (isPdfFile) {
            boolean isEncrypted = mPDFUtils.isPDFEncrypted(mFilePaths.get(position));
            holder.mFileName.setText(FileUtils.getFileName(mFilePaths.get(position)));
            holder.mEncryptionImage.setVisibility(isEncrypted ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /**
     * check whether the file is a real pdf file or not (whether the file has the header of pdf file)
     */
    public static boolean isPdfFile(String filePath) {
        String header = getFileHeader(filePath);
        String end = getLastLine(filePath);
        end = bytesToHexString(end.getBytes());
        // '%PDF-1.4 in HEX is '255044462D312E'
        // '%PDF' in HEX is '25504446'
        // '%%EOF' in HEX is '2525454F46'
        return header.startsWith("25504446") && end.startsWith("2525454F46");
    }

    public static String getFileHeader(String filePath) {
        FileInputStream is = null;

        String value = "";
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[20];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (byte b : src) {
            // Returns a string representation of an integer parameter in hexadecimal (Radix 16)
            // unsigned integer form and converts it to uppercase
            hv = Integer.toHexString(b & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    public static String getLastLine(String filePath) {
        File file = new File(filePath);
        // Store results
        StringBuilder builder = new StringBuilder();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            // The pointer position starts at 0, so the maximum length is length-1
            long fileLastPointer = randomAccessFile.length() - 1;
            // Read file from back to front
            for (long filePointer = fileLastPointer; filePointer != -1; filePointer--) {
                // Move pointer to
                randomAccessFile.seek(filePointer);
                int readByte = randomAccessFile.readByte();
                if (0xA == readByte) {
                    //  LF='\n'=0x0A change line
                    if (filePointer == fileLastPointer) {
                        // If it is the last line feed, filter it out
                        continue;
                    }
                    break;
                }
                if (0xD == readByte) {
                    //  CR ='\r'=0x0D enter
                    if (filePointer == fileLastPointer - 1) {
                        // If it is the last carriage return, it is also filtered out
                        continue;
                    }
                    break;
                }
                builder.append((char) readByte);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.reverse().toString();
    }

    @Override
    public int getItemCount() {
        return mFilePaths == null ? 0 : mFilePaths.size();
    }

    public class ViewMergeFilesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.fileName)
        TextView mFileName;
        @BindView(R.id.encryptionImage)
        ImageView mEncryptionImage;
        @BindView(R.id.itemMerge_checkbox)
        AppCompatCheckBox mCheckbox;

        ViewMergeFilesHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mFileName.setOnClickListener(this);
            if (mIsMergeFragment) mCheckbox.setVisibility(View.VISIBLE);
            else mCheckbox.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {

            if (getAdapterPosition() >= mFilePaths.size())
                return;

            if (mIsMergeFragment) mCheckbox.toggle();
            mOnClickListener.onItemClick(mFilePaths.get(getAdapterPosition()));
        }

        @OnClick(R.id.itemMerge_checkbox)
        public void onCheckboxClick() {
            mOnClickListener.onItemClick(mFilePaths.get(getAdapterPosition()));
        }

    }

    public interface OnClickListener {
        void onItemClick(String path);
    }
}
