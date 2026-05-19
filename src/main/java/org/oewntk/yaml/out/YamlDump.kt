package org.oewntk.yaml.out

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.StringWriter

class YamlDump(val options: DumperOptions = DumperOptions()) {

    val yaml = Yaml(options)

    fun dump(vararg objects: Any): String = StringWriter()
        .apply {
            objects.forEach {
                yaml.dump(it, this)
            }
        }.toString()

    fun dump(file: String = "-", vararg objects: Any) {
        val result = dump(*objects)
        if ("-" == file)
            print(result)
        else
            File(file).writeText(result)
    }
}