package mustcsie.test.nurse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
 * Created by Administrator on 2016/10/25.
 */

public class carjob  extends AppCompatActivity implements  AdapterView.OnItemClickListener {
    ListView WorkList1= null;
    private static String carnum;
    private Button BT1;
    private Button BT2;
    private TextView TV1;

    ListAdapter mListAdapter = null;
    private static final String defaultworkget_REQUEST_URL = "http://xxx.xxx.xxx/defaultworkget.php";
    static RequestQueue queue ;
    Timer timer ;
    TimerTask timerTask;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.car);
        WorkList1= (ListView)findViewById(R.id.work_list_1);
        queue = Volley.newRequestQueue(carjob.this);
        Intent intent = getIntent();
        carnum = intent.getStringExtra("carnum");
        mListAdapter = new ListAdapter(this);
        WorkList1.setAdapter(mListAdapter);
        WorkList1.setOnItemClickListener(this);
        BT1= (Button) findViewById(R.id.car_change_bt);
        BT2= (Button) findViewById(R.id.car_back_bt);
        TV1=(TextView)findViewById(R.id.TV15);
        BT1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(carjob.this, check_patching.class);
                intent.putExtra("carnum",carnum);
                carjob.this.startActivity(intent);
                finish();
            }
        });
        BT2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        synchronized(mListAdapter)
        {
            getserverdata();
            mListAdapter.notifyDataSetChanged();
        }

    }
    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

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

    public void getserverdata()
    {


        StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                defaultworkget_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                // Log.d("RP", response);
                try {

                    JSONArray J = new JSONArray(response);
                    mListAdapter.clear();
                    String[] H = response.split("time=");
                    String[] H2 = H[1].split("\"");
                    TV1.setText("目前時段:"+H2[0]);
                    int len = J.length();
                    for (int i=0;i<len;i++){
                        JSONArray K = J.getJSONArray(i);
                        String chk = K.getString(1);
                        String settext ="";
                        switch (chk){
                            case "0" :
                                settext="未巡房";
                                break;
                            case "1" :
                                settext="當前目標";
                                break;
                            case "2" :
                                settext="下一間";
                                break;
                            default:
                                settext="已巡房";
                                break;
                        }

                            mListAdapter.addItem(new LtItem(i+1+".",K.getString(0),settext,Integer.valueOf(chk)));


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
                params.put("carnum", carnum);
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
                view = View.inflate(mContext, R.layout.nurse_car_item, null);

            // view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            if ((!mListItems.isEmpty()) && mListItems.size() > position) {
                TextView text1 = (TextView) view.findViewById(R.id.JobNumTV2);
                TextView text2 = (TextView) view.findViewById(R.id.JobTextTV2);
                TextView text3 = (TextView) view.findViewById(R.id.textView9);
                LinearLayout LLT1=(LinearLayout)view.findViewById(R.id.LLT1);

                LtItem item = (LtItem) mListItems.toArray()[position];
                text1.setText(item.text1);
                text2.setText(item.text2);
                text3.setText(item.text3);

                switch (item.select){
                    case 0:
                        item.Color1=0x0000000;
                        text1.setEnabled(false);
                        text2.setEnabled(false);
                        text3.setEnabled(false);
                    break;
                    case 1:
                        item.Color1=0xFF888888;
                        text1.setEnabled(true);
                        text2.setEnabled(true);
                        text3.setEnabled(true);

                    break;
                    case 2:
                        item.Color1=0x0000000;
                        text1.setEnabled(true);
                        text2.setEnabled(true);
                        text3.setEnabled(true);

                    break;
                    case 3:
                        item.Color1=0x0000000;
                        text1.setEnabled(false);
                        text2.setEnabled(false);
                        text3.setEnabled(false);

                        break;

                }
                LLT1.setBackgroundColor(item.Color1);
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
        public int Color1=0x0000000;
        public int select = 0;

        public LtItem() {
        }

        public LtItem(String text1, String text2, String text3,int select) {
            this.text1 = text1;
            this.text2 = text2;
            this.text3 = text3;
            this.select=select;

        }
    }
}

