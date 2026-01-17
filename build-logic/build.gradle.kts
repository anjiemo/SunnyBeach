// build-logic 根项目
// 这是一个复合构建 (Composite Build)，有独立的构建系统

// 为所有项目注册 clean 任务（如果还没有的话）
allprojects {
    afterEvaluate {
        if (!tasks.names.contains("clean")) {
            tasks.register<Delete>("clean") {
                delete(layout.buildDirectory)
            }
        }
    }
}
