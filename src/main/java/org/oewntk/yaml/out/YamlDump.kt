package org.oewntk.yaml.out

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.StringWriter

class YamlDump(val options: DumperOptions = compatDumperOptions) {

    val yaml = Yaml(options)

    fun dump(vararg objects: Any): String = StringWriter()
        .apply {
            objects.forEach {
                yaml.dump(it, this)
            }
        }.toString()

    fun dump(file: String = "-", vararg objects: Any) {
        val result = dump(*objects)
        if ("-" == file)
            print(result)
        else
            File(file).writeText(result)
    }

    companion object {
        val defaultDumperOptions = DumperOptions()
        val compatDumperOptions = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            defaultScalarStyle = DumperOptions.ScalarStyle.PLAIN // DumperOptions.ScalarStyle.SINGLE_QUOTED, DumperOptions.ScalarStyle.DOUBLE_QUOTED
            isPrettyFlow = true
            width = 80
            indent = 2
        }
        val autoDumperOptions = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.AUTO
            defaultScalarStyle = DumperOptions.ScalarStyle.PLAIN // DumperOptions.ScalarStyle.SINGLE_QUOTED, DumperOptions.ScalarStyle.DOUBLE_QUOTED
            isPrettyFlow = true
            width = 80
            indent = 2
        }
        val flawDumperOptions = DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.FLOW
            defaultScalarStyle = DumperOptions.ScalarStyle.PLAIN // DumperOptions.ScalarStyle.SINGLE_QUOTED, DumperOptions.ScalarStyle.DOUBLE_QUOTED
            isPrettyFlow = true
            width = 80
            indent = 2
        }
    }
}