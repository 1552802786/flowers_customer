package com.yuangee.flower.customer;

import com.squareup.otto.Bus;

/**
 * Otto总线
 * Created by admin on 2018/2/3.
 */

public class AppBus extends Bus{
    private static AppBus bus;

    public static AppBus getInstance() {
        if (bus == null) {
            bus = new AppBus();
        }
        return bus;
    }
}
