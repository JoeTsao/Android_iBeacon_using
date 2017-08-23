package mustcsie.test.nurseboss;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;

import mustcsie.test.R;

/**
 * Created by Administrator on 2016/11/30.
 */

public class historyconditions  extends AppCompatActivity{
    RadioGroup conditionsRG;
    CheckBox conditionsNameCB;
    EditText conditionsNameET;
    CheckBox conditionsDonetimeCB;
    EditText conditionsDonetimeDET;
    EditText conditionsDonetimeMET;
    EditText conditionsDonetimeYET;
    CheckBox conditionsTimerangeCB;
    Spinner  conditionsTimerangeSP;
    CheckBox conditionsRoomNoCB;
    EditText conditionsRoomNoET;
    Button conditionsSerchBT;
    String name="";
    String Donetime="";
    String DonetimeD="";
    String DonetimeM="";
    String DonetimeY="";
    String Timerange="";
    String RoomNo="";
    Spinner SP1= null;
    Spinner SP2= null;
    private ArrayAdapter<String> lunchList2=null;
    private ArrayAdapter<String> lunchList3=null;
    public static File file;
    public SharedPreferences setting;
    boolean RGselect;
    boolean NameCBCHK;
    boolean DonetimeCBCHK;
    boolean TimerangeCBCHK;
    boolean RoomNoCBCHK;
    int RGSLid;
    int SPselect;
    int SP2select;
    int SP3select;
    ArrayList<String> timerangeLT=null;
    ArrayList<String> Order1=null;
    ArrayList<String> Order2=null;
    String Order="ASC";
    String OrderBy="roomnum";
    private ArrayAdapter<String> lunchList=null;
 /** ================================================ */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nurseboss_history_set_ui);
        Intent intent = getIntent();
        timerangeLT=new ArrayList<String>();
        SP1 = (Spinner)findViewById(R.id.conditions_spinner);
        SP2 = (Spinner)findViewById(R.id.conditions_spinner2);
        Order1=new ArrayList<String>();
        Order2=new ArrayList<String>();
        Order1.add("房號");
        Order1.add("完成日期");
        Order1.add("姓名");
        Order2.add(" 由小到大");
        Order2.add(" 由大到小");
        timerangeLT.add("早上");
        timerangeLT.add("中午");
        timerangeLT.add("晚上");
        conditionsRG = (RadioGroup)findViewById(R.id.conditions_RG);
        conditionsNameCB = (CheckBox)findViewById(R.id.conditions_nameCB);
        conditionsNameET = (EditText)findViewById(R.id.conditions_nameET);
        conditionsDonetimeCB = (CheckBox)findViewById(R.id.conditions_donetimeCB);
        conditionsDonetimeDET = (EditText)findViewById(R.id.conditions_donetimeDET);
        conditionsDonetimeMET = (EditText)findViewById(R.id.conditions_donetimeMET);
        conditionsDonetimeYET = (EditText)findViewById(R.id.conditions_donetimeYET);
        conditionsTimerangeCB = (CheckBox)findViewById(R.id.conditions_timerangeCB);
        conditionsTimerangeSP = (Spinner) findViewById(R.id.conditions_timerangeSP);
        conditionsRoomNoCB = (CheckBox)findViewById(R.id.conditions_roomNoCB);
        conditionsRoomNoET = (EditText)findViewById(R.id.conditions_roomNoET);
        conditionsSerchBT = (Button)findViewById(R.id.conditions_serchBT);
        lunchList = new ArrayAdapter<String>(historyconditions.this,android.R.layout.simple_spinner_item,timerangeLT);
        lunchList2 = new ArrayAdapter<String>(historyconditions.this,android.R.layout.simple_spinner_item,Order1);
        lunchList3 = new ArrayAdapter<String>(historyconditions.this,android.R.layout.simple_spinner_item,Order2);
        lunchList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lunchList2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lunchList3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SP1.setAdapter(lunchList2);
        SP2.setAdapter(lunchList3);
        conditionsTimerangeSP.setAdapter(lunchList);
        RGselect=true;
        conditionsNameCB.setEnabled(false);
        conditionsNameET.setEnabled(false);
        conditionsDonetimeCB.setEnabled(false);
        conditionsDonetimeDET.setEnabled(false);
        conditionsDonetimeYET.setEnabled(false);
        conditionsDonetimeMET.setEnabled(false);
        conditionsTimerangeCB.setEnabled(false);
        conditionsTimerangeSP.setEnabled(false);
        conditionsRoomNoCB.setEnabled(false);
        conditionsRoomNoET.setEnabled(false);
        file = new File("/data/data/mustcsie.test/shared_prefs", "historyconditions.xml");
        if(file.exists()){
            ReadValue();
            conditionsTimerangeCB.setChecked(TimerangeCBCHK);
            conditionsDonetimeCB.setChecked(DonetimeCBCHK);
            conditionsNameCB.setChecked(NameCBCHK);
            conditionsRoomNoCB.setChecked( RoomNoCBCHK);
            conditionsDonetimeDET.setText(DonetimeD);
            conditionsDonetimeMET.setText(DonetimeM);
            conditionsDonetimeYET.setText(DonetimeY);
            conditionsNameET.setText(name);
            conditionsRoomNoET.setText(RoomNo);
            conditionsTimerangeSP.setSelection(SPselect);
            SP1.setSelection(SP2select);
            SP2.setSelection(SP3select);
            conditionsRG.check(RGSLid);
            conditionsDonetimeDET.setEnabled(DonetimeCBCHK);
            conditionsDonetimeMET.setEnabled(DonetimeCBCHK);
            conditionsDonetimeYET.setEnabled(DonetimeCBCHK);
            conditionsNameET.setEnabled(NameCBCHK);
            conditionsRoomNoET.setEnabled(RoomNoCBCHK);
            conditionsTimerangeSP.setEnabled(TimerangeCBCHK);
        }
