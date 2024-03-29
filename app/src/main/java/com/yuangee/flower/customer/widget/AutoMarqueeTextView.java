package com.yuangee.flower.customer.widget;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * <p>Title:     自动滚动TextView    </p>
 * <p>Description:                     </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class AutoMarqueeTextView extends TextView{
	
	public AutoMarqueeTextView(Context context) {
		super(context);
		init();
	}
	
	public AutoMarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AutoMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public void init(){
		setEllipsize(TruncateAt.MARQUEE);
		setMarqueeRepeatLimit(-1);
		setSelected(true);
		setSingleLine(true);
	}
}
