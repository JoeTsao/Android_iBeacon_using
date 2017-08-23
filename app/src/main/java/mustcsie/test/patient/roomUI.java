package mustcsie.test.patient;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mustcsie.test.R;
import mustcsie.test.nurse.carjob;
import mustcsie.test.ui.UIMain;
import mustcsie.test.ui.Utils;

/**
 * Created by Administrator on 2016/11/18.
 */

public class roomUI extends AppCompatActivity {
    Timer timer ;
    TimerTask timerTask;
    TextView roomui_TimeTV;
    TextView roomui_RoomnumTV;
    TextView roomui_PCTV;
    TextView roomui_Time2TV;
    Button roomui_choosePBT;
    Button roomui_chkroomdoneBT;
    Button roomui_leftroomBT;
    private static String roomnum;
    private static String name;
    private static String carnum;
    private static String datetime;
    private static String datetimeH;
    private static String timerange="";
    public SharedPreferences setting;
    private static String count;
    private static String timearea="3";
    static RequestQueue queue ;
    private static final String patientdataCheck_REQUEST_URL = "http://xxx.xxx.xxx/patientdataCheck.php";
    private static final String PaitentRoomUI_REQUEST_URL = "http://xxx.xxx.xxx/patientRoomUI.php";
    private static final String PATIENT_REQUEST_URL = "http://xxx.xxx.xxx/getpatientdata.php";
    private static final String uploadchkroomtime_REQUEST_URL = "http://xxx.xxx.xxx/uploadchkroomtime.php";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomui);
        Intent intent = getIntent();
        setting = getSharedPreferences("LoginInfo", 0);
        name = setting.getString("name", "");
        roomnum = intent.getStringExtra("roomnum");
        carnum=setting.getString("carnum", "");
        queue = Volley.newRequestQueue(roomUI.this);
        roomui_TimeTV = (TextView) findViewById(R.id.roomui_TimeTV);
        roomui_RoomnumTV = (TextView) findViewById(R.id.roomui_RoomnumTV);
        roomui_PCTV = (TextView) findViewById(R.id.roomui_PCTV);
        roomui_Time2TV = (TextView) findViewById(R.id.roomui_Time2TV);
        roomui_choosePBT = (Button) findViewById(R.id.roomui_choosePBT);
        roomui_chkroomdoneBT = (Button) findViewById(R.id.roomui_chkroomdoneBT);
        roomui_leftroomBT = (Button) findViewById(R.id.roomui_leftroomBT);

        roomui_choosePBT.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean CH = Utils.isFastDoubleClick();
                if (CH) {
                    StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                            PATIENT_REQUEST_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray J = new JSONArray(response);
                                final CharSequence[] list2 = new CharSequence[J.length()];
                                int len = J.length();
                                for (int i = 0; i < len; i++) {
                                    list2[i] = J.getString(i).substring(J.getString(i).indexOf("\"") + 1, J.getString(i).lastIndexOf("\""));
                                }
                                AlertDialog.Builder dlg = new AlertDialog.Builder(roomUI.this);
                                dlg.setTitle("選擇病人");
                                dlg.setItems(list2, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, final int which) {
                                        Intent intent = new Intent(roomUI.this, patientActivity.class);
                                        intent.putExtra("list", list2[which]);
                                        roomUI.this.startActivity(intent);
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
                            params.put("select", "1");
                            params.put("index", "patient");
                            params.put("room", roomnum);
                            return params;
                        }

                    };
                    queue.add(mStringRequest);
            }
          }
        });

        roomui_chkroomdoneBT.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                        patientdataCheck_REQUEST_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject J = new JSONObject(response);

                            if(Integer.parseInt(timearea)==3){
                                final AlertDialog dlg = new AlertDialog.Builder(roomUI.this).create();
                                dlg.setTitle("錯誤");
                                dlg.setMessage("目前非巡房時間");
                                dlg.setButton(AlertDialog.BUTTON_POSITIVE, "返回", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dlg.dismiss();
                                    }
                                });

                                dlg.show();
                            }else {
                                String count = J.getString("count");
                                if (Integer.parseInt(count) > 0) {

                                    JSONArray K =  J.getJSONArray("name");
                                    final AlertDialog.Builder dlg = new AlertDialog.Builder(roomUI.this);
                                    dlg.setTitle("錯誤");
                                    String a = "";
                                    for (int i = 0; i < Integer.parseInt(count); i++) {
                                        a = a + K.getString(i) + "\n";
                                    }
                                    dlg.setMessage("以下病人未上傳資料，無法完成巡房\n" + a);
                                    dlg.setPositiveButton("返回上傳資料", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });
                                    dlg.setNegativeButton("離開房間", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            stoptimertask();
                                            Intent intent = new Intent(roomUI.this, UIMain.class);
                                            roomUI.this.startActivity(intent);
                                            finish();
                                        }
                                    });

                                    dlg.show();
                                }else{
                                    stoptimertask();
                                    uploadtime();
                                    Intent intent = new Intent(roomUI.this, carjob.class);
                                    intent.putExtra("carnum",carnum);
                                    roomUI.this.startActivity(intent);
                                    finish();
                                }

                            }

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
                        params.put("carnum",carnum);
                        params.put("roomnum", roomnum);
                        params.put("timearea", timearea);
                        return params;
                    }

                };
                queue.add(mStringRequest);

            }
        });

        roomui_leftroomBT.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dlg = new AlertDialog.Builder(roomUI.this);
                dlg.setTitle("注意");
                dlg.setMessage("確定要離開房間嗎?");
                dlg.setPositiveButton("返回上傳資料", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                dlg.setNegativeButton("離開房間", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        stoptimertask();
                        Intent intent = new Intent(roomUI.this, UIMain.class);
                        roomUI.this.startActivity(intent);
                        finish();
                    }
                });

                dlg.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(roomUI.this);
        dlg.setTitle("注意");
        dlg.setMessage("確定要離開房間嗎?");
        dlg.setPositiveButton("返回上傳資料", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        dlg.setNegativeButton("離開房間", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stoptimertask();
                Intent intent = new Intent(roomUI.this, UIMain.class);
                roomUI.this.startActivity(intent);
                finish();

            }
        });

        dlg.show();
    return;
    }

    protected void onPause(){
        super.onPause();

    }

    protected void onResume(){
        super.onResume();
        startTimer();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        stoptimertask();
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask,100, 2000);

    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.d(" timerTask"+this.toString(),"RUN");
                getserverdata();

                queue.getCache().clear();
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

    public void uploadtime(){

        switch (timearea){
            case "0" :
                timerange="早上";
                break;
            case "1" :
                timerange="中午";
                break;
            case "2" :
                timerange="晚上";
                break;
        }

        StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                uploadchkroomtime_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject J = new JSONObject(response);
                    if(J.getString("success").matches("UPDATE")){
                        Toast.makeText(roomUI.this, "資料更新成功.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(roomUI.this, "資料上傳成功.", Toast.LENGTH_SHORT).show();
                    }


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
                params.put("roomnum", roomnum);
                params.put("timearea", timerange);
                params.put("name", name);
                return params;
            }

        };
        queue.add(mStringRequest);
    }
    public void getserverdata(){
        StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                PaitentRoomUI_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject J = new JSONObject(response);
                    datetime = J.getString("datetime");
                    datetimeH = J.getString("datetimeH");
                    int T =Integer.parseInt(datetimeH);
                    count = J.getString("count");
                    roomui_TimeTV.setText("現在時間："+datetime);
                    roomui_RoomnumTV.setText("您已進入病房　"+roomnum+"　號房");
                    roomui_PCTV.setText("病房人數："+count+" 人");
                    if(T>=6 && T<=10){
                        roomui_Time2TV.setText("時段:  早上");
                        timearea=""+0;
                    }
                    else if(T>=11 && T<=16){
                        roomui_Time2TV.setText("時段:  中午");
                        timearea=""+1;
                    }
                    else if (T>=17 && T<=23){
                        roomui_Time2TV.setText("時段:  晚上");
                        timearea=""+2;
                    }else{
                        roomui_Time2TV.setText("時段:  深夜");
                        timearea=""+3;
                    }

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
                params.put("roomnum", roomnum);
                params.put("timearea", timearea);
                return params;
            }

        };
        queue.add(mStringRequest);
    }
}
