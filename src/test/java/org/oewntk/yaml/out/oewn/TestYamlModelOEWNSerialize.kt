package org.oewntk.yaml.out.oewn

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.Filename
import org.oewntk.model.LibTestGen
import org.oewntk.ser.`in`.LibTestsSerCommon
import org.oewntk.yaml.out.YamlDump

class TestYamlModelOEWNSerialize {

    val yaml = YamlDump(options = YamlDump.compatDumperOptions)

    @Test
    fun testModelSerialization() {
        val serialized: Sequence<Pair<Map<String, Any>, Filename>> = LibTestGen.genModelSerializables(LibTestsSerCommon.model)
        serialized.forEach { (data: Map<String, Any>, _: Filename) ->
            val yamlString = yaml.dumpAsMap(data)
            LibTestsSerCommon.ps.println(yamlString)
        }
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