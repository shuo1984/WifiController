package cn.ctsms.wificontroller;

import android.app.Application;
import android.content.Context;

/**
 * Created by Shuo on 2017/10/25.
 */

public class IApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

}
