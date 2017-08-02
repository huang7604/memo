package com.memo.myview;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.memo.R;

/**
 * Created by user on 2017/2/28.
 */
public class MainFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){
        View view=inflater.inflate(R.layout.main_view,viewGroup,true);
        return view;
    }
}
