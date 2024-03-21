package com.example.kotlinjetpack.view.nestedrv;

/**
 * ParentAdapter 需实现此接口
 * 参考：<a href="https://juejin.cn/post/7312338839695081499">掘金</a>
 * GitHub：<a href="https://github.com/smuyyh/NestedRecyclerView">GitHub</a>
 */
public interface INestedParentAdapter {

    /**
     * 获取当前需要联动的子RecyclerView
     *
     * @return ChildRecyclerView
     */
    ChildRecyclerView getCurrentChildRecyclerView();
}
