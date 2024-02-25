package com.example.kotlinjetpack.designpatterns.factoryMode

/**
 * 1、 简单工厂模式
 * 违背开闭原则
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2023/9/14 18:52
 */
class PizzaFactory {
    companion object {
        fun createPizza(type: String?) = when (type) {
            "cheese" -> CheesePizza()
            "bacon" -> BaconPizza()
            else -> null
        }
    }
}

interface Pizza {
    fun eat() {}
    fun bake() {}
}

class CheesePizza : Pizza {}
class BaconPizza : Pizza {}

class PizzaStore {
    fun orderPizza(type: String?): Pizza? = PizzaFactory.createPizza(type)?.apply {
        eat()
        bake()
    }
}


/**
 * 2、工厂方法模式
 * 依赖倒置原则：上层组件不能依赖下层组件，并且它们都不能依赖具体，而应该依赖抽象。
 */
abstract class AbPizzaStore {
    fun orderPizza(type: String?): Pizza? = createPizza(type)?.apply {
        eat()
        bake()
    }

    protected abstract fun createPizza(type: String?): Pizza?
}

//A商店提供芝士和培根两种pizza
class PizzaStoreA : AbPizzaStore() {
    override fun createPizza(type: String?): Pizza? = when (type) {
        "cheese" -> CheesePizza()
        "bacon" -> BaconPizza()
        else -> null
    }

}


/**
 * 2、抽象工厂模式
 *
 */


