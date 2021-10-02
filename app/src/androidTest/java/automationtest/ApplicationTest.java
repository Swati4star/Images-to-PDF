package automationtest;

import static android.app.Activity.RESULT_OK;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.CoreMatchers.allOf;

import android.Manifest;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.InputType;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.zhihu.matisse.ui.MatisseActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.activity.MainActivity;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ApplicationTest {

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Before
    public void setUpStubs() {
//        UiAutomation uiAutomation = getInstrumentation().getUiAutomation();
//        uiAutomation.grantRuntimePermission("swati4star.createpdf", Manifest.permission.WRITE_EXTERNAL_STORAGE);

        Intents.init();
        Resources resources = getInstrumentation().getTargetContext().getResources();

        Uri parse = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                        resources.getResourcePackageName(R.drawable.abc_vector_test));
        Intent resultIntent = new Intent();
        ArrayList<String> paths = new ArrayList<>();
        paths.add(parse.getPath());
        resultIntent.putStringArrayListExtra(MatisseActivity.EXTRA_RESULT_SELECTION_PATH, paths);

        intending(hasComponent("com.zhihu.matisse.ui.MatisseActivity")).respondWith(new Instrumentation.ActivityResult(RESULT_OK, resultIntent));
    }



    /*TODO
    * Workflow is currently upto Saving File
    * File Is Not saving Successfully , because of some storage permission issue
    * Need to fix it and add Assertion after saving a file
    */
    @Test
    public void shouldDoImagesToPDF() {

        ActivityScenario.launch(MainActivity.class);


        ViewInteraction mainActivitityViewInteraction = Espresso.onView(withId(R.id.drawer_layout));
        mainActivitityViewInteraction.check(matches(isDisplayed()));


        Espresso.onView(allOf(withId(R.id.images_to_pdf), withParentIndex(0))).perform(click());

        Espresso.onView(withId(R.id.addImages)).perform(click());

        Espresso.onView(withText(R.string.snackbar_images_added)).check(matches(isDisplayed()));

        Espresso.onView(withId(R.id.pdfCreate)).perform(click());

        Espresso.onView(withInputType(InputType.TYPE_CLASS_TEXT)).perform(clearText(), typeText("test_file"));

        Espresso.onView(withText(android.R.string.ok)).perform(click());

    }

}
