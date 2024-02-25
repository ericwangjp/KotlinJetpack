package com.example.kotlinjetpack.designpatterns.strategicMode

import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * desc: attributeDelegation
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/9/15 11:49
 */

/**
 * 类委托
 */
// 基础接口
interface Base {
    fun print()
}

// 基础对象
class BaseImpl(val x: Int) : Base {
    override fun print() {
        print(x)
    }
}

// 被委托类
class Derived(b: Base) : Base by b

//fun main() {
//    val b = BaseImpl(10)
//    Derived(b).print()
////    b.print()
//}

/**
 * 属性委托
 */
class DelegateExample {
    var prop: String by Delegate()
}

// 基础类
class Delegate {
    private var _realValue: String = "aa"

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        println("getValue")
        return _realValue
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("setValue")
        _realValue = value
    }
}

//fun main() {
//    DelegateExample().apply {
//        println(prop)
//        prop = "bbb"
//        println(prop)
//    }
//}


/**
 * 局部变量委托
 */
//fun main() {
//    val lazyValue: String by lazy {
//        println("lazy init completed！")
//        "hello lazy boy"
//    }
//    var i = 1
//    while (i < 5) {
//        i++
//        println(lazyValue)
//    }
//}


/**
 * 可观察属性
 */
class ObservableUser {
    var name: String by Delegates.observable("初始值") { property, oldValue, newValue ->
        println("属性：$property --> 旧值：$oldValue --> 新值：$newValue")
    }
}

//fun main() {
//    ObservableUser().apply {
//        name = "第一次更新值"
//        name = "第二次更新值"
//        println("最新的值为：$name")
//    }
//}

/**
 * Map / MutableMap 也可以用来实现属性委托,此时字段名是 Key，属性值是 Value
 * 属性名需要和 map 中 key 对应，否则抛异常 Key name is missing in the map
 */
class DelegationUser(map: Map<String, Any?>) {
    val name: String by map
}

fun main() {
    val map = mutableMapOf("name" to "李")
    DelegationUser(map).apply {
        println(name)
        map["name"] = "张"
        println(name)
    }
    val a: String by mapOf("a" to "aa", "b" to "bb")
    println(a)
}

/**
 * ReadOnlyProperty / ReadWriteProperty
 */
val name by ReadOnlyProperty<Any?, String> { thisRef, property -> "$thisRef - $property" }
val age by object : ReadWriteProperty<Any?, Int> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        println("getValue：")
        return 18
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        println("setValue:$value")
    }
}