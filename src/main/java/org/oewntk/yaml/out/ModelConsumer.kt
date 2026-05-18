/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.oewntk.model.DataModel
import org.oewntk.model.Model
import java.io.File
import java.io.IOException
import java.util.function.Consumer

/**
 * Main class that serializes the model
 *
 * @property file output file
 * @param prettyPrintFlag pretty print output
 * @author Bernard Bou
 */
class ModelConsumer(private val file: File, prettyPrintFlag: Boolean = false) : Consumer<Model> {

    private fun yamlCoreModel(model: Model, file: File) {
        val yamlString = model.toYaml()
        // println(yamlString)
        File(file, "test2.yaml").writeText(yamlString)
        // throw NotImplementedError()
    }

    override fun accept(model: Model) {
        Tracing.psInfo.printf("[Model] %s%n", model.sources.contentToString())
        try {
            yamlCoreModel(model, file)
        } catch (e: IOException) {
            e.printStackTrace(Tracing.psErr)
        }
    }
}
