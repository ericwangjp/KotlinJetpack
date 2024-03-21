package com.example.kotlinjetpack.view.nestScroll;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent3;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kotlinjetpack.R;


/**
 * desc: FoldNavScrollLayout
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/18 12:04
 */
public class FoldNavScrollLayout extends LinearLayout implements NestedScrollingParent3 {
    private View navView, contentView;
    private int navHeight;
    private ValueAnimator navAnim;
    private static final int dySlop = 20;
    private int dyOffset;
    private boolean lastDirectionIsDown; //上次的方向
    private boolean isAniming;
    private boolean isExpand = true;
    private IFoldNavListener mListener;
    private static final String TAG = "FoldNavScrollLayout";

    public FoldNavScrollLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        navView = findViewById(R.id.fold_nav_layout);
//        tabView = findViewById(R.id.fold_tab_layout);
        contentView = findViewById(R.id.fold_content_layout);
        navView.post(() -> navHeight = navView.getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        navView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//        ViewGroup.LayoutParams params = contentView.getLayoutParams();
//        params.height = getMeasuredHeight() - tabView.getMeasuredHeight();
//        setMeasuredDimension(getMeasuredWidth(), navView.getMeasuredHeight() + tabView.getMeasuredHeight() + params.height);
//        setMeasuredDimension(getMeasuredWidth(), navView.getMeasuredHeight() + params.height);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        Log.e(TAG, "onStartNestedScroll: ");
        return true;
    }


    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        Log.e(TAG, "onNestedScrollAccepted: ");
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        Log.e(TAG, "onStopNestedScroll: ");
        if (lastDirectionIsDown && !isExpand && (!target.canScrollVertically(-1))) {
//                            展开
            dyOffset = 0;
            startAnim(true);
        }
//        if (!target.canScrollVertically(-1)) {
//            Log.e(TAG, "onStopNestedScroll: 滚动到顶部了-===");
//        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        Log.e(TAG, "onNestedScroll: ");
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        Log.e(TAG, "onNestedPreScroll ScrollY: " + getScrollY());
        Log.e(TAG, "target is recyclerview: " + (target instanceof RecyclerView));
        if (target instanceof RecyclerView) {
            if (!target.canScrollVertically(-1)) {
                Log.e(TAG, "onNestedPreScroll: 滚动到顶部");
            }

            RecyclerView recyclerView = (RecyclerView) target;
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
//                int firstVisiblePos = layoutManager.findFirstVisibleItemPosition();
//                if (firstVisiblePos <= 1) {
                int topOffset = 0;
                if (recyclerView.getChildCount() > 0) {
                    topOffset = recyclerView.getChildAt(0).getTop();
                }
                Log.d(TAG, "距离顶部距离：" + topOffset);
//            if (dy < 0 && topOffset >= 0) {
//                Log.e(TAG, "onNestedPreScroll: 展开")
//                LiveEventBus.get<String>("update").post("expand")
//            } else if (dy > 0 && topOffset <= 0) {
//                        consumed[1] = dy // 关键代码!!parentView正在消费事件,并且通知 childView
//                        Log.e(TAG, "onNestedPreScroll: 收起") LiveEventBus.get<String>
//                        ("update").post("collapse")
//                    }

                if (isAniming) return;
                if (lastDirectionIsDown && dy < 0) {
                    dyOffset += Math.abs(dy);
                } else if (!lastDirectionIsDown && dy > 0) {
                    dyOffset += dy;
                } else {
                    dyOffset = 0;
                }
                Log.e(TAG, "fqy - onNestedPreScroll: dy:" + dy);
                Log.e(TAG, "fqy - onNestedPreScroll: dyOffset:" + dyOffset);
                if (dyOffset > dySlop) {
                    if (lastDirectionIsDown && !isExpand && (!target.canScrollVertically(-1))) {
//                            展开
                        dyOffset = 0;
                        startAnim(true);
                    } else if (!lastDirectionIsDown && isExpand) {
//                            收起
                        dyOffset = 0;
                        startAnim(false);
                    }
                }
                lastDirectionIsDown = dy < 0;
//                }
            }
        }

    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        Log.e(TAG, "onNestedPreFling: ");
        return false;
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        Log.e(TAG, "onNestedFling: ");
        Log.e(TAG, "onNestedFling: velocityX：" + velocityX + " velocityY:" + velocityY + " consumed:" + consumed);
        Log.e(TAG, "onNestedFling: ScrollY: " + getScrollY());
        if (target instanceof RecyclerView) {
            Log.e(TAG, "onNestedFling: RecyclerView");
            if (!target.canScrollVertically(-1)) {
                Log.e(TAG, "onNestedFling: 滚动到顶部fffff");
            }
//            if (velocityY <= 0 && getScrollY() > 0) {
//                Log.e(TAG, "onNestedPreScroll: 滚动到顶部22");
//                startAnim(true);
//            }
        }
        return false;
    }


    @Override
    public int getNestedScrollAxes() {
        return 0;
    }


    public void startAnim(final boolean isAnimExpand) {
        isExpand = isAnimExpand;
        if (navAnim == null) {
            navAnim = new ValueAnimator();
            navAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isAniming = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isAniming = false;
                    if (null != mListener) {
                        if (isExpand) {
                            mListener.onNavExpandFinish();
                        } else {
                            mListener.onNavFoldFinish();
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            navAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Log.e(TAG, "onAnimationUpdate: 滚动距离：" + animation.getAnimatedValue());
                    scrollTo(0, (Integer) animation.getAnimatedValue());
                }
            });
            navAnim.setInterpolator(new DecelerateInterpolator());
        } else {
            navAnim.cancel();
        }
        if (isAnimExpand) {
            navAnim.setIntValues(navHeight, 0);
        } else {
            navAnim.setIntValues(0, navHeight);
        }
        if (null != mListener) {
            if (isExpand) {
                mListener.onNavExpandBegin();
            } else {
                mListener.onNavFoldBegin();
            }
        }
        navAnim.start();
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setOnFoldNavListener(IFoldNavListener listener) {
        this.mListener = listener;
    }

}
