package org.oewntk.yaml.out

import org.oewntk.model.*
import org.yaml.snakeyaml.Yaml
import java.io.StringWriter

/**
 * Pronunciation to YAML
 * Keys if map:
 *  - value
 *  - variety
 */
fun Pronunciation.toYaml(): Any {
    return if (variety == null)
        value
    else
        mapOf(
            "value" to value,
            "variety" to variety
        )
}

/*
 * Example 'Pair<text, source> to YAML
 * @return Text string if source is null a Map otherwise
 * Keys if a map
    text
    source
*/

/**
 * Sense to YAML
 * Keys:
 * - id
 * - synset
 * - adjposition
 * - subcat
 * - sent
 * - <relations>
 */
fun Sense.toYaml(): Map<String, Any> {
    return mutableMapOf<String, Any>(
        "id" to senseKey,
        "synset" to synsetId,
    ).apply {
        adjPosition?.let { this["adjposition"] = it }
        examples?.let { this["sent"] = it.map { it2 -> if (it2.second == null) it2.first else mapOf("text" to it2.first, "source" to it2.second) } }
        verbFrames?.let { this["subcat"] = it }
        relations?.forEach { (rel: String, target) ->
            this[rel] = target.toList()
        }
    }
}

/**
 * Synset to YAML
 * Keys:
 * - members
 * - partOfSpeech
 * - definition
 * - example
 * - usage
 * - wikidata
 * - ili
 * - <relations>
 */

fun Synset.toYaml(): Map<String, Any> {
    return mutableMapOf<String, Any>(
        // "id" to synsetId,
        "partOfSpeech" to partOfSpeech,
        "definition" to definition!!,
        "members" to members.toList(),
        "source" to "${lexfile!!}.yaml",
    ).apply {
        examples?.let { this["example"] = it.map { it2 -> if (it2.second == null) it2.first else mapOf("text" to it2.first, "source" to it2.second) } }
        usages?.let { this["usage"] = it }
        relations?.forEach { (rel, target) ->
            this[rel] = target.toList()
        }
        wikidata?.let { this["wikidata"] = it }
        ili?.let { this["ili"] = it }
    }
}

fun Lex.toYaml(resolver: (SenseKey) -> Sense?): Map<String, Any> {
    return mutableMapOf<String, Any>(
        // "key2" to key2,
        "sense" to senseKeys.map { resolver.invoke(it)!! }.map(Sense::toYaml).toList(),
    ).apply {
        forms?.let { this["form"] = it.map { it2 -> it2 }.toList() }
        pronunciations?.let { this["pronunciation"] = it.map(Pronunciation::toYaml).toList() }
        source?.let { this["source"] = it }
    }
}

fun CoreModel.toFlatYaml(): String {

    fun Lex.toYaml(): Map<String, Any> = toYaml { sensesById!![it]!! }

    // RANGE

    val someLexes = arrayOf("force", "lead", "row", "bow", "galore")
        .flatMap { lexesByLemma!![it]!! }
        .toList()


    val someSenses = arrayOf("force%1:07:00::", "force%1:07:01::", "force%1:19:00::")
        .map { sensesById!![it]!! }
        .toList()

    val whichEntriesAll = lexesByLemma!!
        .asSequence()

    val whichEntriesRandomRange = lexesByLemma!!
        .asSequence()
        .drop(19000)
        .take(200)

    val whichEntriesSome = arrayOf("force", "lead", "row", "bow", "galore")
        .asSequence()
        .map { it to lexesByLemma!![it]!! }


    val whichSynsetsAll = synsets

    val whichSynsetsSome = arrayOf("05042508-n", "05201846-n", "11479041-n")
        .map { synsetsById!![it]!! }
        .toList()

    val whichEntries = whichEntriesSome
    val whichSynsets = whichSynsetsSome

    // MAIN

    val yEntries = mutableMapOf<String, Any>().apply {
        whichEntries.forEach { (lemma, lexes) ->
            this[lemma] = lexes.associate { it.key2 to it.toYaml() }
        }
    }

    val ySynsets = whichSynsets.associate { it.synsetId to it.toYaml() }

    val yLexes = someLexes
        .map(Lex::toYaml)
        .toList()

    val ySenses = someSenses
        .map(Sense::toYaml)
        .toList()

    val writer = StringWriter()
    Yaml().apply {
        dump(yEntries, writer)
        dump(ySynsets, writer)

        //dump(yLexes, writer)
        //dump(ySenses, writer)
    }
    return writer.toString()
}

fun CoreModel.toYaml(): Sequence<Pair<String, String>> {
    fun Lex.toYaml(): Map<String, Any> = toYaml { sensesById!![it]!! }

    return sequence {
        lexes
            .groupBy { it.source }
            .forEach { (file, lexes) ->
                val whichEntries = lexes.groupBy { it.lemma }
                val yEntries = mutableMapOf<String, Any>().apply {
                    whichEntries.forEach { (lemma, lexes) ->
                        this[lemma] = lexes.associate { it.key2 to it.toYaml() }
                    }
                }
                val writer = StringWriter()
                Yaml().apply {
                    dump(yEntries, writer)
                }
                val content = writer.toString()
                yield(content to file!!) // write content to source.yaml
            }

        synsets
            .groupBy { it.lexfile }
            .forEach { (lexfile, synsets) ->
                val whichSynsets = synsets
                val ySynsets = whichSynsets.associate { it.synsetId to it.toYaml() }
                val writer = StringWriter()
                Yaml().apply {
                    dump(ySynsets, writer)
                }
                val content = writer.toString()
                yield(content to "$lexfile.yaml") // write content to source.yaml
            }
    }
}