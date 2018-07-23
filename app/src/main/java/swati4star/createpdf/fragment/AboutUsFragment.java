package swati4star.createpdf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;
import swati4star.createpdf.R;


public class AboutUsFragment extends Fragment {

    private Activity mActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_about_us, container, false);
        ButterKnife.bind(this, rootview);
        try {
            PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
            TextView versionText = rootview.findViewById(R.id.version_value);
            versionText.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return rootview;
    }

    @OnClick(R.id.layout_email)
    public void sendmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"swati4star@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getResources().getString(R.string.feedback_subject));
        intent.putExtra(Intent.EXTRA_TEXT, mActivity.getResources().getString(R.string.feedback_text));
        try {
            mActivity.startActivity(Intent.createChooser(intent, mActivity.getString(R.string.feedback_chooser)));
        } catch (android.content.ActivityNotFoundException ex) {
            Snackbar.make(Objects.requireNonNull(mActivity).findViewById(android.R.id.content),
                    R.string.snackbar_no_email_clients,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.layout_website)
    void openWeb() {
        openWebPage("http://swati4star.github.io/Images-to-PDF/");
    }

    @OnClick(R.id.layout_slack)
    void joinSlack() {
        openWebPage("https://goo.gl/J6Hrd4");
    }

    @OnClick(R.id.layout_github)
    void githubRepo() {
        openWebPage("https://github.com/Swati4star/Images-to-PDF");
    }

    @OnClick(R.id.layout_contri)
    void contributorsList() {
        openWebPage("https://github.com/Swati4star/Images-to-PDF/graphs/contributors");
    }

    @OnClick(R.id.layout_playstore)
    void openPlaystore() {
        openWebPage("https://play.google.com/store/apps/details?id=swati4star.createpdf");
    }

    @OnClick(R.id.layout_privacy)
    void privacyPolicy() {
        openWebPage("https://sites.google.com/view/privacy-policy-image-to-pdf/home");
    }

    @OnClick(R.id.layout_license)
    void license() {
        openWebPage("https://github.com/Swati4star/Images-to-PDF/blob/master/LICENSE.md");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    private void openWebPage(String url) {
        Uri uri = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(mActivity.getPackageManager()) != null)
            startActivity(intent);
    }

}
