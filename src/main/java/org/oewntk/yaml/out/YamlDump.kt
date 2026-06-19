package org.oewntk.yaml.out

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Represent
import org.yaml.snakeyaml.representer.Representer
import java.io.File
import java.io.StringWriter

class YamlDump(val options: DumperOptions = compatDumperOptions, noCast: Boolean = true) {

    val yaml = if (! noCast)
        Yaml(options)
    else
        Yaml(object : Representer(options) {
            init {
                this.representers[LinkedHashSet::class.java] = Represent { data ->
                    representSequence(Tag.SEQ, data as Iterable<*>, options.defaultFlowStyle)
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