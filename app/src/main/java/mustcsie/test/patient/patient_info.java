package mustcsie.test.patient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import mustcsie.test.Login.LoginRequest;
import mustcsie.test.R;

/**
 * Created by Administrator on 2016/10/22.
 */

public class patient_info extends AppCompatActivity {
    private static final String PATIENT_REQUEST_URL = "http://xxx.xxx.xxx/getpatientdata.php";
    RequestQueue queue;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_information);
        final TextView patientname_view = (TextView) findViewById(R.id.patientname_view) ;
        final TextView patientsex_view = (TextView) findViewById(R.id.patientsex_view) ;
        final TextView patientage_view = (TextView) findViewById(R.id.patientage_view) ;
        final TextView patientexterdate_view = (TextView) findViewById(R.id.patientexterdate_view) ;
        final TextView patientdisease_view = (TextView) findViewById(R.id.patientdisease_view) ;
        final TextView patientremark_view = (TextView) findViewById(R.id.patientremark_view) ;
        Intent intent = getIntent();
        String list = intent.getStringExtra("list2");
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jr = new JSONObject(response);
                    String sex = jr.getString("sex");
                    String age = jr.getString("age");
                    String enterdate = jr.getString("enterdate");
                    String name = jr.getString("name");
                    String disease = jr.getString("disease");
                    String note = jr.getString("note");
                    patientname_view.setText(name);
                    patientsex_view.setText(sex);
                    patientage_view.setText(age);
                    patientexterdate_view.setText(enterdate);
                    patientdisease_view.setText(disease);
                    patientremark_view.setText(note);
                    queue.getCache().clear();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        LoginRequest loginRequest = new LoginRequest(PATIENT_REQUEST_URL,0, list, responseListener);
        queue = Volley.newRequestQueue(patient_info.this);
        queue.add(loginRequest);
    }
}
