package mustcsie.test.nurse;

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
import android.widget.Button;
import android.widget.CheckBox;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mustcsie.test.R;

/**
 * Created by Administrator on 2016/11/13.
 */

public class check_patching extends AppCompatActivity {
    private Button BT1;
    private Button BT2;
    private Button BT3;
    private static String carnum;
    ListAdapter mListAdapter = null;
    private static final String SENDCARDATA_REQUEST_URL = "http://xxx.xxx.xxx/cardataIO.php";
    ListView mLVBLE= null;
    static RequestQueue queue ;
    private static List<Boolean> mChecked = new ArrayList<Boolean>();
    private static List<String> jobsave = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nurse_patching_ui);
        Intent intent = getIntent();
        if(mChecked.size()!=0){
            mChecked.clear();
        }
        carnum = intent.getStringExtra("carnum");
        mLVBLE= (ListView)findViewById(R.id.patchinglist);
        queue = Volley.newRequestQueue(check_patching.this);
        mListAdapter = new ListAdapter(this);
        mLVBLE.setAdapter(mListAdapter);
        BT1= (Button) findViewById(R.id.send_databt);
        BT2= (Button) findViewById(R.id.back_bt);
        BT3= (Button) findViewById(R.id.chk_default_work_bt);
        BT1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendtoserver();
                finish();
            }
        });

        BT2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        BT3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(check_patching.this, carjob.class);
                intent.putExtra("carnum",carnum);
                check_patching.this.startActivity(intent);
                finish();
            }
        });



        mLVBLE.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AlertDialog dlg2 = new AlertDialog.Builder(check_patching.this).create();
                if(mListAdapter.getItem(position).Jobdone){
                    dlg2.setTitle("(已完成的工作)");
                    dlg2.setMessage(jobsave.get(position));
                    dlg2.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dlg2.dismiss();
                        }
                    });
                    dlg2.show();
                }
            }
        });
        StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                SENDCARDATA_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray J =new JSONArray(response);

                    for(int u=0;u<J.length();u++){
                        JSONArray L =J.getJSONArray(u);
                        boolean temp = L.getString(1).matches("1") ? true:false;
                        mListAdapter.addItem(new LtItem( u+1,temp?"(已完成工作)":L.getString(0),temp));
                        mListAdapter.notifyDataSetChanged();
                        jobsave.add(u,L.getString(0));
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
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("carnum",carnum);
                params.put("select","get");

                return params;
            }
        };

        queue.add(mStringRequest);

    }

    public void getserverdata()
    {

    StringRequest mStringRequest = new StringRequest(Request.Method.POST,
            SENDCARDATA_REQUEST_URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            // response
            // Log.d("RP", response);
            try {

                JSONArray J = new JSONArray(response);
                mListAdapter.clear();

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


    public void sendtoserver()
    {
        StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                SENDCARDATA_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                // Log.d("RP", response);
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
                params.put("carnum", carnum);
                params.put("select", "upload");
                for (int d = 0 ; d<mChecked.size();d++ ) {
                    String text = mChecked.get(d)?"1":"0";
                    params.put("jobchk[" + d + "]", text);
                    params.put("job["+d+"]",jobsave.get(d));
                }
                return params;
            }

        };
        queue.add(mStringRequest);
    }


    class ListAdapter extends BaseAdapter {
        private Context mContext;
        List<LtItem> mListItems = new ArrayList<LtItem>();

        HashMap<Integer,View> map = new HashMap<Integer,View>();

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
        public LtItem getItem(int position) {
            if ((!mListItems.isEmpty()) && mListItems.size() > position) {
                return mListItems.get(position);
            }
            return null;
        }
        public String[] getallItemtext() {
            String [] A=null;
            if ((!mListItems.isEmpty())) {
                A=new String[mListItems.size()];
                for(int i =0 ; i<mListItems.size();i++){
                    A[i]=mListItems.get(i).JobText;
                }
                return A;
            }
            return null;
        }
        public String getItemText(int position) {
            if ((!mListItems.isEmpty()) && mListItems.size() > position) {
                return ((LtItem) mListItems.toArray()[position]).JobText;
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
            ViewHolder holder = null;

            if (null == view)
                view = View.inflate(mContext, R.layout.nurse_patching_ui_item, null);
                holder = new ViewHolder();


            // view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            if ((!mListItems.isEmpty()) && mListItems.size() > position) {
                TextView TV1= (TextView) view.findViewById(R.id.JobNumTV);
                TextView TV2= (TextView) view.findViewById(R.id.JobTextTV);
                holder.selected = (CheckBox) view.findViewById(R.id.JobCheck);

                LtItem item = (LtItem) mListItems.toArray()[position];
                TV1.setText(item.JobNum);
                TV2.setText(item.JobText);
                holder.selected.setEnabled(item.JobdoneBT);

                final int p = position;
                map.put(position, view);
                holder.selected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox)v;
                        mChecked.set(p, cb.isChecked());
                    }
                });
                holder.selected.setChecked(mChecked.get(position));
                view.setTag(holder);
            } else {
                view.setVisibility(View.GONE);
            }

            return view;

        }
        class ViewHolder{
            CheckBox selected;
        }
        /** ================================================ */
        public boolean addItem(LtItem item)
        {
            mListItems.add(item);
            return true;

        }

        public boolean removeItem(LtItem item){
            mListItems.remove(item);
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
        public String JobNum = "";
        public String JobText = "";
        public int Color1=0x00000000;
        public boolean Jobdone = false;
        public boolean JobdoneBT = true;
        public LtItem() {
        }

        public LtItem(int JobNum ,String JobText,boolean Jobdone ) {
            this.JobNum = JobNum+". ";
            this.JobText = JobText;
            this.Color1 = 0x00000000;
            this.Jobdone = Jobdone;
            this.JobdoneBT = !Jobdone;
            mChecked.add(Jobdone);
        }
    }



}
