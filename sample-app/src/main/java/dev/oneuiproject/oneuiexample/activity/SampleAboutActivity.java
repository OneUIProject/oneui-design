package dev.oneuiproject.oneuiexample.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sec.sesl.tester.R;

import dev.oneuiproject.oneui.layout.AppInfoLayout;
import dev.oneuiproject.oneui.widget.Toast;

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
                Toast.makeText(SampleAboutActivity.this,
                        "onUpdateClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetryClicked(View v) {
                Toast.makeText(SampleAboutActivity.this,
                        "onRetryClicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changeStatus(View v) {
        int s = appInfoLayout.getStatus() + 1;
        if (s == 4) s = -1;
        appInfoLayout.setStatus(s);
    }

    public void openGitHubPage(View v) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://github.com/OneUIProject/oneui-design"));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(
                    this, "No suitable activity found", Toast.LENGTH_SHORT).show();
        }
    }
}