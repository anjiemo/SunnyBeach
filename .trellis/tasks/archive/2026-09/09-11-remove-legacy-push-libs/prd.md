# 移除历史推送遗留原生库

## 范围与依据

删除 app/libs 下 arm64-v8a、armeabi-v7a、armeabi、x86 的 libcocklogic-1.1.3.so 和 libtnet-3.1.14.so，共 8 个文件。保留 JNI 配置及当前 SDK 依赖。

这些文件随 22e319c3 初始提交进入仓库。原友盟 Push/Agoo/ACCS/Tnet 依赖在 ae22c759 中删除，二进制遗留至今。原 agoo_tnet4android:3.1.14.10 的 SpdyAgent 明确引用 tnet-3.1.14；cocklogic 含阿里守护进程相关符号，确切加载方未能确认。当前 Debug DEX 未检出对应推送类和加载名称。

## 验收

- [x] Debug APK 构建成功。
- [x] APK 不含两个遗留库，其余原生库条目保持一致。
- [x] 记录体积变化及运行验证限制。

不运行访问生产 API 的开发者测试，不修改用户未跟踪文件。

## 验证结果

`gradlew.bat :app:assembleDebug --console=plain` 成功，退出码 0，耗时 7 秒。检查 APK ZIP 条目，两个库均不存在，其余 17 个原生库名称及未压缩大小不变。APK 为 47,049,741 字节，较删除前减少 704,832 字节（约 688 KiB，含打包对齐变化）。`git diff --check` 通过。

尚未执行真机启动、统计及分享回归。仅验证构建与打包，不声称运行路径已验证。
