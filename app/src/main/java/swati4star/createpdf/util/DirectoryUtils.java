package swati4star.createpdf.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import swati4star.createpdf.R;

import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.excelExtension;
import static swati4star.createpdf.util.Constants.excelWorkbookExtension;
import static swati4star.createpdf.util.Constants.pdfExtension;

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
     * @return ArrayList containing all the pdf files matching the search query
     */
    ArrayList<File> searchPDF(String query) {
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

    // RETURNING LIST OF FILES OR DIRECTORIES

    /**
     * Returns pdf files from folder
     *
     * @param files list of files (folder)
     */
    ArrayList<File> getPdfsFromPdfFolder(File[] files) {
        ArrayList<File> pdfFiles = new ArrayList<>();
        if (files == null)
            return pdfFiles;
        for (File file : files) {
            if (isPDFAndNotDirectory(file))
                pdfFiles.add(file);
        }
        return pdfFiles;
    }

    private ArrayList<File> searchPdfsFromPdfFolder(File[] files) {
        ArrayList<File> pdfFiles = getPdfsFromPdfFolder(files);
        if (files == null)
            return pdfFiles;
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
     * @return tru - if condition satisfies, else false
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
                StringUtils.getInstance().getDefaultStorageLocation()));
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
        walkDir(getOrCreatePdfDirectory());
        ArrayList<File> files = new ArrayList<>();
        for (String path : mFilePaths)
            files.add(new File(path));
        return files;
    }


    /**
     * gets a list of all the pdf files on the user device
     * @return - list of file absolute paths
     */
    ArrayList<String> getAllPDFsOnDevice() {
        mFilePaths = new ArrayList<>();
        walkDir(Environment.getExternalStorageDirectory());
        return mFilePaths;
    }

    /**
     * Walks through given dir & sub directory, and append file path to mFilePaths
     * @param dir - root directory
     */
    private void walkDir(File dir) {
        walkDir(dir, Collections.singletonList(pdfExtension));
    }

    /**
     * Walks through given dir & sub direc, and append file path to mFilePaths
     * @param dir - root directory
     * @param extensions - a list of file extensions to search for
     */
    private void walkDir(File dir, List<String> extensions) {
        File[] listFile = dir.listFiles();
        if (listFile != null) {
            for (File aListFile : listFile) {

                if (aListFile.isDirectory()) {
                    walkDir(aListFile, extensions);
                } else {
                    for (String extension: extensions) {
                        if (aListFile.getName().endsWith(extension)) {
                            //Do what ever u want
                            mFilePaths.add(aListFile.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

    /**
     * gets a list of all the excel files on the user device
     * @return - list of file absolute paths
     */
    ArrayList<String> getAllExcelDocumentsOnDevice() {
        mFilePaths = new ArrayList<>();
        walkDir(Environment.getExternalStorageDirectory(), Arrays.asList(excelExtension, excelWorkbookExtension));
        return mFilePaths;
    }

    /**
     * creates new folder for temp files
     */
    public static void makeAndClearTemp() {
        String dest = Environment.getExternalStorageDirectory().toString() +
                Constants.pdfDirectory + Constants.tempDirectory;
        File folder = new File(dest);
        boolean result = folder.mkdir();

        // clear all the files in it, if any
        if (result && folder.isDirectory()) {
            String[] children = folder.list();
            for (String child : children) {
                new File(folder, child).delete();
            }
        }
    }
}
