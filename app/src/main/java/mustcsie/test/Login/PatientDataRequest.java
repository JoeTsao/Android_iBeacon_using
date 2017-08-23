package mustcsie.test.Login;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/23.
 */

public class PatientDataRequest extends StringRequest {
    private  Map<String, String> params;


    public PatientDataRequest(String url, String bp, String tp, String spo2 ,String patient,String cheak, Response.Listener<String> listener) {
        super(Method.POST,url, listener, null);
        params = new HashMap<>();
        params.put("bp", bp);
        params.put("tp", tp);
        params.put("spo2", spo2);
        params.put("cheak", cheak);
        params.put("patient", patient);
    }
    @Override
    public java.util.Map<String, String> getParams() {
        return params;
    }
}
