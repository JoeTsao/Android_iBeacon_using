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
import mustcsie.test.nurseboss.check_scheduleActivity;
import mustcsie.test.nurseboss.history;
import mustcsie.test.nurseboss.patching;

/**
 * Created by Administrator on 2016/10/21.
 */


public class choose_nurseboss extends AppCompatActivity {


    private Button big_nurse_check;
    private Button  big_nurse_information;
    private Button  big_nurse_patching;
    private Button  big_nurse_history;
    private Button logout;
    String carnum = "";
    RequestQueue queue;
    private static String name;
    public static File file;
    public SharedPreferences setting;
    public long logintime1;
    Timer timer ;
    TimerTask timerTask;
    private static final String CARDATA_URL = "http://xxx.xxx.xxx/cardata.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_nurseboss);
        startTimer();
        file = new File("/data/data/mustcsie.test/shared_prefs","LoginInfo.xml");
        setting = getSharedPreferences("LoginInfo", 0);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        TextView welcomemsg = (TextView) findViewById(R.id.welcomemsg) ;
        welcomemsg.setText("" + name);
        //timer.schedule(new updatedata(), 100, 1000);
         carnum = setting.getString("carnum", "");
        queue = Volley.newRequestQueue(choose_nurseboss.this);
        big_nurse_check = (Button)findViewById(R.id.big_nurse_check);
        big_nurse_information = (Button)findViewById(R.id.big_nurse_information);
        big_nurse_patching = (Button)findViewById(R.id.big_nurse_patching);
        big_nurse_history = (Button)findViewById(R.id.big_nurse_history);
        logout = (Button)findViewById(R.id.logoutnurseboss);
        big_nurse_check.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(choose_nurseboss.this, check_scheduleActivity.class);
                choose_nurseboss.this.startActivity(intent);
            }

        });
        big_nurse_information.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(choose_nurseboss.this, patientdatas.class);
                choose_nurseboss.this.startActivity(intent);
            }
        });

        big_nurse_patching.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(choose_nurseboss.this, patching.class);
                choose_nurseboss.this.startActivity(intent);
            }
        });
        big_nurse_history.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(choose_nurseboss.this, history.class);
                intent.putExtra("RGselect",true);
                choose_nurseboss.this.startActivity(intent);
            }
        });
        logout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                        CARDATA_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject J = new JSONObject(response);
                            Intent intent = new Intent(choose_nurseboss.this, LoginActivity.class);
                            choose_nurseboss.this.startActivity(intent);
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
                        params.put("carnum", "");
                        params.put("caruser", "");
                        params.put("write","T");

                        return params;
                    }

                };
                queue.add(mStringRequest);
                file.delete();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stoptimertask();
        if(file.exists()){
            file.delete();
        }
    }

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder ddgg=new AlertDialog.Builder(choose_nurseboss.this);
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
                final  File file = new File("/data/data/mustcsie.test/shared_prefs","LoginInfo.xml");
                StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                        CARDATA_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject J = new JSONObject(response);
                            Intent intent = new Intent(choose_nurseboss.this, LoginActivity.class);
                            choose_nurseboss.this.startActivity(intent);
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
                        params.put("carnum", "");
                        params.put("caruser", "");
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

}

