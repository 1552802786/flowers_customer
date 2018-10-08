package com.yuangee.flower.customer.widget;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuangee.flower.customer.R;

import me.majiajie.pagerbottomtabstrip.internal.RoundMessageView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;

public class HomeItemView extends BaseTabItem {

    private ImageView mIcon;
    private final TextView mTitle;
    private final RoundMessageView mMessages;

    private int mDefaultDrawable;
    private int mCheckedDrawable;

    private int mDefaultTextColor = 0xFF9A9A9A;
    private int mCheckedTextColor = 0xFFFFFFFF;

    public HomeItemView(Context context) {
        this(context,null);
    }

    public HomeItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HomeItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.item_normal, this, true);

        mIcon = (ImageView) findViewById(R.id.icon);
        mTitle = (TextView) findViewById(R.id.title);
        mMessages = (RoundMessageView) findViewById(R.id.messages);
    }

    /**
     * 方便初始化的方法
     * @param drawableRes           默认状态的图标
     * @param checkedDrawableRes    选中状态的图标
     * @param title                 标题
     */
    public void initialize(@DrawableRes int drawableRes, @DrawableRes int checkedDrawableRes, String title)
    {
        mDefaultDrawable = drawableRes;
        mCheckedDrawable = checkedDrawableRes;
        mTitle.setText(title);
    }

    @Override
    public void setChecked(boolean checked) {
        if(checked){
            mIcon.setImageResource(mCheckedDrawable);
            mTitle.setTextColor(mCheckedTextColor);
        } else {
            mIcon.setImageResource(mDefaultDrawable);
            mTitle.setTextColor(mDefaultTextColor);
        }
    }

    @Override
    public void setMessageNumber(int number) {
        mMessages.setMessageNumber(number);
    }

    @Override
    public void setHasMessage(boolean hasMessage) {
        mMessages.setHasMessage(hasMessage);
    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setDefaultDrawable(Drawable drawable) {

    }

    @Override
    public void setSelectedDrawable(Drawable drawable) {

    }

    @Override
    public String getTitle() {
        return mTitle.getText().toString();
    }

    public void setTextDefaultColor(@ColorInt int color){
        mDefaultTextColor = color;
    }

    public void setTextCheckedColor(@ColorInt int color){
        mCheckedTextColor = color;
    }
}
