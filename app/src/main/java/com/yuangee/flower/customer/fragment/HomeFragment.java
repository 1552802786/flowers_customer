package com.yuangee.flower.customer.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.databinding.FragmentHomeBinding;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_home, null);
        binding = DataBindingUtil.bind(view);

        Log.e("lifecircle","onCreateView");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        Log.e("lifecircle","onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        Log.e("lifecircle","onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.e("lifecircle","onDestroyView");
        super.onDestroyView();
    }
}
