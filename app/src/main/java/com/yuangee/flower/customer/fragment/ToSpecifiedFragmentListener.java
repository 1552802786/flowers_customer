package com.yuangee.flower.customer.fragment;

/**
 * Created by developerLzh on 2017/9/20 0020.
 */

public interface ToSpecifiedFragmentListener {
    void toFragment(int position);
    void toShoppingByParams(String genre,String genreSub,String params);
}
