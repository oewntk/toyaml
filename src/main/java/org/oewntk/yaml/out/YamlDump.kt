package org.oewntk.yaml.out

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Represent
import org.yaml.snakeyaml.representer.Representer
import java.io.File
import java.io.StringWriter
import org.oewntk.model.*

class YamlDump(val options: DumperOptions = compatDumperOptions, noCast: Boolean = true) {

    val yaml = if (!noCast)
        Yaml(options)
    else
        Yaml(object : Representer(options) {
            init {
                this.representers[LinkedHashSet::class.java] = Represent { data ->
                    representSequence(Tag.SEQ, data as Iterable<*>, options.defaultFlowStyle)
                }
                this.representers[SynsetId::class.java] = Represent { data ->
                    representScalar(Tag.STR, (data as SynsetId).id)
                }
                this.representers[SenseKey::class.java] = Represent { data ->
                    representScalar(Tag.STR, (data as SenseKey).id)
                }
                this.representers[PronunciationValue::class.java] = Represent { data ->
                    representScalar(Tag.STR, (data as PronunciationValue).ipa)
                }
            }
        }, options)


    fun dump(vararg objects: Any): String = StringWriter()
        .apply {
            objects.forEach {
                yaml.dump(it, this)
            }
        }.toString()

    fun dumpAsMap(map: Map<*, *>): String = yaml.dumpAsMap(map)

    fun dump(file: String = "-", vararg objects: Any) {
        val result = dump(*objects)
        if ("-" == file)
            print(result)
        else
            File(file).writeText(result)
    }

    companion object {

        // Option	        Description	D                                                               Default
        // ____________________________________________________________________________________________________
        // defaultStyle	    Default scalar style (plain, single-quoted, double-quoted, literal, folded)	PLAIN
        // defaultFlowStyle	Default collection style (flow, block, auto)	                            AUTO
        // canonical	    Whether to produce canonical YAML output	                                false
        // allowUnicode	    Whether to allow Unicode characters in output	                            true
        // indent	        Number of spaces for indentation	                                        2
        // width	        Preferred line width for wrapping	                                        80
        // lineBreak	    Line break style (UNIX, WIN, MAC)	                                        UNIX
        // splitLines	    Whether to split long lines	                                                true
        // explicitStart	Whether to include "---" at the start	                                    false
        // explicitEnd	    Whether to include "..." at the end	                                        false
        // prettyFlow	    Whether to add line breaks in flow styles	                                false

        val defaultDumperOptions = DumperOptions()

        val autoDumperOptions = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.AUTO
            defaultScalarStyle = DumperOptions.ScalarStyle.PLAIN // DumperOptions.ScalarStyle.SINGLE_QUOTED, DumperOptions.ScalarStyle.DOUBLE_QUOTED
            isPrettyFlow = true
            width = 80
            indent = 2
        }

        val blockDumperOptions = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            defaultScalarStyle = DumperOptions.ScalarStyle.PLAIN // DumperOptions.ScalarStyle.SINGLE_QUOTED, DumperOptions.ScalarStyle.DOUBLE_QUOTED
            isPrettyFlow = true
            width = 80
            indent = 2
        }

        val flowDumperOptions = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.FLOW
            defaultScalarStyle = DumperOptions.ScalarStyle.PLAIN // DumperOptions.ScalarStyle.SINGLE_QUOTED, DumperOptions.ScalarStyle.DOUBLE_QUOTED
            isPrettyFlow = true
            width = 80
            indent = 2
        }

        /**
         * Everything Might Get Quoted:
         * Because we force ScalarStyle.DOUBLE_QUOTED, numbers and booleans will often end up wrapped in quotes (e.g., "30" instead of 30),
         * depending on the exact version of SnakeYAML and how the data types are passed.
         * This technically violates strict JSON types, though many JSON parsers will handle it.
         */
        val jsonDumperOptions = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.FLOW
            defaultScalarStyle = DumperOptions.ScalarStyle.DOUBLE_QUOTED
            isPrettyFlow = true // Makes it readable rather than a single lineblockDumperOptions
            width = 80
            indent = 2
        }
        val compatDumperOptions = blockDumperOptions
    }
}