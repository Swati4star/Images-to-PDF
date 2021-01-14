package util;


import android.app.Activity;
import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    public void isGoogleDriveFileTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method _isDriveFileMethodTest = RealPathUtil.class.getDeclaredMethod("isDriveFile", URI.class);
        _isDriveFileMethodTest.setAccessible(true);
        boolean _isGoogleDrive = (boolean) _isDriveFileMethodTest.invoke(_realPathUtil, "https://com.google.android.apps.docs.storage");
        Assert.assertTrue(_isGoogleDrive);
        boolean _isGoogleDriveLegacy = (boolean) _isDriveFileMethodTest.invoke(_realPathUtil, "com.google.android.apps.docs.storage.legacy");
        Assert.assertTrue(_isGoogleDriveLegacy);

    }

    //test if the real path of download path is passed , is the result true as must be
    @Test
    public void isDownloadsDocumentTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method _isDownloadsDocumenteMethodTest = RealPathUtil.class.getDeclaredMethod("isDownloadsDocument", URI.class);
        _isDownloadsDocumenteMethodTest.setAccessible(true);
        boolean _isDownloadsDocumente = (boolean) _isDownloadsDocumenteMethodTest.invoke(_realPathUtil, "content://com.android.providers.downloads.documents/document/3025");
        Assert.assertTrue(_isDownloadsDocumente);

    }
    //test if the real path of download path is  not passed , is the result true as must be
    @Test
    public void isRawDownloadsDocumentTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method _isRawDownloadsDocumenttMethodTest = RealPathUtil.class.getDeclaredMethod("isRawDownloadsDocument", URI.class);
        _isRawDownloadsDocumenttMethodTest.setAccessible(true);
        boolean _isRawDownloadsDocumen = (boolean) _isRawDownloadsDocumenttMethodTest.invoke(_realPathUtil, "content://com.android.providers.downloads.documents/document/3025");
        Assert.assertFalse(_isRawDownloadsDocumen);

    }
    //test if the real path of download path with subfolder  is passed , does the result equals as must be
    @Test
    public void getSubFoldersTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method _getSubFoldersMethodTest = RealPathUtil.class.getDeclaredMethod("getSubFolders", URI.class);
        _getSubFoldersMethodTest.setAccessible(true);
        String _getSubFolders = (String) _getSubFoldersMethodTest.invoke(_realPathUtil, "com.android.providers.downloads.documents/document/raw/Download/IsraaPhone/Israa/FinalPro");
        Assert.assertEquals(_getSubFolders, "IsraaPhone/Israa/");

    }
}
