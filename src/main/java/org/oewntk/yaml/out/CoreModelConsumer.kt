/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.oewntk.model.CoreModel
import org.oewntk.model.toOneSerializable
import org.oewntk.model.toSplitSerializable
import org.oewntk.yaml.out.YamlDump.Companion.compatDumperOptions
import org.yaml.snakeyaml.DumperOptions
import java.io.File
import java.io.IOException
import java.util.function.Consumer

/**
 * Main class that serializes the core model.
 *
 * @property outDir output dir
 * @author Bernard Bou
 */
class CoreModelConsumer(
    private val outDir: File,
    val split: Boolean = true,
    val generated: Boolean = false,
    dumperOptions: DumperOptions = compatDumperOptions
) : Consumer<CoreModel> {

    private val yaml = YamlDump(dumperOptions)

    private fun yamlCoreModel(model: CoreModel, dir: File) {
        if (split) {
            model.toSplitSerializable(generated = generated).forEach { (serializable, file) ->
                Tracing.psInfo.printf("[File] %s%n", file)
                val content = yaml.dump(serializable)
                File(dir, file).writeText(content)
            }
        } else {
            val file = File(dir, "oewn.yaml")
            val serializable = model.toOneSerializable()
            val content = yaml.dump(serializable)
            Tracing.psInfo.printf("[File] %s%n", file)
            file.writeText(content)
        }
    }

    override fun accept(model: CoreModel) {
        Tracing.psInfo.printf("[CoreModel] %s%n", model.source)
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        try {
            yamlCoreModel(model, outDir)
        } catch (e: IOException) {
            e.printStackTrace(Tracing.psErr)
        }
    }
}
