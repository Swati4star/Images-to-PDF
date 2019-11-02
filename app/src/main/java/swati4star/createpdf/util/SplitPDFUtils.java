package swati4star.createpdf.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;

import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.Constants.pdfExtension;

public class SplitPDFUtils {

    private static final int NO_ERROR = 0;
    private static final int ERROR_PAGE_NUMBER = 1;
    private static final int ERROR_PAGE_RANGE = 2;
    private static final int ERROR_INVALID_INPUT = 3;

    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;

    public SplitPDFUtils(Activity context) {
        this.mContext = context;
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
    }

    /**
     * Breaks up the splitDetail String into ranges where a "," is found
     *
     * @param path        the input pdf path
     * @param splitDetail string that contains split configuration
     * @return output splitted string array
     */
    public ArrayList<String> splitPDFByConfig(String path, String splitDetail) {
        String splitConfig = splitDetail.replaceAll("\\s+", "");
        ArrayList<String> outputPaths = new ArrayList<>();
        String delims = "[,]";
        String[] ranges = splitConfig.split(delims);
        Log.v("Ranges", Arrays.toString(ranges));

        // if input is invalid then return empty arraylist
        if (path == null || !isInputValid(path, ranges))
            return outputPaths;

        try {
            String folderPath = mSharedPreferences.getString(STORAGE_LOCATION,
                    StringUtils.getInstance().getDefaultStorageLocation());
            PdfReader reader = new PdfReader(path);
            PdfCopy copy;
            Document document;
            for (String range : ranges) {
                int startPage;
                int endPage;

                String fileName = folderPath + FileUtils.getFileName(path);

                /*
                 * If the pdf is single page only then convert whole range into int
                 * else break the range on "-",where startpage will be substring
                 * from first letter to "-" and endpage will be from "-" till last letter.
                 *
                 */
                if (reader.getNumberOfPages() > 1) {
                    if (!range.contains("-")) {
                        startPage = Integer.parseInt(range);
                        document = new Document();
                        fileName = fileName.replace(pdfExtension,
                                "_" + startPage + pdfExtension);
                        copy = new PdfCopy(document, new FileOutputStream(fileName));

                        document.open();
                        copy.addPage(copy.getImportedPage(reader, startPage));
                        document.close();
                        outputPaths.add(fileName);
                        new DatabaseHelper(mContext).insertRecord(fileName,
                                mContext.getString(R.string.created));
                    } else {

                        startPage = Integer.parseInt(range.substring(0, range.indexOf("-")));
                        endPage = Integer.parseInt(range.substring(range.indexOf("-") + 1));
                        if (reader.getNumberOfPages() == endPage - startPage + 1) {
                            StringUtils.getInstance().showSnackbar(mContext, R.string.split_range_alert);
                        } else {
                            document = new Document();
                            fileName = fileName.replace(pdfExtension,
                                    "_" + startPage + "-" + endPage + pdfExtension);
                            copy = new PdfCopy(document, new FileOutputStream(fileName));
                            document.open();
                            for (int page = startPage; page <= endPage; page++) {
                                copy.addPage(copy.getImportedPage(reader, page));
                            }
                            document.close();

                            new DatabaseHelper(mContext).insertRecord(fileName,
                                    mContext.getString(R.string.created));
                            outputPaths.add(fileName);
                        }
                    }
                } else {
                    StringUtils.getInstance().showSnackbar(mContext, R.string.split_one_page_pdf_alert);
                }
            }
        } catch (IOException | DocumentException | IllegalArgumentException e) {
            e.printStackTrace();
            StringUtils.getInstance().showSnackbar(mContext, R.string.file_access_error);
        }
        return outputPaths;
    }

    /**
     * checks if the user entered split ranges are valid or not
     *
     * @param path   the input pdf path
     * @param ranges string array that contain page range, can be a single integer or range separated by dash like 2-5
     * @return true if input is valid, otherwise false
     */
    private boolean isInputValid(String path, String[] ranges) {
        try {
            PdfReader reader = new PdfReader(path);
            int numOfPages = reader.getNumberOfPages();
            int result = checkRangeValidity(numOfPages, ranges);
            switch (result) {
                case ERROR_PAGE_NUMBER:
                    StringUtils.getInstance().showSnackbar(mContext, R.string.error_page_number);
                    break;
                case ERROR_PAGE_RANGE:
                    StringUtils.getInstance().showSnackbar(mContext, R.string.error_page_range);
                    break;
                case ERROR_INVALID_INPUT:
                    StringUtils.getInstance().showSnackbar(mContext, R.string.error_invalid_input);
                    break;
                default:
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * checks if the user entered split ranges are valid or not
     * the returnValue is initialized with NO_ERROR
     * if no range is given, ERROR_INVALID_INPUT is returned
     * for all the given ranges, if single page (starting page) is only given then we fetch the starting page
     * if starting page is not a number then exception is caught and ERROR_INVALID_INPUT is returned
     * if the starting page is greater than number of pages or is 0 then ERROR_PAGE_NUMBER is returned
     * for hyphenated ranges, e.g 4-8, the start and end page are read
     * if the start or end page are not valid numbers then ERROR_INVALID_INPUT is returned
     * if the start and end page are out of range then ERROR_PAGE_NUMBER is returned
     * if the start page is greater than end page then the range is invalid so ERROR_PAGE_RANGE is returned
     *
     * @param numOfPages total number of pages of pdf
     * @param ranges     string array that contain page range,
     *                   can be a single integer or range separated by dash like 2-5
     * @return 0 if all ranges are valid
     * ERROR_PAGE_NUMBER    if range greater than max number of pages
     * ERROR_PAGE_RANGE     if range is invalid like 9-4
     * ERROR_INVALID_INPUT  if input is invalid like -3 or 3--4 or 3,,4
     */
    public static int checkRangeValidity(int numOfPages, String[] ranges) {
        int startPage, endPage;
        int returnValue = NO_ERROR;

        if (ranges.length == 0)
            returnValue = ERROR_INVALID_INPUT;
        else {
            for (String range : ranges) {
                if (!range.contains("-")) {
                    try {
                        startPage = Integer.parseInt(range);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        returnValue = ERROR_INVALID_INPUT;
                        break;
                    }
                    if (startPage > numOfPages || startPage == 0) {
                        returnValue = ERROR_PAGE_NUMBER;
                        break;
                    }
                } else {
                    try {
                        startPage = Integer.parseInt(range.substring(0, range.indexOf("-")));
                        endPage = Integer.parseInt(range.substring(range.indexOf("-") + 1));
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        returnValue = ERROR_INVALID_INPUT;
                        break;
                    }
                    if (startPage > numOfPages || endPage > numOfPages || startPage == 0 || endPage == 0) {
                        returnValue = ERROR_PAGE_NUMBER;
                        break;
                    } else if (startPage >= endPage) {
                        returnValue = ERROR_PAGE_RANGE;
                        break;
                    }
                }
            }
        }
        return returnValue;
    }
}

