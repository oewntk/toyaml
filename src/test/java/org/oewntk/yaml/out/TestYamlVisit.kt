/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.LibTestGen.genDummyMap
import org.oewntk.model.LibVisitTransform.visit
import org.oewntk.ser.`in`.LibTestsSerCommon.ps
import org.oewntk.yaml.out.YamlDump.Companion.compatDumperOptions

class TestYamlVisit {

    val yaml = YamlDump(options = compatDumperOptions)

    val m = genDummyMap()
    val m2 = visit(m)

    @Test
    fun testMap() {
        val yamlString = yaml.dump(m)
        ps.println(yamlString)
    }

    @Test
    fun testVisitedMap() {
        val yamlString = yaml.dump(m2)
        ps.println(yamlString)
    }

    //companion object {
    //    @JvmStatic
    //    @BeforeClass
    //    fun init() {
    //    }
    //}
}
