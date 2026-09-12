# ChatImage Continued

这是 CoGitOwl 维护的 ChatImage 分支，保留 kitUIN 的原始版权与 MIT 许可证。开发分支为 `continued/1.19-26.2`，所有工作仅提交到此 fork。

目标是覆盖 Minecraft Java 1.19 至 26.2 的 Fabric、Forge、Quilt、NeoForge，并跟进后续正式版。适配正在进行中；具体进度以 `maintenance/targets.json` 为准，不代表全部目标已可用于整合包。

## 当前验证

- Fabric / Quilt：26.1、26.1.1、26.1.2、26.2 已构建。
- Fabric：26.1.2、26.2 已通过客户端图片加载与悬浮渲染测试。
- Quilt：26.2 已在真正的 Quilt 0.30.1 上通过相同客户端测试。
- NeoForge：26.2.0.82 已通过构建和客户端测试。26.2.0.87 的开发环境在编译 Minecraft `HolderSet` 时失败，因此当前固定使用官方示例采用的 26.2.0.82。
- Forge：26.2-65.1.3 已构建，并使用实际发布 JAR 通过客户端测试。
- Fabric：1.19.1 至 1.21.11 的全部现有构建目标已通过编译。1.21.11 使用新的聊天点击注入点。
- Quilt：1.19.1 至 1.21.11 的 14 个构建目标全部通过编译；1.21.11 已通过真实加载器启动、Mixin 审计及 PNG 纹理注册测试。
- NeoForge：1.21.8、1.21.10、1.21.11 已通过客户端图片渲染测试；1.21.9、26.1、26.1.1、26.1.2 已构建。
- Forge：26.1、26.1.2、1.21.8、1.21.9、1.21.10 已构建；1.21.11 已生成 SRG 发行包，并在开发环境通过客户端图片渲染测试。安装版 SRG JAR 的完整启动测试尚未完成。
- 其他旧版本继承上游的源码生成机制，正在逐项重新验证。尚未完成跨加载器联机文件传输、专用服务器和所有游戏补丁版本的测试。

NeoForge 没有对应 1.19 系列和 1.20 的正式加载器，不能为这些版本生成 NeoForge 模组。

Forge 也没有发布 1.20.5 和 1.21.2 的加载器；这些游戏版本只能使用当时存在的其他加载器。

## 安装

每个 Minecraft 实例只放入一个对应版本、对应加载器的 ChatImage JAR。26.x 使用 Java 25；Fabric / Quilt 还需要对应游戏版本的 Fabric API。Quilt 从 26.1 起使用 Fabric API，不再使用已停止更新的 QFAPI。Mod Menu 为可选配置入口，也可以通过 End 键打开设置。

modern 目录下的版本已内置 ChatImageCode，并直接维护 `show_chatimage` 的聊天事件适配，不再需要单独安装 ActionLib。旧版依赖仍按上游各版本的元数据执行，Mod Menu 已改为可选编译依赖。

## 构建

26.x 每个目标都有独立 Gradle 项目，例如：

```sh
cd modern/fabric-26.2
bash gradlew build
```

可把目录中的 `fabric` 换成 `quilt`、`forge` 或 `neoforge`，游戏版本见 `maintenance/targets.json`。产物在该项目的 `build/libs/`。不要安装 `-sources.jar` 或测试用的 `ChatImage-smoke-*.jar`。

1.20.5 至 1.21.11 的新 Forge / NeoForge 项目也位于 `modern/`，使用 Java 21。Forge 1.x 安装包必须使用 `-srg.jar`。

旧版先在仓库根目录以 Java 21 运行 `bash init.sh` 生成源码，再进入 `fabric/fabric-*`、`forge/forge-*` 或 `neoforge/neoforge-*` 构建。旧版 Forge 构建使用 Java 17，Fabric / NeoForge 构建使用 Java 21。ForgeGradle 7 的开发启动器额外需要可被 Gradle 找到的 Java 8；游戏本身按版本使用 Java 17、21 或 25。

旧版 Quilt 与 Fabric 共用源码，通过 `bash gradlew build -Pquilt` 生成 Quilt 标记的包。可用 `bash gradlew runSmoke -Pquilt` 验证实际 Quilt 运行；省略 `-Pquilt` 可验证 Fabric。旧版测试覆盖启动、Mixin 与 PNG 纹理注册。

`maintenance/collect_artifacts.py --loader <加载器> --target <构建目标>` 只收集可安装的发行包，并生成 SHA-256 校验文件。

## 客户端回归测试

Fabric / Quilt 26.x：`bash gradlew runSmoke`。

Forge / NeoForge 26.x：`bash gradlew runClient -Psmoke`。

测试会打开单独的开发客户端，审计 Mixin，往返编码 `show_chatimage`，加载临时 PNG，注册纹理，执行真实悬浮图片渲染，然后关闭客户端。成功必须产生 `run-smoke/smoke-result.txt` 中的 `OK:`；Gradle 会在结果缺失或失败时报告错误。该测试代码使用独立 source set，不会打包进正式模组。测试不覆盖完整多人联机功能。

## 维护结构

`modern/common` 放共享图片、配置界面、聊天与网络数据代码；`modern/game-26.1`、`modern/game-26.2` 处理游戏界面所有权的变化；`modern/fabric`、`modern/forge`、`modern/neoforge` 处理加载器生命周期与网络接口。Fabric 和 Quilt 使用同一功能实现，并分别启动验证。

GitHub Actions 会按明确的目标矩阵构建。当前 fork 的 Actions 因 GitHub 账号 billing lock 无法开始运行，本次验证在本地完成。此状态不改变任何模组编译结果；启用云端构建前需要解除 GitHub 的账号限制。

后续版本通过本次 Codex 任务中的定期维护检查跟进（每周三 10:00，Asia/Shanghai）。任务检查正式版与官方加载器版本，有变化时继续适配与验证，只在有结果或实质性阻碍时通知。由于任务使用本地仓库，电脑和应用需要处于运行状态；该机制不保证新游戏版本发布当天即可完成适配。
