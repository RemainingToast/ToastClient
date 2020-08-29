package dev.toastmc.client.module

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class ModuleManifest(val label: String, val aliases: Array<String> = [], val description: String = "", val usage: String = "", val category: Category = Category.NONE, val hidden: Boolean = false, val persistent: Boolean = false, val enabled: Boolean = false, val key: Int = -1) 