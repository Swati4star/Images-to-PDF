package swati4star.createpdf.fragment;

import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;

import com.dd.morphingbutton.MorphingButton;

import java.lang.reflect.Method;
import java.util.Locale;

import swati4star.createpdf.R;

public class ChangeLanguageFragment extends Fragment implements View.OnClickListener {
    private MorphingButton button1, button2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.change_language, container, false);
        button1 = view.findViewById(R.id.button1);
        button2 = view.findViewById(R.id.button2);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
            case R.id.button2:
                Intent intent2 = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }


}


