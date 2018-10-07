package swati4star.createpdf.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfImageObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import swati4star.createpdf.R;
import swati4star.createpdf.database.DatabaseHelper;
import swati4star.createpdf.interfaces.DataSetChanged;
import swati4star.createpdf.interfaces.OnPDFCompressedInterface;
import swati4star.createpdf.model.TextToPDFOptions;

import static swati4star.createpdf.util.Constants.STORAGE_LOCATION;
import static swati4star.createpdf.util.DialogUtils.createCustomDialogWithoutContent;
import static swati4star.createpdf.util.StringUtils.getDefaultStorageLocation;
import static swati4star.createpdf.util.StringUtils.getSnackbarwithAction;
import static swati4star.createpdf.util.StringUtils.showSnackbar;

public class PDFUtils {

    private final Activity mContext;
    private final FileUtils mFileUtils;
    private SparseIntArray mAngleRadioButton;
    private SharedPreferences mSharedPreferences;

    public PDFUtils(Activity context) {
        this.mContext = context;
        this.mFileUtils = new FileUtils(mContext);
        mAngleRadioButton = new SparseIntArray();
        mAngleRadioButton.put(R.id.deg90, 90);
        mAngleRadioButton.put(R.id.deg180, 180);
        mAngleRadioButton.put(R.id.deg270, 270);
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
    }

    /**
     * Creates a mDialog with details of given PDF file
     *
     * @param file - file name
     */
    public void showDetails(File file) {

        String name = file.getName();
        String path = file.getPath();
        String size = FileUtils.getFormattedSize(file);
        String lastModDate = FileUtils.getFormattedSize(file);

        TextView message = new TextView(mContext);
        TextView title = new TextView(mContext);
        message.setText(String.format
                (mContext.getResources().getString(R.string.file_info), name, path, size, lastModDate));
        message.setTextIsSelectable(true);
        title.setText(R.string.details);
        title.setPadding(20, 10, 10, 10);
        title.setTextSize(30);
        title.setTextColor(mContext.getResources().getColor(R.color.black));
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();
        builder.setView(message);
        builder.setCustomTitle(title);
        builder.setPositiveButton(mContext.getResources().getString(R.string.ok),
                (dialogInterface, i) -> dialog.dismiss());
        builder.create();
        builder.show();
    }


    /**
     * Create a PDF from a Text File
     *
     * @param mTextToPDFOptions TextToPDFOptions Object
     */
    public void createPdf(TextToPDFOptions mTextToPDFOptions)
            throws DocumentException, IOException {

        Document document = new Document(PageSize.getRectangle(mTextToPDFOptions.getPageSize()));
        String finalOutput = mSharedPreferences.getString(STORAGE_LOCATION,
                getDefaultStorageLocation()) +
                mTextToPDFOptions.getOutFileName() + ".pdf";
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(finalOutput));
        writer.setPdfVersion(PdfWriter.VERSION_1_7);
        if (mTextToPDFOptions.isPasswordProtected()) {
            writer.setEncryption(mTextToPDFOptions.getPassword().getBytes(),
                    mContext.getString(R.string.app_name).getBytes(),
                    PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY,
                    PdfWriter.ENCRYPTION_AES_128);
        }

        document.open();
        Font myfont = new Font(mTextToPDFOptions.getFontFamily());
        myfont.setStyle(Font.NORMAL);
        myfont.setSize(mTextToPDFOptions.getFontSize());

        document.add(new Paragraph("\n"));
        readTextFile(mTextToPDFOptions.getInFileUri(), document, myfont);
        document.close();

