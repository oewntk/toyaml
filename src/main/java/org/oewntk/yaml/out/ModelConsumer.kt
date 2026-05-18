/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.oewntk.model.Model
import java.io.File
import java.io.IOException
import java.util.function.Consumer

/**
 * Main class that serializes the model
 *
 * @property dir output dir
 * @author Bernard Bou
 */
class ModelConsumer(private val dir: File) : Consumer<Model> {

    private fun yamlCoreModel(model: Model, dir: File) {
        model.toYaml().forEach { (content, file) ->
            File(dir, file).writeText(content)
        }
    }

    override fun accept(model: Model) {
        Tracing.psInfo.printf("[Model] %s%n", model.sources.contentToString())
        try {
            yamlCoreModel(model, dir)
        } catch (e: IOException) {
            e.printStackTrace(Tracing.psErr)
        }
    }
}
