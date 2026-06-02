/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.Filename
import org.oewntk.model.LibTestGen.genModelSerializables
import org.oewntk.ser.`in`.LibTestsSerCommon.checkOrig
import org.oewntk.ser.`in`.LibTestsSerCommon.model
import org.oewntk.ser.`in`.LibTestsSerCommon.ps
import org.oewntk.yaml.out.YamlDump.Companion.compatDumperOptions

class TestYamlModelSerialize {

    val yaml = YamlDump(options = compatDumperOptions)

    @Test
    fun testModelSerialization() {
        val serialized: Sequence<Pair<Map<String, Any>, Filename>> = genModelSerializables(model)
        serialized.forEach { (data: Map<String, Any>, _: Filename) ->
            val yamlString = yaml.dumpAsMap(data)
            ps.println(yamlString)
        }
    }

    @Test
    fun testOrig() {
        checkOrig()
    }

    companion object {

        @JvmStatic
        @BeforeClass
        fun init() {
            model //eager
        }
    }
}
