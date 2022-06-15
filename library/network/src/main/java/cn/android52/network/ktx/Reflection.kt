package cn.android52.network.ktx

inline fun <reified T : Annotation> Annotation.getCustomAnnotationOrNull(): T? = annotationClass.java.getAnnotation(T::class.java)
