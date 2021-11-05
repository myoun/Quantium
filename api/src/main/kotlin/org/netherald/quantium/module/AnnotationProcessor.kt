package org.netherald.quantium.module

import org.netherald.quantium.module.config.*
import org.yaml.snakeyaml.Yaml
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.tools.StandardLocation
import kotlin.reflect.KClass


class AnnotationProcessor : AbstractProcessor() {

    private lateinit var filer : Filer

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        val yaml = Yaml()
        val createConfig = fun (data : Any) =
            filer.createResource(StandardLocation.SOURCE_OUTPUT, yaml.dump(data), ModuleConfigPath.FILE_NAME)

        val out = HashMap<String, Any>()

        annotations.forEach { typeElement ->
            out[ModuleConfigPath.MAIN] = typeElement.qualifiedName.toString()
            typeElement.getAnnotation(QuantiumModuleMark::class.java)?.let { mark ->

                out[ModuleConfigPath.NAME] = mark.name

                typeElement.getAnnotation(
                    Depend::class.java)?.let { out[ModuleConfigPath.DEPEND] = it.values }

                typeElement.getAnnotation(
                    SoftDepend::class.java)?.let { out[ModuleConfigPath.SOFT_DEPEND] = it.values }

                typeElement.getAnnotation(
                    PluginDepend::class.java)?.let { out[ModuleConfigPath.PLUGIN_DEPEND] =  it.values }

                typeElement.getAnnotation(
                    PluginSoftDepend::class.java)?.let { out[ModuleConfigPath.PLUGIN_SOFT_DEPEND] =  it.values }

                typeElement.getAnnotation(
                    Libraries::class.java)?.let { out[ModuleConfigPath.LIBRARIES] = it.values }
            }
        }

        createConfig(out)
        return true
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return HashSet<String>().apply {
            val add = fun KClass<*>.() = this@apply.add(this.java.canonicalName)
            Depend::class.add()
            SoftDepend::class.add()
            PluginDepend::class.add()
            PluginSoftDepend::class.add()
            Libraries::class.add()
            QuantiumModuleMark::class.add()
        }
    }
}