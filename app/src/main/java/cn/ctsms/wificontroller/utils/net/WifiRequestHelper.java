package cn.ctsms.wificontroller.utils.net;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import cn.ctsms.wificontroller.model.Device;
import cn.ctsms.wificontroller.model.IConstant;
import cn.ctsms.wificontroller.utils.DataFormat;

/**
 * Created by Shuo on 2018/2/6.
 */

public class WifiRequestHelper {

    //使用Apache的http请求发送可以让GET请求在请求头中正确显示
    public static void sendCmdApache(final String cmdStr, final String inputIP , final ICallback callback){
        try {
            new AsyncTask() {

                @Override
                protected void onPreExecute() {
                    if (callback != null) {
                        callback.onPrepare();
                    }
                }

                @Override
                protected Object doInBackground(Object[] objects) {

                    HttpClient httpCient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(IConstant.ipPrefix + inputIP + "/?cmd=" + cmdStr);
                    try {
                        HttpResponse httpResponse = httpCient.execute(httpGet);
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            HttpEntity entity = httpResponse.getEntity();
                            String response = EntityUtils.toString(entity, "utf-8");//将entity当中的数据转换为字符串
                            return response;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    if (callback != null) {
                        callback.onComplete(o);
                    }
                }
            }.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public static void obtainDeviceIP(final Device device, final ICallback callback){
        try {
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    final String[] ip = {null};
                    HTTPParams params = new HTTPParams(HTTPMethod.POST, DataFormat.Normal);
                    params.addRequestParam(new BasicNameValuePair("device", device.toString()));
                    HttpHelper.sendHttpRequest("http://api.ctsms.cn/api/smartdevices/getDeviceIp.php", params, new Closure() {
                        @Override
                        public void execute(Object input) {
                            try {
                                if (input != null) {
                                    String result = input.toString();
                                    JSONObject jo = new JSONObject(result);
                                    int resCode = jo.getInt("res_code");
                                    if (resCode == 0 && !jo.isNull("ip")) {
                                        ip[0] = jo.getString("ip");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    return ip;
                }

                @Override
                protected void onPostExecute(Object o) {
                    if (o != null && callback != null) {
                        String[] ip = (String[]) o;
                        if (ip.length > 0 && ip[0] != null) {
                            callback.onComplete(ip[0]);
                            // inputIP.setText(ip[0]);
                        }
                    }
                }
            }.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
