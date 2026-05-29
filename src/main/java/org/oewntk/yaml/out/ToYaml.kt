package org.oewntk.yaml.out

import org.oewntk.model.*
import org.oewntk.yaml.out.YamlDump.Companion.compatDumperOptions
import org.yaml.snakeyaml.DumperOptions

const val INCLUDE_LEXFILE = false

class ToYaml(options: DumperOptions = compatDumperOptions) {

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
    fun entriesToYaml(entries: Sequence<LexEntry>, resolver: (SenseKey) -> Sense?): String = dumper.dump(entriesToSerializable(entries, resolver))

    /**
     * Lexes to YAML string
     *
     * @param lexes lexes sequence of lexes
     * @param resolver senseKey to sense resolver
     * @return YAML string
     */
    fun lexValuesToYaml(lexes: Sequence<Lex>, resolver: (SenseKey) -> Sense?): String = dumper.dump(lexesAsValuesToSerializable(lexes, resolver))

    /**
     * Lexes to YAML string
     *
     * @param lexes lexes sequence of lexes
     * @return YAML string
     */
    fun lexesToYaml(lexes: Sequence<Lex>): String = dumper.dump(lexesAsEntriesToSerializable(lexes))

    /**
     * Senses to YAML string
     *
     * @param senses senses sequence of senses
     * @return YAML string
     */
    fun sensesToYaml(senses: Sequence<Sense>): String = dumper.dump(sensesToSerializableList(senses))

    /**
     * Synsets to YAML string
     * @param synsets sequence of synsets
     * @return YAML string
     */
    fun synsetsToYaml(synsets: Sequence<Synset>): String = dumper.dump(synsetsToSerializable(synsets))

    // M O D E L

    /**
     * Flat YAML producer
     *
     * @param whichEntries which entries to select, by default all
     * @param whichSynsets which synsets, by default all
     * @receiver core model
     * @return YAML string
     */
    fun toFlatYaml(
        model: CoreModel,
        whichEntries: Sequence<LexEntry> = model.lexEntries.sortedBy { it.key },
        whichSynsets: Sequence<Synset> = model.synsets.asSequence().sortedBy { it.synsetId },
    ): String {
        val (yEntries, ySynsets) = model.toFlatSerializable(whichEntries = whichEntries, whichSynsets = whichSynsets)
        return dumper.dump(yEntries, ySynsets)
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