//@file:DependsOn("jakarta.persistence:jakarta.persistence-api:3.1.0")
//
//import java.io.File
//import java.net.URLClassLoader
//import kotlin.reflect.full.declaredMemberProperties
//import kotlin.reflect.full.hasAnnotation
//import javax.persistence.Entity
//
//tasks.register("scanModels") {
//    doLast {
//        val packageName = "com.example.rces.models"
//        val packagePath = packageName.replace('.', '/')
//        val buildDir = project.layout.buildDirectory.get().asFile
//
//        val classesDir = File(buildDir, "classes/kotlin/main")
//
//        val classLoader = URLClassLoader(arrayOf(classesDir.toURI().toURL()), Thread.currentThread().contextClassLoader)
//        val packageDir = File(classesDir, packagePath)
//
//        val classFiles = packageDir.walkTopDown().filter { it.extension == "class" }.toList()
//
//        println("=== Сущностей в $packageName ===")
//
//        classFiles.forEach { file ->
//            val relativePath = file.relativeTo(classesDir).path.removeSuffix(".class")
//            val className = relativePath.replace(File.separatorChar, '.')
//            try {
//                val clazz = classLoader.loadClass(className).kotlin
//                 if (!clazz.hasAnnotation<Entity>()) return@forEach
//
//                println("• ${clazz.simpleName}")
//
//                clazz.declaredMemberProperties.forEach { prop ->
//                    println("   - ${prop.name}: ${prop.returnType}")
//                }
//            } catch (e: Throwable) {
//                println("   [!] Failed to load class $className — ${e.message}")
//            }
//        }
//    }
//}