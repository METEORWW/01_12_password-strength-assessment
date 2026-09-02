# 🔐 多策略密码强度评测器

> 大一Java面向对象课程期末项目 —— 基于多策略模式的密码强度评测系统

## 📋 项目简介

本系统是一个带有图形界面的密码强度评测工具，采用**策略模式**等面向对象设计思想，综合多种评测规则对用户密码进行安全评分。系统支持单条密码实时评测、各规则得分详情展示、改进建议生成，以及批量密码文件检测功能。

**安全特色**：弱口令字典匹配、键盘相邻序列检测、重复模式识别、字符组合复杂度分析等，帮助用户识别弱密码，提升网络安全意识。

## 🎯 主要功能

1. **实时密码评测**：输入密码即时显示评分和强度等级
2. **6 大评测规则**：
   - 长度检测 —— 密码越长越安全
   - 字符组合检测 —— 大小写字母、数字、特殊符号混合程度
   - 弱口令字典检测 —— 匹配常见弱口令黑名单
   - 重复模式检测 —— 识别 aaa、abcabc 等不安全模式
   - 键盘序列检测 —— 识别 qwerty、123456、asdf 等键盘相邻序列
   - 常见模式检测 —— 识别纯数字生日、年份等模式
3. **改进建议**：针对低分规则给出具体改进建议
4. **批量检测**：从文本文件导入密码列表进行批量评测
5. **图形界面**：基于 Java Swing 的友好交互界面，含进度条和颜色分级

## 🛠 运行环境

- **JDK 版本**：JDK 8 及以上
- **操作系统**：Windows / macOS / Linux
- **依赖**：无任何第三方依赖，仅使用 Java 标准库

## 🚀 编译与运行

### 命令行编译运行

```bash
# 1. 进入项目根目录
cd PasswordStrengthEvaluator

# 2. 创建编译输出目录
mkdir -p out

# 3. 编译所有源文件（Windows 用 \; 替换 :）
javac -encoding UTF-8 -d out \
  src/passwordevaluator/core/*.java \
  src/passwordevaluator/rules/*.java \
  src/passwordevaluator/loader/*.java \
  src/passwordevaluator/ui/*.java \
  src/passwordevaluator/Main.java

# 4. 运行程序
java -cp out passwordevaluator.Main