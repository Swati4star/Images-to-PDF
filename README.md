# Images To PDF

### Badges
[![Build Status](https://travis-ci.org/Swati4star/Images-to-PDF.svg?branch=master)](https://travis-ci.org/Swati4star/Images-to-PDF)
[![Code Climate](https://codeclimate.com/github/Swati4star/Images-to-PDF.svg)](https://codeclimate.com/github/Swati4star/Images-to-PDF) 
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-PDF%20Converter-blue.svg?style=true)](https://android-arsenal.com/details/3/7132)

# Table of Contents
1. [Features](#Features)
2. [Contributing](#Contributing)
3. [Installing & Contributing](#Installing--Contributing)
4. [Code & Issues](#Code--Issues)
5. [Project Maintainers](#Project-Maintainers)


[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/app/swati4star.createpdf)
[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" alt="Get it on Google Play" height=
"80">](https://play.google.com/store/apps/details?id=swati4star.createpdf)

### About 
Have JPG when you need a PDF? Convert JPG to PDF in a few seconds! :smiley:  
Here is an easy to use Android app to convert images to PDF file! This 
gives people an easy, reliable way to present and exchange documents - 
regardless of the software, hardware, or operating systems being used by anyone who views the document. 
This apps allows users to do just that along with an abundance of other features.

<img src="./screenshots/image_to_pdf2.gif"  width="300px">

### Support Us
<a href="https://www.buymeacoffee.com/qITGMWB" target="_blank"><img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" alt="Buy Me A Coffee" style="height: auto !important;width: auto !important;" ></a>


### Features 
- Create PDF from multiple images from camera or gallery
- View your converted PDFs
  - Open, Rename, Delete, print, share files
  - Sort the files order based on a number of options
  - View File details
  - Encrypt PDF
  - Decrypt PDF
  - Rotate Pages
- Different themes
- Merge existing PDFs
- Split existing PDFs
- Convert text file to PDFs
- Compress existing PDF
- Remove pages from a PDF
- Rearrange pages of a PDF
- Extract images from PDF
- History : View all the PDF related conversions

Here is a home page to get quick access to all the features "PDF Converter" provides:

<img src="./screenshots/0_1_home.png" width="190px"> <img src="./screenshots/0_2_home.png" width="190px">

### Creating a PDF file

##### Step 1 : User can view the PDF files previously created or create a new one! 
<img src="./screenshots/1_home.png" width="190px">


##### Step 2 : Select the images
<img src="./screenshots/2_1_camera.png" width="190px"> <img src="./screenshots/2_gallery.png" width="190px">


##### Step 3 : Name the PDF file
<img src="./screenshots/3_rename.png" width="190px">


##### Step 4 : Creating PDF
<img src="./screenshots/4_converting.png" width="190px">


Hurray! PDF files of selected images are created.


### Viewing the PDF files

<img src="./screenshots/5_viewfiles.png" width="190px"> <img src="./screenshots/6_viewfiles_Action.png" width="190px">

### Different themes
Go to Settings, and you can have three type of themes : Black, Dark and White, [Code Example](Code--Example)

Black | Dark | White |
--- | --- | --- |
<img src="./screenshots/home_theme_black.png" width="190px"> | <img src="./screenshots/home_theme_dark.png" width="190px"> |  <img src="./screenshots/home_theme_white.png" width="190px"> |

### Merge PDF
<img src="./screenshots/7_merge_pdf.png" width="190px">

### Text to PDF
<img src="./screenshots/8_text_to_pdf.png" width="190px">

### History
<img src="./screenshots/9_history.png" width="190px">

### Installing & Contributing
1. Create a fork and git clone a copy of the repository.
2. Navigate through the repository and search for any issues or places that can be improved.
3. You can also work on any known issues if you have not found any others already.
4. Work off your own fork
5. Recommended to work off Android Studio in order avoid any compiling errors.
6. JUnit can be used to test your code/functionality
7. Submit a pull request

#### Note: Make sure submitted issues, commits, and branches are descriptive.

### Code Example
- Here is an example of a function setting the theme for the app.

```
  public void setThemeApp(Context context) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String themeName = mSharedPreferences.getString(Constants.DEFAULT_THEME_TEXT,
                Constants.DEFAULT_THEME);
        if (themeName == null)
            return;
        switch (themeName) {
            case THEME_WHITE:
                context.setTheme(R.style.AppThemeWhite);
                break;
            case THEME_BLACK:
                context.setTheme(R.style.AppThemeBlack);
                break;
            case THEME_DARK:
                context.setTheme(R.style.ActivityThemeDark);
                break;
            case THEME_SYSTEM:
            default:
                if ((context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                    context.setTheme(R.style.ActivityThemeDark);
                } else {
                    context.setTheme(R.style.AppThemeWhite);
                }
        }
    }
```



### Testing
Here is an example of a proper test case for a functionality:
- Android Studio allows for testing app functionality through a virtual emulator or physical android device.

```

    public void shouldSortPathsByFileNamesAscending() {
        // given
        int ascendingSortOption = 0;
        List<String> paths = getFilePaths();

        // when
        ImageSortUtils.getInstance().performSortOperation(ascendingSortOption, paths);

        // then
        Assert.assertEquals(
                asList(
                        "src/A-oldest",
                        "src/B-middle",
                        "src/C-latest"
                ),
                paths
        );
    }
    
``` 
 
  
#### Dependencies
+ [Butterknife](https://jakewharton.github.io/butterknife/)
+ [Folderpicker](https://github.com/kashifo/android-folder-picker-library)
+ [Image-cropper](https://github.com/ArthurHub/Android-Image-Cropper)
+ [iTextG](http://developers.itextpdf.com/itextg-android)
+ [Lottie](https://github.com/airbnb/lottie-android)
+ [Matisse](https://github.com/zhihu/Matisse)
+ [Material Dialogs](https://github.com/afollestad/material-dialogs)
+ [Material Ripple](https://github.com/balysv/material-ripple)
+ [Morphing Button](https://github.com/dmytrodanylyk/android-morphing-button)
+ [Picasso](http://square.github.io/picasso/)
+ [Picasso-transformations](https://github.com/wasabeef/picasso-transformations)
+ [PhotoEditor](https://github.com/burhanrashid52/PhotoEditor)
+ [viewpager-transformers](https://github.com/geftimov/android-viewpager-transformers)
+ [zxing](https://github.com/zxing/zxing)

#### Code & Issues
If you are a developer and you wish to contribute to the app please fork the project
and submit a pull request.
Follow [Github Flow](https://help.github.com/articles/github-flow/) for collaboration!
If you have any questions, feel free to ask [me](mailto:swati4star@gmail.com) about whatever you want.
[Here](https://github.com/Swati4star/Images-to-PDF/issues) is the list of known issues.

### Project Maintainers
This project is founded and actively maintained by [Swati Garg](https://github.com/Swati4star/). For any sort of queries feel free to mail at swati4star@gmail.com.

