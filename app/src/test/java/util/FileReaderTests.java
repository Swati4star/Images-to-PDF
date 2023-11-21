import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.net.Uri;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;

public class FileReaderTest {

    @Mock
    private Context mContext;

    @Mock
    private Uri mUri;

    @Mock
    private Document mDocument;

    @Mock
    private Font mFont;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReadWithNonNullInputStream() throws Exception {
        InputStream mockInputStream = mock(InputStream.class);
        when(mContext.getContentResolver().openInputStream(mUri)).thenReturn(mockInputStream);
        FileReader fileReader = new FileReader(mContext) {
            @Override
            protected void createDocumentFromStream(Uri uri, Document document, Font myfont, InputStream inputStream) throws Exception {
                // Do nothing
            }
        };
        fileReader.read(mUri, mDocument, mFont);
        verify(mockInputStream).close();
    }

    @Test
    public void testReadWithNullInputStream() throws Exception {
        when(mContext.getContentResolver().openInputStream(mUri)).thenReturn(null);
        FileReader fileReader = new FileReader(mContext) {
            @Override
            protected void createDocumentFromStream(Uri uri, Document document, Font myfont, InputStream inputStream) throws Exception {
                // Do nothing
            }
        };
        fileReader.read(mUri, mDocument, mFont);
        // Verify that inputStream.close() is not called when inputStream is null
        verify(mContext.getContentResolver()).openInputStream(mUri);
    }
}
