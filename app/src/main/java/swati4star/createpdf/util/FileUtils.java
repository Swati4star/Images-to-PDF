package swati4star.createpdf.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import swati4star.createpdf.R;



public class FileUtils {

    Context context;

    public FileUtils(Context context) {
        this.context = context;
    }

    /**
     * Sorts the given file list in increasing alphabetical  order
     *
     * @param filesList list of files to be sorted
     */
    public void sortByNameAlphabetical(ArrayList<File> filesList) {
        Collections.sort(filesList);
    }

    /**
     * Sorts the given file list by date from newest to oldest
     *
     * @param filesList list of files to be sorted
     */
    public void sortFilesByDateNewestToOldest(ArrayList<File> filesList) {
        Collections.sort(filesList, new Comparator<File>() {
            @Override
            public int compare(File file, File file2) {
                return Long.compare(file2.lastModified(), file.lastModified());
            }
        });
    }

    /**
     * Sorts the given file list in increasing order of file size
     *
     * @param filesList list of files to be sorted
     */
    public void sortFilesBySizeIncreasingOrder(ArrayList<File> filesList) {
        Collections.sort(filesList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return Long.compare(file1.length(), file2.length());
            }
        });
    }

    /**
     * Sorts the given file list in decreasing order of file size
     *
     * @param filesList list of files to be sorted
     */
    public void sortFilesBySizeDecreasingOrder(ArrayList<File> filesList) {
        Collections.sort(filesList, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return Long.compare(file2.length(), file1.length());
            }
        });
    }
    
    public ArrayList<File> getPdfsFromFolder(File[] files) {
        final ArrayList<File> pdfFiles = new ArrayList<>();
        for (File file : files) {
            if (!file.isDirectory() && file.getName().endsWith(context.getString(R.string.pdf_ext))) {
                pdfFiles.add(file);
                Log.v("adding", file.getName());
            }
        }
        return pdfFiles;
    }

    /**
     * Returns pdf files from folder
     *
     * @param files list of files (folder)
     */
    public ArrayList<File> getPdfsFromPdfFolder(File[] files) {
        return getPdfsFromFolder(files);
    }

    /**
     * create PDF directory if directory does not exists
     */
    public File getOrCreatePdfDirectory() {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + context.getResources().getString(R.string.pdf_dir));
        if (!folder.exists()) {
            boolean isCreated = folder.mkdir();
        }
        return folder;
    }
}