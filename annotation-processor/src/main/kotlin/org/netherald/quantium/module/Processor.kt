package org.netherald.quantium.module

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import org.netherald.quantium.module.config.*

class Processor : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {

        resolver.getSymbolsWithAnnotation(QuantiumModuleMark::class.java.simpleName)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.validate() }.apply {
                if (toList().size != 1) return emptyList()
            }.map {
                it.accept(Visitor(), Unit)
            }
        return emptyList()
    }

    inner class Visitor : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            if (classDeclaration.classKind != ClassKind.CLASS) {
                throw Exception("QuantiumModuleMark can't be on not class target")
            }
            val out = HashMap<String, Any>()
            @Suppress("ReplaceGetOrSet", "UNCHECKED_CAST")
            val values = fun KSAnnotation.() = arguments.get(0).value as Array<out String>
            classDeclaration.annotations.forEach {
                if (it.shortName.asString().startsWith("org.netherald.quantium.module.config")) {
                    out[ModuleAnnotationMap[it.shortName.getShortName()]!!] = it.values()
                }
            }
            TODO("create file")
        }
    }
}

class ProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Processor()
    }
}