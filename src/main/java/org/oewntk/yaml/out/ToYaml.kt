package org.oewntk.yaml.out

import org.oewntk.model.*
import org.oewntk.yaml.out.YamlDump.Companion.blockDumperOptions
import org.yaml.snakeyaml.DumperOptions
import kotlin.sequences.map
import kotlin.sequences.toList

class ToYaml(options: DumperOptions = blockDumperOptions) {

    private val dumper = YamlDump(options)

    fun dump(vararg objects: Any) = dumper.dump(objects)

    fun dumpAsMap(map: Map<*,*>) = dumper.dumpAsMap(map)

    /**
     * Pronunciation to YAML
     *
     * @param pronunciation pronunciation
     * @return YAML string
     */
    fun toYaml(pronunciation: Pronunciation): String = dump(pronunciation.toSerializable())

    /**
     * Lex to YAML string
     *
     * @param lex lex
     * @param resolver senseKey to sense resolver
     * @return YAML string
     */
    fun toYaml(lex: Lex, resolver: (SenseKey) -> Sense?): String = dump(lex.toSerializable(resolver))

    /**
     * Sense to YAML string
     *
     * @param sense sense
     * @return YAML string
     */
    fun toYaml(sense: Sense): String = dump(sense.toSerializable())

    /**
     * Synset to YAML string
     *
     * @param synset synset
     * @return YAML string
     */
    fun toYaml(synset: Synset): String = dump(synset.toSerializable())

    /**
     * Lexes to YAML string
     *
     * @param lexes lexes sequence of lexes
     * @return YAML string
     */
    fun lexesToYaml(lexes: Sequence<Lex>, resolver: (SenseKey) -> Sense?): List<String> = lexes.map { dump(it.toSerializable(resolver)) }.toList()

    /**
     * Senses to YAML string
     *
     * @param senses senses sequence of senses
     * @return YAML string
     */
    fun sensesToYaml(senses: Sequence<Sense>): List<String> = senses.map { dump(it.toSerializable()) }.toList()

    /**
     * Synsets to YAML string
     * @param synsets sequence of synsets
     * @return YAML string
     */
    fun synsetsToYaml(synsets: Sequence<Synset>): List<String> = synsets.map { dump(it.toSerializable()) }.toList()

    // M O D E L

    /**
     * Flat YAML producer
     *
     * @param whichLexes which lexes to select, by default all
     * @param whichSynsets which synsets, by default all
     * @receiver core model
     * @return YAML string
     */
    fun toRawYaml(
        model: CoreModel,
        whichLexes: Sequence<Lex> = model.lexes.asSequence().sortedWith(compareBy(Lex::lemma).thenBy(Lex::key2)),
        whichSynsets: Sequence<Synset> = model.synsets.asSequence().sortedBy { it.synsetId },
    ): String {
        val (yLexes, ySynsets) = model.asData(whichLexes = whichLexes, whichSynsets = whichSynsets)
        return dump(yLexes.entries + ySynsets.entries)
    }

    /**
     * Split YAML generator
     *
     * @receiver core model
     * @yield YAML string to file
     */
    fun toSplitSerializedYaml(
        model: CoreModel,
        generated: Boolean = false
    ): Sequence<Pair<String, String>> {
        return model.toSplitSerializable(generated = generated)
            .map { (data, filename) ->
                dump(data) to "$filename.yaml"
            }
    }
}