        new DatabaseHelper(mContext).insertRecord(finalOutput, mContext.getString(R.string.created));
    }

    /**
     * Read the Text File and put it in document
     *
     * @param uri      URL to create PDF
     * @param document PDF Document
     * @param myfont   Font style in PDF
     */
    private void readTextFile(Uri uri, Document document, Font myfont) {
        InputStream inputStream;
        try {
            inputStream = mContext.getContentResolver().openInputStream(uri);
            if (inputStream == null)
                return;
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                Paragraph para = new Paragraph(line + "\n", myfont);
                para.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(para);
            }
            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if a PDF at given path is encrypted
     *
     * @param path - path of PDF
     * @return true - if encrypted otherwise false
     */
    public boolean isPDFEncrypted(String path) {
        boolean isEncrypted = false;
        try {
            new PdfReader(path);
        } catch (IOException e) {
            isEncrypted = true;
        }
        return isEncrypted;
    }

    /**
     * Show the dialog for angle of rotation of pdf pages
     *
     * @param sourceFilePath - path of file to be rotated
     */
    public void rotatePages(String sourceFilePath, final DataSetChanged dataSetChanged) {
        MaterialDialog.Builder builder = createCustomDialogWithoutContent(mContext,
                R.string.rotate_pages);
        builder.customView(R.layout.dialog_rotate_pdf, true)
                .onPositive((dialog, which) -> {
                    final RadioGroup angleInput = dialog.getCustomView().findViewById(R.id.rotation_angle);
                    int angle = mAngleRadioButton.get(angleInput.getCheckedRadioButtonId());
                    String destFilePath = mFileUtils.getFileDirectoryPath(sourceFilePath);
                    String fileName = mFileUtils.getFileName(sourceFilePath);
                    destFilePath += String.format(mContext.getString(R.string.rotated_file_name),
                            fileName.substring(0, fileName.lastIndexOf('.')),
                            Integer.toString(angle),
                            mContext.getString(R.string.pdf_ext));
                    boolean result = rotatePDFPages(angle, sourceFilePath,
                            destFilePath, dataSetChanged);
                    if (result) {
                        new DatabaseHelper(mContext).insertRecord(destFilePath,
                                mContext.getString(R.string.rotated));
                    }
                })
                .show();
    }


    /**
     * Rotates pages in pdf
     *
     * @param angle          rotation angle
     * @param sourceFilePath source file path
     * @param destFilePath   destination file path
     * @return true if no error else false
     */
    private boolean rotatePDFPages(int angle, String sourceFilePath, String destFilePath,
                                   final DataSetChanged dataSetChanged) {
        try {
            PdfReader reader = new PdfReader(sourceFilePath);
            int n = reader.getNumberOfPages();
            PdfDictionary page;
            PdfNumber rotate;
            for (int p = 1; p <= n; p++) {
                page = reader.getPageN(p);
                rotate = page.getAsNumber(PdfName.ROTATE);
                if (rotate == null)
                    page.put(PdfName.ROTATE, new PdfNumber(angle));
                else
                    page.put(PdfName.ROTATE, new PdfNumber((rotate.intValue() + angle) % 360));
            }
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destFilePath));
            stamper.close();
            reader.close();
            showSnackbar(mContext, R.string.snackbar_pdfCreated);
            dataSetChanged.updateDataset();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar(mContext, R.string.encrypted_pdf);
        }
        return false;
    }

    public void compressPDF(String inputPath, String outputPath, int quality,
                            OnPDFCompressedInterface onPDFCompressedInterface) {
        new CompressPdfAsync(inputPath, outputPath, quality, onPDFCompressedInterface)
                .execute();
    }

    private static class CompressPdfAsync extends AsyncTask<String, String, String> {

        int quality;
        String inputPath, outputPath;
        boolean success;
        OnPDFCompressedInterface mPDFCompressedInterface;

        CompressPdfAsync(String inputPath, String outputPath, int quality,
                         OnPDFCompressedInterface onPDFCompressedInterface) {
            this.inputPath = inputPath;
            this.outputPath = outputPath;
            this.quality = quality;
            this.mPDFCompressedInterface = onPDFCompressedInterface;
            success = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPDFCompressedInterface.pdfCompressionStarted();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                PdfReader reader = new PdfReader(inputPath);
                int n = reader.getXrefSize();
                PdfObject object;
                PRStream stream;

                for (int i = 0; i < n; i++) {
                    object = reader.getPdfObject(i);
                    if (object == null || !object.isStream())
                        continue;
                    stream = (PRStream) object;
                    PdfObject pdfsubtype = stream.get(PdfName.SUBTYPE);
                    System.out.println(stream.type());
                    if (pdfsubtype != null && pdfsubtype.toString().equals(PdfName.IMAGE.toString())) {
                        PdfImageObject image = new PdfImageObject(stream);
                        byte[] imageBytes = image.getImageAsBytes();
                        Bitmap bmp;
                        bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        if (bmp == null) continue;

                        int width = bmp.getWidth();
                        int height = bmp.getHeight();

                        Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        Canvas outCanvas = new Canvas(outBitmap);
                        outCanvas.drawBitmap(bmp, 0f, 0f, null);

                        ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();

                        outBitmap.compress(Bitmap.CompressFormat.JPEG, quality, imgBytes);
                        stream.clear();
                        stream.setData(imgBytes.toByteArray(), false, PRStream.BEST_COMPRESSION);
                        stream.put(PdfName.TYPE, PdfName.XOBJECT);
                        stream.put(PdfName.SUBTYPE, PdfName.IMAGE);
                        stream.put(PdfName.FILTER, PdfName.DCTDECODE);
                        stream.put(PdfName.WIDTH, new PdfNumber(width));
                        stream.put(PdfName.HEIGHT, new PdfNumber(height));
                        stream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                        stream.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
                    }
                }

                reader.removeUnusedObjects();
                // Save altered PDF
                PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPath));
                stamper.setFullCompression();
                stamper.close();
                reader.close();
                success = true;
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
                success = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mPDFCompressedInterface.pdfCompressionEnded(outputPath, success);
        }
    }

    public void reorderRemovePDF(String inputPath, String output, String pages) {
        try {
            PdfReader reader = new PdfReader(inputPath);
            reader.selectPages(pages);
            if (reader.getNumberOfPages() == 0) {
                showSnackbar(mContext, R.string.remove_pages_error);
                return;
            }
            //if (reader.getNumberOfPages() )
            PdfStamper pdfStamper = new PdfStamper(reader,
                    new FileOutputStream(output));
            pdfStamper.close();
            getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction, v -> mFileUtils.openFile(output)).show();
            new DatabaseHelper(mContext).insertRecord(output,
                    mContext.getString(R.string.created));

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            showSnackbar(mContext, R.string.remove_pages_error);
        }
    }

    public ArrayList<String> splitPDF(String path) {
        ArrayList<String> outputPaths = new ArrayList<>();
        try {
            String folderPath = mSharedPreferences.getString(STORAGE_LOCATION,
                    getDefaultStorageLocation());
            PdfReader reader = new PdfReader(path);
            PdfCopy copy;
            Document document;
            int pages = reader.getNumberOfPages();
            for (int i = 1; i <= pages; i++) {
                document = new Document();
                String fileName = folderPath + mFileUtils.getFileName(path);
                fileName = fileName.replace(mContext.getString(R.string.pdf_ext),
                        i + mContext.getString(R.string.pdf_ext));
                Log.v("splitting", fileName);
                copy = new PdfCopy(document, new FileOutputStream(fileName));
                document.open();
                copy.addPage(copy.getImportedPage(reader, i));
                document.close();
                outputPaths.add(fileName);
                new DatabaseHelper(mContext).insertRecord(fileName,
                        mContext.getString(R.string.created));
            }
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            showSnackbar(mContext, R.string.split_error);
        }
        return outputPaths;
    }
}
