package com.example.kotlinjetpack.view.nestScrollDemo;

/**
 * desc: IFoldNavListener
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/6/18 12:05
 */
public interface IFoldNavListener {
    /**
     * 开始执行折叠动画
     */
    void onNavFoldBegin();

    /**
     * 折叠完成
     */
    void onNavFoldFinish();

    /**
     * 开始执行展开动画
     */
    void onNavExpandBegin();

    /**
     * 展开完成
     */
    void onNavExpandFinish();
}
