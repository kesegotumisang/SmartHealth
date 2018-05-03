package bw.ub.cs.smarthealth;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SymptomsFragment extends Fragment {


    public SymptomsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_symptoms, container, false);

        WebView webView = rootView.findViewById(R.id.webView);
        webView.loadUrl("https://legacy.priaid.ch/en-us/enter-symptoms");
        return rootView;
    }

}
