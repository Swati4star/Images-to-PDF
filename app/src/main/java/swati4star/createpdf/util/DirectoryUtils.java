package swati4star.createpdf.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import swati4star.createpdf.R;

import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;

public class DirectoryUtils {

    private final Context mContext;
    private final SharedPreferences mSharedPreferences;

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
        ArrayList<File> pdfFiles = new ArrayList<>();
        File folder = getOrCreatePdfDirectory();
        File[] files = folder.listFiles();
        if (files == null)
            return null;
        for (File file : files) {
            if (file.isDirectory())
                Collections.addAll(pdfFiles, file.listFiles());
        }
        if (pdfFiles.isEmpty())
            return null;
        return pdfFiles;
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
     * get all the file paths (inside the directory & on home directory)
     * @return - list of file paths
     */
    public ArrayList<String> getAllFilePaths() {
        ArrayList<String> pdfPaths = new ArrayList<>();
        ArrayList<File> pdfFiles;
        ArrayList<File> pdfFromOtherDir = getPdfFromOtherDirectories();
        final File[] files = getOrCreatePdfDirectory().listFiles();
        if ((files == null || files.length == 0) && pdfFromOtherDir == null) {
            return null;
        } else {
            pdfFiles = getPdfsFromPdfFolder(files);
            if (pdfFromOtherDir != null) {
                pdfFiles.addAll(pdfFromOtherDir);
            }
        }
        if (pdfFiles != null) {
            for (File pdf : pdfFiles) {
                pdfPaths.add(pdf.getAbsolutePath());
            }
        }
        return pdfPaths;
    }

    /**
     * Get parent folder name of file with given path
     * @param p - path of file
     * @return - parent folder name
     */
    public String getParentFolder(String p) {
        String folName = null;
        try {
            //Get Name of Parent Folder of File
            // Folder Name found between first occurance of string %3A and %2F from path
            // of content://...
            if (p.contains("%3A")) {
                int beg = p.indexOf("%3A") + 3;
                folName = p.substring(beg, p.indexOf("%2F"));
                Log.d("img", folName);
            } else {
                folName = null;
            }

        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
            e.printStackTrace();
        }
        return folName;
    }
}
