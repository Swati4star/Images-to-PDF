package swati4star.createpdf.pickit;

interface CallBackTask {
    void PickiTonPreExecute();
    void PickiTonProgressUpdate(int progress);
    void PickiTonPostExecute(String path, boolean wasDriveFile, boolean wasSuccessful, String reason);
}