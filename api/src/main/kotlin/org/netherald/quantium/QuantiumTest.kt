package org.netherald.quantium

import java.util.*

class QuantiumTest(
    val description : String,
    val instance : MiniGameInstance,
    val testType : TestType,
    val parent : QuantiumTest? = null
) {

    fun now(description: String, function : QuantiumTest.() -> Unit) {
        QuantiumTest(description, instance, TestType.NOW, this).function()
    }

    fun then(description: String, function : QuantiumTest.() -> Unit) {
        QuantiumTest(description, instance, TestType.THEN, this).function()
    }

    operator fun String.invoke(function: QuantiumTest.() -> Unit) {
        QuantiumTest(description, instance, TestType.INVOKE, this@QuantiumTest).function()
    }

    infix fun <T> T.shouldBe(value: T) { if (this != value) { printStackTrack() } }

    fun shouldBeTrue(function : () -> Boolean) { if (!function()) { printStackTrack() } }

    private fun printStackTrack() {
        println()
        println("${instance.miniGame.name}'s instance ${instance.uuid}'s test is wrong")
        val testString = fun QuantiumTest.() = "${testType}(${description}):"

        parent ?: run {
            println(testString())
        }
        val tests = LinkedList<QuantiumTest>()
        var now : QuantiumTest = parent!!
        while (now.parent != null) {
            now = now.parent!!
            tests.add(now)
        }
        val testsSize = tests.size

        var nowCount = 0
        while (tests.isNotEmpty()) {
            val test = tests.pop()
            val stringBuilder = StringBuilder()
            for (i in 0 until 0) { stringBuilder.append("  ") }
            stringBuilder.append(test.testString())
            println(stringBuilder)
            nowCount++
        }

        val stringBuilder = StringBuilder()
        for (i in 0..testsSize) { stringBuilder.append("  ") }
        stringBuilder.append(testString()).append(" failed")
        println(stringBuilder)
        println()
        Thread.currentThread().stackTrace.forEach { println(it) }
        println()

    }

    enum class TestType {
        TEST,
        THEN,
        NOW,
        INVOKE,
    }
}