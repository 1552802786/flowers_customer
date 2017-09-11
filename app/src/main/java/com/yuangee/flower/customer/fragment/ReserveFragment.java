package com.yuangee.flower.customer.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuangee.flower.customer.R;
import com.yuangee.flower.customer.databinding.FragmentReserveBinding;

/**
 * Created by developerLzh on 2017/8/21 0021.
 */

public class ReserveFragment extends Fragment {

    private FragmentReserveBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_reserve, null);
        binding = DataBindingUtil.bind(view);
        return view;
    }
}
