package mustcsie.test.patient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mustcsie.test.R;

/**
 * Created by Administrator on 2016/10/23.
 */

public class patient_physiologicalActivity extends AppCompatActivity {
    private static final String PATIENT_REQUEST_URL = "http://xxx.xxx.xxx/getpatientdata.php";
    private static final String WRITE_REQUEST_URL = "http://xxx.xxx.xxx/writepatientdata.php";
    private static final String IOPUTSERVER_REQUEST_URL = "http://xxx.xxx.xxx/patientdataIO.php";
    private Button loginendbuttonBT;
    private static String select = "0";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_physiological);
        final RequestQueue queue = Volley.newRequestQueue(patient_physiologicalActivity.this);
        loginendbuttonBT = (Button) findViewById(R.id.loginendbutton) ;
        final TextView loginname_view = (TextView) findViewById(R.id.loginname_view) ;
        final TextView view_loginyear = (TextView) findViewById(R.id.view_loginyear) ;
        final TextView textView6 = (TextView) findViewById(R.id.textView6) ;
        final EditText patient_tp = (EditText) findViewById(R.id.patient_tp) ;
        final EditText patient_blood = (EditText) findViewById(R.id.patient_blood) ;
        final EditText patient_spo2 = (EditText) findViewById(R.id.patient_spo2) ;

        Intent intent = getIntent();
        final String list = intent.getStringExtra("list2");
        loginname_view.setText(list);

        StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                PATIENT_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
               // Log.d("RP", response);
                try {
                    JSONObject jr = new JSONObject(response);
                    String bp = jr.getString("bp");
                    String tp = jr.getString("tp");
                    String spo2 = jr.getString("spo2");
                    String datetime = jr.getString("datetime");
                    String datetimeH = jr.getString("datetimeH");
                    int timeH = Integer.parseInt(datetimeH);
                    patient_tp.setText(tp);
                    patient_blood.setText(bp);
                    patient_spo2.setText(spo2);
                    view_loginyear.setText(datetime);
                    if(timeH>=6 && timeH<=10){
                        select = "0";
                        textView6.setText("目前時段:  早上");
                    }
                    else if(timeH>=11 && timeH<=16){
                        select = "1";
                        textView6.setText("目前時段:  中午");
                    }
                    else if (timeH>=17 && timeH<=23){
                        select = "2";
                        textView6.setText("目前時段:  晚上");
                    }else{
                        textView6.setText("目前時段:  不可上傳");
                        final AlertDialog dlg2 = new AlertDialog.Builder(patient_physiologicalActivity.this).create();

                        dlg2.setTitle("提醒");
                        dlg2.setMessage("非可上傳資料的時段");
                        dlg2.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dlg2.dismiss();
                                finish();
                            }
                        });
                        dlg2.show();
                    }

                    queue.getCache().clear();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("select", "0");
                params.put("patient", list);
                return params;
            }

        };
        queue.add(mStringRequest);

        loginendbuttonBT.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String tp = patient_tp.getText().toString();
                final String bp = patient_blood.getText().toString();
                final String spo2 = patient_spo2.getText().toString();
                if(tp.matches("") || bp.matches("") || spo2.matches("")){
                    final AlertDialog.Builder dlg=new AlertDialog.Builder(patient_physiologicalActivity.this);
                    dlg.setTitle("數值錯誤");
                    dlg.setMessage("請輸入數值");
                    dlg.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dlg.show();
                }
                else {
                    int tpi = Integer.parseInt(tp);
                    int bpi = Integer.parseInt(bp);
                    int spo2i = Integer.parseInt(spo2);
                    if (tpi > 24 && tpi < 51 && bpi > 19 && bpi < 301 && spo2i > -1 && spo2i < 101) {
                        final String patient = list;
                        final String cheak = "1";

                        StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                                IOPUTSERVER_REQUEST_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                //Log.d("Response", response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("bp", bp);
                                params.put("tp", tp);
                                params.put("spo2", spo2);
                                params.put("cheak", select);
                                params.put("patient", patient);
                                return params;
                            }
                        };
                        queue.add(mStringRequest);

                        StringRequest mStringRequest2 = new StringRequest(Request.Method.POST,
                                WRITE_REQUEST_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if (success) {
                                        Toast.makeText(patient_physiologicalActivity.this, "資料上傳成功", Toast.LENGTH_SHORT).show();
                                        System.gc();
                                        finish();
                                    } else {
                                        String errorlog = jsonResponse.getString("errorlog");
                                        Toast.makeText(patient_physiologicalActivity.this, "資料上傳失敗", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(patient_physiologicalActivity.this, errorlog, Toast.LENGTH_SHORT).show();
                                    }
                                    queue.getCache().clear();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.toString());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("bp", bp);
                                params.put("tp", tp);
                                params.put("spo2", spo2);
                                params.put("cheak", cheak);
                                params.put("patient", patient);
                                return params;
                            }
                        };
                        queue.add(mStringRequest2);


                    } else {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(patient_physiologicalActivity.this);
                        dlg.setTitle("數值錯誤");
                        dlg.setMessage("請輸入正確數值\n體溫(25~50℃)\n血壓(20~500mmHg)\n血氧(0~100%)");
                        dlg.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dlg.show();
                    }
                }
            }
        });

    }
}