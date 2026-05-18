/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.oewntk.model.CoreModel
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.IOException
import java.io.StringWriter
import java.util.function.Consumer

/**
 * Main class that serializes the core model.
 *
 * @property dir output dir
 * @author Bernard Bou
 */
class CoreModelConsumer(private val dir: File) : Consumer<CoreModel> {

    private fun yamlCoreModel(model: CoreModel, dir: File) {
        model.toYaml().forEach { (content, file) ->
            File(dir, file).writeText(content)
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
