package bw.ub.cs.smarthealth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

public class CommonSTIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_sti);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Common STIs");

        String text = "<h1>Common STIs</h1>" +
                "<h2>Human papillomavirus infection </h2> \n" +
                "An infection that causes warts in various parts of the body, depending on the strain.\n" +
                "<h2>Genital herpes </h2>\n" +
                "A common sexually transmitted infection marked by genital pain and sores.\n" +
                "<h2>Chlamydia </h2>\n" +
                "A common sexually transmitted infection that may not cause symptoms.\n" +
                "<h2>Gonorrhea </h2>\n" +
                "A sexually transmitted bacterial infection that, if untreated, may cause infertility.\n" +
                "<h2>HIV/AIDS </h2>\n" +
                "HIV causes AIDS and interferes with the body's ability to fight infections.\n" +
                "<h2>Syphilis </h2>\n" +
                "A bacterial infection usually spread by sexual contact that starts as a painless sore.";

        Spanned spanned = Html.fromHtml(text);

        TextView textView = findViewById(R.id.common_sti_txt);
        textView.setText(spanned);
    }
}
