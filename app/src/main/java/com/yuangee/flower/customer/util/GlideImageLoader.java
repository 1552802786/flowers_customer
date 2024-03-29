package com.yuangee.flower.customer.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.youth.banner.loader.ImageLoader;
import com.yuangee.flower.customer.R;


public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
        RequestOptions options = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.ic_no_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(path)
                .apply(options)
                .into(imageView);
    }


}
