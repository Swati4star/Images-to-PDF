package swati4star.createpdf.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import swati4star.createpdf.R;



public class FileUtils {

    private Context mContext;

    public FileUtils(Context context) {
        this.mContext = context;
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
            if (!file.isDirectory() && file.getName().endsWith(mContext.getString(R.string.pdf_ext))) {
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
                + mContext.getResources().getString(R.string.pdf_dir));
        if (!folder.exists()) {
            boolean isCreated = folder.mkdir();
        }
        return folder;
    }

    /**
     * Gives a formatted last modified date for pdf ListView
     * @param file file object whose last modified date is to be returned
     *
     * @return String date modified in formatted form
     **/
    public static String getFormattedDate(File file) {

        Date lastModDate = new Date(file.lastModified());
        String[] formatdate = lastModDate.toString().split(" ");
        String time = formatdate[3];

        String[] formattime =  time.split(":");
        String date = formattime[0] + ":" + formattime[1];
        return formatdate[0] + ", " + formatdate[1] + " " + formatdate[2] + " at " + date;
    }

    /**
     * Gives a formatted size in MB for every pdf in pdf ListView
     * @param file file object whose size is to be returned
     *
     * @return String Size of pdf in formatted form
     */
    public static String getFormattedSize(File file) {

        return String.format("%.2f MB", (double) file.length() / (1024 * 1024));
    }

    /**
     * Prints a file
     * @param file the file to be printed
     */
    public void printFile(final File file) {

        final PrintDocumentAdapter mPrintDocumentAdapter = new PrintDocumentAdapter() {

            @Override
            public void onWrite(PageRange[] pages,
                                ParcelFileDescriptor destination,
                                CancellationSignal cancellationSignal,
                                WriteResultCallback callback) {
                InputStream input = null;
                OutputStream output = null;
                try {
                    input = new FileInputStream(file.getName());
                    output = new FileOutputStream(destination.getFileDescriptor());

                    byte[] buf = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = input.read(buf)) > 0) {
                        output.write(buf, 0, bytesRead);
                    }

                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                } catch (Exception e) {
                    //Catch exception
                } finally {
                    try {
                        if (input != null) {
                            input.close();
                        }
                        if (output != null) {
                            output.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes,
                                 PrintAttributes newAttributes,
                                 CancellationSignal cancellationSignal,
                                 LayoutResultCallback callback,
                                 Bundle extras) {

                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }
                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("myFile")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .build();

                callback.onLayoutFinished(pdi, true);
            }
        };

        PrintManager printManager = (PrintManager) mContext
                .getSystemService(Context.PRINT_SERVICE);

        String jobName = mContext.getString(R.string.app_name) + " Document";
        if (printManager != null)
            printManager.print(jobName, mPrintDocumentAdapter, null);
    }

    /**
     * Emails the desired PDF using application of choice by user
     *
     * @param  file - the file to be shared
     */
    public void shareFile(File file) {
        Uri uri = FileProvider.getUriForFile(mContext, "com.swati4star.shareFile", file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "I have attached a PDF to this message");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("application/pdf");
        mContext.startActivity(Intent.createChooser(intent, "Sharing"));
    }

    /**
     * Share the desired PDFs using application of choice by user
     *
     * @param  files - the list of files to be shared
     */
    public void shareMultipleFiles(List<File> files) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (File file: files) {
            Uri uri = FileProvider.getUriForFile(mContext, "com.swati4star.shareFile", file);
            uris.add(uri);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.i_have_attached_pdfs_to_this_message));
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("application/pdf");
        mContext.startActivity(Intent.createChooser(intent, "Sharing"));
    }

    /**
     * Opens the given PDF file in appropriate Intent
     * @param file - the file to be opened
     */
    public void openFile(File file) {
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Uri uri = FileProvider.getUriForFile(mContext, "com.swati4star.shareFile", file);

        target.setDataAndType(uri,  mContext.getString(R.string.pdf_type));
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, mContext.getString(R.string.open_file));
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(Objects.requireNonNull(mContext),
                    R.string.snackbar_no_pdf_app,
                    Snackbar.LENGTH_LONG).show();
        }
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

        if ( q.containsAll(f) || f.containsAll(q) ) {
            return 1;
        }
        return 0;
    }

    /**
     * Used to search for PDF matching the search query
     * @param query - Query from search bar
     * @return ArrayList containg all the pdf files matching the search query
     */
    public ArrayList<File> searchPDF(String query) {
        ArrayList<File> searchResult = new ArrayList<>();
        final File[] files = getOrCreatePdfDirectory().listFiles();
        ArrayList<File> pdfs = getPdfsFromPdfFolder(files);
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

}