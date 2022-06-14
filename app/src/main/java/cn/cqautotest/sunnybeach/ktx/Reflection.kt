package cn.cqautotest.sunnybeach.ktx

inline fun <reified T : Annotation> Annotation.getCustomAnnotation(): T? = annotationClass.java.getAnnotation(T::class.java)
