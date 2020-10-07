In this repo some android codes are deprecated, I only mention

"import android.support.v7.app.AppCompatActivity;" this import statement is deprecated in new AS versions, With Android Studio 3.2 and higher, you can migrate an existing project to AndroidX by selecting Refactor > Migrate to AndroidX from the menu bar. https://developer.android.com/jetpack/androidx/migrate check this page for more informations.

You can use the following import statement to import AppCompatActivity, "androidx.appcompat.app.AppCompatActivity" https://developer.android.com/reference/androidx/appcompat/app/AppCompatActivity check this page for more informations.

AndroidX replaces the original support library APIs with packages in the androidx namespace. Only the package and Maven artifact names changed; class, method, and field names did not change.

