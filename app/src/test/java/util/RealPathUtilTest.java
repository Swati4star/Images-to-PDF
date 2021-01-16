package util;


import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import swati4star.createpdf.util.RealPathUtil;

import static org.mockito.Mockito.mock;

public class RealPathUtilTest {
    RealPathUtil _realPathUtil;
    Context _context;

    @Before
    public void setup() {
        _context = mock(Context.class);
        _realPathUtil = RealPathUtil.getInstance();


    }

    //test if the real path of google drive is passed , is the result true as must be
    @Test
    public void isGoogleDriveFileTest()
            throws NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        Method isDriveFileMethodTest = RealPathUtil.class.getDeclaredMethod("isDriveFile", URI.class);
        isDriveFileMethodTest.setAccessible(true);
        boolean isGoogleDrive = (boolean) isDriveFileMethodTest.invoke(_realPathUtil, "https://com.google.android.apps.docs.storage");
        Assert.assertTrue(isGoogleDrive);
        boolean isGoogleDriveLegacy =
                (boolean) isDriveFileMethodTest.invoke
                        (_realPathUtil, "com.google.android.apps.docs.storage.legacy");
        Assert.assertTrue(isGoogleDriveLegacy);

    }

    //test if the real path of download path is passed , is the result true as must be
    @Test
    public void isDownloadsDocumentTest()
            throws NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        Method isDownloadsDocumenteMethodTest = RealPathUtil.class.getDeclaredMethod("isDownloadsDocument", URI.class);
        isDownloadsDocumenteMethodTest.setAccessible(true);
        boolean isDownloadsDocumente =
                (boolean) isDownloadsDocumenteMethodTest.invoke
                        (_realPathUtil, "content://com.android.providers.downloads.documents/document/3025");
        Assert.assertTrue(isDownloadsDocumente);

    }

    //test if the real path of download path is  not passed , is the result true as must be
    @Test
    public void isRawDownloadsDocumentTest()
            throws NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        Method isRawDownloadsDocumenttMethodTest =
                RealPathUtil.class.getDeclaredMethod("isRawDownloadsDocument", URI.class);
        isRawDownloadsDocumenttMethodTest.setAccessible(true);
        boolean isRawDownloadsDocumen =
                (boolean) isRawDownloadsDocumenttMethodTest.invoke
                        (_realPathUtil, "content://com.android.providers.downloads.documents/document/3025");
        Assert.assertFalse(isRawDownloadsDocumen);

    }

    //test if the real path of download path with subfolder  is passed , does the result equals as must be
    @Test
    public void getSubFoldersTest()
            throws NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        Method getSubFoldersMethodTest =
                RealPathUtil.class.getDeclaredMethod("getSubFolders", URI.class);
        getSubFoldersMethodTest.setAccessible(true);
        String getSubFolders =
                (String) getSubFoldersMethodTest.invoke
                        (_realPathUtil,
                                "com.android.providers.downloads.documents" +
                                        "/document/raw/Download/IsraaPhone/Israa/FinalPro");
        Assert.assertEquals(getSubFolders, "IsraaPhone/Israa/");

    }
}
