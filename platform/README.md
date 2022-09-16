## 环境依赖
Node.js 16.13.1
Java.   1.8

## 运行
运行 com/fudan/annotation/platform/backend/BackendApplication.java:13

## 说明
### 1 统一返回数据格式
com.fudan.annotation.platform.backend.controller 暴露的接口返回的格式统一， 参考com.fudan.annotation.platform.backend.controller.AccountController.getCurrentUser接口。
### 2 建议统一管理返回的状态码和信息
在 com.fudan.annotation.platform.backend.enums.ResponseCodeMsg 中集中管理返回码和信息。
### 3 controller 保持简介，除了调用 service 方法不要有任何额外代码。
### 4 数据库使用云数据库（暂时未调通，rich 可以尝试下能不能搞定）

## TODO
1. 按照 说明中修改代码


