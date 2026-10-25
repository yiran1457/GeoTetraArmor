# GeoTetraArmor

基于 Tetra 的模块化护甲模组（Forge 1.20.1）。

## 支持的 Tetra 版本

本工程用 [Stonecutter](https://stonecutter.kikugie.dev/) 维护两个版本节点：

| 节点 | Tetra | mutil | 附带内容 |
| --- | --- | --- | --- |
| `6.9.0` | 6.9.0 | 6.2.0 | Tetra Material Overhaul（jarJar 打包）、ExtraHoloPage 兼容 |
| `6.17.0` | 6.17.0 | 6.3.0 | 无（这两者都没有 6.17 可用构建） |

## 构建

```bash
# 构建指定节点
gradlew :6.9.0:build
gradlew :6.17.0:build

# 两个节点的产物汇总到 build/libs/<mod_version>/
gradlew buildAndCollect
```

产物文件名带节点后缀，例如 `GeoTetraArmor-6.9.0-1.0.5-6.17.0.jar`，两个 jar 可以共存。

## 运行

```bash
gradlew :6.9.0:runClient
gradlew :6.17.0:runClient
gradlew :6.17.0:runServer
```

专用服务端运行会自动排除 ExtraHoloPage（它是纯客户端 mod，构造时会访问 `net.minecraft.client.Options`，
如果不排除会让整个服务端加载失败）。手动停用它可加 `-Pwith_ehp=false`。

## 切换 active 节点

Stonecutter 用「active 版本」决定根目录 `src/` 里条件注释的展开状态：

```bash
gradlew "Set active project to 6.17.0"
gradlew "Set active project to 6.9.0"
gradlew "Reset active project"     # 提交前还原到 6.9.0
```

也可以 `gradlew stonecutterIdea` 生成对应的 IDEA 运行配置。**提交前请先 `Reset active project`**，
避免把某个节点的展开结果当成源码提交。

## 新增一个版本节点

1. 在 `settings.gradle.kts` 的 `versions(...)` 里加一项。
2. 新建 `versions/<节点>/gradle.properties`，至少包含 `tetra_file`、`mutil_file`、`geckolib_file`、
   `with_tmo`、`with_ehp`，以及 MC/Forge/parchment 等共用字段。
3. 若新版本的 Tetra API 又有变化，在 `build.gradle.kts` 里用 `sc.eval(sc.current.version, "...:...") `
   分支依赖，并在代码里用 `//? if <版本>` 条件注释收口。

## 写代码时的三条规则

1. **Tetra 模型类型一律走 `ModuleModelAccess`。**
   Tetra 6.9 用 `se.mickelus.tetra.module.data.ModuleModel`，6.10 起换成
   `se.mickelus.tetra.module.model.IModuleModel`。所有模型处理只允许经过
   `model/ModuleModelAccess`，它是唯一知道这个差异的地方。

2. **跨代 API 一律用条件注释收口。**
   例如 `GuiStatBar` 构造参数在 6.17 变了（`ITooltipGetter, boolean` →
   `ITooltipGetter, ICondition[], boolean`）、`IModularItem.getReducedDamage` 由 public 变 private，
   都写在 `//? if <6.10 { ... }` 里。

3. **可选 mod 兼容只出现在各自开关的作用域内。**
   `with_tmo` / `with_ehp` 同时驱动依赖声明与源码条件，不要在开关作用域外引用
   `net.yiran.tmo` 或 `net.yiran.extraholopage`。

## 已知差异（6.17 节点）

- 不带 Tetra Material Overhaul：带 `"contexts"` 的材料在该节点不会获得按 context 的属性/效果加成。
  数据文件里的 `"contexts"` 键保留原样（6.17 上作为未知字段被忽略）。
- 不带 ExtraHoloPage：全息台不会显示本模组护甲的自定义图标。

## 6.17 节点的一个实现要点

Tetra 6.10 起，模块数据里未注册的 `"type"`（本项目数据中大量使用 `"gecko"`）会直接抛
`JsonParseException: No deserializer found for module model type`。因此 6.17 节点在
`FMLCommonSetupEvent` 里通过 `ModuleModelRegistry.register(...)` 注册 `gecko`，
把该类型映射成 Tetra 自己的 `GridTextureModelData`（其 type 为 `geotetraarmor:gecko`），
这样既保留了 Tetra 的材料拼接/染色逻辑，又不需要改动 46 处数据文件。

## 许可

LGPL 3.0
