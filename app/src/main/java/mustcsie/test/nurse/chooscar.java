package mustcsie.test.nurse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mustcsie.test.Login.LoginActivity;
import mustcsie.test.R;

/**
 * Created by Administrator on 2016/11/10.
 */

public class chooscar extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListAdapter mListAdapter = null;
    private static final String CARDATA_URL = "http://xxx.xxx.xxx/cardata.php";
    static RequestQueue queue ;
    Timer timer ;
    TimerTask timerTask;
    ListView mLVBLE=null;
    private static int lenX = 0;
    private static int lenY = 0;
    private static  String[][] list = {{""}, {""}};
    private static  String carnum;
    private static String name = "";
    public static File file;
    private static boolean successUSER;
    public SharedPreferences setting;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nurse_carchoose);
        queue = Volley.newRequestQueue(chooscar.this);
        mLVBLE= (ListView)findViewById(R.id.choosecarLS);
        mLVBLE.setOnItemClickListener(this);
        mListAdapter = new ListAdapter(this);
        setting = getSharedPreferences("LoginInfo", 0);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        synchronized(mListAdapter)
        {
            getserverdata();
            mListAdapter.notifyDataSetChanged();
            mLVBLE.setAdapter(mListAdapter);
        }
    }

    protected void onPause(){
        super.onPause();
        queue.getCache().clear();
        stoptimertask();
    }

    protected void onResume(){
        super.onResume();
        startTimer();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        carnum = mListAdapter.getItemText(position);
        StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                CARDATA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject J = new JSONObject(response);
                    successUSER = J.getBoolean("success");
                    if(successUSER) {
                        setting = getSharedPreferences("LoginInfo", 0);
                        Intent intent = new Intent(chooscar.this, choose_nurse.class);
                        intent.putExtra("name", name);
                        StringRequest  mStringRequest2 = new StringRequest(Request.Method.POST,
                                CARDATA_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject O = new JSONObject(response);

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
                        queue.add(mStringRequest2);

                        setting.edit().putString("carnum", carnum).commit();
                        chooscar.this.startActivity(intent);
                        chooscar.this.finish();

                    }else{
                        final AlertDialog dlg = new AlertDialog.Builder(chooscar.this).create();
                        dlg.setTitle("錯誤");
                        dlg.setMessage("此藥車已登入，請選擇其他台");
                        dlg.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dlg.dismiss();
                            }
                        });
                        dlg.show();
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
                params.put("carnum", carnum);
                return params;
            }

        };
        queue.add(mStringRequest);

    }


    @Override
    public void onBackPressed() {
        file = new File("/data/data/mustcsie.test/shared_prefs", "LoginInfo.xml");
        if (file.exists()) {
            file.delete();
        }
        Intent intent = new Intent(chooscar.this, LoginActivity.class);
        chooscar.this.startActivity(intent);
        finish();
        return;
    }

public void getserverdata(){
    StringRequest mStringRequest = new StringRequest(Request.Method.POST,
            CARDATA_URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                mListAdapter.clear();
                JSONArray J = new JSONArray(response);
                lenX = J.length();
                list = new String[lenX][];
                for (int x = 0; x < lenX; x++) {
                    JSONArray K = J.getJSONArray(x);
                    lenY = K.length();
                    list[x] = new String[lenY];
                    for (int a = 0; a < lenY; a++) {
                        list[x][a] = K.getString(a);
                    }
                    if (list[x][1].matches("無使用者")) {
                        mListAdapter.addItem(new LtItem(list[x][0]));
                    }
                }
                mListAdapter.notifyDataSetChanged();
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
            return params;
        }

    };
    queue.add(mStringRequest);

}
    class ListAdapter extends BaseAdapter {
        private Context mContext;
        List<LtItem> mListItems = new ArrayList<LtItem>();
        /**
         * ================================================
         */
        public ListAdapter(Context c) {
            mContext = c;
        }

        /**
         * ================================================
         */
        public int getCount() {
            return mListItems.size();

        }

        /**
         * ================================================
         */
        public Object getItem(int position) {
            if ((!mListItems.isEmpty()) && mListItems.size() > position) {
                return mListItems.toArray()[position];
            }
            return null;
        }

        public String getItemText(int position) {
            if ((!mListItems.isEmpty()) && mListItems.size() > position) {
                return ((LtItem) mListItems.toArray()[position]).text1;
            }
            return null;
        }

        /**
         * ================================================
         */
        public long getItemId(int position) {
            return 0;
        }

        /**
         * ================================================
         */

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = (View) convertView;

            if (null == view)
                view = View.inflate(mContext, R.layout.nurse_carchoose_item, null);

            // view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            if ((!mListItems.isEmpty()) && mListItems.size() > position) {
                TextView text1 = (TextView) view.findViewById(R.id.carnum);

                LtItem item = (LtItem) mListItems.toArray()[position];
                text1.setText(item.text1);

            } else {
                view.setVisibility(View.GONE);
            }

            return view;
        }
        /** ================================================ */
        public boolean addItem(LtItem item)
        {
            mListItems.add(item);
            return true;

        }

        /** ================================================ */
        public void clear()
        {
            mListItems.clear();
        }
    }
    /** ============================================================== */
    class LtItem {
        public String text1 = "";
        public LtItem() {
        }
        public LtItem(String text1) {
            this.text1 = text1;
        }
    }
}
