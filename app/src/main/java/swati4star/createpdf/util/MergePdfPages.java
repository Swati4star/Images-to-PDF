package swati4star.createpdf.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MergePdfPages extends AsyncTask<Void, Void, Uri> {

    public interface Callback {
        void onComplete(Uri outputUri);
    }

    private final Context mContext;
    private final Uri mInputUri;
    private final String mPages;
    private final String mOutputName;
    private final Callback mCallback;

    public MergePdfPages(Context context,
                         Uri inputUri,
                         String pages,
                         String outputName,
                         Callback callback) {

        this.mContext = context.getApplicationContext();
        this.mInputUri = inputUri;
        this.mPages = pages;
        this.mOutputName = outputName;
        this.mCallback = callback;
    }

    @Override
    protected Uri doInBackground(Void... voids) {

        try (InputStream inputStream =
                     mContext.getContentResolver().openInputStream(mInputUri)) {

            if (inputStream == null) return null;

            PdfReader reader = new PdfReader(inputStream);

            Uri outputUri = createOutputUri(mOutputName);
            if (outputUri == null) return null;

            OutputStream out = openOutputStream(outputUri);
            if (out == null) return null;

            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, out);

            document.open();

            Integer[] pages = parsePages(mPages);
            int total = reader.getNumberOfPages();

            for (Integer p : pages) {
                if (p >= 1 && p <= total) {
                    copy.addPage(copy.getImportedPage(reader, p));
                }
            }

            reader.close();
            document.close();
            out.close();

            return outputUri;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Uri uri) {
        if (mCallback != null) {
            mCallback.onComplete(uri);
        }
    }

    // ---------- STORAGE HANDLING ----------

    private Uri createOutputUri(String name) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".pdf");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            values.put(MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS);

            return mContext.getContentResolver().insert(
                    MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    values
            );

        } else {

            File downloads =
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS);

            File file = new File(downloads, name + ".pdf");

            return Uri.fromFile(file);
        }
    }

    private OutputStream openOutputStream(Uri uri) throws Exception {

        if ("content".equals(uri.getScheme())) {
            return mContext.getContentResolver().openOutputStream(uri);
        } else {
            return new FileOutputStream(new File(uri.getPath()));
        }
    }

    // ---------- PAGE PARSER ----------

    private static Integer[] parsePages(String input) {

        List<Integer> result = new ArrayList<>();

        for (String part : input.split(",")) {

            part = part.trim();

            if (part.contains("-")) {

                String[] r = part.split("-");

                int start = Integer.parseInt(r[0].trim());
                int end = Integer.parseInt(r[1].trim());

                for (int i = start; i <= end; i++) {
                    result.add(i);
                }

            } else {
                result.add(Integer.parseInt(part));
            }
        }

        return result.toArray(new Integer[0]);
    }
}