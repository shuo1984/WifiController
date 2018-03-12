package cn.ctsms.wificontroller.activity;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import cn.ctsms.wificontroller.R;
import cn.ctsms.wificontroller.fragement.AboutFragment;
import cn.ctsms.wificontroller.fragement.PumpFragment;
import cn.ctsms.wificontroller.fragement.SettingsFragment;
import cn.ctsms.wificontroller.fragement.VehicleControlFragment;
import cn.ctsms.wificontroller.fragement.NavigationDrawerFragment;

public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.menuClickListener {

    private Toolbar toolbar;
    private Context context;
    private TextView appbarTitle;
    private ActionBarDrawerToggle mDrawerToggle;         //定义toolbar左上角的弹出左侧菜单按钮
    private DrawerLayout drawer_main;                    //定义左侧滑动布局，其实就是主布局

    private VehicleControlFragment vehicleControlFragment;    //定义智能小车fragment
    private PumpFragment pumpFragment;                   //定义充气泵fragment
    private AboutFragment aboutFragment;
    private SettingsFragment settingsFragment;
    private Fragment isFragment;                         //记录当前正在使用的fragment


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        initToolbar();
        initFragment(savedInstanceState);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.mipmap.ic_calling_contact_info);
        appbarTitle = (TextView)findViewById(R.id.toolbar_title);
        //添加菜单点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(toolbar, "Click setNavigationIcon", Snackbar.LENGTH_SHORT).show();
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            //actionBar.setDisplayHomeAsUpEnabled(true);//显示回退按钮
            actionBar.setDisplayShowTitleEnabled(false);//不显示默认标题

        }

        //为了生成，工具栏左上角的动态图标，要使用下面的方法
        drawer_main = (DrawerLayout) findViewById(R.id.drawer_main);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer_main, toolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        drawer_main.setDrawerListener(mDrawerToggle);

    }

    private void initFragment(Bundle savedInstanceState) {
        //判断activity是否重建，如果不是，则不需要重新建立fragment.
        if(savedInstanceState==null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            if (vehicleControlFragment == null) {
                vehicleControlFragment = new VehicleControlFragment();
            }
            isFragment = vehicleControlFragment;
            ft.replace(R.id.frame_main, vehicleControlFragment).commit();

        }

    }

    @Override
    public void menuClick(String menuName) {
        getSupportActionBar().setTitle(menuName);                                   //修改Toolbar菜单的名字

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (menuName)
        {
            case "智能小车" :

                FragmentManager fmanager = getSupportFragmentManager();
                FragmentTransaction ftransaction = fmanager.beginTransaction();
                if(vehicleControlFragment==null) {
                    vehicleControlFragment = new VehicleControlFragment();
                }
                ftransaction.replace(R.id.frame_main, vehicleControlFragment);
                ftransaction.commit();
                break;
            case "智能充气泵":
                fmanager = getSupportFragmentManager();
                ftransaction = fmanager.beginTransaction();
                if(pumpFragment ==null) {
                    pumpFragment = new PumpFragment();
                }
                ftransaction.replace(R.id.frame_main, pumpFragment);
                ftransaction.commit();
                break;
            case "设置":
                fmanager = getSupportFragmentManager();
                ftransaction = fmanager.beginTransaction();
                if(settingsFragment==null) {
                    settingsFragment = new SettingsFragment();
                }
                ftransaction.replace(R.id.frame_main, settingsFragment);
                ftransaction.commit();
                break;
            case "关于":
                fmanager = getSupportFragmentManager();
                ftransaction = fmanager.beginTransaction();
                if(aboutFragment==null) {
                    aboutFragment = new AboutFragment();
                }
                ftransaction.replace(R.id.frame_main, aboutFragment);
                ftransaction.commit();
                break;
            case "退出" :
                android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
                System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
                break;

        }

        invalidateOptionsMenu();

        /**
         * 关闭左侧滑出菜单
         */
        drawer_main.closeDrawers();
    }

    public void setAppbarTitle(String title){
        appbarTitle.setText(title);
    }
}
