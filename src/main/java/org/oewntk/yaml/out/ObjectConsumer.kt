package org.oewntk.yaml.out

import org.oewntk.model.CoreModel
import org.oewntk.model.SerializationMode
import org.yaml.snakeyaml.DumperOptions
import java.io.PrintStream
import java.util.function.BiConsumer

/**
 * Class that serializes an object to a YAML string
 *
 * @author Bernard Bou
 */
open class ObjectTransformer(
    val mode: SerializationMode = SerializationMode.DATA,
    dumperOptions: DumperOptions = YamlDump.compatDumperOptions,
    noCast: Boolean = true,
    val leaveRedundantRelation: Boolean = false,
) : (Any, CoreModel) -> String {

    private val yaml = YamlDump(dumperOptions, noCast = noCast)

    override fun invoke(obj: Any, model: CoreModel): String {

        val serializable = mode.serialize(obj, model.senseResolver, leaveRedundantRelation = leaveRedundantRelation)
        return yaml.dump(serializable)
    }
}

open class ObjectConsumer(
    val ps: PrintStream,
    mode: SerializationMode = SerializationMode.DATA,
    dumperOptions: DumperOptions = YamlDump.compatDumperOptions,
    noCast: Boolean = true,
    leaveRedundantRelation: Boolean = false,
) : ObjectTransformer(mode = mode, dumperOptions = dumperOptions, noCast = noCast, leaveRedundantRelation = leaveRedundantRelation), BiConsumer<Any, CoreModel> {

    override fun accept(obj: Any, model: CoreModel) {
        val str = super.invoke(obj, model)
        ps.println(str)
    }
}
