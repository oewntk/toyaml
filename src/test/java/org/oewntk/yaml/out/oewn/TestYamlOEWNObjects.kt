package org.oewntk.yaml.out.oewn

import org.junit.BeforeClass
import org.junit.Test
import org.oewntk.model.*
import org.oewntk.model.Lex.Groups.lexByLemmaThenByKey2
import org.oewntk.model.LibModelSubset.lexSubset
import org.oewntk.model.LibModelSubset.synsetSubset
import org.oewntk.model.MapFactory.synsetsById
import org.oewntk.ser.`in`.LibTestsSerCommon
import org.oewntk.yaml.out.YamlDump
import java.io.File
import kotlin.test.assertEquals

class TestYamlOEWNObjects {

    val yaml = YamlDump(options = YamlDump.compatDumperOptions)

    @Test
    fun testDummyEmptyLex() {
        val lex = Lex(Lemma("jest"), "n").apply { senseKeys = mutableListOf() }
        val serializable: Map<String, Any> = lex.toOEWNDataValue(LibTestsSerCommon.model.senseResolver)
        val yamlString = yaml.dumpAsMap(serializable)
        LibTestsSerCommon.ps.println(yamlString)
    }

    @Test
    fun testDummyLex() {
        val lex = Lex(Lemma("jest"), "n", listOf(SenseKey("jest%1:10:00::"), SenseKey("jest%1:04:00::")))
        val serializable: Map<String, Any> = lex.toOEWNDataValue(LibTestsSerCommon.model.senseResolver)
        val yamlString = yaml.dumpAsMap(serializable)
        LibTestsSerCommon.ps.println(yamlString)
    }

    @Test
    fun testDummySynset() {
        val synset = Synset(
            SynsetId("77777777-n"),
            SynsetType.N,
            "domain",
            setOf(Lemma("member1"), Lemma("member2")),
            listOf("definition", "definition2"),
        )
        val serializable: Map<String, Any> = synset.toOEWNDataValue()
        val yamlString = yaml.dumpAsMap(serializable)
        LibTestsSerCommon.ps.println(yamlString)
    }

    @Test
    fun testSense() {
        val sense = LibTestsSerCommon.model.senseResolver(SenseKey("jest%1:10:00::"))
        val serializable: Map<String, Any> = sense.toOEWNData()
        val yamlString = yaml.dumpAsMap(serializable)
        LibTestsSerCommon.ps.println(yamlString)
    }

    @Test
    fun testSenses() {
        val someSenses = arrayOf(SenseKey("force%1:07:00::"), SenseKey("force%1:07:01::"), SenseKey("force%1:19:00::"))
            .map(LibTestsSerCommon.model.senseResolver)
            .asSequence()
        val serializables: Sequence<Map<String, Any>> = someSenses.map { it.toOEWNData() }
        val yamlStrings: Sequence<String> = serializables.map { yaml.dumpAsMap(it) }
        LibTestsSerCommon.ps.println(yamlStrings.joinToString("\n\n"))
    }

    @Test
    fun testSynset() {
        val synset: Synset = LibTestsSerCommon.model.synsetResolver(SynsetId("05042508-n"))
        val serializable: Map<String, Any> = synset.toOEWNDataValue()
        val yamlString = yaml.dumpAsMap(serializable)
        LibTestsSerCommon.ps.println(yamlString)
    }

    @Test
    fun testSynsets() {
        val someSynsets = arrayOf(SynsetId("05042508-n"), SynsetId("05201846-n"), SynsetId("11479041-n"))
            .map(LibTestsSerCommon.model.synsetResolver)
            .asSequence()
        val serializables: Sequence<Map<String, Any>> = someSynsets.map { it.toOEWNDataValue() }
        val yamlStrings: Sequence<String> = serializables.map { yaml.dumpAsMap(it) }
        LibTestsSerCommon.ps.println(yamlStrings.joinToString("\n\n"))
    }

    @Test
    fun testRandomSynsets() {
        val someSynsets: Sequence<Synset> = LibTestsSerCommon.model.synsetSubset().asSequence()
        val serializables: Sequence<Map<String, Any>> = someSynsets.map { it.toOEWNDataValue() }
        val yamlStrings: Sequence<String> = serializables.map { yaml.dumpAsMap(it) }
        LibTestsSerCommon.ps.println(yamlStrings.joinToString("\n\n"))
    }

    @Test
    fun testLex() {
        val lex: Lex = LibTestsSerCommon.model.lexResolver1(Lemma("jest"), Key2("n"))
        val serializable: Map<String, Any> = lex.toOEWNDataValue(LibTestsSerCommon.model.senseResolver)
        val yamlString = yaml.dumpAsMap(serializable)
        LibTestsSerCommon.ps.println(yamlString)
    }

    @Test
    fun testLexes() {
        val someLexes = arrayOf(Lemma("force"), Lemma("lead"), Lemma("row"), Lemma("bow"), Lemma("galore"))
            .flatMap(LibTestsSerCommon.model.lexResolver)
            .asSequence()
        val serializables: Sequence<Map<String, Any>> = someLexes.map { it.toOEWNDataValue(LibTestsSerCommon.model.senseResolver) }
        val yamlStrings: Sequence<String> = serializables.map { yaml.dumpAsMap(it) }
        LibTestsSerCommon.ps.println(yamlStrings.joinToString("\n\n"))
    }

    @Test
    fun testRandomLexes() {
        val someLexes: Sequence<Lex> = LibTestsSerCommon.model.lexSubset().asSequence()
        val serializables: Sequence<Map<String, Any>> = someLexes.map { it.toOEWNDataValue(LibTestsSerCommon.model.senseResolver) }
        val yamlStrings: Sequence<String> = serializables.map { yaml.dumpAsMap(it) }
        LibTestsSerCommon.ps.println(yamlStrings.joinToString("\n\n"))
    }

    @Test
    fun testSomeLexesByLemmaThenByKey2() {
        val someLexes: Sequence<Lex> = LibTestsSerCommon.model.lexSubset(howMany = 5).asSequence()
        val map: HyperMap1 = someLexes.lexByLemmaThenByKey2()
        val serializedMap: Map<String, Any> = map.toOEWNData(LibTestsSerCommon.model.senseResolver)
        val yamlString = yaml.dumpAsMap(serializedMap)
        LibTestsSerCommon.ps.println(yamlString)
    }

    @Test
    fun testSomeSynsetsBySynsetId() {
        val someSynsets: Sequence<Synset> = LibTestsSerCommon.model.synsetSubset(howMany = 5).asSequence()
        val map: Map<SynsetId, Synset> = someSynsets.synsetsById()
        val serializedMap: Map<SynsetId, Any> = map.toOEWNData()
        val yamlString = yaml.dumpAsMap(serializedMap)
        LibTestsSerCommon.ps.println(yamlString)
    }

    @Test
    fun testOrig() {
        val orig: String = System.getProperty("INFO")!!
        val origInfo = File(orig).readText()
        val info = LibTestsSerCommon.model.info()
        val counts = ModelInfo.counts(LibTestsSerCommon.model)
        val modelInfo = "$info\n$counts"
        LibTestsSerCommon.ps.println(modelInfo)
        assertEquals(origInfo, modelInfo)
    }

    companion object {

        @JvmStatic
        @BeforeClass
        fun init() {
            LibTestsSerCommon.model // eager
        }
    }
}