/** ================================================ */
        switch (conditionsRG.getCheckedRadioButtonId()){
            case R.id.conditions_allRB :
                RGselect=true;
                conditionsTimerangeCB.setChecked(false);
                conditionsDonetimeCB.setChecked(false);
                conditionsNameCB.setChecked(false);
                conditionsRoomNoCB.setChecked(false);
                conditionsNameCB.setEnabled(false);
                conditionsDonetimeCB.setEnabled(false);
                conditionsTimerangeCB.setEnabled(false);
                conditionsRoomNoCB.setEnabled(false);
                break;
            case R.id.conditions_selectRB :
                RGselect=false;
                conditionsNameCB.setEnabled(true);
                conditionsDonetimeCB.setEnabled(true);
                conditionsTimerangeCB.setEnabled(true);
                conditionsRoomNoCB.setEnabled(true);
                break;
        }
/** ================================================ */
        conditionsRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (conditionsRG.getCheckedRadioButtonId()){
                    case R.id.conditions_allRB :
                        RGselect=true;
                        conditionsTimerangeCB.setChecked(false);
                        conditionsDonetimeCB.setChecked(false);
                        conditionsNameCB.setChecked(false);
                        conditionsRoomNoCB.setChecked(false);
                        conditionsNameCB.setEnabled(false);
                        conditionsDonetimeCB.setEnabled(false);
                        conditionsTimerangeCB.setEnabled(false);
                        conditionsRoomNoCB.setEnabled(false);
                        conditionsDonetimeDET.setEnabled(false);
                        conditionsDonetimeMET.setEnabled(false);
                        conditionsDonetimeYET.setEnabled(false);
                        conditionsTimerangeSP.setEnabled(false);
                        conditionsRoomNoET.setEnabled(false);
                        break;
                    case R.id.conditions_selectRB :
                        RGselect=false;
                        conditionsNameCB.setEnabled(true);
                        conditionsDonetimeCB.setEnabled(true);
                        conditionsTimerangeCB.setEnabled(true);
                        conditionsRoomNoCB.setEnabled(true);
                        break;
                }
            }
        });
/** ================================================ */
        conditionsNameCB.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    conditionsNameET.setEnabled(true);
                }else{
                    conditionsNameET.setText("");
                    conditionsNameET.setEnabled(false);
                }
                NameCBCHK=isChecked;
            }
        });
/** ================================================ */
        conditionsDonetimeCB.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setting = getSharedPreferences("historyconditions", 0);
                if(isChecked){
                    conditionsDonetimeDET.setEnabled(true);
                    conditionsDonetimeMET.setEnabled(true);
                    conditionsDonetimeYET.setEnabled(true);
                }else{
                    conditionsDonetimeDET .setText("");
                    conditionsDonetimeMET .setText("");
                    conditionsDonetimeYET .setText("");
                    conditionsDonetimeDET.setEnabled(false);
                    conditionsDonetimeMET.setEnabled(false);
                    conditionsDonetimeYET.setEnabled(false);
                    setting.edit().putString("DonetimeY", "").commit();
                    setting.edit().putString("DonetimeM", "").commit();
                    setting.edit().putString("DonetimeD", "").commit();
                }
                DonetimeCBCHK=isChecked;
            }
        });
/** ================================================ */
        conditionsRoomNoCB.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    conditionsRoomNoET.setEnabled(true);
                }else{
                    conditionsRoomNoET.setText("");
                    conditionsRoomNoET.setEnabled(false);
                }
                RoomNoCBCHK=isChecked;
            }
        });
