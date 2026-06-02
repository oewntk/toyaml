/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.oewntk.model.CoreModel
import org.oewntk.model.dataSerialize
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
class CoreModelDataConsumer(
    private val outDir: File,
    val split: Boolean = true,
    val generated: Boolean = false,
    dumperOptions: DumperOptions = compatDumperOptions
) : Consumer<CoreModel> {

    private val yaml = YamlDump(dumperOptions)

    private fun yamlCoreModel(model: CoreModel, dir: File) {
        val (dataLexes, dataSynsets, dataSenses) = model.dataSerialize()
        val lexContent = yaml.dump(dataLexes)
        val synsetContent = yaml.dump(dataSynsets)
        val senseContent = yaml.dump(dataSenses)

        if (split) {
            var file = File(dir, "oewn-synsets.yaml")
            Tracing.psInfo.printf("[File] %s%n", file)
            file.writeText(lexContent + synsetContent + senseContent)

            file = File(dir, "oewn-lexes.yaml")
            Tracing.psInfo.printf("[File] %s%n", file)
            file.writeText(lexContent + synsetContent + senseContent)

            file = File(dir, "oewn-senses.yaml")
            Tracing.psInfo.printf("[File] %s%n", file)
            file.writeText(lexContent + synsetContent + senseContent)
        } else {
            val file = File(dir, "oewn.yaml")
            Tracing.psInfo.printf("[File] %s%n", file)
            file.writeText(lexContent + "\n\n" + synsetContent + "\n\n" + senseContent)
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
