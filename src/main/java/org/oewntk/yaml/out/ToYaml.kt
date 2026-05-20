package org.oewntk.yaml.out

import org.oewntk.model.*
import org.oewntk.model.InverseRelationFactory.INVERSE_SENSE_RELATIONS_SET
import org.oewntk.model.InverseRelationFactory.INVERSE_SYNSET_RELATIONS_SET
import org.yaml.snakeyaml.DumperOptions

typealias LexEntry = Map.Entry<Lemma, Collection<Lex>>

const val INCLUDE_LEXFILE = false

/**
 * Pronunciation to YAML
 * @return map
 * Keys:
 *  - value
 *  - variety
 */
fun Pronunciation.toYaml(): Map<String, Any> {
    return mutableMapOf("value" to value)
        .apply {
            variety?.let { this["variety"] = it }
        }
}

/*
 * Example (Pair<text, source>) to YAML
 * @return text string if source is null a map otherwise
 * Keys if a map
    text
    source
*/

/**
 * Lex to YAML map
 * @param resolver senseKey to sense resolver
 * @return map
 * Keys:
 * - form
 * - pronunciation
 * - sense
 * - source
 */
fun Lex.toYaml(resolver: (SenseKey) -> Sense?): Map<String, Any> {
    return mutableMapOf<String, Any>(
        "sense" to senseKeys.map { resolver.invoke(it)!! }.map(Sense::toYaml).toList(),
        // "key2" to key2,
    ).apply {
        forms?.let { this["form"] = it.map { it2 -> it2 }.toList() }
        pronunciations?.let { this["pronunciation"] = it.map(Pronunciation::toYaml).toList() }
        if (INCLUDE_LEXFILE) lexfile.let { this["lexfile"] = it }
    }.toSortedMap()
}

/**
 * Sense to YAML map
 * @return map
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
        relations
            ?.filterNot { it.key in INVERSE_SENSE_RELATIONS_SET }
            ?.forEach { (rel: String, target) ->
                this[rel] = target.toList()
            }
    }.toSortedMap()
}

/**
 * Synset to YAML map
 * @return map
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
        "definition" to listOf(definition!!),
        "members" to members.toList(),
    ).apply {
        examples?.let { this["example"] = it.map { it2 -> if (it2.second == null) it2.first else mapOf("source" to it2.second, "text" to it2.first) } }
        usages?.let { this["usage"] = it }
        relations
            ?.filterNot { it.key in INVERSE_SYNSET_RELATIONS_SET }
            ?.forEach { (rel, target) ->
                this[rel] = target.toList()
            }
        wikidata?.let { if (it.isNotEmpty()) this["wikidata"] = if (it.size == 1) it[0] else it }
        ili?.let { this["ili"] = it }
        source?.let { this["source"] = it }
        if (INCLUDE_LEXFILE) this["lexfile"] = "${lexfile!!}.yaml"
    }.toSortedMap()
}

/**
 * Entries to YAML map
 * @param entries entries
 * @param resolver senseKey to sense resolver
 * @return map of YAML entries by lemma
 */
fun entriesToYaml(entries: Sequence<LexEntry>, resolver: (SenseKey) -> Sense?): Map<String, Any> {
    return mutableMapOf<String, Any>().apply {
        entries.forEach { (lemma, lexes) ->
            this[lemma] = lexes.associate { it.key2 to it.toYaml(resolver) }
        }
    }
}

/**
 * Lexes to YAML map
 * @param lexes lexes
 * @param resolver senseKey to sense resolver
 * @return list of YAML lexes
 */
fun lexesToYaml(lexes: Sequence<Lex>, resolver: (SenseKey) -> Sense?): List<Any> {
    fun Lex.toYaml(): Map<String, Any> = toYaml { resolver.invoke(it)!! }
    return lexes
        .map(Lex::toYaml)
        .toList()
}

/**
 * Senses to YAML map
 * @param senses senses
 * @return list of YAML sense
 */
fun sensesToYaml(senses: Sequence<Sense>): List<Any> {
    return senses
        .map(Sense::toYaml)
        .toList()
}

/**
 * Synsets to YAML map
 * @param synsets
 * @return map of YAML synset by id
 */
fun synsetsToYaml(synsets: Sequence<Synset>): Map<SynsetId, Any> {
    return synsets.associate { it.synsetId to it.toYaml() }
}

/**
 * Flat YAML producer
 *
 * @param whichEntries which entries to select, by default all
 * @param whichSynsets which synsets, by default all
 * @param options dump options
 * @receiver core model
 * @return content
 */
fun CoreModel.toFlatYaml(
    whichEntries: Sequence<LexEntry> = lexesByLemma!!.asSequence().sortedBy { it.key },
    whichSynsets: Sequence<Synset> = synsets.asSequence().sortedBy { it.synsetId },
    options: DumperOptions = DumperOptions()
): String {
    val yEntries = entriesToYaml(whichEntries) { sensesById!![it]!! }
    val ySynsets = synsetsToYaml(whichSynsets)
    return YamlDump(options).dump(yEntries, ySynsets)
}

/**
 * Split YAML generator
 *
 * @param options dump options
 * @receiver core model
 * @yield content to file
 */
fun CoreModel.toSplitYaml(options: DumperOptions = DumperOptions(), generated: Boolean = false): Sequence<Pair<String, String>> {
    fun Lex.toYaml(): Map<String, Any> = toYaml { sensesById!![it]!! }

    val dumper = YamlDump(options)
    return sequence {
        lexes
            .groupBy {
                if (generated) {
                    if (it.generated) "entries-generated.yaml" else it.lexfile
                } else it.lexfile
            }
            .forEach { (file, lexes) ->
                val yEntries = mutableMapOf<String, Any>().apply {
                    lexes
                        .sortedBy { it.lemma }
                        .groupBy { it.lemma }
                        .forEach { (lemma, lexes) ->
                            this[lemma] = lexes.associate { it.key2 to it.toYaml() }
                        }
                }
                val content = dumper.dump(yEntries)
                yield(content to file) // write content to source.yaml
            }

        synsets
            .sortedBy { it.synsetId }
            .groupBy { it.lexfile }
            .forEach { (lexfile, synsets) ->
                val ySynsets = synsets.associate { it.synsetId to it.toYaml() }
                val content = dumper.dump(ySynsets)
                yield(content to "$lexfile.yaml") // write content to source.yaml
            }
    }
}