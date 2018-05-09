package bw.ub.cs.smarthealth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

public class STIFactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stfacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("STI Facts");

        String text = "<ol  > " +
                "<li>New estimates show that there are about 20 million new sexually transmitted infections in the United States each year.</li>" +
                "<li>Young people, between the ages of 15 to 24, account for 50% of all new STDs, although they represent just 25% of the sexually experienced population</li>" +
                "<li>46% of American high school students have had sexual intercourse and potentially are at risk for human immunodeficiency virus (HIV) infection and other STDs. Get yourself tested for HIV -- and tell others you did! Sign up for Update Your Status.</li>" +
                "<li>In 2012, gonorrhea rates were highest among adolescents and young adults. In 2012, the highest rates were observed among women aged 20–24 years (578.5) and 15–19 years (521.2).</li>" +
                "<li>The Centers for Disease Control and Prevention estimates that there are more than 110 million STIs among men and women in the US. This includes both new and existing infections.</li>" +
                "<li>The annual number of new infections is roughly equal among teen girls (51%) and teen guys (49%).</li>\n" +
                "<li>HPV (human papillomavirus) accounts for the majority of prevalent STIs in the US.</li>\n" +
                "<li>The US has the highest rate of STD infection in the industrialized world.</li>\n" +
                "<li>6 in 10 sexually active high school teens reported using condoms during their most recent sexual intercourse.</li>\n" +
                "<li>1 in 4 teens contract a sexually transmitted disease every year.<li>\n" +
                "<li>Less than half of adults age 18 to 44 have ever been tested for an STD other than HIV/AIDS.</li>" +
                "</ol>";

        Spanned spanned = Html.fromHtml(text);

        TextView textView = findViewById(R.id.sti_facts_txt);
        textView.setText(spanned);
    }
}
