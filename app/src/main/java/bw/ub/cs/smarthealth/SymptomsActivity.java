package bw.ub.cs.smarthealth;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import bw.ub.cs.smarthealth.client.DiagnosisClient;
import bw.ub.cs.smarthealth.model.DiagnosedSpecialisation;
import bw.ub.cs.smarthealth.model.Gender;
import bw.ub.cs.smarthealth.model.HealthDiagnosis;
import bw.ub.cs.smarthealth.model.HealthIssueInfo;
import bw.ub.cs.smarthealth.model.HealthItem;
import bw.ub.cs.smarthealth.model.HealthSymptomSelector;
import bw.ub.cs.smarthealth.model.SelectorStatus;

public class SymptomsActivity extends AppCompatActivity {
    private static DiagnosisClient _diagnosisClient;
    String userName;
    String password;
    String authUrl;
    String language;
    String healthUrl;
    WebView webView;
    private ProgressBar progressBar;
    private ImageView imgHeader;
    private String postUrl = "https://legacy.priaid.ch/en-gb/start";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        webView = new WebView(this);
        setContentView(webView  );

       webView.getSettings().setJavaScriptEnabled(true);

        final Activity activity = this;
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadUrl(postUrl);
        /*FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SymptomsActivity.this, MainActivity.class));
            }
        });*/

