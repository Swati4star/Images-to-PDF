package swati4star.createpdf.pickit;

public interface PickiTCallbacks {
    void PickiTonStartListener();
    void PickiTonProgressUpdate(int progress);
    void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason);
}