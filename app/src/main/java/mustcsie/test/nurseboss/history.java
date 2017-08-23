package mustcsie.test.nurseboss;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mustcsie.test.R;

/**
 * Created by Administrator on 2016/11/29.
 */

public class history extends AppCompatActivity {
    ListView mLVBLE= null;
    ListAdapter mListAdapter = null;
    Button BT1;
    Boolean RGselect=true;
    String name="false";
    String Donetime="false";
    String Timerange="false";
    String RoomNo="false";
    String Order="ASC";
    String OrderBy="roomnum";
    public static File file;
    public SharedPreferences setting;
    private static final String PATIENTBOSS_REQUEST_URL = "http://xxx.xxx.xxx/roomhistory.php";
    static RequestQueue queue ;
    /** ================================================ */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_room);
        queue = Volley.newRequestQueue(history.this);
        file = new File("/data/data/mustcsie.test/shared_prefs", "historyconditions.xml");
        if(file.exists()){
            ReadValue();
        }
        Intent intent = getIntent();
        RGselect = intent.getBooleanExtra("RGselect",true);
        mListAdapter = new ListAdapter(this);
        mLVBLE=(ListView)findViewById(R.id.list_his);
        mLVBLE.setAdapter(mListAdapter);
        getserverdata();
        BT1=(Button)findViewById(R.id.conditionssetBT);
        BT1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(history.this, historyconditions.class);
                history.this.startActivity(intent);
            }
        });
    }
    /** ================================================ */
    public void getserverdata() {
        final StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                PATIENTBOSS_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray J = new JSONArray(response);
                    int len = J.length();
                    mListAdapter.clear();
                    for (int x =0;x<len ;x++){
                        JSONArray K =  J.getJSONArray(x);
                        mListAdapter.addItem(new LtItem(K.getString(0), K.getString(1), K.getString(3), K.getString(2)));
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
                params.put("RGselect",RGselect.toString());
                params.put("name",name);
                params.put("Donetime",Donetime);
                params.put("Timerange",Timerange);
                params.put("RoomNo",RoomNo);
                params.put("Order",Order);
                params.put("OrderBy",OrderBy);

                return params;
            }

        };
        queue.add(mStringRequest);
    }
    @Override
    protected void onResume(){
        super.onResume();
        queue.getCache().clear();
        mListAdapter.clear();
        if(file.exists()){
            ReadValue();
        }
        getserverdata();
    }
    /** ================================================ */
class ListAdapter extends BaseAdapter {
    private Context mContext;
    List<LtItem> mListItems = new ArrayList<>();
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
            view = View.inflate(mContext, R.layout.item_historyroom, null);

        // view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        if ((!mListItems.isEmpty()) && mListItems.size() > position) {
            TextView text1 = (TextView) view.findViewById(R.id.show_roomnum);
            TextView text2 = (TextView) view.findViewById(R.id.show_date);
            TextView text3 = (TextView) view.findViewById(R.id.show_name);
            TextView text4 = (TextView) view.findViewById(R.id.show_range);


            LtItem item = (LtItem) mListItems.toArray()[position];
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
    /** ================================================ */
    public void ReadValue() {
        setting = getSharedPreferences("historyconditions", 0);
        RGselect=setting.getBoolean("RGselect",false);
        name = setting.getString("name", "");
        Donetime=setting.getString("Donetime", "");
        RoomNo = setting.getString("RoomNo", "");
        Timerange=setting.getString("Timerange","");
        Order = setting.getString("Order", "");
        OrderBy = setting.getString("OrderBy", "");

    }
}
