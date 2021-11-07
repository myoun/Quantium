package org.netherald.quantium.module

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import org.netherald.quantium.module.config.*
import org.yaml.snakeyaml.Yaml

class Processor(
    val codeGenerator: CodeGenerator
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val symbols = resolver.getSymbolsWithAnnotation(QuantiumModuleMark::class.java.name).apply {
                if (toList().size != 1) throw Exception("Wrong QuantiumModuleMark Count. count : ${toList().size}")
            }
        val ret = symbols.filter { it.validate() }
        symbols.filter {
            it is KSClassDeclaration || it.validate()
        }.map {
            it.accept(Visitor(), Unit)
        }
        return ret.toList()
    }

    inner class Visitor() : KSVisitorVoid() {
        @Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
        @OptIn(KspExperimental::class)
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            if (classDeclaration.classKind != ClassKind.CLASS) {
                throw Exception("QuantiumModuleMark can't be on not class target")
            }
            val out = HashMap<String, Any>()
            @Suppress("ReplaceGetOrSet", "UNCHECKED_CAST")
            val values = fun KSAnnotation.() = arguments.get(0).value as Array<out String>
            classDeclaration.annotations.forEach {
                if (it.shortName.asString().startsWith("org.netherald.quantium.module.config")) {
                    if (it.shortName.asString() == QuantiumModuleMark::class.java.name) {
                        @Suppress("ReplacePutWithAssignment")
                        out.put(ModuleConfigPath.NAME, it.arguments[0].value as String)
                        return@forEach
                    }
                    if (it.values().isNotEmpty()) {
                        out[ModuleAnnotationMap[it.shortName.getShortName()]!!] = it.values()
                    }
                }
            }
            println(out)
            codeGenerator.createNewFile(
                Dependencies(true), "", ModuleConfigPath.FILE_NAME, "yml"
            ).write(Yaml().dump(out).toByteArray())

        }
    }
}

class ProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Processor(environment.codeGenerator)
    }
}