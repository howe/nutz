package cn.nutz.interFace;

import cn.nutz.tool.HttpClientTool;

/**
 * 网址缩短接口
 * 
 * @author howe
 * 
 */
public class Url2ShortInterFace {

	private static final String APP_ID = "nutz";
	private static final String ACCESS_TOKEN = "nutz";
	
	/**
	 * 网址缩短
	 * @param longUrl
	 * @return
	 */
	public String long2Short(String longUrl) {

		if (longUrl.equals(""))
			return null;
		else {
			String tmp = HttpClientTool
					.get("http://api.189.cn/EMP/shorturl/long2short?app_id="
							+ APP_ID + "&access_token=" + ACCESS_TOKEN
							+ "&longurl=" + longUrl);
			if (tmp.indexOf("shorturl") >= 0)
				return tmp.substring(tmp.indexOf("<shorturl>") + 10,
						tmp.indexOf("</shorturl>"));
			else
				return null;
		}
	}

	/**
	 * 短网址还原
	 * @param shortUrl
	 * @return
	 */
	public String short2Long(String shortUrl) {

		if (shortUrl.indexOf("http://189.io/") < 0)
			return null;
		else {
			String tmp = HttpClientTool
					.get("http://api.189.cn/EMP/shorturl/short2long?app_id="
							+ APP_ID + "&access_token=" + ACCESS_TOKEN
							+ "&shorturl="
							+ shortUrl.replaceAll("http://189.io/", ""));
			if (tmp.indexOf("longurl") >= 0){
				return tmp.substring(tmp.indexOf("<longurl>") + 9,
						tmp.indexOf("</longurl>"));
			}
			else
				return null;
		}
	}
}
