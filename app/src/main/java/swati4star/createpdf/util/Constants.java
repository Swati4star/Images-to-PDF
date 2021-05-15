package swati4star.createpdf.util;

import android.Manifest;
import android.graphics.Color;

public class Constants {

    public static final String DEFAULT_COMPRESSION = "DefaultCompression";
    public static final String SORTING_INDEX = "SORTING_INDEX";
    public static final String IMAGE_EDITOR_KEY = "first";
    public static final String DEFAULT_FONT_SIZE_TEXT = "DefaultFontSize";
    public static final int DEFAULT_FONT_SIZE = 11;
    public static final String PREVIEW_IMAGES = "preview_images";
    public static final String DATABASE_NAME = "ImagesToPdfDB.db";
    public static final String DEFAULT_FONT_FAMILY_TEXT = "DefaultFontFamily";
    public static final String DEFAULT_FONT_FAMILY = "TIMES_ROMAN";
    public static final String DEFAULT_FONT_COLOR_TEXT = "DefaultFontColor";
    public static final int DEFAULT_FONT_COLOR = -16777216;
    // key for text to pdf (TTP) page color
    public static final String DEFAULT_PAGE_COLOR_TTP = "DefaultPageColorTTP";
    // key for images to pdf (ITP) page color
    public static final String DEFAULT_PAGE_COLOR_ITP = "DefaultPageColorITP";
    public static final int DEFAULT_PAGE_COLOR = Color.WHITE;
    public static final String DEFAULT_THEME_TEXT = "DefaultTheme";
    public static final String DEFAULT_THEME = "System";
    public static final String DEFAULT_IMAGE_BORDER_TEXT = "Image_border_text";
    public static final String RESULT = "result";
    public static final String SAME_FILE = "SameFile";
    public static final String DEFAULT_PAGE_SIZE_TEXT = "DefaultPageSize";
    public static final String DEFAULT_PAGE_SIZE = "A4";
    public static final String CHOICE_REMOVE_IMAGE = "CHOICE_REMOVE_IMAGE";
    public static final int DEFAULT_QUALITY_VALUE = 30;
    public static final int DEFAULT_BORDER_WIDTH = 0;
    public static final String STORAGE_LOCATION = "storage_location";
    public static final String DEFAULT_IMAGE_SCALE_TYPE_TEXT = "image_scale_type";
    public static final String IMAGE_SCALE_TYPE_STRETCH = "stretch_image";
    public static final String IMAGE_SCALE_TYPE_ASPECT_RATIO = "maintain_aspect_ratio";
    public static final String PG_NUM_STYLE_PAGE_X_OF_N = "pg_num_style_page_x_of_n";
    public static final String PG_NUM_STYLE_X_OF_N = "pg_num_style_x_of_n";
    public static final String PG_NUM_STYLE_X = "pg_num_style_x";
    public static final String MASTER_PWD_STRING = "master_password";

    public static final String IMAGE_TO_PDF_KEY = "Images to PDF";
    public static final String TEXT_TO_PDF_KEY = "Text To PDF";
    public static final String QR_BARCODE_KEY = "QR & Barcodes";
    public static final String VIEW_FILES_KEY = "View Files";
    public static final String HISTORY_KEY = "History";
    public static final String ADD_TEXT_KEY = "Add Text";
    public static final String ADD_PASSWORD_KEY = "Add password";
    public static final String REMOVE_PASSWORD_KEY = "Remove password";
    public static final String ROTATE_PAGES_KEY = "Rotate Pages";
    public static final String ADD_WATERMARK_KEY = "Add Watermark";
    public static final String ADD_IMAGES_KEY = "Add Images";
    public static final String MERGE_PDF_KEY = "Merge PDF";
    public static final String SPLIT_PDF_KEY = "Split PDF";
    public static final String INVERT_PDF_KEY = "Invert Pdf";
    public static final String COMPRESS_PDF_KEY = "Compress PDF";
    public static final String REMOVE_DUPLICATE_PAGES_KEY = "Remove Duplicate Pages";
    public static final String REMOVE_PAGES_KEY = "Remove Pages";
    public static final String REORDER_PAGES_KEY = "Reorder Pages";
    public static final String EXTRACT_TEXT_KEY = "Extract Text";
    public static final String EXTRACT_IMAGES_KEY = "Extract Images";
    public static final String PDF_TO_IMAGES_KEY = "PDF to Images";
    public static final String EXCEL_TO_PDF_KEY = "Excel to PDF";
    public static final String ZIP_TO_PDF_KEY = "ZIP to PDF";

    public static final String BUNDLE_DATA = "bundle_data";
    public static final String REORDER_PAGES = "Reorder pages";
    public static final String REMOVE_PAGES = "Remove pages";
    public static final String COMPRESS_PDF = "Compress PDF";
    public static final String ADD_PWD = "Add password";
    public static final String REMOVE_PWd = "Remove password";
    public static final String ADD_IMAGES = "add_images";
    public static final String PDF_TO_IMAGES = "pdf_to_images";
    public static final String EXTRACT_IMAGES = "extract_images";

    public static final String LAUNCH_COUNT = "launch_count";

    public static final String pdfDirectory = "/PDF Converter/";
    public static final String pdfExtension = ".pdf";
    public static final String appName = "PDF Converter";
    public static final String PATH_SEPERATOR = "/";
    public static final String textExtension = ".txt";
    public static final String excelExtension = ".xls";
    public static final String excelWorkbookExtension = ".xlsx";
    public static final String docExtension = ".doc";
    public static final String docxExtension = ".docx";
    public static final String tempDirectory = "temp";

    public static final String AUTHORITY_APP = "com.swati4star.shareFile";

    public static final String ACTION_SELECT_IMAGES = "android.intent.action.SELECT_IMAGES";
    public static final String ACTION_VIEW_FILES = "android.intent.action.VIEW_FILES";
    public static final String ACTION_TEXT_TO_PDF = "android.intent.action.TEXT_TO_PDF";
    public static final String ACTION_MERGE_PDF = "android.intent.action.MERGE_PDF";
    public static final String OPEN_SELECT_IMAGES = "open_select_images";

    public static final String THEME_WHITE = "White";
    public static final String THEME_BLACK = "Black";
    public static final String THEME_DARK = "Dark";
    public static final String THEME_SYSTEM = "System";

    public static final String IS_WELCOME_ACTIVITY_SHOWN = "is_Welcome_activity_shown";
    public static final String SHOW_WELCOME_ACT = "show_welcome_activity";

    public static final String VERSION_NAME = "VERSION_NAME";

    public static final String PREF_PAGE_STYLE = "pref_page_number_style";
    public static final String PREF_PAGE_STYLE_ID = "pref_page_number_style_rb_id";

    public static final int REQUEST_CODE_FOR_WRITE_PERMISSION = 4;
    public static final int REQUEST_CODE_FOR_READ_PERMISSION = 5;


    public static final String[] WRITE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final String[] READ_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    public static final int MODIFY_STORAGE_LOCATION_CODE = 1;

    public static final int ROTATE_PAGES = 20;
    public static final int ADD_PASSWORD = 21;
    public static final int REMOVE_PASSWORD = 22;
    public static final int ADD_WATERMARK = 23;

    //Preference key name.
    public static final String RECENT_PREF = "Recent";
}