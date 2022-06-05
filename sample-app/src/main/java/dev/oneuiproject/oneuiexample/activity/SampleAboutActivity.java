package dev.oneuiproject.oneuiexample.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sec.sesl.tester.R;

import dev.oneuiproject.oneui.layout.AppInfoLayout;

public class SampleAboutActivity extends AppCompatActivity {
    private AppInfoLayout appInfoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample3_activity_about);

        appInfoLayout = findViewById(R.id.appInfoLayout);

        appInfoLayout.addOptionalText("Extra 1");
        appInfoLayout.addOptionalText("Extra 2");

        appInfoLayout.setMainButtonClickListener(new AppInfoLayout.OnClickListener() {
            @Override
            public void onUpdateClicked(View v) {
                Toast.makeText(SampleAboutActivity.this, "update", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetryClicked(View v) {
                Toast.makeText(SampleAboutActivity.this, "retry", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changeStatus(View v) {
        int s = appInfoLayout.getStatus() + 1;
        if (s == 4) s = -1;
        appInfoLayout.setStatus(s);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.app_info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_app_info) {
            appInfoLayout.openSettingsAppInfo();
            return true;
        }
        return false;
    }
}