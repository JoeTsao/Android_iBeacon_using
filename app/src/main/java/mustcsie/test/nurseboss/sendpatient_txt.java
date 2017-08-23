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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mustcsie.test.R;

/**
 * Created by Administrator on 2016/11/6.
 */

public class sendpatient_txt extends AppCompatActivity implements View.OnClickListener {
    private static String num = "";
    static RequestQueue queue ;
    Button Send = null;
    Button Add = null;
    Button back = null;
    Button selectall = null;
    TextView choosecon=null;
    int selectcount=0;
    String name;
    Button Delete = null;
    private static String[] AAA={};
    private static List<Boolean> mChecked = new ArrayList<Boolean>();
    ListView JobView = null;
    ListAdapter mListAdapter = null;
    EditText JobEdit =null ;
    private static final String GETCARDATA_REQUEST_URL = "http://xxx.xxx.xxx/cardata.php";
    private static final String SENDCARDATA_REQUEST_URL = "http://xxx.xxx.xxx/cardataIO.php";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_patchingtxt);
        queue = Volley.newRequestQueue(sendpatient_txt.this);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        Send=(Button)findViewById(R.id.send_button);
        Add=(Button)findViewById(R.id.add_button);
        back=(Button)findViewById(R.id.back);
        selectall=(Button)findViewById(R.id.selectall);
        choosecon=(TextView) findViewById(R.id.choosecon);
        Delete=(Button)findViewById(R.id.delete_button);
        JobView=(ListView) findViewById(R.id.jobview);
        JobEdit = (EditText) findViewById(R.id.jobedit);
        mListAdapter = new ListAdapter(this);
        JobView.setAdapter(mListAdapter);
        Send.setOnClickListener(this);
        mChecked.clear();
        num = intent.getStringExtra("num");
        JobEdit.setText("");
        JobView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LtItem item = mListAdapter.getItem(position);
                int Size = mListAdapter.mListItems.size();
                item.select=!item.select;
                mChecked.set(position,item.select);
                if(item.select){
                    selectcount++;
                    choosecon.setText("已選擇 "+selectcount+" 個項目");
                    item.Color1=(0xFF888888);
                }
                else
                {
                    selectcount--;
                    if(selectcount>0){
                        choosecon.setText("已選擇 "+selectcount+" 個項目");
                    }
                    else{
                        choosecon.setText("");
                    }
                    item.Color1=(0x00000000);
                }
                mListAdapter.notifyDataSetChanged();
                if(selectcount==Size){
                    selectall.setText("取消全選");
                }else{
                    selectall.setText("全選");
                }

            }

        });


        back.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        selectall.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Size = mListAdapter.mListItems.size();
                if(Size>0){
                if(selectcount!=Size){
                    for(int i=0;i<Size;i++) {
                        LtItem item = mListAdapter.getItem(i);

                        if (!item.select) {
                            item.select = !item.select;
                            mChecked.set(i,item.select);
                            selectcount++;
                            choosecon.setText("已選擇 " + selectcount + " 個項目");
                            item.Color1 = (0xFF888888);
                        }
                        mListAdapter.notifyDataSetChanged();
                    }
                }else{
                    for(int i=0;i<Size;i++) {
                        LtItem item = mListAdapter.getItem(i);
                        if (item.select) {
                            item.select = !item.select;
                            mChecked.set(i,item.select);
                            selectcount--;
                            if(selectcount>0){
                                choosecon.setText("已選擇 "+selectcount+" 個項目");
                            }
                            else{
                                choosecon.setText("");
                            }
                            item.Color1 = (0x00000000);
                        }
                        mListAdapter.notifyDataSetChanged();
                    }
                    }

                    if(selectcount==Size){
                        selectall.setText("取消全選");
                    }else{
                        selectall.setText("全選");
                    }
                }
            }
        });


        Delete.setOnClickListener (new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                LtItem item =null;
                int Size = mListAdapter.mListItems.size();
                for(int i=0;i<Size;i++){
                    item=mListAdapter.getItem(i);
                    if( item!=null){
                        if(item.select ){
                            mListAdapter.removeItem(item);
                            mChecked.remove(i);
                            mListAdapter.notifyDataSetChanged();
                            Size=Size-1;
                            i=-1;
                        }
                        item.JobNum= (i+1)+". ";
                    }
                }
                int Size2 = mListAdapter.mListItems.size();
                for(int i=0;i<Size2;i++){
                    item=mListAdapter.getItem(i);
                    item.JobNum= (i+1)+". ";
                    mListAdapter.notifyDataSetChanged();
                }
                selectcount=0;
                choosecon.setText("");
                selectall.setText("全選");

            }
        });
        Add.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!JobEdit.getText().toString().matches("")){
                mListAdapter.addItem(new LtItem(mListAdapter.mListItems.size()+1,JobEdit.getText().toString()));
                JobEdit.setText("");
                mListAdapter.notifyDataSetChanged();
                }
            }
        });


        StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                SENDCARDATA_REQUEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray J = new JSONArray(response);
                    for(int u=0;u<J.length();u++){
                        JSONArray K = J.getJSONArray(u);
                        mListAdapter.addItem(new LtItem(u+1,K.getString(0)));
                        mListAdapter.notifyDataSetChanged();
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
                int C=0;
                Map<String, String> params = new HashMap<String, String>();
                params.put("carnum",num);
                params.put("select","get");

                return params;
            }
        };

        queue.add(mStringRequest);
    }

    @Override
    public void onClick(View v) {
        AAA=mListAdapter.getallItemtext();

            StringRequest mStringRequest = new StringRequest(Request.Method.POST,
                    SENDCARDATA_REQUEST_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray J = new JSONArray(response);
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
                    params.put("carnum", num);
                    params.put("select", "delete");

                    return params;
                }
            };

            queue.add(mStringRequest);
        if(AAA!=null) {
            mStringRequest = new StringRequest(Request.Method.POST,
                    SENDCARDATA_REQUEST_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject J = new JSONObject(response);
                        String O = J.getString("con");
                        Toast.makeText(sendpatient_txt.this, "已更新 " + O + " 筆資料", Toast.LENGTH_SHORT).show();
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
                    int C = 0;
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("carnum", num);
                    params.put("select", "add");
                    for (String text : AAA) {
                        params.put("job[" + (C++) + "]", text);
                    }

                    return params;
                }
            };

            queue.add(mStringRequest);
        }else{
            Toast.makeText(sendpatient_txt.this, "已刪除所有資料", Toast.LENGTH_SHORT).show();
        }

        finish();
    }




    class ListAdapter extends BaseAdapter {
        private Context mContext;
        List<LtItem> mListItems = new ArrayList<LtItem>();
        HashMap<Integer,View> map1 = new HashMap<Integer,View>();

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
            ViewHolder holder1 = null;
            if (null == view)
                view = View.inflate(mContext, R.layout.send_patch_item, null);
            holder1 = new ViewHolder();

            // view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            if ((!mListItems.isEmpty()) && mListItems.size() > position) {
                TextView TV1= (TextView) view.findViewById(R.id.NBJobNumTV);
                TextView TV2= (TextView) view.findViewById(R.id.NBJobTextTV);
                holder1.selected = (CheckBox) view.findViewById(R.id.NBJobCheck);

                LtItem item = (LtItem) mListItems.toArray()[position];
                TV1.setText(item.JobNum);
                TV2.setText(item.JobText);
                TV2.setBackgroundColor(item.Color1);


                final int p = position;
                map1.put(position, view);
                holder1.selected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int Size = mListAdapter.mListItems.size();
                        CheckBox cb = (CheckBox)v;
                        mChecked.set(p, cb.isChecked());
                        LtItem item = mListAdapter.getItem(p);

                        item.select=!item.select;
                        mChecked.set(p,item.select);
                        if(item.select){
                            selectcount++;
                            choosecon.setText("已選擇 "+selectcount+" 個項目");
                            item.Color1=(0xFF888888);
                        }
                        else
                        {
                            selectcount--;
                            if(selectcount>0){
                                choosecon.setText("已選擇 "+selectcount+" 個項目");
                            }
                            else{
                                choosecon.setText("");
                            }
                            item.Color1=(0x00000000);
                        }
                        mListAdapter.notifyDataSetChanged();
                        if(selectcount==Size){
                            selectall.setText("取消全選");
                        }else{
                            selectall.setText("全選");
                        }


                    }
                });

                holder1.selected.setChecked(mChecked.get(position));
                view.setTag(holder1);

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
        public boolean select = false;
        public LtItem() {
        }

        public LtItem(int JobNum,String JobText ) {
            this.JobNum = JobNum+". ";
            this.JobText = JobText;
            this.Color1 = 0x00000000;
            this.select = false;
            mChecked.add(false);
        }
        public LtItem(int JobNum,String JobText,int Color,boolean select ) {
            this.JobNum = JobNum+". ";
            this.JobText = JobText;
            this.Color1 = Color;
            this.select = select;
            mChecked.add(select);
        }
    }

}

