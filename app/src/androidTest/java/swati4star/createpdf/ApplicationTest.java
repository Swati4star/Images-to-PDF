package swati4star.createpdf;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("swati4star.createpdf", appContext.getPackageName());
    }
}