        // enable / disable javascript
        //webView.getSettings().setJavaScriptEnabled(true);
    }
    static int GetRandom(int maxNumber)
    {
        return ThreadLocalRandom.current().nextInt(0, maxNumber);
    }

    static void writeHeaderMessage(String message)
    {
        Log.d("Health Start:","---------------------------------------------");
        Log.d("Health Message :",message);
        Log.d("Health End:","---------------------------------------------");
    }
    static int loadBodySublocations(int locId) throws Exception
    {
        List<HealthItem> bodySublocations = _diagnosisClient.loadBodySubLocations(locId);

        if (bodySublocations == null || bodySublocations.size() == 0)
            throw new Exception("Empty body sublocations results");

        for (HealthItem loc : bodySublocations)
            System.out.println(loc.Name + " " + loc.ID);

        int randomLocIndex = GetRandom(bodySublocations.size());
        HealthItem randomLocation = bodySublocations.get(randomLocIndex);

        writeHeaderMessage("Sublocations symptoms for selected location " + randomLocation.Name);

        return randomLocation.ID;
    }
    static Integer loadBodyLocations() throws Exception{
        List<HealthItem> bodyLocations = _diagnosisClient.loadBodyLocations();

        if (bodyLocations == null || bodyLocations.size() == 0)
            throw new Exception("Empty body locations results");

        writeHeaderMessage("Body locations:");

        for (HealthItem loc : bodyLocations)
            System.out.println(loc.Name + " (" + loc.ID + ")");

        int randomLocIndex = GetRandom(bodyLocations.size());
        HealthItem randomLocation = bodyLocations.get(randomLocIndex);

        writeHeaderMessage("Sublocations for randomly selected location " + randomLocation.Name);

        return randomLocation.ID;
    }

    static List<HealthSymptomSelector> LoadSublocationSymptoms(int subLocId) throws Exception
    {
        List<HealthSymptomSelector> symptoms = _diagnosisClient.loadSublocationSymptoms(subLocId, SelectorStatus.Man);

        if (symptoms == null || symptoms.size() == 0)
        {
            System.out.println("Empty body sublocations symptoms results");
            return null;
        }

        writeHeaderMessage("Body sublocations symptoms:");

        for (HealthSymptomSelector sym : symptoms)
            System.out.println(sym.Name);

        int randomSymptomIndex = GetRandom(symptoms.size());

        randomSymptomIndex = GetRandom(symptoms.size());

        HealthSymptomSelector randomSymptom = symptoms.get(randomSymptomIndex);

        writeHeaderMessage("Randomly selected symptom: " + randomSymptom.Name);

        List<HealthSymptomSelector> selectedSymptoms = new ArrayList<HealthSymptomSelector>();
        selectedSymptoms.add(randomSymptom);

        LoadRedFlag(randomSymptom);

        return selectedSymptoms;
    }

    static List<Integer> LoadDiagnosis(List<HealthSymptomSelector> selectedSymptoms) throws Exception
    {
        writeHeaderMessage("Diagnosis");

        List<Integer> selectedSymptomsIds = new ArrayList<Integer>();
        for(HealthSymptomSelector symptom : selectedSymptoms){
            selectedSymptomsIds.add(symptom.ID);
        }

        List<HealthDiagnosis> diagnosis = _diagnosisClient.loadDiagnosis(selectedSymptomsIds, Gender.Male, 1988);

        if (diagnosis == null || diagnosis.size() == 0)
        {
            writeHeaderMessage("No diagnosis results for symptom " + selectedSymptoms.get(0).Name);
            return null;
        }

        for (HealthDiagnosis d : diagnosis){
            String specialistions = "";
            for(DiagnosedSpecialisation spec : d.Specialisation)
                specialistions = specialistions.concat(spec.Name + ", ");
            System.out.println(d.Issue.Name + " - " + d.Issue.Accuracy + "% \nSpecialisations : " + specialistions);
        }

        List<Integer> retValue = new ArrayList<Integer>();
        for(HealthDiagnosis diagnose : diagnosis)
            retValue.add(diagnose.Issue.ID);
        return retValue;
    }


    static void LoadSpecialisations(List<HealthSymptomSelector> selectedSymptoms) throws Exception
    {
        writeHeaderMessage("Specialisations");

        List<Integer> selectedSymptomsIds = new ArrayList<Integer>();
        for(HealthSymptomSelector symptom : selectedSymptoms){
            selectedSymptomsIds.add(symptom.ID);
        }

        List<DiagnosedSpecialisation> specialisations = _diagnosisClient.loadSpecialisations(selectedSymptomsIds, Gender.Male, 1988);

        if (specialisations == null || specialisations.size() == 0)
        {
            writeHeaderMessage("No specialisations for symptom " + selectedSymptoms.get(0).Name);
            return;
        }

        for (DiagnosedSpecialisation s : specialisations)
            System.out.println(s.Name + " - " + s.Accuracy + "%");
    }


    static void LoadRedFlag(HealthSymptomSelector selectedSymptom) throws Exception
    {
        String redFlag = "Symptom " + selectedSymptom.Name + " has no red flag";

        if(selectedSymptom.HasRedFlag)
            redFlag = _diagnosisClient.loadRedFlag(selectedSymptom.ID);

        writeHeaderMessage(redFlag);
    }

    static void LoadIssueInfo(int issueId) throws Exception
    {
        HealthIssueInfo issueInfo = _diagnosisClient.loadIssueInfo(issueId);
        writeHeaderMessage("Issue info");
        System.out.println("Name: " + issueInfo.Name);
        System.out.println("Professional Name: " +issueInfo.ProfName );
        System.out.println("Synonyms: " + issueInfo.Synonyms);
        System.out.println("Short Description: " + issueInfo.DescriptionShort );
        System.out.println("Description: " + issueInfo.Description);
        System.out.println("Medical Condition: " + issueInfo.MedicalCondition);
        System.out.println("Treatment Description: " +issueInfo.TreatmentDescription );
        System.out.println("Possible symptoms: " + issueInfo.PossibleSymptoms + "\n");
    }

    static void LoadProposedSymptoms(List<HealthSymptomSelector> selectedSymptoms) throws Exception
    {
        List<Integer> selectedSymptomsIds = new ArrayList<Integer>();
        for(HealthSymptomSelector symptom : selectedSymptoms){
            selectedSymptomsIds.add(symptom.ID);
        }
        List<HealthItem> proposedSymptoms = _diagnosisClient.loadProposedSymptoms(selectedSymptomsIds, Gender.Male, 1988);

        if (proposedSymptoms == null || proposedSymptoms.size() == 0)
        {
            writeHeaderMessage("No proposed symptoms for selected symptom " + selectedSymptoms.get(0).Name);
            return;
        }

        String proposed = "";
        for(HealthItem diagnose : proposedSymptoms)
            proposed = proposed.concat(diagnose.Name) + ", ";

        writeHeaderMessage("Proposed symptoms: " + proposed);
    }

    static void simulate(){

        try {
            // Load body locations
            int selectedLocationID = loadBodyLocations();

            // Load body sublocations
            int selectedSublocationID = loadBodySublocations(selectedLocationID);

            // Load body sublocations symptoms
            List<HealthSymptomSelector> selectedSymptoms = LoadSublocationSymptoms(selectedSublocationID);

            // Load diagnosis (reloading if data is not conclusive)
            int count = 0;
            int maxTries = 10;
            boolean sucess = false;
            List<Integer> diagnosis = new ArrayList<Integer>();
            while(sucess!=true) {
                try {
                    diagnosis = LoadDiagnosis(selectedSymptoms);
                    sucess= true;
                } catch (Exception diagnosisException){
                    // reload data if diagnosis result is not conclusive
                    selectedLocationID = loadBodyLocations();
                    selectedSymptoms = LoadSublocationSymptoms(selectedSublocationID);
                    if (++count == maxTries) throw diagnosisException;
                    sucess=false;
                }
            }


            // Load specialisations
            LoadSpecialisations(selectedSymptoms);

            // Load issue info
            for (Integer issueId : diagnosis)
                LoadIssueInfo(issueId);

            // Load proposed symptoms
            LoadProposedSymptoms(selectedSymptoms);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private class Callback extends WebViewClient {  //HERE IS THE MAIN CHANGE.

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return (false);
        }

    }
}
