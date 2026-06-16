/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.LibTestGen.genLexEntries
import org.oewntk.model.LibTestGen.genSynsetEntries
import org.oewntk.ser.`in`.LibTestsSerCommon.ps
import org.oewntk.yaml.out.YamlDump.Companion.compatDumperOptions

class TestYamlOutput {

    val yaml = YamlDump(options = compatDumperOptions)

    @Test
    fun testEntries() {
        val m = genLexEntries()
        val yamlString = yaml.dump(m)
        ps.println(yamlString)
    }

    @Test
    fun testSynsets() {
        val m = genSynsetEntries()
        val yamlString = yaml.dump(m)
        ps.println(yamlString)
    }

    // companion object {
    //     @JvmStatic
    //     @BeforeClass
    //     fun init() {
    //     }
    // }
}
