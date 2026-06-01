/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.ser.`in`.LibTestsSerCommon.ps
import org.oewntk.yaml.out.YamlDump.Companion.compatDumperOptions

class TestYamlOutput {

    val yaml = YamlDump(options = compatDumperOptions)

    fun genTestMap() = mutableMapOf(
        "a" to "r",
        "b" to mutableListOf("s", "t"),
        "c" to mutableMapOf(
            "m" to "u",
            "n" to "v"
        ),
        "d" to mutableMapOf(
            "m" to "u",
            "n" to mutableListOf("v", "w"),
            "p" to mutableMapOf(
                "m" to "u",
                "n" to mutableListOf("v", "w"),
            )
        ),
    )

    fun visitMap(m: MutableMap<String, Any>): Any {
        return _visitMap(m, 0)
    }

    fun _visitMap(item: Any?, level: Int): Any {
        return when (item) {
            is String -> {
                "$item#"
            }

            is List<*> -> {
                item
                    .map {
                        _visitMap(it, level + 1)
                    }
                    .toList()
            }

            is MutableMap<*, *> -> {
                item
                    .mapKeys {
                        _visitMap(it.key, level + 1)
                    }
                    .mapValues {
                        _visitMap(it.value, level + 1)
                    }
                    .apply {
                        val m: MutableMap<Any, Any> = (this as MutableMap<Any, Any>)
                        m["+x"] = "+y"
                    }
            }

            else -> throw IllegalArgumentException(item.toString())
        }
    }

    @Test
    fun testMap() {
        val yamlString = yaml.dump(genTestMap())
        ps.println(yamlString)
    }

    @Test
    fun testMapVisit() {
        val m = visitMap(genTestMap())
        val yamlString = yaml.dump(m)
        ps.println(yamlString)
    }

    private fun genSenses(i: Int, j: Int, n: Int = 3) = Array(n) {
        mapOf(
            "id" to "sk-$i-$j-$it",
            "synset" to "sy-$i-$j-$it",
            "relation1" to "sk-$i-$j-$it",
            "relation2" to "sk-$i-$j-${it + 1}",
        )
    }.toList()

    private fun genLexes(i: Int, n: Int = 3) = Array(n) { j ->
        mapOf(
            "pronunciation" to "a:ha:",
            "sense" to genSenses(i, j, n = 3)
        )
    }

    private fun genEntries() = (1 until 5).associate { i ->
        "lemma-$i" to mapOf(
            "pos-1" to genLexes(i),
            "pos-2" to genLexes(i + 1, n = 1),
            "pos-3" to genLexes(i + 3, n = 2),
        )
    }

    @Test
    fun testOEWN() {
        val m = genEntries()
        val yamlString = yaml.dump(m)
        ps.println(yamlString)
    }

    companion object {

        @JvmStatic
        @BeforeClass
        fun init() {
        }
    }
}
