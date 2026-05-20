/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.oewntk.model.CoreModel
import org.yaml.snakeyaml.DumperOptions
import java.io.File
import java.io.IOException
import java.util.function.Consumer

/**
 * Main class that serializes the core model.
 *
 * @property dir output dir
 * @author Bernard Bou
 */
class CoreModelConsumer(private val dir: File, val split: Boolean = true, val generated: Boolean = false
) : Consumer<CoreModel> {

    private fun yamlCoreModel(model: CoreModel, dir: File) {
        val options = DumperOptions().apply{
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK // DumperOptions.FlowStyle.FLOW, DumperOptions.FlowStyle.AUTO
            defaultScalarStyle = DumperOptions.ScalarStyle.PLAIN // DumperOptions.ScalarStyle.SINGLE_QUOTED, DumperOptions.ScalarStyle.DOUBLE_QUOTED
            isPrettyFlow = true
            width = 80
            indent = 2
        }
        if (split) {
            model.toSplitYaml(options = options, generated = generated).forEach { (content, file) ->
                Tracing.psInfo.printf("[File] %s%n", file)
                File(dir, file).writeText(content)
            }
        } else {
            val file = File(dir, "oewn.yaml")
            val content = model.toFlatYaml(options = options)
            Tracing.psInfo.printf("[File] %s%n", file)
            file.writeText(content)
        }
    }

    override fun accept(model: CoreModel) {
        Tracing.psInfo.printf("[CoreModel] %s%n", model.source)
        try {
            yamlCoreModel(model, dir)
        } catch (e: IOException) {
            e.printStackTrace(Tracing.psErr)
        }
    }
}
