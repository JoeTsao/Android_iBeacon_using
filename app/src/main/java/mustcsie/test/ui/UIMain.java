/** ============================================================== */
package mustcsie.test.ui;
/** ============================================================== */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.THLight.USBeacon.App.Lib.BatteryPowerData;
import com.THLight.USBeacon.App.Lib.USBeaconConnection;
import com.THLight.USBeacon.App.Lib.USBeaconList;
import com.THLight.USBeacon.App.Lib.USBeaconServerInfo;
import com.THLight.USBeacon.App.Lib.iBeaconData;
import com.THLight.USBeacon.App.Lib.iBeaconScanManager;
import com.THLight.Util.THLLog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mustcsie.test.R;
import mustcsie.test.ibeacon.ScanediBeacon;
import mustcsie.test.ibeacon.THLApp;
import mustcsie.test.ibeacon.THLConfig;
import mustcsie.test.patient.roomUI;

/** ============================================================== */
public class UIMain extends Activity implements iBeaconScanManager.OniBeaconScan, USBeaconConnection.OnResponse, View.OnClickListener {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1; /*位置權限代碼*/
	private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 101;/*存取權限代碼*/
	private static final String getroom_REQUEST_URL = "http://xxx.xxx.xxx/getroom.php";
	private static final String GET_ROOM_FROMUUID_URL = "http://xxx.xxx.xxx/getroomnumfromuuid.php";
    private GoogleApiClient client;
	/** this UUID is generate by Server while register a new account. */
	final UUID QUERY_UUID		= UUID.fromString("15CB7079-1B9D-4E22-9FE3-A8DF625F9FCF");
	/** server http api url. */
	final String HTTP_API		= "http://www.usbeacon.com.tw/api/func";
	static RequestQueue queue ;
	static String STORE_PATH	= Environment.getExternalStorageDirectory().toString()+ "/USBeaconSample/";
	int Ichk=0;
	int Hchk=0;
	final int REQ_ENABLE_BT		= 2000;
	final int REQ_ENABLE_WIFI	= 2001;
	
	final int MSG_SCAN_IBEACON			= 1000;
	final int MSG_UPDATE_BEACON_LIST	= 1001;
	final int MSG_START_SCAN_BEACON		= 2000;
	final int MSG_STOP_SCAN_BEACON		= 2001;
	final int MSG_SERVER_RESPONSE		= 3000;
	
	final int TIME_BEACON_TIMEOUT		= 3000;
	String chk = "";
	THLApp App		= null;
	THLConfig Config= null;
	
	BluetoothAdapter mBLEAdapter= BluetoothAdapter.getDefaultAdapter();

	/** scaner for scanning iBeacon around. */
	iBeaconScanManager miScaner	= null;
	
	/** USBeacon server. */
	USBeaconConnection mBServer	= new USBeaconConnection();

	USBeaconList mUSBList		= null;
	
	ListView mLVBLE= null;
	
	BLEListAdapter mListAdapter		= null;
	List<ScanediBeacon> miBeacons	= new ArrayList<ScanediBeacon>();
	String RoomNum = "";
	String [][] list = {{""},{""}};
	String CarNum="";
	String [][] list2 = {{""},{""}};

	private static SharedPreferences setting;
	private static String RP = "";
	int lenX=0;
	int lenY=0;
	int lenX2=0;
	int lenY2=0;
	int ch=0;
	/** ================================================ */
	Handler mHandler= new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case MSG_SCAN_IBEACON:
					{
						if(miScaner!=null){
						int timeForScaning		= msg.arg1;
						int nextTimeStartScan	= msg.arg2;
							miScaner.startScaniBeacon(timeForScaning);


						this.sendMessageDelayed(Message.obtain(msg), nextTimeStartScan);
						}
					}
					break;
				case MSG_STOP_SCAN_BEACON:
				{
					if(miScaner!=null)
					{
					 miScaner.stopScaniBeacon();
					}
						miScaner		= null;



				}
				break;
				case MSG_UPDATE_BEACON_LIST:
					if(mListAdapter!=null){
					synchronized(mListAdapter)
					{
						if(ch==0){
							ch=1;
						getserverdata();
						}
						verifyiBeacons();
						mListAdapter.notifyDataSetChanged();

						mHandler.sendEmptyMessageDelayed(MSG_UPDATE_BEACON_LIST, 500);
					}
			}
					break;
				
