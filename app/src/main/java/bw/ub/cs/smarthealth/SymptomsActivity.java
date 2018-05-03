package bw.ub.cs.smarthealth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class SymptomsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_symptoms);


        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        WebView webView = new WebView(this);
        webView.loadUrl("https://legacy.priaid.ch/en-us/enter-symptoms");
        setContentView(webView);
        // enable / disable javascript
        webView.getSettings().setJavaScriptEnabled(true);
    }
}
