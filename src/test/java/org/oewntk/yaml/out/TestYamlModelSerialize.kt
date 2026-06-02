/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.CoreModel
import org.oewntk.model.Filename
import org.oewntk.model.LibModelSubset.lexSubset
import org.oewntk.model.LibModelSubset.synsetSubset
import org.oewntk.model.LibTestGen.genModelSerializables
import org.oewntk.model.SData
import org.oewntk.model.toSerializable
import org.oewntk.ser.`in`.LibTestsSerCommon.checkOrig
import org.oewntk.ser.`in`.LibTestsSerCommon.model
import org.oewntk.ser.`in`.LibTestsSerCommon.ps
import org.oewntk.yaml.out.YamlDump.Companion.compatDumperOptions

class TestYamlModelSerialize {

    val yaml = YamlDump(options = compatDumperOptions)

    @Test
    fun testModelSerialization() {
        val serialized: Sequence<Pair<SData, Filename>> = genModelSerializables(model)
        serialized.forEach { (sdata: SData, _: Filename) ->
            val yamlString = yaml.dumpAsMap(sdata)
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