				case MSG_SERVER_RESPONSE:
					switch(msg.arg1)
					{
						case USBeaconConnection.MSG_NETWORK_NOT_AVAILABLE:
							break;
							
						case USBeaconConnection.MSG_HAS_UPDATE:
							mBServer.downloadBeaconListFile();
							/*Toast.makeText(UIMain.this, "資料更新成功.", Toast.LENGTH_SHORT).show();*/
							break;
							
						case USBeaconConnection.MSG_HAS_NO_UPDATE:
							/*Toast.makeText(UIMain.this, "No new BeaconList.", Toast.LENGTH_SHORT).show();*/
							break;
							
						case USBeaconConnection.MSG_DOWNLOAD_FINISHED:
							break;
		
						case USBeaconConnection.MSG_DOWNLOAD_FAILED:
							Toast.makeText(UIMain.this, "Download file failed!", Toast.LENGTH_SHORT).show();
							break;
							
						case USBeaconConnection.MSG_DATA_UPDATE_FINISHED:
							{
								USBeaconList BList= mBServer.getUSBeaconList();

								if(null == BList)
								{
									Toast.makeText(UIMain.this, "Data Updated failed.", Toast.LENGTH_SHORT).show();
									THLLog.d("debug", "update failed.");
								}
								else if(BList.getList().isEmpty())
								{
									Toast.makeText(UIMain.this, "Data Updated but empty.", Toast.LENGTH_SHORT).show();
									THLLog.d("debug", "this account doesn't contain any devices.");
								}
								else
								{
									/*Toast.makeText(UIMain.this, "Data Updated("+ BList.getList().size()+ ")", Toast.LENGTH_SHORT).show();
									
									for(USBeaconData data : BList.getList())
									{
										THLLog.d("debug", "Name("+ data.name+ "), Ver("+ data.major+ "."+ data.minor+ ")");
									}*/
								}
							}
							break;
							
						case USBeaconConnection.MSG_DATA_UPDATE_FAILED:
							Toast.makeText(UIMain.this, "UPDATE_FAILED!", Toast.LENGTH_SHORT).show();
							break;
					}
					break;
			}
		}
	};
	
	/** ================================================ */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main);
		ListView BL = (ListView) findViewById(R.id.beacon_list);

		queue = Volley.newRequestQueue(UIMain.this);
		getserverdata();

		setting = getSharedPreferences("LoginInfo", 0);
		CarNum = setting.getString("carnum", "");
		getroomdata(CarNum);
		/*安卓6.0 權限要求*/
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			/*需要位置權限部分*/
			if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("本程式需要定位權限");
				builder.setMessage("按下確認後繼續.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@RequiresApi(api = Build.VERSION_CODES.M)
					public void onDismiss(DialogInterface dialog) {
						requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
					}
				});
				builder.show();
			}
			/*存取權限部分*/
			if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("本程式需要檔案存取權限");
				builder.setMessage("按下確認後繼續.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@RequiresApi(api = Build.VERSION_CODES.M)
					public void onDismiss(DialogInterface dialog) {
						requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
					}
				});
				builder.show();
			}
		}
		App		= THLApp.getApp();
		Config	= THLApp.Config;
		
		/** create instance of iBeaconScanManager. */
		miScaner		= new iBeaconScanManager(this, this);
		
		mListAdapter	= new BLEListAdapter(this);
		
		mLVBLE			= (ListView)findViewById(R.id.beacon_list);
		mLVBLE.setAdapter(mListAdapter);

		if(!mBLEAdapter.isEnabled())
		{
			mBLEAdapter.enable();
			Message msg= Message.obtain(mHandler, MSG_SCAN_IBEACON, 1000, 1100);
			msg.sendToTarget();
		}
		else
		{
			Message msg= Message.obtain(mHandler, MSG_SCAN_IBEACON, 1000, 1100);
			msg.sendToTarget();
		}

		/** create store folder. */
		File file= new File(STORE_PATH);
		if(!file.exists())
		{

			if(!file.mkdirs())
			{
				Toast.makeText(this, "Create folder("+ STORE_PATH+ ") failed.", Toast.LENGTH_SHORT).show();
			}
		}
		
		/** check network is available or not. */
		ConnectivityManager cm	= (ConnectivityManager)getSystemService(UIMain.CONNECTIVITY_SERVICE);
		if(null != cm)
		{
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if(null == ni || (!ni.isConnected()))
			{
				dlgNetworkNotAvailable();
			}
			else
			{
				THLLog.d("debug", "NI not null");

				NetworkInfo niMobile= cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if(null != niMobile)
				{
					boolean is3g	= niMobile.isConnectedOrConnecting();
					
					if(is3g)
					{
						if(!THLApp.Config.allow3G){
							dlgNetwork3G();
						}
						else
						{
							USBeaconServerInfo info= new USBeaconServerInfo();
							info.serverUrl		= HTTP_API;
							info.queryUuid		= QUERY_UUID;
							info.downloadPath	= STORE_PATH;

							mBServer.setServerInfo(info, this);
							mBServer.checkForUpdates();
						}
					}
					else
					{
						USBeaconServerInfo info= new USBeaconServerInfo();
						
						info.serverUrl		= HTTP_API;
						info.queryUuid		= QUERY_UUID;
						info.downloadPath	= STORE_PATH;
						
						mBServer.setServerInfo(info, this);
						mBServer.checkForUpdates();
					}
				}
			}
		}
		else
		{
			THLLog.d("debug", "CM null");
		}
		
		mHandler.sendEmptyMessageDelayed(MSG_UPDATE_BEACON_LIST, 500);

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}
	/*權限處理相關*/
	@SuppressLint("NewApi")
	@Override
	public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case PERMISSION_REQUEST_COARSE_LOCATION: {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d("log", "coarse location permission granted");
				} else {
					final AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Functionality limited");
					builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
						}

					});
					builder.show();
				}
				return;
			}
			case PERMISSION_WRITE_EXTERNAL_STORAGE: {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d("log", "coarse location permission granted");
				} else {
					final AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Functionality limited");
					builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

						@Override
						public void onDismiss(DialogInterface dialog) {
						}

					});
					builder.show();
				}
				return;
			}
		}
	}
	
	/** ================================================ */

	@Override
	public void onResume()
	{

		super.onResume();
	}
	
	/** ================================================ */
	@Override
	public void onPause()
	{
		if(mListAdapter!=null){
		mListAdapter.clear();

		verifyiBeacons();
		mListAdapter.notifyDataSetChanged();
		}
		mListAdapter=null;
		Message msg= Message.obtain(mHandler, MSG_STOP_SCAN_BEACON, 1000, 1100);
		msg.sendToTarget();
		super.onPause();
	}
	@Override
	public void onStart()
	{
		mListAdapter	= new BLEListAdapter(this);
		mListAdapter.clear();
		mListAdapter.notifyDataSetChanged();
		mLVBLE.setAdapter(mListAdapter);
		verifyiBeacons();

		if(miScaner==null){
			miScaner= new iBeaconScanManager(this, this);
			Message msg= Message.obtain(mHandler, MSG_SCAN_IBEACON, 1000, 1100);
			msg.sendToTarget();
		}
		super.onStart();
	}


	protected void onDestroy(){
		super.onDestroy();
		Message msg= Message.obtain(mHandler, MSG_STOP_SCAN_BEACON, 1000, 1100);
		msg.sendToTarget();


	}
	/** ================================================ */

	
	/** ================================================ */
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
  	{
  		THLLog.d("DEBUG", "onActivityResult()");

  		switch(requestCode)
  		{
  			case REQ_ENABLE_BT:
	  			if(RESULT_OK == resultCode)
	  			{
				}
	  			break;
	  			
  			case REQ_ENABLE_WIFI:
  				if(RESULT_OK == resultCode)
	  			{
				}
  				break;
  		}
  	}

    /** ================================================ */

	@Override
	public void onScaned(iBeaconData iBeacon)
	{
		if(mListAdapter!=null){
		synchronized(mListAdapter)
		{
			addOrUpdateiBeacon(iBeacon);
		}

		if(!mBLEAdapter.isEnabled()) {
			mBLEAdapter.enable();
			Context context1 = getApplication();
			Toast.makeText(context1,"請勿關閉藍芽，這可能會讓應用程式無法順利執行，已自動重新開啟",Toast.LENGTH_SHORT).show();
		}
		}
	}
	/** ================================================ */

	@Override

	public void onBatteryPowerScaned(BatteryPowerData batteryPowerData) {
		// TODO Auto-generated method stub
		Log.d("debug", batteryPowerData.batteryPower+"");
		for(int i = 0 ; i < miBeacons.size() ; i++)
		{
			if(miBeacons.get(i).macAddress.equals(batteryPowerData.macAddress))
			{
				ScanediBeacon ib = miBeacons.get(i);
				ib.batteryPower = batteryPowerData.batteryPower;
				miBeacons.set(i, ib);
			}
		}
	}
	
	/** ========================================================== */
	public void onResponse(int msg)
	{
		THLLog.d("debug", "Response("+ msg+ ")");
		mHandler.obtainMessage(MSG_SERVER_RESPONSE, msg, 0).sendToTarget();
	}

	/** ========================================================== */
	public void dlgNetworkNotAvailable()
	{
		final AlertDialog dlg = new AlertDialog.Builder(UIMain.this).create();
		
		dlg.setTitle("網路異常");
		dlg.setMessage("請開起網路來更新 beacon 資料.");

		dlg.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				dlg.dismiss();
			}
		});
		
		dlg.show();
	}
	
	/** ========================================================== */
	public void dlgNetwork3G()
	{
		final AlertDialog dlg = new AlertDialog.Builder(UIMain.this).create();
		
		dlg.setTitle("連線許可");
		dlg.setMessage("應用程式將通過網路收發數據，這可能會有額外的數據流量費.");

		dlg.setButton(AlertDialog.BUTTON_POSITIVE, "接受", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				Config.allow3G= true;
				dlg.dismiss();
				USBeaconServerInfo info= new USBeaconServerInfo();
				
				info.serverUrl		= HTTP_API;
				info.queryUuid		= QUERY_UUID;
				info.downloadPath	= STORE_PATH;
				
				mBServer.setServerInfo(info, UIMain.this);
				mBServer.checkForUpdates();
			}
		});
		
		dlg.setButton(AlertDialog.BUTTON_NEGATIVE, "不接受", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				Config.allow3G= false;
				dlg.dismiss();
			}
		});
	
		dlg.show();
	}
	
	/** ========================================================== */
	public double RssiToMeter(int rssi){
		int iRssi = Math.abs(rssi);
		double power = (iRssi-63.437791837549794641391998891156)/(10*2);
		return Math.pow(10, power);
	}
	public void addOrUpdateiBeacon(iBeaconData iBeacon)
	{
		long currTime= System.currentTimeMillis();
		
		ScanediBeacon beacon= null;
		
		for(ScanediBeacon b : miBeacons)
		{
			if(b.equals(iBeacon, false))
			{
				beacon= b;
				break;
			}
		}
		
		if(null == beacon)
		{
			beacon= ScanediBeacon.copyOf(iBeacon);
			miBeacons.add(beacon);
		}
		else
		{
			beacon.rssi= iBeacon.rssi;
		}
		
		beacon.lastUpdate= currTime;
	}
	
	/** ========================================================== */
	public void verifyiBeacons()
	{

		if(mListAdapter!=null){
			mListAdapter.clear();
		}
		{
			long currTime	= System.currentTimeMillis();
			
			int len= miBeacons.size();
			Double []I1 = new Double[len];

			ScanediBeacon beacon= null;
			
			for(int i= len- 1; 0 <= i; i--)
			{
				I1[i]=RssiToMeter(miBeacons.get(i).rssi);
				beacon = miBeacons.get(i);

				if(null != beacon && TIME_BEACON_TIMEOUT < (currTime- beacon.lastUpdate))
				{
					miBeacons.remove(i);
				}
			}
		}

		{
			Collections.sort(miBeacons,
					new Comparator<ScanediBeacon>() {
						public int compare(ScanediBeacon o1, ScanediBeacon o2) {
							return o2.rssi - o1.rssi;
						}
					});

			for (ScanediBeacon beacon : miBeacons) {

				DecimalFormat df = new DecimalFormat("#.##");
				String s = df.format(RssiToMeter(beacon.rssi));

				USBeaconList BList = mBServer.getUSBeaconList();
				//for(USBeaconData data : BList.getList())
				{

					String room = getrommnum(beacon.macAddress);
					boolean found = false;
					for (int x = 0; x < lenX2; x++) {
						for (int y = 0; y < lenY2; y++) {
							if (list2[x][y].contains(room)) {
								RP = list2[x][0];
								found = true;
								break;
							}
						}
					}
					if (!found) {
						RP = "0";
					}
					String CH = RP;
					switch (CH) {
						case "3":
							mListAdapter.addItem(new ListItem(room, "已完成", "" + s));
							break;
						case "1":
							mListAdapter.addItem(new ListItem(room, "目標房間", "" + s));
							break;
						case "2":
							mListAdapter.addItem(new ListItem(room, "下個目標", "" + s));
							break;
						default:
							mListAdapter.addItem(new ListItem(room, "", "" + s));
							break;

					}


				}

			}

			if (mListAdapter != null) {
				mListAdapter.notifyDataSetChanged();

				if (miBeacons.size() > 0) {
					double D = Double.valueOf(mListAdapter.getItem(0).text5);
					if (D <= 1) {
						miBeacons.get(0).batteryPower++;

						if (miBeacons.get(0).batteryPower >= 3) {
							boolean found2 = false;
							for (int x = 0; x < lenX2; x++) {
								for (int y = 0; y < lenY2; y++) {
									if (list2[x][y].contains(mListAdapter.getItem(0).text1)) {
										RP = list2[x][0];
										if (RP.matches("1") ) {
											found2 = true;
										}
										break;
									}
								}
							}
							if (found2) {
								EnterRoom(mListAdapter.getItem(0).text1);
							}
						}
					} else {
						miBeacons.get(0).batteryPower = 0;
					}
				}
			}
		}
	}
	public String getrommnum(String UUID) {
		if(list[0][0]==null){
			getserverdata();
		}
		int X1 = 0;
		for (int x =0;x<lenX ;x++){
				if(list[x][1].matches(UUID)){
					X1=x;
					break;
				}
		}
		RoomNum=list[X1][0];
		return list[X1][0];
	}
	/** ========================================================== */
	public void getserverdata() {

		final StringRequest mStringRequest = new StringRequest(Request.Method.POST,
				GET_ROOM_FROMUUID_URL, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				// response
				// Log.d("RP", response);
				try {
					JSONArray J = new JSONArray(response);
					lenX = J.length();
					list = new String[lenX][];
					for (int x =0;x<lenX ;x++) {
						JSONArray K = J.getJSONArray(x);
						lenY = K.length();
						list[x] = new String[lenY];
						for (int a = 0; a < lenY; a++) {
							list[x][a] = K.getString(a);

						}
					}

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

				return params;
			}

		};
		queue.add(mStringRequest);
	}

	public void getroomdata(final String carnum) {

		final StringRequest mStringRequest = new StringRequest(Request.Method.POST,
				getroom_REQUEST_URL, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				// response
				// Log.d("RP", response);
				try {


						JSONArray J = new JSONArray(response);
						lenX2 = J.length();
						list2 = new String[lenX2][];
						for (int x =0;x<lenX2 ;x++) {
							JSONArray K = J.getJSONArray(x);
							lenY2 = K.length();
							list2[x] = new String[lenY2];
							for (int a = 0; a < lenY2; a++) {
								list2[x][a] = K.getString(a);

							}
						}



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
				params.put("carnum",carnum);
				return params;
			}

		};
		queue.add(mStringRequest);


	}
	/** ========================================================== */
	public void cleariBeacons()
	{
		mListAdapter.clear();
	}

	@Override
	public void onClick(View v) {

	}


	@Override
	public void onBackPressed() {
				finish();

		return;
	}

	public void EnterRoom(String Room){

		if(Hchk==0) {
			Hchk=1;
			if (miScaner != null) {
				miScaner.stopScaniBeacon();
			}
			Intent intent = new Intent(UIMain.this, roomUI.class);
			intent.putExtra("roomnum", Room);
			UIMain.this.startActivity(intent);
			finish();
		}
	}

	/*@Override
	public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
		/*if(miScaner!=null){miScaner.stopScaniBeacon();}

		Intent intent = new Intent(UIMain.this, roomUI.class);
		intent.putExtra("roomnum", mListAdapter.getItem(position).text1);
		UIMain.this.startActivity(intent);
		finish();
		}*/

	}





