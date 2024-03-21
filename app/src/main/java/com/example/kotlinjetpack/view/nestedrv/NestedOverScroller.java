package com.example.kotlinjetpack.view.nestedrv;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;

/**
 * 平滑滚动效果的辅助类
 * 参考：<a href="https://juejin.cn/post/7312338839695081499">掘金</a>
 * GitHub：<a href="https://github.com/smuyyh/NestedRecyclerView">GitHub</a>
 */
public class NestedOverScroller {

    public static float invokeCurrentVelocity(@NonNull RecyclerView rv) {
        try {
            Field viewFlinger = null;
            for (Class<?> superClass = rv.getClass().getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
                try {
                    viewFlinger = superClass.getDeclaredField("mViewFlinger");
                    break;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            if (viewFlinger == null) {
                return 0.0F;
            } else {
                viewFlinger.setAccessible(true);
                Object viewFlingerValue = viewFlinger.get(rv);
                Field scroller = viewFlingerValue.getClass().getDeclaredField("mScroller");
                scroller.setAccessible(true);
                Object scrollerValue = scroller.get(viewFlingerValue);
                Field scrollerY = scrollerValue.getClass().getDeclaredField("mScrollerY");
                scrollerY.setAccessible(true);
                Object scrollerYValue = scrollerY.get(scrollerValue);
                Field currVelocity = scrollerYValue.getClass().getDeclaredField("mCurrVelocity");
                currVelocity.setAccessible(true);
                return (Float) currVelocity.get(scrollerYValue);
            }
        } catch (Throwable ignored) {
            return 0.0F;
        }
    }
}
