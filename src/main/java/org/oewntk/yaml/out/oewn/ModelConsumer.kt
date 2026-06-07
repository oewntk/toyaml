package org.oewntk.yaml.out.oewn

import org.oewntk.model.Model
import org.oewntk.yaml.out.Tracing
import org.oewntk.yaml.out.YamlDump
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
class ModelConsumer(
    private val outDir: File,
    val split: Boolean = true,
    val fileext: String = "yaml",
    val generated: Boolean = false,
    dumperOptions: DumperOptions = YamlDump.compatDumperOptions,
    private val verbose: Boolean = false,
) : Consumer<Model> {

    private val yaml = YamlDump(dumperOptions)

    private fun yamlModel(model: Model, dir: File) {
        val frameMap = model.verbFrames.associate { it.id to it.frame }
        val frameContent = yaml.dump(frameMap)
        val frameFile = File(dir, "frames.$fileext")
        Tracing.psInfo.printf("[File] %s%n", frameFile)
        frameFile.writeText(frameContent)

        val templateMap = model.verbTemplates.associate { it.id to it.template }
        val templateContent = yaml.dump(templateMap)
        val templateFile = File(dir, "templates.$fileext")
        Tracing.psInfo.printf("[File] %s%n", templateFile)
        templateFile.writeText(templateContent)
    }

    override fun accept(model: Model) {
        Tracing.psInfo.println("[Model] $model")
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        CoreModelConsumer(outDir, split = split, fileext = fileext, generated = generated).accept(model)
        try {
            yamlModel(model, outDir)
        } catch (e: IOException) {
            e.printStackTrace(Tracing.psErr)
        }
    }
}