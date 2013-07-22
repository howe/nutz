开始使用
1.导入database.sql
2.配置/conf/ioc/dao.js 数据库信息
3.配置 src/cn/nutz/interFace/RandCodeInterFace.java 相关信息
4.配置 src/cn/nutz/interFace/Url2ShortInterFace.java 相关信息
PS: ACCESS_TOKEN 生成方式 浏览器打开 https://oauth.api.189.cn/emp/oauth2/v2/authorize?app_id=您应用的APPID&redirect_uri=您应用填的回调地址&app_secret=您应用的APPSECRT&response_type=token
成功返回链接里的access_token的值就是需要的ACCESS_TOKEN


操作方法

1.网址缩短
调用方式 POST/GET
地址 http://您的应用地址/url/long2short?url=需缩短的网址
成功返回 {"error":0,"url":"http://189.io/~hVPnn"}
失败返回 {"error":1}

2.短网址还原
调用方式 POST/GET
地址 http://您的应用地址/url/short2long?url=需还原的缩短的网址
成功返回 {"error":0,"url":"http://open.189.cn/"}
失败返回 {"error":1}

3.发送验证码
调用方式 POST/GET
地址 http://您的应用地址/randcode/send?mobi=手机号码
成功返回 {"error":0}
失败返回 {"error":1}

4.清空某手机接收到的验证码
调用方式 POST/GET
地址 http://您的应用地址/randcode/empty?mobi=手机号码
成功返回 {"error":0}
失败返回 {"error":1}

5.验证接收到的验证码
调用方式 POST/GET
地址 http://您的应用地址/randcode/verify?mobi=手机号码&randcode=接收到的验证码
成功返回 {"error":0}
失败返回 {"error":1}