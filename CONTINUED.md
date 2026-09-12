# ChatImage Continued

CoGitOwl 维护的 ChatImage 分支，保留 kitUIN 的版权与 MIT 许可证。开发分支为 [`continued/1.19-26.2`](https://github.com/cogitowl/ChatImage/tree/continued/1.19-26.2)，所有工作仅提交到此 fork。

本分支只维护 **Fabric 和 Quilt**，覆盖 Minecraft Java 1.19 至 26.2，并跟进后续正式版。Forge / NeoForge 已按维护者要求退出当前及后续维护范围；仓库中的相关旧源码仅作历史保留，旧工作流已停用归档。

## 版本与验证

每种加载器有 19 个构建目标，共 38 个发行包。详细状态以 [`maintenance/targets.json`](maintenance/targets.json) 为准。

| 安装包中的游戏版本标记 | 选择安装包时使用的游戏版本 |
| --- | --- |
| 1.19、1.19.1、1.19.2、1.19.3、1.19.4 | 各自对应版本 |
| 1.20 | 1.20、1.20.1、1.20.2 |
| 1.20.3 | 1.20.3、1.20.4 |
| 1.20.5 | 1.20.5、1.20.6 |
| 1.21 | 1.21、1.21.1 |
| 1.21.2 | 1.21.2、1.21.3 |
| 1.21.4、1.21.5 | 各自对应版本 |
| 1.21.6 | 1.21.6、1.21.7、1.21.8 |
| 1.21.9 | 1.21.9、1.21.10 |
| 1.21.11、26.1、26.1.1、26.1.2、26.2 | 各自对应版本 |

同一包声明多个补丁版本的范围继承自上游，不表示已在每个补丁版本逐一运行。1.19 与 1.19.1 的聊天接口不同，本分支为 1.19 单独生成包，并收窄 1.19.1 的声明范围。

1.19.2、1.19.3、1.19.4 继承的元数据使用较宽的 `~` 版本条件；请仍按表中的精确版本选择包。

本次验证在本地完成：

- Fabric / Quilt 的全部目标均进行独立构建，产物收集时检查主类、模组元数据与测试代码隔离，并生成 SHA-256。
- Fabric 26.1.2、26.2 及真实 Quilt 0.30.1 上的 26.2：通过客户端启动、Mixin 审计、聊天图片事件往返编码、PNG 纹理加载及真实悬浮图片渲染测试。
- Fabric / Quilt 1.19，以及 Quilt 1.21.11：通过真实加载器启动、Mixin 审计及 PNG 纹理注册测试。
- 尚未逐一验证所有补丁版本、完整多人联机传输、专用服务器及整合包兼容性。构建通过与客户端测试通过分别记录，不等同于全面运行认证。

## 安装

按上表选择游戏版本，再选择文件名以 `+fabric.jar` 或 `+quilt.jar` 结尾的包。每个游戏实例的 `mods` 文件夹只安装一个对应的 ChatImage 包，不能把整个压缩包中的所有 JAR 一起放进去。

1.19–1.20.4 使用 Java 17，1.20.5–1.21.11 使用 Java 21，26.x 使用 Java 25。安装对应游戏版本的 Fabric 或 Quilt Loader；Quilt 验证使用 0.30.1。

26.x 还需要对应游戏版本的 Fabric API。Quilt 26.x 同样使用 Fabric API。旧版已内置需要的 Fabric API 模块、ChatImageCode 和 ActionLib，不必重复安装这些依赖；其他模组要求完整 Fabric API 时，仍须按其说明安装。

Mod Menu 为可选配置入口，也可通过 End 键打开设置。不要安装 `-sources.jar`、开发 JAR 或 `ChatImage-smoke-*.jar`。

## 构建与测试

旧版先在仓库根目录使用 Java 21 运行 `bash init.sh` 生成源码，再进入对应的 `fabric/fabric-*` 目录：

```sh
bash gradlew build            # Fabric
bash gradlew build -Pquilt    # Quilt
bash gradlew runSmoke         # Fabric 客户端测试
bash gradlew runSmoke -Pquilt # 实际 Quilt 客户端测试
```

旧版发行包位于 `ChatImage-jar/<模组版本>/`。编译输出按游戏版本使用 Java 17 或 21。

26.x 使用 Java 25，进入 `modern/fabric-<游戏版本>` 或 `modern/quilt-<游戏版本>`：

```sh
bash gradlew build
bash gradlew runSmoke
```

26.x 发行包位于各项目 `build/libs/`。客户端测试会启动单独的开发游戏，结束时写入 `run-smoke/smoke-result.txt`；结果缺失或不以 `OK:` 开头时，构建任务失败。测试代码位于独立 source set，不进入发行包。

在仓库根目录使用 `python3 maintenance/collect_artifacts.py --loader fabric --target 26.2` 收集一个可安装包及其 SHA-256；`--loader` 只接受 `fabric`、`quilt`。

完整压缩包可在构建各目标后运行 `python3 maintenance/package_release.py --output dist/ChatImage-Continued-Fabric-Quilt.zip` 生成。该命令按维护清单收集发行包、核对版本声明并附上安装说明和校验文件。

## 后续维护

`modern/common` 放共享图片、配置界面、聊天与网络数据代码；`modern/game-26.1`、`modern/game-26.2` 处理游戏界面差异；`modern/fabric` 供 Fabric / Quilt 共用。旧版继续使用源码模板生成机制。

`.github/workflows/continued.yml` 只构建 Fabric / Quilt。上游工作流保存在 `maintenance/upstream-workflows/`，不再执行。当前 fork 的 GitHub Actions 因账号 billing lock 无法启动，需要解除账号限制后才能使用云端构建。

后续正式版通过本次 Codex 任务每周三 10:00（Asia/Shanghai）的维护检查跟进，仅考虑 Fabric / Quilt。发现变化后继续适配和验证，有结果或实质性阻碍时通知。该维护任务依赖本地电脑和应用运行，不保证新游戏正式版发布当天完成适配。
