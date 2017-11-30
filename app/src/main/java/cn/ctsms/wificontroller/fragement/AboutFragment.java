package cn.ctsms.wificontroller.fragement;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ctsms.wificontroller.R;
import cn.ctsms.wificontroller.activity.MainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        MainActivity activity = (MainActivity) getActivity();
        activity.setAppbarTitle("关于");
    }

}