/** ================================================ */
        conditionsTimerangeCB.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    conditionsTimerangeSP.setEnabled(true);
                }else{
                    conditionsTimerangeSP.setEnabled(false);
                }
                TimerangeCBCHK=isChecked;
            }
        });

/** ================================================ */

        conditionsTimerangeSP.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SPselect=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SP1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SP2select=position;
                switch (position){
                    case 0:
                        OrderBy="roomnum";
                        break;
                    case 1:
                        OrderBy="donedate";
                        break;
                    case 2:
                        OrderBy="name";
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
/** ================================================ */
        SP2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SP3select=position;
                if(position==0) {Order="ASC";}else{Order="DESC";}
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
/** ================================================ */
        conditionsSerchBT.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                setting = getSharedPreferences("historyconditions", 0);
                if(RGselect){
                    setting.edit().putBoolean("RGselect", RGselect).commit();
                }else{
                    name=conditionsNameCB.isChecked()?conditionsNameET.getText().toString():"false";
                    if(conditionsDonetimeCB.isChecked()){
                        String Y=conditionsDonetimeYET.getText().toString();
                        String M=conditionsDonetimeMET.getText().toString();
                        String D=conditionsDonetimeDET.getText().toString();
                        setting.edit().putString("DonetimeY", Y).commit();
                        setting.edit().putString("DonetimeM", M).commit();
                        setting.edit().putString("DonetimeD", D).commit();
                        int H=0;
                        if(!Y.matches("")){
                            H=H+1;
                            Y=Y+"-";
                        }
                        if(!M.matches("")){
                            if(H<1){
                                M="-"+M+"-";
                            }else if(H>0){
                                M=M+"-";
                            }
                            H=H+1;
                        }else{
                            if(H>0 && !D.matches("")){M="%";}
                        }
                        if(!D.matches("") && M.matches("%") || H<1){
                            D="-"+D;
                        }
                        Donetime=Y+M+D;
                    }else{
                        Donetime="false";
                    }
                    Timerange=conditionsTimerangeCB.isChecked()?timerangeLT.get(SPselect):"false";
                    RoomNo=conditionsRoomNoCB.isChecked()?conditionsRoomNoET.getText().toString():"false";
                }
                if(name.matches("false") && Donetime.matches("false") && Timerange.matches("false") && RoomNo.matches("false")){conditionsRG.check(R.id.conditions_allRB);}
                SaveValue();
                finish();
            }
        });
    }
/** ================================================ */
    public void ReadValue() {
        setting = getSharedPreferences("historyconditions", 0);
        RGselect=setting.getBoolean("RGselect",false);
        RGSLid=setting.getInt("RGSLid",0);
        name = setting.getString("name", "");
        if(name.matches("false"))name="";
        DonetimeD=setting.getString("DonetimeD", "");
        DonetimeM=setting.getString("DonetimeM", "");
        DonetimeY=setting.getString("DonetimeY", "");
        Donetime=setting.getString("Donetime", "");
        SPselect = setting.getInt("SPselect", 0);
        SP2select = setting.getInt("SP2select", 0);
        SP3select = setting.getInt("SP3select", 0);
        Order = setting.getString("Order", "");
        OrderBy = setting.getString("OrderBy", "");
        RoomNo = setting.getString("RoomNo", "");
        if(RoomNo.matches("false"))RoomNo="";
        NameCBCHK=setting.getBoolean("NameCBCHK",false);
        TimerangeCBCHK=setting.getBoolean("TimerangeCBCHK",false);
        DonetimeCBCHK=setting.getBoolean("DonetimeCBCHK",false);
        RoomNoCBCHK=setting.getBoolean("RoomNoCBCHK",false);
    }
/** ================================================ */
    public void SaveValue() {
        setting = getSharedPreferences("historyconditions", 0);
        setting.edit().putBoolean("RGselect", RGselect).commit();
        setting.edit().putInt("RGSLid", conditionsRG.getCheckedRadioButtonId()).commit();
        setting.edit().putString("Timerange", Timerange).commit();
        setting.edit().putString("name", name).commit();
        setting.edit().putString("Donetime", Donetime).commit();
        setting.edit().putInt("SPselect", SPselect).commit();
        setting.edit().putInt("SP2select", SP2select).commit();
        setting.edit().putInt("SP3select", SP3select).commit();
        setting.edit().putString("Order", Order).commit();
        setting.edit().putString("OrderBy", OrderBy).commit();
        setting.edit().putString("RoomNo", RoomNo).commit();
        setting.edit().putBoolean("NameCBCHK", NameCBCHK).commit();
        setting.edit().putBoolean("TimerangeCBCHK", TimerangeCBCHK).commit();
        setting.edit().putBoolean("DonetimeCBCHK", DonetimeCBCHK).commit();
        setting.edit().putBoolean("RoomNoCBCHK", RoomNoCBCHK).commit();
    }
}
