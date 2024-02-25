package com.example.kotlinjetpack.designpatterns.decoratorMode

import android.app.Activity
import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * desc: Feather
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/9/14 16:27
 */
class Feather : Accessory {
    override fun name(): String = "Feather"

    override fun cost(): Int = 20

    override fun type(): String = "body accessory"
}

class Ring : Accessory {
    override fun name(): String = "Ring"

    override fun cost(): Int = 30

    override fun type(): String = "body accessory"

}

class Earrings : Accessory {
    override fun name(): String = "Earrings"

    override fun cost(): Int = 15

    override fun type(): String = "body accessory"

}


/**
 * 继承的方式
 */
class FeatherRing : Accessory {
    override fun name(): String = "FeatherRing"
    override fun cost(): Int = 35
    override fun type(): String = "body accessory"
}

class FeatherEarrings : Accessory {
    override fun name(): String = "FeatherEarrings"
    override fun cost(): Int = 45
    override fun type(): String = "body accessory"
}

/**
 * 装饰者模式
 * 调用 FeatherDecoratorPattern(Ring())、FeatherDecoratorPattern(Earrings())
 */
class FeatherDecoratorPattern(private var accessory: Accessory) : Accessory {
    override fun name(): String = "Feather" + accessory.name()
    override fun cost(): Int = 20 + accessory.cost()
    override fun type(): String = accessory.type()
}

/**
 * 委托模式
 */
class FeatherDelegatePattern(private val accessory: Accessory) : Accessory by accessory {
    override fun name(): String = "Feather" + accessory.name()
    override fun cost(): Int = 20 + accessory.cost()
}

/**
 * 属性委托
 */
class DelegateOne {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "Delegate"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
    }
}

class DelegateTwo : ReadWriteProperty<Any?, String> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "Delegate"
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
    }
}

class TestDelegate {
    var strOne: String by DelegateOne()
    var strTwo: String by DelegateTwo()
}

/**
 * 属性委托的应用
 * 新建 Extras 类作为被委托类
 * 使用：
 * private val id by Extras("id","0")
 */
class Extras<out T>(private val key: String, private val default: T) {
    // 重载取值操作符
    operator fun getValue(thisRef: Any, kProperty: KProperty<*>): T? = when (thisRef) {
        // 获取传递给 Activity 的参数
        is Activity -> {
            thisRef.intent?.extras?.get(key) as? T ?: default
        }
        // 获取传递给 Fragment 的参数
        is Fragment -> {
            thisRef.arguments?.get(key) as? T ?: default
        }

        else -> default
    }
}


