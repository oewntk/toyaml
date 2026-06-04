package org.oewntk.yaml.out.data

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.LibModelSubset.subset
import org.oewntk.model.toLexesData
import org.oewntk.model.toSensesData
import org.oewntk.model.toSynsetsData
import org.oewntk.ser.`in`.LibTestsSerCommon
import org.oewntk.yaml.out.YamlDump

class TestYamlModelDataSerialize {

    val yaml = YamlDump(options = YamlDump.compatDumperOptions)

    @Test
    fun testModelSerialization() {
        val (someLexes, someSynsets, someSenses) = LibTestsSerCommon.model.subset()
        val dataLexes =  someLexes.toLexesData()
        val dataSynsets = someSynsets.toSynsetsData()
        val dataSenses =  someSenses.toSensesData()
        val jsonLexesString = yaml.dump(dataLexes)
        val jsonSynsetsString = yaml.dump(dataSynsets)
        val jsonSensesString = yaml.dump(dataSenses)
        LibTestsSerCommon.ps.println(jsonLexesString)
        LibTestsSerCommon.ps.println(jsonSynsetsString)
        LibTestsSerCommon.ps.println(jsonSensesString)
    }

    @Test
    fun testOrig() {
        LibTestsSerCommon.checkOrig()
    }

    companion object {

        @JvmStatic
        @BeforeClass
        fun init() {
            LibTestsSerCommon.model //eager
        }
    }
}