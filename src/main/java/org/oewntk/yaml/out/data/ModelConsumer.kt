package org.oewntk.yaml.out.data

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
    dumperOptions: DumperOptions = YamlDump.compatDumperOptions,
    private val verbose: Boolean = false,
) : Consumer<Model> {

    private val yaml = YamlDump(dumperOptions)

    private fun yamlModel(model: Model, dir: File) {
        val frameMap = model.verbFrames.associate { it.id to it.frame }
        val frameContent = yaml.dump(frameMap)
        val templateMap = model.verbTemplates.associate { it.id to it.template }
        val templateContent = yaml.dump(templateMap)
        if (split) {
            val frameFile = File(dir, "frames.$fileext")
            Tracing.psInfo.printf("[File] %s%n", frameFile)
            frameFile.writeText(frameContent)
            val templateFile = File(dir, "templates.$fileext")
            Tracing.psInfo.printf("[File] %s%n", templateFile)
            templateFile.writeText(templateContent)
        } else {
            val frameAndTemplateFile = File(dir, "frames_templates.$fileext")
            Tracing.psInfo.printf("[File] %s%n", frameAndTemplateFile)
            frameAndTemplateFile.writeText(frameContent + "\n\n" + templateContent)
        }
    }

    override fun accept(model: Model) {
        Tracing.psInfo.println("[Model] $model")
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        CoreModelConsumer(outDir, split = split, fileext = fileext).accept(model)
        try {
            yamlModel(model, outDir)
        } catch (e: IOException) {
            e.printStackTrace(Tracing.psErr)
        }
    }
}