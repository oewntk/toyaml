/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.ser.`in`.LibTestsSerCommon.checkOrig
import org.oewntk.ser.`in`.LibTestsSerCommon.model
import org.oewntk.ser.`in`.LibTestsSerCommon.ps
import org.oewntk.model.LibModelSubset.subset
import org.oewntk.model.lexesDataSerialize
import org.oewntk.model.sensesDataSerialize
import org.oewntk.model.synsetsDataSerialize
import org.oewntk.yaml.out.YamlDump.Companion.compatDumperOptions

class TestYamlModelDataSerialize {

    val yaml = YamlDump(options = compatDumperOptions)

    @Test
    fun testModelSerialization() {
        val (someLexes, someSynsets, someSenses) = model.subset()
        val dataLexes =  someLexes.lexesDataSerialize()
        val dataSynsets = someSynsets.synsetsDataSerialize()
        val dataSenses =  someSenses.sensesDataSerialize()
        val yamlString = yaml.dump(dataLexes, dataSynsets, dataSenses)
        ps.println(yamlString)
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
