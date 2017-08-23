package mustcsie.test.nurseboss;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

public class patching  extends AppCompatActivity implements TabHost.OnTabChangeListener, AdapterView.OnItemClickListener {
    ListView mLVBLEM= null;

    TextView Time = null;
    String TABID ="已派遣";
    ListAdapter mListAdapter = null;
    private static final String GETCARDATA_REQUEST_URL = "http://xxx.xxx.xxx/cardata.php";
    ListView mLVBLEN= null;
    static RequestQueue queue ;
    Timer timer ;
    TimerTask timerTask;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.nurseboss_patching);
        mLVBLEM= (ListView)findViewById(R.id.patching_list_yes);
        mLVBLEN= (ListView)findViewById(R.id.patching_list_no);
        queue = Volley.newRequestQueue(patching.this);
        mListAdapter = new ListAdapter(this);
        mLVBLEM.setAdapter(mListAdapter);
        mLVBLEN.setAdapter(mListAdapter);
        mLVBLEM.setOnItemClickListener(this);
        mLVBLEN.setOnItemClickListener(this);
        synchronized(mListAdapter)
        {
            getserverdata();
            mListAdapter.notifyDataSetChanged();
            mLVBLEM.setAdapter(mListAdapter);
        }
        TabHost host=(TabHost)findViewById(R.id.tap1);
        host.setup();
        TabHost.TabSpec spec=host.newTabSpec("已派遣");
        spec.setContent(R.id.已派遣);
        spec.setIndicator("已派遣");
        host.addTab(spec);
        spec=host.newTabSpec("未派遣");
        spec.setContent(R.id.未派遣);
        spec.setIndicator("未派遣");
        host.addTab(spec);
        host.setCurrentTab(0);
        host.setOnTabChangedListener(this);
    }

    @Override
    public void onTabChanged(String tabId) {
        TABID=tabId;
        mListAdapter.clear();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final String SSS = mListAdapter.getItemText(position);
        if (TABID.matches("已派遣")) {
            Intent intent = new Intent(patching.this, sendpatient_txt.class);
            intent.putExtra("num", SSS);
            patching.this.startActivity(intent);
        }
        else{
            Intent intent = new Intent(patching.this, sendpatient_txt.class);
            intent.putExtra("num", SSS);
            patching.this.startActivity(intent);

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
                GETCARDATA_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                // Log.d("RP", response);
                try {

                    JSONArray J = new JSONArray(response);
                    mListAdapter.clear();
                    final String [] list = new String[J.length()];
                    int len = J.length();
                    for (int i=0;i<len;i++){
                        JSONArray K = J.getJSONArray(i);
                        String chk = K.getString(2);
                        String settext ="";
                        switch (chk){
                            case "0" :
                                settext="未派遣";
                                break;
                            default:
                                settext="已派遣";
                                break;
                        }
                        String name = "";
                        if(K.getString(1).matches("")){
                            name = "無使用者";
                        }else{
                            name = K.getString(1);
                        }
                        if(TABID.matches(settext)){
                            mListAdapter.addItem(new LtItem(K.getString(0),settext,name));
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
                view = View.inflate(mContext, R.layout.item_patching, null);

            // view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            if ((!mListItems.isEmpty()) && mListItems.size() > position) {
                TextView text1 = (TextView) view.findViewById(R.id.carnum);
                TextView text2 = (TextView) view.findViewById(R.id.statushow);
                TextView text3 = (TextView) view.findViewById(R.id.showid);
                LtItem item = (LtItem) mListItems.toArray()[position];
                text1.setText(item.text1);
                text2.setText(item.text2);
                text3.setText(item.text3);
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


        public LtItem() {
        }

        public LtItem(String text1, String text2, String text3) {
            this.text1 = text1;
            this.text2 = text2;
            this.text3 = text3;

        }
    }
}

