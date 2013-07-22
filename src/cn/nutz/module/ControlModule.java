package cn.nutz.module;

import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import cn.nutz.interFace.RandCodeInterFace;
import cn.nutz.interFace.Url2ShortInterFace;

public class ControlModule {

	/**
	 * 网址缩短
	 * 
	 * @param longUrl
	 * @return
	 */
	@Ok("raw")
	@At("/url/long2short")
	public String long2Short(@Param("url") String longUrl) {

		String shortUrl = new Url2ShortInterFace().long2Short(longUrl);
		if (shortUrl != null) {
			return "{\"error\":0,\"url\":\"" + shortUrl + "\"}".toString();
		} else
			return "{\"error\":1}".toString();
	}

	/**
	 * 短网址还原
	 * 
	 * @param shortUrl
	 * @return
	 */
	@Ok("raw")
	@At("/url/short2long")
	public String short2Long(@Param("url") String shortUrl) {

		String longUrl = new Url2ShortInterFace().short2Long(shortUrl);
		if (shortUrl != null) {
			return "{\"error\":0,\"url\":\"" + longUrl + "\"}".toString();
		} else
			return "{\"error\":1}".toString();
	}

	/**
	 * 发送验证码
	 * 
	 * @param mobi
	 * @return
	 */
	@Ok("raw")
	@At("/randcode/send")
	public String send(@Param("mobi") String mobi) {

		if (new RandCodeInterFace().send(mobi))
			return "{\"error\":0}".toString();
		else
			return "{\"error\":1}".toString();
	}

	/**
	 * 清空验证码
	 * 
	 * @param mobi
	 * @return
	 */
	@Ok("raw")
	@At("/randcode/empty")
	public String empty(@Param("mobi") String mobi) {

		if (new RandCodeInterFace().empty(mobi))
			return "{\"error\":0}".toString();
		else
			return "{\"error\":1}".toString();
	}

	/**
	 * 验证验证码
	 * 
	 * @param mobi
	 * @return
	 */
	@Ok("raw")
	@At("/randcode/verify")
	public String verify(@Param("mobi") String mobi,
			@Param("randcode") String randCode) {

		if (new RandCodeInterFace().verify(mobi, randCode))
			return "{\"error\":0}".toString();
		else
			return "{\"error\":1}".toString();
	}

	/**
	 * 接收电信推送验证码
	 * 
	 * @param identifier
	 * @param randCode
	 * @return
	 */
	@Ok("raw")
	@At("/randcode/insert")
	public String insert(@Param("identifier") String identifier,
			@Param("rand_code") String randCode) {

		if (new RandCodeInterFace().insert(randCode, identifier))
			return "{\"result\":\"1\"}".toString().toString();
		else
			return "{\"result\":\"0\"}".toString().toString();
	}
}
