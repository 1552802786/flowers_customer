package com.yuangee.flower.customer.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by developerLzh on 2017/9/11 0011.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    int left;
    int right;
    int top;
    int bottom;

    int orientation;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (orientation == LinearLayout.HORIZONTAL) {
            outRect.top = top;
            outRect.bottom = bottom;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = left;
            }
            outRect.right = right;
        }

        if (orientation == LinearLayout.VERTICAL) {
            outRect.left = left;
            outRect.right = right;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = top;
            }
            outRect.bottom = bottom;
        }
    }

    /**
     * 当orientation 为LinearLayout.HORIZONTAL时 left为第一个的左间距 right为每个的右间距
     * 当orientation 为LinearLayout.VERTICAL top为第一个的上间距 bottom为每个的右间距
     *
     * @param top
     * @param bottom
     * @param left
     * @param right
     * @param orientation
     */
    public SpaceItemDecoration(int top, int bottom, int left, int right, int orientation) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.orientation = orientation;
    }
}
