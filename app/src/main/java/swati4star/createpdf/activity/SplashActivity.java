package swati4star.createpdf.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void clearHighlights() {
        // 例如，重置所有菜单项的背景或文本颜色
        homeMenuItem.setBackgroundColor(defaultColor);
        settingsMenuItem.setBackgroundColor(defaultColor);
        // 添加其他菜单项
    }
}