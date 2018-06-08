package com.dd.sample;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Indeterminate Progress Sample
 */
public class Sample1Activity extends Activity {

    public static void startThisActivity(Activity activity) {
        activity.startActivity(new Intent(activity, Sample1Activity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_sample_1);

        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setTitle(R.string.IndeterminateProgressSample);
        }

        final CircularProgressButton circularButton1 = (CircularProgressButton) findViewById(R.id.circularButton1);
        circularButton1.setIndeterminateProgressMode(true);
        circularButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = circularButton1.getProgress();
                Log.e(">>>>", ">>>> progress: " + progress);

                if (progress == 0) {
                    circularButton1.setProgress(50);
                } else if (progress == 100) {
                    circularButton1.setProgress(0);
                } else {
                    circularButton1.setProgress(100);
                }
            }
        });
    }
}
