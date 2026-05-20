/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.oewntk.model.Model
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
class ModelConsumer(
    private val dir: File,
    val split: Boolean = true,
    val generated: Boolean = false,
    val dumperOptions: DumperOptions = compatDumperOptions
) : Consumer<Model> {

    private fun yamlModel(model: Model, dir: File) {
        val frameMap = model.verbFrames.associate { it.id to it.frame }
        val frameContent = YamlDump(dumperOptions).dump(frameMap)
        val frameFile = File(dir, "frames.yaml")
        Tracing.psInfo.printf("[File] %s%n", frameFile)
        frameFile.writeText(frameContent)

        val templateMap = model.verbTemplates.associate { it.id to it.template }
        val templateContent = YamlDump(dumperOptions).dump(templateMap)
        val templateFile = File(dir, "templates.yaml")
        Tracing.psInfo.printf("[File] %s%n", templateFile)
        templateFile.writeText(templateContent)
    }

    override fun accept(model: Model) {
        Tracing.psInfo.printf("[Model] %s%n", model.source)
        CoreModelConsumer(dir, split = split, generated = generated).accept(model)
        try {
            yamlModel(model, dir)
        } catch (e: IOException) {
            e.printStackTrace(Tracing.psErr)
        }
    }
}
