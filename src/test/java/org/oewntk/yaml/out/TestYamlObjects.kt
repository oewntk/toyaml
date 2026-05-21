/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.*
import org.oewntk.ser.`in`.LibTestsSerCommon
import org.oewntk.ser.`in`.LibTestsSerCommon.ps
import org.oewntk.yaml.out.YamlDump.Companion.autoDumperOptions
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class TestYamlObjects {

    val dumper = YamlDump(options = autoDumperOptions)

    @Test
    fun testDummyEmptyLex() {
        val lex = Lex("jest", "n").apply { senseKeys = mutableListOf() }
        val yamlString = dumper.dump(lex.toYaml(model.senseResolver))
        println(yamlString)
    }

    @Test
    fun testDummyLex() {
        val lex = Lex("jest", "n", listOf("jest%1:10:00::", "jest%1:04:00::"))
        val yamlString = dumper.dump(lex.toYaml(model.senseResolver))
        println(yamlString)
    }

    @Test
    fun testDummySynset() {
        val synset = Synset(
            "77777777-n",
            'n',
            "domain",
            arrayOf("member1", "member2"),
            arrayOf("definition", "definition2"),
        )
        val yamlString = dumper.dump(synset.toYaml())
        println(yamlString)
    }

    @Test
    fun testSense() {
        val sense = model.senseResolver("jest%1:10:00::")
        val yamlString = dumper.dump(sense.toYaml())
        println(yamlString)
    }

    @Test
    fun testSenses() {
        val someSenses = arrayOf("force%1:07:00::", "force%1:07:01::", "force%1:19:00::")
            .map(model.senseResolver)
            .asSequence()
        val yamlString = dumper.dump(sensesToYaml(someSenses))
        println(yamlString)
    }

    @Test
    fun testSynsets() {
        val someSynsets = arrayOf("05042508-n", "05201846-n", "11479041-n")
            .map(model.synsetResolver)
            .asSequence()
        val yamlString = dumper.dump(synsetsToYaml(someSynsets))
        println(yamlString)
    }

    @Test
    fun testSomeLexes() {
        val someLexes = arrayOf("force", "lead", "row", "bow", "galore")
            .flatMap(model.lexResolver)
            .asSequence()
        val yamlString = dumper.dump(lexesToYaml(someLexes, model.senseResolver))
        println(yamlString)
    }

    @Test
    fun testLexes2() {
        val someEntries: Sequence<LexEntry> = arrayOf("force", "lead", "row", "bow", "galore")
            .asSequence()
            .map { it to model.lexResolver(it) }
            .map { AbstractMap.SimpleEntry(it.first, it.second) }
        val yamlString = dumper.dump(entriesToYaml(someEntries, model.senseResolver))
        println(yamlString)
    }

    @Test
    fun test100RandomEntries() {
        val someEntries: Sequence<LexEntry> = model.lexEntries
            .drop((1000..100000).random())
            .take(100)
        val yamlString = dumper.dump(entriesToYaml(someEntries, model.senseResolver))
        println(yamlString)
    }

    @Test
    fun testSomeEntries() {
        val someEntries: Sequence<LexEntry> = arrayOf("force", "lead", "row", "bow", "galore")
            .asSequence()
            .map { AbstractMap.SimpleEntry(it, model.lexResolver(it)) }
        val yamlString = dumper.dump(entriesToYaml(someEntries, model.senseResolver))
        println(yamlString)
    }

    @Test
    fun testOrig() {
        assertEquals(origInfo, modelInfo)
    }

    companion object {

        lateinit var origInfo: String

        lateinit var modelInfo: String

        lateinit var model: CoreModel

        @JvmStatic
        @BeforeClass
        fun init() {
            val orig: String = System.getProperty("INFO")!!
            origInfo = File(orig).readText()

            model = checkNotNull(LibTestsSerCommon.model)

            val info = model.info()
            val counts = ModelInfo.counts(model)
            modelInfo = "$info\n$counts"
            ps.println(modelInfo)
            ps.println()

        }
    }
}
