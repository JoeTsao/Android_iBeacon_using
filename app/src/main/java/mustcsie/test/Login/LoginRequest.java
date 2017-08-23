package mustcsie.test.Login;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2016/10/10.
 */
public class LoginRequest extends StringRequest {

    private Map<String, String> params;


    public LoginRequest(String url,String username, String password, Response.Listener<String> listener) {
        super(Method.POST,url, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

    }
    public LoginRequest(String url, String index,int select, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        String s=""+select;
        params = new HashMap<>();
        params.put("index", index);
        params.put("select",s);
    }
    public LoginRequest(String url, int select, String patient , Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        String s=""+select;
        params = new HashMap<>();
        params.put("patient", patient);
        params.put("select",s);
    }
    public LoginRequest(String url, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        params = new HashMap<>();

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
