package mustcsie.test.nurseboss;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Administrator on 2016/10/29.
 */

public class lookphy   extends AppCompatActivity {
    ListView mLVBLE= null;
    ListAdapter mListAdapter = null;
    private static final String PATIENTBOSS_REQUEST_URL = "http://xxx.xxx.xxx/getpatientdataboss.php";
    String list = "";
    String TABID = "";
    static RequestQueue queue ;
    Timer timer ;
    TimerTask timerTask;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nurseboss_item_lookphysiological);
        queue = Volley.newRequestQueue(lookphy.this);
        Intent intent = getIntent();
        list = intent.getStringExtra("list2");
        TABID = intent.getStringExtra("TABID");
        mLVBLE=(ListView)findViewById(R.id.list_phy);
        mListAdapter = new ListAdapter(this);
        mLVBLE.setAdapter(mListAdapter);
        TextView title = (TextView) findViewById(R.id.title);
        switch (TABID){
            case "早" :
                title.setText("姓名: "+list+"  早上生理資訊");
                break;
            case "中":
                title.setText("姓名: "+list+"  中午生理資訊");
                break;
            case "晚" :
                title.setText("姓名: "+list+"  晚上生理資訊");
                break;
        }

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
        final StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                PATIENTBOSS_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                // Log.d("RP", response);
                try {
                    JSONArray J = new JSONArray(response);

                    final String [] list = new String[J.length()];

                    int len = J.length();
                    mListAdapter.clear();
                    for (int x =0;x<len ;x++){
                        JSONArray K =  J.getJSONArray(x);

                        if(!K.getString(0).matches("0")  && !K.getString(1).matches("0") && !K.getString(2).matches("0") ) {
                            mListAdapter.addItem(new LtItem(K.getString(3), K.getString(0), K.getString(1), K.getString(2)));
                        }
                    }
                    boolean SD =mListAdapter.mListItems.isEmpty();
                    if(SD) {
                        final AlertDialog dlg2 = new AlertDialog.Builder(lookphy.this).create();

                        dlg2.setTitle("錯誤");
                        dlg2.setMessage("查無生理資訊");

                        dlg2.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                dlg2.dismiss();
                                finish();
                            }
                        });
                        timer.cancel();
                        dlg2.show();
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
                params.put("select", TABID);
                params.put("name",  list);
                return params;
            }

        };
        queue.add(mStringRequest);
    }
    class ListAdapter extends BaseAdapter {
        private Context mContext;
        List<lookphy.LtItem> mListItems = new ArrayList<lookphy.LtItem>();
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
                return ((lookphy.LtItem) mListItems.toArray()[position]).text1;
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
                view = View.inflate(mContext, R.layout.nurseboss_lookphysiological, null);

            // view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            if ((!mListItems.isEmpty()) && mListItems.size() > position) {
                TextView text1 = (TextView) view.findViewById(R.id.date_show);
                TextView text2 = (TextView) view.findViewById(R.id.tp_show);
                TextView text3 = (TextView) view.findViewById(R.id.bp_show);
                TextView text4 = (TextView) view.findViewById(R.id.spo2_show);

                lookphy.LtItem item = (lookphy.LtItem) mListItems.toArray()[position];
                text1.setText(item.text1);
                text2.setText(item.text2);
                text3.setText(item.text3);
                text4.setText(item.text4);
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
        public String text2 = "";
        public String text3 = "";
        public String text4 = "";

        public LtItem() {
        }

        public LtItem(String text1, String text2, String text3, String text4) {
            this.text1 = text1;
            this.text2 = text2;
            this.text3 = text3;
            this.text4 = text4;
        }
    }
}
