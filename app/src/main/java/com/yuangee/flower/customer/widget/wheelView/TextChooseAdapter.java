package com.yuangee.flower.customer.widget.wheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018/1/20.
 */

public class TextChooseAdapter implements WheelAdapter {
    private List<String> data = new ArrayList<>();
    private int flag = -1;

    public TextChooseAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public int getItemsCount() {
        return data.size();
    }

    @Override
    public String getItem(int index) {

        return data.get(index);

    }

    @Override
    public int indexOf(String o) {
        return data.indexOf(o)<0?0:data.indexOf(o);
    }
}
