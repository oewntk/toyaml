package org.oewntk.yaml.out

import org.oewntk.model.*
import org.yaml.snakeyaml.Yaml
import java.io.StringWriter

fun CoreModel.toYaml(): String {

    fun Pronunciation.toYaml(): Map<String, Any> {
        return mutableMapOf<String, Any>(
            "value" to value,
        ).apply {
            variety?.let { this["variety"] = it }
        }
    }

    fun Sense.toYaml(): Map<String, Any> {
        return mutableMapOf<String, Any>(
            "id" to senseKey,
            "synset" to synsetId,
        ).apply {
            relations?.forEach { (rel: String, target) ->
                this[rel] = target.toList()
            }
        }
    }

    fun Lex.toYaml(): Map<String, Any> {
        return mutableMapOf<String, Any>(
            "key" to key2,
            "sense" to senseKeys.map { sensesById!![it]!! }.map(Sense::toYaml).toList(),
        ).apply {
            forms?.let { this["form"] = it }
            pronunciations?.let { this["pronunciation"] = it.map(Pronunciation::toYaml).toList() }
            source?.let { this["source"] = it }
        }
    }

    fun Synset.toYaml(): Map<String, Any> {
        return mutableMapOf(
            "id" to synsetId,
            "members" to members.toList(),
        ).apply {
            relations?.forEach { (rel, target) ->
                this[rel] = target.toList()
            }
        }
    }

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

