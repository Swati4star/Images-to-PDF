package swati4star.createpdf.util;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;

import java.io.InputStream;

public abstract class FileReader {
    Context mContext;

    public FileReader(@NonNull Context context) {
        mContext = context;
    }

    void read(Uri uri, Document document, Font myfont) {
        try {
            InputStream inputStream;
            inputStream = mContext.getContentResolver().openInputStream(uri);
            if (inputStream == null)
                return;
            createDocumentFromStream(uri, document, myfont, inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void createDocumentFromStream(
            @NonNull Uri uri, @NonNull Document document, @NonNull Font myfont, @NonNull InputStream inputStream) throws Exception;
}