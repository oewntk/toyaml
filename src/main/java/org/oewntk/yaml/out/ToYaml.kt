package org.oewntk.yaml.out

import org.oewntk.model.*
import org.oewntk.yaml.out.YamlDump.Companion.blockDumperOptions
import org.yaml.snakeyaml.DumperOptions

class ToYaml(options: DumperOptions = blockDumperOptions) {

    val dumper = YamlDump(options)

    /**
     * Pronunciation to YAML
     *
     * @param pronunciation pronunciation
     * @return YAML string
     */
    fun toYaml(pronunciation: Pronunciation): String = dumper.dump(pronunciation.toSerializable())

    /**
     * Lex to YAML string
     *
     * @param lex lex
     * @param resolver senseKey to sense resolver
     * @return YAML string
     */
    fun toYaml(lex: Lex, resolver: (SenseKey) -> Sense?): String = dumper.dump(lex.toSerializable(resolver))

    /**
     * Sense to YAML string
     *
     * @param sense sense
     * @return YAML string
     */
    fun toYaml(sense: Sense): String = dumper.dump(sense.toSerializable())

    /**
     * Synset to YAML string
     *
     * @param synset synset
     * @return YAML string
     */
    fun toYaml(synset: Synset): String = dumper.dump(synset.toSerializable())

    /**
     * Entries to YAML string
     *
     * @param entries entries sequence of entries
     * @param resolver senseKey to sense resolver
     * @return YAML string
     */
    fun entriesToYaml(entries: Sequence<LexEntry>, resolver: (SenseKey) -> Sense?): String = dumper.dump(entries.toSerializable(resolver))

    /**
     * Lexes to YAML string
     *
     * @param lexes lexes sequence of lexes
     * @param resolver senseKey to sense resolver
     * @return YAML string
     */
    fun lexValuesToYaml(lexes: Sequence<Lex>, resolver: (SenseKey) -> Sense?): String = dumper.dump(lexes.asValuesToSerializable(resolver))

    /**
     * Lexes to YAML string
     *
     * @param lexes lexes sequence of lexes
     * @return YAML string
     */
    fun lexesToYaml(lexes: Sequence<Lex>): String = dumper.dump(lexes.asEntriesToSerializable())

    /**
     * Senses to YAML string
     *
     * @param senses senses sequence of senses
     * @return YAML string
     */
    fun sensesToYaml(senses: Sequence<Sense>): String = dumper.dump(senses.toSerializable())

    /**
     * Synsets to YAML string
     * @param synsets sequence of synsets
     * @return YAML string
     */
    fun synsetsToYaml(synsets: Sequence<Synset>): String = dumper.dump(synsets.toSerializable())

    // M O D E L

    /**
     * Flat YAML producer
     *
     * @param whichLexes which lexes to select, by default all
     * @param whichSynsets which synsets, by default all
     * @receiver core model
     * @return YAML string
     */
    fun toFlatYaml(
        model: CoreModel,
        whichLexes: Sequence<Lex> = model.lexes.asSequence().sortedWith(compareBy(Lex::lemma).thenBy(Lex::key2)),
        whichSynsets: Sequence<Synset> = model.synsets.asSequence().sortedBy { it.synsetId },
    ): String {
        val (yLexes, ySynsets) = model.toFlatSerializableOfLexes(whichLexes = whichLexes, whichSynsets = whichSynsets)
        return dumper.dump(yLexes.entries + ySynsets.entries)
    }

    /**
     * Split YAML generator
     *
     * @receiver core model
     * @yield YAML string to file
     */
    fun toSplitYaml(
        model: CoreModel,
        generated: Boolean = false
    ): Sequence<Pair<String, String>> {
        return model.toSplitSerializable(generated = generated)
            .map { (data, filename) ->
                dumper.dump(data) to "$filename.yaml"
            }
    }
}