/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.yaml.out

import org.oewntk.model.CoreModel
import org.oewntk.model.DataCoreModel
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.IOException
import java.io.StringWriter
import java.util.function.Consumer

/**
 * Main class that serializes the core model.
 *
 * @property file output file
 * @param prettyPrintFlag pretty print output
 * @author Bernard Bou
 */
@Suppress("unused")
class CoreModelConsumer(private val file: File, prettyPrintFlag: Boolean = false) : Consumer<CoreModel> {

    val writer = StringWriter()

    val yaml = Yaml()

    private fun yamlCoreModel(model: CoreModel, file: File) {
        val yamlString = model.toYaml()
        println(yamlString)
        File(file, "test.yaml").writeText(yamlString)
     }

    override fun accept(model: CoreModel) {
        Tracing.psInfo.printf("[CoreModel] %s%n", model.source)
        try {
            yamlCoreModel(model, file)
        } catch (e: IOException) {
            e.printStackTrace(Tracing.psErr)
        }
    }
}
