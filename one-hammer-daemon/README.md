![logo](logo.png)

##### 介绍下你的项目

```shell
# swagger本地地址
http://127.0.0.1:8080/swagger-ui/index.html
# JSON 地址
http://127.0.0.1:8080/v3/api-docs/

```

##### 开发注意事项

- 按照功能（JIRA号）拉分支，分支名字是JIRA号
- 开发工具[安装阿里的编码规范插件](http://192.168.0.186:8090/pages/viewpage.action?pageId=8388630)  ， 代码提交之前要保证没有检测出问题
- 合并代码之前要做git rebase, 不要产生有两个parent的merge commit
- 提交代码之前要对代码做格式化，应用jetbrain idea的format工具
- 提交代码之前要对代码测试，保证代码能在JDK8，JDK17的环境下运行
- 代码尽量写单元测试
- Sonar代码检测不要有Blocker/Critical/Major的告警，[Sonar](http://192.168.0.185:8000/project/issues?id=com.hw.hmesh%3Ahmesh&resolved=false&sinceLeakPeriod=true&types=BUG)
