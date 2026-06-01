/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.*
import org.oewntk.ser.`in`.LibTestsSerCommon.model
import org.oewntk.ser.`in`.LibTestsSerCommon.ps
import org.oewntk.yaml.out.YamlDump.Companion.compatDumperOptions
import java.io.File
import java.util.*
import kotlin.test.assertEquals

class TestYamlObjects {

    val yaml = ToYaml(options = compatDumperOptions)

    @Test
    fun testDummyEmptyLex() {
        val lex = Lex("jest", "n").apply { senseKeys = mutableListOf() }
        val yamlString = yaml.toYaml(lex, model.senseResolver)
        ps.println(yamlString)
    }

    @Test
    fun testDummyLex() {
        val lex = Lex("jest", "n", listOf("jest%1:10:00::", "jest%1:04:00::"))
        val yamlString = yaml.toYaml(lex, model.senseResolver)
        ps.println(yamlString)
    }

    @Test
    fun testDummySynset() {
        val synset = Synset(
            "77777777-n",
            SynsetType.N,
            "domain",
            arrayOf("member1", "member2"),
            arrayOf("definition", "definition2"),
        )
        val yamlString = yaml.toYaml(synset)
        ps.println(yamlString)
    }

    @Test
    fun testSense() {
        val sense = model.senseResolver("jest%1:10:00::")
        val yamlString = yaml.toYaml(sense)
        ps.println(yamlString)
    }

    @Test
    fun testSenses() {
        val someSenses = arrayOf("force%1:07:00::", "force%1:07:01::", "force%1:19:00::")
            .map(model.senseResolver)
            .asSequence()
        val yamlString = yaml.sensesToYaml(someSenses).joinToString("\n\n")
        ps.println(yamlString)
    }

    @Test
    fun testSynsets() {
        val someSynsets = arrayOf("05042508-n", "05201846-n", "11479041-n")
            .map(model.synsetResolver)
            .asSequence()
        val yamlString = yaml.synsetsToYaml(someSynsets).joinToString(separator = "\n\n")
        ps.println(yamlString)
    }

    @Test
    fun testLexes() {
        val someLexes = arrayOf("force", "lead", "row", "bow", "galore")
            .flatMap(model.lexResolver)
            .asSequence()
        val yamlString = yaml.lexesToYaml(someLexes, model.senseResolver).joinToString("\n\n")
        ps.println(yamlString)
    }

    @Test
    fun testEntries() {
        val someEntries: Sequence<LexEntry> = arrayOf("force", "lead", "row", "bow", "galore")
            .asSequence()
            .map { AbstractMap.SimpleEntry(it, model.lexResolver(it)) }
        val yamlString = yaml.entriesToYaml(someEntries, model.senseResolver)
        ps.println(yamlString)
    }

    @Test
    fun testPairEntries() {
        val someEntries: Sequence<LexEntry> = arrayOf("force", "lead", "row", "bow", "galore")
            .asSequence()
            .map { it to model.lexResolver(it) }
            .map { AbstractMap.SimpleEntry(it.first, it.second) }
        val yamlString = yaml.entriesToYaml(someEntries, model.senseResolver)
        ps.println(yamlString)
    }

    @Test
    fun test100RandomEntries() {
        val someEntries: Sequence<LexEntry> = model.lexEntries
            .drop((1000..100000).random())
            .take(100)
        val yamlString = yaml.entriesToYaml(someEntries, model.senseResolver)
        ps.println(yamlString)
    }

    @Test
    fun testOrig() {
        val orig: String = System.getProperty("INFO")!!
        val origInfo = File(orig).readText()
        val info = model.info()
        val counts = ModelInfo.counts(model)
        val modelInfo = "$info\n$counts"
        ps.println(modelInfo)
        assertEquals(origInfo, modelInfo)
    }

    companion object {

        @JvmStatic
        @BeforeClass
        fun init() {
            model // eager
        }
    }
}
