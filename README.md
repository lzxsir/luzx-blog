# luzx-blog 博客
整体架构采用springCloudAlibaba微服务的方式来实现。

使用luzx-blog作为所有微服务的父层级。

技术架构：

jdk1.8

maven3.8.0


springboot2.7.10

## 用户管理
[百度](http://www.baidu.com)

luzx-user服务来进行做用户注册、登录、授权

用户注册：根据用户提交的账号密码进行校验（密码格式，密码强度）保存到数据库。返回注册结果信息。

用户登录：提交用户凭证信息，校验凭证，生成JWT，返回给用户。

用户授权：OAuth2.0、JWT 根据用户角色进行权限设置。



## 文章管理


## 评论系统
## 标签和分类
## 搜索功能
luzx-search服务来做检索。
技术：采用es来进行检索。


## 推荐系统
## 后台管理
## 统计和分析
