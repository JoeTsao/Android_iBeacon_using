package mustcsie.test.Login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import mustcsie.test.R;
import mustcsie.test.nurse.chooscar;
import mustcsie.test.nurse.choose_nurse;
import mustcsie.test.nurse.choose_nurseboss;
import mustcsie.test.ui.GETdate;


public class LoginActivity extends AppCompatActivity {
    private static final String LOGIN_REQUEST_URL = "http://xxx.xxx.xxx/Login.php";
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 101;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    EditText etUsername = null;
    EditText etPassword = null;

    String username = "";
    String password = "";
    private static boolean successUSER = false;
    private static String name = "";
    private static String activechk="";
    private static  String carnum="";

    long logintime1=0;
    long logintime2=0;
    private static SharedPreferences setting;
    public static File file;
    public static File file2;
    RequestQueue queue;
   // private static ArrayList data = null;
    //Timer timer = new Timer(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
               // builder.show();
            }
        }
        name="";
        carnum="";
        activechk="";
        queue = Volley.newRequestQueue(LoginActivity.this);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        setting = getSharedPreferences("LoginInfo", 0);
        final Button bLogin = (Button) findViewById(R.id.bSignIn);
        file2 = new File("/data/data/mustcsie.test/shared_prefs", "historyconditions.xml");
        if (file2.exists()) {
            file2.delete();
        }
        file = new File("/data/data/mustcsie.test/shared_prefs", "LoginInfo.xml");
        if (file.exists()) {

            ReadValue();

            if (!name.equals("")) {
                logintime2 = System.currentTimeMillis();
                if (logintime2 - logintime1 < 120000 ) {
                    setting.edit().putLong("logintime1", logintime2).commit();

                        login();

                }else{
                    file.delete();
                    name="";
                    carnum="";
                    activechk="";
                }
            }
        }

        bLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                username=etUsername.getText().toString();
                password=etPassword.getText().toString();
                login();
            }
        });


    }

    /** ================================================ */
    public void login() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(LoginActivity.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (null == ni || (!ni.isConnected())) {
                final AlertDialog dlg = new AlertDialog.Builder(LoginActivity.this).create();

                dlg.setTitle("網路異常");
                dlg.setMessage("請確認網路連線.");

                dlg.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dlg.dismiss();
                    }
                });

                dlg.show();
            } else {
                // Response received from the server
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);

                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {

                                name = jsonResponse.getString("name");
                                int age = jsonResponse.getInt("age");
                                setting.edit().putString("name", name).commit();
                                String active = jsonResponse.getString("active");
                                setting.edit().putString("active", active).commit();
                                if (active.matches("Boss")) {
                                    Intent intent = new Intent(LoginActivity.this, choose_nurseboss.class);
                                    intent.putExtra("name", name);
                                    intent.putExtra("age", age);
                                    intent.putExtra("username", username);
                                        setting.edit().putString("USERNAME", username).commit();
                                        setting.edit().putString("PASSWORD", password).commit();
                                        logintime1 = System.currentTimeMillis();
                                        setting.edit().putLong("logintime1", logintime1).commit();
                                    LoginActivity.this.startActivity(intent);
                                    LoginActivity.this.finish();
                                } else {

                                        Intent intent=null;
                                        if(carnum.matches("")){
                                            intent = new Intent(LoginActivity.this, chooscar.class);
                                        }else{
                                            intent = new Intent(LoginActivity.this, choose_nurse.class);
                                        }
                                        intent.putExtra("name", name);
                                        intent.putExtra("age", age);
                                        intent.putExtra("username", username);
                                        setting.edit().putString("USERNAME", username).commit();
                                        setting.edit().putString("PASSWORD", password).commit();
                                        logintime1 = System.currentTimeMillis();
                                        setting.edit().putLong("logintime1", logintime1).commit();
                                        LoginActivity.this.startActivity(intent);
                                        LoginActivity.this.finish();
                                }
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("帳號或密碼有誤，請重新輸入")
                                        .setNegativeButton("確認", null)
                                        .create()
                                        .show();
                            }
                            queue.getCache().clear();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(LOGIN_REQUEST_URL, username, password, responseListener);

                queue.add(loginRequest);


            }

        }

    }
    /** ================================================ */
    public void ReadValue() {
        setting.edit().putString("lastReadtime", GETdate.getDateTime());
        name = setting.getString("name", "");
        activechk=setting.getString("active", "");
        username = setting.getString("USERNAME", "");
        carnum = setting.getString("carnum", "");
        password = setting.getString("PASSWORD", "");
        logintime1 = setting.getLong("logintime1", 0);
    }
/** ================================================ */
}