/** ============================================================== */
class ListItem
{
	public String text1= "";
	public String text3= "";
	public String text5= "";
	
	public ListItem()
	{
	}
	
	public ListItem(String text1, String text3,String text5)
	{
		this.text1= text1;
		this.text3= text3;
		this.text5= text5;
	}
}

/** ============================================================== */
class BLEListAdapter extends BaseAdapter
{
	private Context mContext;
	  
	List<ListItem> mListItems= new ArrayList<ListItem>();

	/** ================================================ */
	public BLEListAdapter(Context c) { mContext= c; }
	
	/** ================================================ */
	public int getCount() { return mListItems.size(); }
	
	/** ================================================ */
	public ListItem getItem(int position)
	{
		if((!mListItems.isEmpty()) && mListItems.size() > position)
		{
			return mListItems.get(position);
		}
		
		return null;
	}
	  
	public String getItemText(int position)
	{
		if((!mListItems.isEmpty()) && mListItems.size() > position)
		{
			return ((ListItem)mListItems.toArray()[position]).text1;
		}
		
		return null;
	}
	
	/** ================================================ */
	public long getItemId(int position) { return 0; }
	
	/** ================================================ */


	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent)
	{
	    View view= (View)convertView;
	     
	    if(null == view)
	    	view= View.inflate(mContext, R.layout.item_text_3, null);
	
	    // view.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

	    if((!mListItems.isEmpty()) && mListItems.size() > position)
	    {
		    TextView text1	= (TextView)view.findViewById(R.id.it3_text1);
		    TextView text3	= (TextView)view.findViewById(R.id.it3_text3);
		    TextView text5	= (TextView)view.findViewById(R.id.it3_text5);

	    	ListItem item= (ListItem)mListItems.toArray()[position];

			text1.setText(item.text1);
			text3.setText(item.text3);
			text5.setText(item.text5+ " m");
		}
	    else
	    {
	    	view.setVisibility(View.GONE);
	    }

	    return view;
	}

	/** ================================================ */
	@Override
    public boolean isEnabled(int position) 
    {
		if(mListItems.size() <= position)
			return false;

        return true;
    }

	/** ================================================ */
	public boolean addItem(ListItem item)
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
