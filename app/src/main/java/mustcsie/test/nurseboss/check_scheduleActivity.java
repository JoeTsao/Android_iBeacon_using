package mustcsie.test.nurseboss;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
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

public class check_scheduleActivity  extends AppCompatActivity implements TabHost.OnTabChangeListener, AdapterView.OnItemClickListener {
    ListView mLVBLEM= null;
    ListView mLVBLEE= null;
    ListView mLVBLEN= null;
    Spinner SP1= null;
    Spinner SP2= null;
    private ArrayAdapter<String> lunchList=null;
    private ArrayAdapter<String> lunchList2=null;
    TextView Time = null;
    String TABID ="已巡房";
    ArrayList<String> Order1=null;
    ArrayList<String> Order2=null;
    String Order="ASC";
    String OrderBy="roomnum";
    ListAdapter mListAdapter = null;
    private static final String GETROOMSTAUTS_REQUEST_URL = "http://xxx.xxx.xxx/getroomstauts.php";
    private static final String PATIENT_REQUEST_URL = "http://xxx.xxx.xxx/getpatientdata.php";
    static RequestQueue queue ;
    Timer timer ;
    TimerTask timerTask;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_schedule);
        queue = Volley.newRequestQueue(check_scheduleActivity.this);
        SP1 = (Spinner)findViewById(R.id.spinner);
        SP2 = (Spinner)findViewById(R.id.spinner2);
        Order1=new ArrayList<String>();
        Order2=new ArrayList<String>();
        mLVBLEM= (ListView)findViewById(R.id.check_list_m);
        mLVBLEN= (ListView)findViewById(R.id.check_list_n);
        mLVBLEE= (ListView)findViewById(R.id.check_list_e);
        Time=(TextView)findViewById(R.id.textView3) ;
        mListAdapter = new ListAdapter(this);
        mLVBLEM.setAdapter(mListAdapter);
        mLVBLEN.setAdapter(mListAdapter);
        mLVBLEE.setAdapter(mListAdapter);
        mLVBLEM.setOnItemClickListener(this);
        mLVBLEE.setOnItemClickListener(this);
        mLVBLEN.setOnItemClickListener(this);
        synchronized(mListAdapter)
        {
            getserverdata();
            mListAdapter.notifyDataSetChanged();
            mLVBLEM.setAdapter(mListAdapter);
        }
        Order1.add("房號");
        Order1.add("完成時間");
        Order2.add(" 由小到大");
        Order2.add(" 由大到小");
        TabHost host=(TabHost)findViewById(R.id.tap1);
        host.setup();
        TabHost.TabSpec spec=host.newTabSpec("已巡房");
        spec.setContent(R.id.已巡房);
        spec.setIndicator("已巡房");
        host.addTab(spec);
        spec=host.newTabSpec("未巡房");
        spec.setContent(R.id.未巡房);
        spec.setIndicator("未巡房");
        host.addTab(spec);
        spec=host.newTabSpec("巡房中");
        spec.setContent(R.id.巡房中);
        spec.setIndicator("巡房中");
        host.addTab(spec);
        host.setCurrentTab(0);
        host.setOnTabChangedListener(this);
        lunchList = new ArrayAdapter<String>(check_scheduleActivity.this,android.R.layout.simple_spinner_item,Order1);
        lunchList2 = new ArrayAdapter<String>(check_scheduleActivity.this,android.R.layout.simple_spinner_item,Order2);
        lunchList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lunchList2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SP1.setAdapter(lunchList);
        SP2.setAdapter(lunchList2);
        SP1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {OrderBy="roomnum";}else{OrderBy="donetime";}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        SP2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {Order="ASC";}else{Order="DESC";}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onTabChanged(String tabId) {
        TABID=tabId;
        switch (TABID){
            case "未巡房" :
                if(Order1.size()>1){
                    Order1.remove(1) ;
                }
                lunchList.notifyDataSetChanged();
                break;
            case "巡房中" :
                if(Order1.size()>1){
                    Order1.remove(1) ;
                }
                lunchList.notifyDataSetChanged();
                break;
            case "已巡房" :
                if(Order1.size()<2){
                    Order1.add("完成時間") ;
                }
                lunchList.notifyDataSetChanged();
                break;
        }
        mListAdapter.clear();
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
                GETROOMSTAUTS_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                // Log.d("RP", response);
                try {

                    JSONArray J = new JSONArray(response);
                    String[] H = response.split("time=");
                    String[] H2 = H[1].split("\"");
                    Time.setText(H2[0]);
                    mListAdapter.clear();
                    final String [] list = new String[J.length()];
                    int len = J.length();
                    for (int i=0;i<len;i++){
                        JSONArray K = J.getJSONArray(i);
                        String chk = K.getString(1);
                        String stauts ="";
                        String settext ="";
                        int Size=30;
                        switch (chk){
                            case "0" :
                                stauts="未巡房";
                                settext=stauts;
                                break;
                            case "1" :
                                stauts="巡房中";
                                settext=stauts;
                                break;
                            case "2" :
                                Size=20;
                                stauts="已巡房";
                                settext="完成時間: \n"+K.getString(2);
                                break;
                        }

                        if(TABID.matches(stauts)){
                            mListAdapter.addItem(new LtItem(K.getString(0), settext,Size));
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
                params.put("Order",Order);
                params.put("OrderBy",OrderBy);
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
                view = View.inflate(mContext, R.layout.item_schedule, null);

            // view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            if ((!mListItems.isEmpty()) && mListItems.size() > position) {
                TextView text1 = (TextView) view.findViewById(R.id.roomnum);
                TextView text2 = (TextView) view.findViewById(R.id.statushow);
                LtItem item = (LtItem) mListItems.toArray()[position];
                text1.setText(item.text1);
                text2.setText(item.text2);
                text2.setTextSize(item.text2Size);

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
    public int text2Size = 40;


    public LtItem() {
    }

    public LtItem(String text1, String text2,int Size) {
        this.text1 = text1;
        this.text2 = text2;
        this.text2Size = Size;

    }
}
}

