import cn.cqautotest.sunnybeach.configureProjectConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 项目全局配置约定插件
 * 用于替代旧的 configs.gradle.kts，提供统一的服务器环境和第三方 Key 配置
 */
class ProjectConfigConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configureProjectConfig()
    }
}
