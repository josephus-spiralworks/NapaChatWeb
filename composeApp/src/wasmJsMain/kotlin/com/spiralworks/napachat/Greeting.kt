package com.spiralworks.napachat

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name} This is deployed via firebase cli - test 4!"
    }
}