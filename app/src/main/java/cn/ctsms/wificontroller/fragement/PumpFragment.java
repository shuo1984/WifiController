package cn.ctsms.wificontroller.fragement;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import cn.ctsms.wificontroller.R;
import cn.ctsms.wificontroller.activity.MainActivity;
import cn.ctsms.wificontroller.model.Device;
import cn.ctsms.wificontroller.utils.net.ICallback;
import cn.ctsms.wificontroller.utils.net.WifiRequestHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class PumpFragment extends Fragment {

    private Button startBtn, stopBtn;
    private Button obtainIP;
    private EditText inputIP;


    public PumpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_air_pump, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        MainActivity activity = (MainActivity) getActivity();
        activity.setAppbarTitle("智能充气泵");

        inputIP = (EditText)view.findViewById(R.id.ip_addr);
        obtainIP = (Button)view.findViewById(R.id.obtain_ip);
        startBtn = (Button)view.findViewById(R.id.start);
        stopBtn = (Button)view.findViewById(R.id.stop);

        setupListeners();
    }

    private void setupListeners() {
        ControllerListener listener = new ControllerListener();
        obtainIP.setOnClickListener(listener);
        startBtn.setOnClickListener(listener);
        stopBtn.setOnClickListener(listener);
    }



    private class ControllerListener implements View.OnClickListener, View.OnTouchListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.obtain_ip:
                    WifiRequestHelper.obtainDeviceIP(Device.Pump, new ICallback() {
                        @Override
                        public void onComplete(Object o) {
                            if(o!=null) {
                                String ip = o.toString();
                                inputIP.setText(ip);
                            }
                        }

                        @Override
                        public void onPrepare() {

                        }
                    });
                    break;
                case R.id.start:
                    WifiRequestHelper.sendCmdApache("start",inputIP.getText().toString(),null);
                    break;
                case R.id.stop:
                    WifiRequestHelper.sendCmdApache("stop",inputIP.getText().toString(),null);
                    break;
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }
}
