package org.oewntk.yaml.out.oewn

import org.oewntk.model.CoreModel
import org.oewntk.model.toOneOEWNData
import org.oewntk.model.toSplitOEWNData
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
class CoreModelConsumer(
    private val outDir: File,
    val split: Boolean = true,
    val fileext: String = "yaml",
    val generated: Boolean = false,
    dumperOptions: DumperOptions = YamlDump.compatDumperOptions,
    private val verbose: Boolean = false,
) : Consumer<CoreModel> {

    private val yaml = YamlDump(dumperOptions)

    private fun yamlCoreModel(model: CoreModel, dir: File) {
        if (split) {
            model.toSplitOEWNData(generated = generated).forEach { (serializable, file) ->
                Tracing.psInfo.printf("[File] %s%n", file)
                val content = yaml.dump(serializable)
                File(dir, "file.$fileext").writeText(content)
            }
        } else {
            val file = File(dir, "oewn.$fileext")
            val serializable = model.toOneOEWNData().toList()
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