package swati4star.createpdf.fragment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.fragment.app.testing.FragmentScenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ImageToPdfFragmentTest {

    @Test
    public void testImagesPreservedOnOrientationChange() {
        ArrayList<String> testUris = new ArrayList<>();
        testUris.add("uri1");
        testUris.add("uri2");

        FragmentScenario<ImageToPdfFragment> scenario = FragmentScenario.launchInContainer(
                ImageToPdfFragment.class, null, swati4star.createpdf.R.style.AppThemeWhite);
        
        scenario.onFragment(fragment -> {
            fragment.mImagesUri.addAll(testUris);
        });

        scenario.recreate();

        scenario.onFragment(fragment -> {
            assertEquals(2, fragment.mImagesUri.size());
            assertTrue(fragment.mImagesUri.contains("uri1"));
            assertTrue(fragment.mImagesUri.contains("uri2"));
        });
    }
}
