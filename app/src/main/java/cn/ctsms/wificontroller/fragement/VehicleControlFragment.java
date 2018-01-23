package cn.ctsms.wificontroller.fragement;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import cn.ctsms.wificontroller.R;
import cn.ctsms.wificontroller.activity.MainActivity;
import cn.ctsms.wificontroller.model.Device;
import cn.ctsms.wificontroller.utils.DataFormat;
import cn.ctsms.wificontroller.utils.StringUtils;
import cn.ctsms.wificontroller.utils.net.BasicNameValuePair;
import cn.ctsms.wificontroller.utils.net.Closure;
import cn.ctsms.wificontroller.utils.net.HTTPMethod;
import cn.ctsms.wificontroller.utils.net.HTTPParams;
import cn.ctsms.wificontroller.utils.net.HttpHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleControlFragment extends Fragment {


    private Button btnForward,btnBackward,btnTurnLeft,btnTurnRight,btnTurnLightON,btnTurnLightOff;
    private Button obtainIP;
    private EditText inputIP;
    private static final String ipPrefix = "http://";
    private Handler handler;

    public VehicleControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        handler = new Handler(Looper.getMainLooper());
        View view = inflater.inflate(R.layout.fragment_vehicle_control, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        MainActivity activity = (MainActivity) getActivity();
        activity.setAppbarTitle("智能小车");
        btnForward = (Button)view.findViewById(R.id.go_forward);
        btnBackward = (Button)view.findViewById(R.id.go_backward);
        btnTurnLeft = (Button)view.findViewById(R.id.turn_left);
        btnTurnRight = (Button)view.findViewById(R.id.turn_right);
        btnTurnLightON = (Button)view.findViewById(R.id.turn_light_on);
        btnTurnLightOff = (Button)view.findViewById(R.id.turn_light_off);
        inputIP = (EditText)view.findViewById(R.id.ip_addr);
        obtainIP = (Button)view.findViewById(R.id.obtain_ip);
        setupListeners();
    }

    private void setupListeners() {
        ControllerListener listener = new  ControllerListener();

        btnForward.setOnTouchListener(listener);

        btnBackward.setOnTouchListener(listener);

        btnTurnLeft.setOnTouchListener(listener);

        btnTurnRight.setOnTouchListener(listener);

        btnTurnLightON.setOnClickListener(listener);

        btnTurnLightOff.setOnClickListener(listener);

        obtainIP.setOnClickListener(listener);

    }

    private void sendCmd(final String cmdStr){

        new AsyncTask() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Object doInBackground(Object[] objects) {
                String apiInput = inputIP.getText().toString();

                if(StringUtils.isNotEmpty(apiInput)){
                    String api = ipPrefix + apiInput+"/?cmd=" + cmdStr;
                    HTTPParams params =new HTTPParams(HTTPMethod.GET, DataFormat.Normal);
                   // params.addRequestParam(new BasicNameValuePair("cmd",cmdStr));
                    HttpHelper.sendHttpRequest(api, params, new Closure() {
                        @Override
                        public void execute(final Object input) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(),"HTTPResponse:" + input.toString(), + Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
                return null;
            }
        }.execute();

    }

    //使用Apache的http请求发送可以让GET请求在请求头中正确显示
    private void sendCmdApache(final String cmdStr){
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String apiInput = inputIP.getText().toString();
                HttpClient httpCient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(ipPrefix + apiInput+"/?cmd=" + cmdStr);
                try {
                        HttpResponse httpResponse = httpCient.execute(httpGet);
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                             HttpEntity entity = httpResponse.getEntity();
                             String response = EntityUtils.toString(entity,"utf-8");//将entity当中的数据转换为字符串
                        }
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                return null;
            }
        }.execute();
    }

    private void obtainIP(){
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    final String[] ip = {null};
                    HTTPParams params = new HTTPParams(HTTPMethod.POST,DataFormat.Normal);
                    params.addRequestParam(new BasicNameValuePair("device", Device.Vehical.toString()));
                    HttpHelper.sendHttpRequest("http://api.ctsms.cn/api/smartdevices/getDeviceIp.php", params, new Closure() {
                        @Override
                        public void execute(Object input) {
                            try {
                                if (input != null) {
                                    String result = input.toString();
                                    JSONObject jo = new JSONObject(result);
                                    int resCode = jo.getInt("res_code");
                                    if(resCode == 0) {
                                        ip[0] = jo.getString("ip");
                                    }

                                }
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    return ip;
                }

                @Override
                protected void onPostExecute(Object o) {
                    if(o!=null){
                        String[] ip = (String[])o;
                        if(ip.length>0 && ip[0]!=null){
                            inputIP.setText(ip[0]);
                        }
                    }
                }
            }.execute();
    }

    private class ControllerListener implements View.OnClickListener, View.OnTouchListener{

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()){
                case R.id.go_forward:
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        sendCmdApache("forward");
                        Snackbar.make(v,"发送前进指令",Snackbar.LENGTH_SHORT).show();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        sendCmdApache("stop");
                        Snackbar.make(v,"发送停车指令",Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.go_backward:
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        sendCmdApache("backward");
                        Snackbar.make(v,"发送后退指令",Snackbar.LENGTH_SHORT).show();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        sendCmdApache("stop");
                        Snackbar.make(v,"发送停车指令",Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.turn_left:
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        sendCmdApache("turnleft");
                        Snackbar.make(v,"发送左转指令",Snackbar.LENGTH_SHORT).show();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        sendCmdApache("stop");
                        Snackbar.make(v,"发送停车指令",Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.turn_right:
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        sendCmdApache("turnright");
                        Snackbar.make(v,"发送右转指令",Snackbar.LENGTH_SHORT).show();
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        sendCmdApache("stop");
                        Snackbar.make(v,"发送停车指令",Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                default:

                    break;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.turn_light_on:
                    sendCmdApache("lighton");
                    Snackbar.make(v,"打开前车灯指令",Snackbar.LENGTH_SHORT).show();
                    break;
                case R.id.turn_light_off:
                    sendCmdApache("lightoff");
                    Snackbar.make(v,"关闭前车灯指令",Snackbar.LENGTH_SHORT).show();
                    break;
                case R.id.obtain_ip:
                    obtainIP();
                    break;
                default:

                    break;
            }
        }
    }

}
