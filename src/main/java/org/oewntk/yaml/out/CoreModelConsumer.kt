/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.oewntk.model.CoreModel
import org.oewntk.yaml.out.YamlDump.Companion.compatDumperOptions
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
class CoreModelConsumer(
    private val dir: File,
    val split: Boolean = true,
    val generated: Boolean = false,
    val dumperOptions: DumperOptions = compatDumperOptions
) : Consumer<CoreModel> {

    private fun yamlCoreModel(model: CoreModel, dir: File) {
        if (split) {
            model.toSplitYaml(options = dumperOptions, generated = generated).forEach { (content, file) ->
                Tracing.psInfo.printf("[File] %s%n", file)
                File(dir, file).writeText(content)
            }
        } else {
            val file = File(dir, "oewn.yaml")
            val content = model.toFlatYaml(options = dumperOptions)
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
