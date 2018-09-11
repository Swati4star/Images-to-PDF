package swati4star.createpdf.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import swati4star.createpdf.R;

import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.pdfExtension;
import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;

public class DirectoryUtils {

    private final Context mContext;
    private final SharedPreferences mSharedPreferences;
    private ArrayList<String> mFilePaths;

    public DirectoryUtils(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Used to search for PDF matching the search query
     * @param query - Query from search bar
     * @return ArrayList containg all the pdf files matching the search query
     */
    public ArrayList<File> searchPDF(String query) {
        ArrayList<File> searchResult = new ArrayList<>();
        final File[] files = getOrCreatePdfDirectory().listFiles();
        ArrayList<File> pdfs = searchPdfsFromPdfFolder(files);
        for (File pdf : pdfs) {
            String path = pdf.getPath();
            String[] fileName = path.split("/");
            String pdfName = fileName[fileName.length - 1].replace("pdf" , "");
            if (checkChar(query , pdfName) == 1) {
                searchResult.add(pdf);
            }
        }
        return searchResult;
    }

    /**
     * Used in searchPDF to give the closest result to search query
     * @param query - Query from search bar
     * @param fileName - name of PDF file
     * @return 1 if the search query and filename has same characters , otherwise 0
     */
    private int checkChar(String query , String fileName) {
        query = query.toLowerCase();
        fileName = fileName.toLowerCase();
        Set<Character> q = new HashSet<>();
        Set<Character> f = new HashSet<>();
        for ( char c : query.toCharArray() ) {
            q.add(c);
        }
        for ( char c : fileName.toCharArray() ) {
            f.add(c);
        }

        if ( q.containsAll(f) || f.containsAll(q) )
            return 1;

        return 0;
    }

    // RETURING LIST OF FILES OR DIRECTORIES

    /**
     * Returns pdf files from folder
     *
     * @param files list of files (folder)
     */
    public ArrayList<File> getPdfsFromPdfFolder(File[] files) {
        ArrayList<File> pdfFiles = new ArrayList<>();
        for (File file : files) {
            if (isPDFAndNotDirectory(file))
                pdfFiles.add(file);
        }
        return pdfFiles;
    }

    private ArrayList<File> searchPdfsFromPdfFolder(File[] files) {
        ArrayList<File> pdfFiles = getPdfsFromPdfFolder(files);
        for (File file : files) {
            if (file.isDirectory()) {
                for (File dirFiles : file.listFiles()) {
                    if (isPDFAndNotDirectory(dirFiles))
                        pdfFiles.add(dirFiles);
                }
            }
        }
        return pdfFiles;
    }

    /**
     * Checks if a given file is PDF
     * @param file - input file
     * @return tru - if condition satistfies, else false
     */
    private boolean isPDFAndNotDirectory(File file) {
        return !file.isDirectory() &&
                file.getName().endsWith(mContext.getString(R.string.pdf_ext));
    }

    /**
     * create PDF directory if directory does not exists
     */
    public File getOrCreatePdfDirectory() {
        File folder = new File(mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation()));
        if (!folder.exists())
            folder.mkdir();
        return folder;
    }

    /**
     * get the PDF files stored in directories other than home directory
     * @return ArrayList of PDF files
     */
    public ArrayList<File> getPdfFromOtherDirectories() {
        mFilePaths = new ArrayList<>();
        walkdir(getOrCreatePdfDirectory());
        ArrayList<File> files = new ArrayList<>();
        for (String path : mFilePaths)
            files.add(new File(path));
        return files;
    }

    /**
     * get the PDF Directory from directory name
     * @param dirName - name of the directory to be searched for
     * @return pdf directory if it exists , else null
     */
    public File getDirectory(String dirName) {
        File folder = new File(mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation()) + dirName);
        if (!folder.exists()) {
            return null;
        }
        return folder;
    }

    /**
     * gets a list of all the pdf files on the user device
     * @return - list of file absolute paths
     */
    public ArrayList<String> getAllPDFsOnDevice() {
        mFilePaths = new ArrayList<>();
        walkdir(Environment.getExternalStorageDirectory());
        return mFilePaths;
    }

    /**
     * Walks through given dir & sub direc, and append file path to mFilePaths
     * @param dir - root directory
     */
    private void walkdir(File dir) {
        File[] listFile = dir.listFiles();
        if (listFile != null) {
            for (File aListFile : listFile) {

                if (aListFile.isDirectory()) {
                    walkdir(aListFile);
                } else {
                    if (aListFile.getName().endsWith(pdfExtension)) {
                        //Do what ever u want
                        mFilePaths.add(aListFile.getAbsolutePath());
                    }
                }
            }
        }
    }
}
