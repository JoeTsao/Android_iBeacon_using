package mustcsie.test.nurse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mustcsie.test.Login.LoginActivity;
import mustcsie.test.R;
import mustcsie.test.nballpatient.patientdatas;
import mustcsie.test.ui.UIMain;


public class choose_nurse extends AppCompatActivity {

    private Button nurse_scanbeacon;
    private Button lookjob;
    private Button logout;
    private Button nurse_patientinfo;
    private static String JOB;
    String carnum = "";
    private static final String GETCARDATA_REQUEST_URL = "http://xxx.xxx.xxx/cardata.php";
    static RequestQueue queue ;
    private static final String CARDATA_URL = "http://xxx.xxx.xxx/cardata.php";
    private static String name;
    public SharedPreferences setting;
    private static  File file;
    public long logintime1;
    Timer timer ;
    TimerTask timerTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_nurse);
        file = new File("/data/data/mustcsie.test/shared_prefs","LoginInfo.xml");
        queue = Volley.newRequestQueue(choose_nurse.this);
        setting = getSharedPreferences("LoginInfo", 0);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        carnum = setting.getString("carnum", "");
        startTimer();
        TextView welcomemsg = (TextView) findViewById(R.id.welcomemsg) ;
        welcomemsg.setText("" + name);
       /* StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                GETCARDATA_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray J = new JSONArray(response);
                    JOB = J.getString(0).substring(J.getString(0).indexOf("\"") + 1, J.getString(0).lastIndexOf("\""));
                    final AlertDialog dlg = new AlertDialog.Builder(choose_nurse.this).create();

                    dlg.setTitle(carnum+" 車 今日派工");
                    dlg.setMessage(JOB);
                    dlg.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dlg.dismiss();
                        }
                    });
                    dlg.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("num", carnum);
                return params;
            }
        };
        queue.add(mStringRequest);*/


        nurse_scanbeacon = (Button)findViewById(R.id.nurse_scanbeacon);

        nurse_scanbeacon.setOnClickListener(new Button.OnClickListener(){

            @Override

            public void onClick(View v) {

                // TODO Auto-generated method stub

                Intent intent = new Intent(choose_nurse.this, UIMain.class);
                choose_nurse.this.startActivity(intent);


            }
        });
        nurse_patientinfo = (Button)findViewById(R.id.nurse_inf);
        nurse_patientinfo.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(choose_nurse.this, patientdatas.class);
                choose_nurse.this.startActivity(intent);
            }
        });

        logout =(Button)findViewById(R.id.logoutnurse);
        logout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer!=null){timer.cancel();}
                timer=null;

                StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                        CARDATA_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject J = new JSONObject(response);
                            Intent intent = new Intent(choose_nurse.this, LoginActivity.class);
                            choose_nurse.this.startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params = new HashMap<>();
                        params.put("carnum", carnum);
                        params.put("caruser", "無使用者");
                        params.put("write","T");

                        return params;
                    }

                };
                queue.add(mStringRequest);
                file.delete();


            }
        });
        lookjob=(Button)findViewById(R.id.lookjob);
        lookjob.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(choose_nurse.this);
                String [] LT={"查看今日派工","查看額外派工"};
                dlg.setTitle("選擇");
                dlg.setItems(LT, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int which) {
                        if(which == 0){
                            Intent intent = new Intent(choose_nurse.this, carjob.class);
                            intent.putExtra("carnum",carnum);
                            choose_nurse.this.startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(choose_nurse.this, check_patching.class);
                            intent.putExtra("carnum",carnum);
                            choose_nurse.this.startActivity(intent);
                        }
                    }
                });
                dlg.show();
            }
        });
    }
    @Override
    public void onBackPressed() {

        final AlertDialog.Builder ddgg=new AlertDialog.Builder(choose_nurse.this);
        ddgg.setTitle("登出");
        ddgg.setMessage("確定要登出使用者嗎?");
        ddgg.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        ddgg.setPositiveButton("登出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stoptimertask();
                StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                        CARDATA_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject J = new JSONObject(response);
                            Intent intent = new Intent(choose_nurse.this, LoginActivity.class);
                            choose_nurse.this.startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params = new HashMap<>();
                        params.put("carnum", carnum);
                        params.put("caruser", "無使用者");
                        params.put("write","T");

                        return params;
                    }

                };
                queue.add(mStringRequest);
                file.delete();


            }
        });


        ddgg.show();
        return;

    }
    @Override
    protected void onPause(){
        super.onPause();
        queue.getCache().clear();
    }
    @Override
    protected void onResume(){
        super.onResume();
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask,100, 1000);

    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.d(" timerTask"+this.toString(),"RUN");
                logintime1 = System.currentTimeMillis();
                setting.edit().putLong("logintime1", logintime1).commit();
                queue.getCache().clear();
                StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                        CARDATA_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject J = new JSONObject(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params = new HashMap<>();
                        params.put("carnum", carnum);
                        params.put("caruser", name);
                        params.put("write","T");
                        return params;
                    }

                };
                queue.add(mStringRequest);

            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        stoptimertask();

        StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                CARDATA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject J = new JSONObject(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params = new HashMap<>();
                params.put("carnum", carnum);
                params.put("caruser", "無使用者");
                params.put("write","T");

                return params;
            }

        };
        queue.add(mStringRequest);
        if(file.exists()){
            file.delete();
        }

    }

}

