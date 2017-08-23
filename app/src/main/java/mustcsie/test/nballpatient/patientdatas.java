package mustcsie.test.nballpatient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mustcsie.test.R;
import mustcsie.test.nurseboss.choosepatient;

/**
 * Created by Administrator on 2016/10/29.
 */

public class patientdatas   extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView mLVBLE= null;
    ListAdapter mListAdapter = null;
    private static final String PATIENT_REQUEST_URL = "http://xxx.xxx.xxx/getpatientdata.php";
    String list = "";

    static RequestQueue queue ;
    Timer timer ;
    TimerTask timerTask;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patientdata_item);
        queue = Volley.newRequestQueue(patientdatas.this);
        Intent intent = getIntent();
        list = intent.getStringExtra("list2");

        mLVBLE=(ListView)findViewById(R.id.list_ptdata);
        mListAdapter = new ListAdapter(this);
        mLVBLE.setAdapter(mListAdapter);
        mLVBLE.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                PATIENT_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                // response
                // Log.d("RP", response);
                try {
                    JSONArray J = new JSONArray(response);
                    final String [] list = new String[J.length()];
                    int len = J.length();
                    for (int i=0;i<len;i++){
                        list[i] = J.getString(i).substring(J.getString(i).indexOf("\"")+1,J.getString(i).lastIndexOf("\""));
                    }
                    AlertDialog.Builder dlg=new AlertDialog.Builder(patientdatas.this);
                    dlg.setTitle("選擇病人");
                    dlg.setItems(list, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, final int which) {
                            Intent intent = new Intent(patientdatas.this, choosepatient.class);
                            intent.putExtra("list", list[which]);
                            patientdatas.this.startActivity(intent);
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

                params = new HashMap<>();
                params.put("index", "patient");
                params.put("room",  mListAdapter.getItemText(position));
                params.put("select","1");

                return params;
            }

        };
        queue.add(mStringRequest);
    }
    protected void onPause(){
        super.onPause();
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

    public void getserverdata() {

        StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                PATIENT_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                // Log.d("RP", response);
                try {
                    JSONArray J = new JSONArray(response);

                    final String [] list = new String[J.length()];
                    int len = J.length();
                    for (int i=0;i<len;i++){
                        list[i] = J.getString(i).substring(J.getString(i).indexOf("\"")+1,J.getString(i).lastIndexOf("\""));
                    }
                    mListAdapter.clear();
                    for (int x =len-1;x>=0 ;x--){
                            mListAdapter.addItem(new LtItem(list[x]));
                    }

                    mListAdapter.notifyDataSetChanged();
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

                params.put("select","2");
                return params;
            }

        };
        queue.add(mStringRequest);
    }
    class ListAdapter extends BaseAdapter {
        private Context mContext;
        List<patientdatas.LtItem> mListItems = new ArrayList<patientdatas.LtItem>();
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
                return ((patientdatas.LtItem) mListItems.toArray()[position]).text1;
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
                view = View.inflate(mContext, R.layout.patientdata, null);

            // view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            if ((!mListItems.isEmpty()) && mListItems.size() > position) {
                TextView text1 = (TextView) view.findViewById(R.id.roomnum);
                patientdatas.LtItem item = (patientdatas.LtItem) mListItems.toArray()[position];
                text1.setText(item.text1);
            } else {
                view.setVisibility(View.GONE);
            }

            return view;
        }
        /** ================================================ */
        public boolean addItem(patientdatas.LtItem item)
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

        public LtItem(String text1 ) {
            this.text1 = text1;

        }
    }
}
