# StepTracker

一个基于 Jetpack Compose 的实时计步与运动轨迹记录应用，包含本地单元测试、Android Instrumentation 测试，以及在 GitHub Actions 上自动运行的测试工作流。

## 功能概览
- 通过 `Sensor.TYPE_STEP_COUNTER` 实时读取用户步数。
- 使用 Fused Location Provider 获取高精度定位，记录运动路径与累计距离。
- 自定义画布轨迹视图 + 实时步数/距离卡片 + 权限状态提示。
- 权限管理（运动识别 + 精准定位），界面内一键请求。

## 模块结构
- `sensor/StepCounterDataSource`：封装传感器监听。
- `location/TrailTracker`：封装定位数据流。
- `model/TrailMath`、`projection/PathProjector`：核心计算与坐标投影。
- `viewmodel/StepTrackerViewModel`：状态管理与协程流收集。
- `ui/StepTrackerScreen`：Compose UI。

## 构建与测试
1. 安装 **JDK 17** 与 **Android SDK (API 34, build-tools 34.0.0)**。
2. 运行单元测试：
   ```bash
   ANDROID_SDK_ROOT=/path/to/android-sdk ./gradlew testDebugUnitTest
   ```
3. 运行 Instrumentation 测试（需启动模拟器或真机）：
   ```bash
   ANDROID_SDK_ROOT=/path/to/android-sdk ./gradlew connectedDebugAndroidTest
   ```

## GitHub Actions
工作流文件位于 `.github/workflows/android.yml`，包含：
- `unit-tests`：在 Ubuntu + Temurin JDK 17 环境执行 `./gradlew testDebugUnitTest`。
- `instrumented-tests`：依赖前一步，通过 `reactivecircus/android-emulator-runner` 启动 API 34 模拟器并执行 `./gradlew connectedDebugAndroidTest`。

所有任务都会自动接受 SDK 许可并缓存 Gradle 依赖，保证 PR/push 时自动验证。 
