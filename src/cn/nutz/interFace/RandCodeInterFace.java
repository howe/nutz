package cn.nutz.interFace;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import cn.nutz.tool.HttpClientTool;
import cn.nutz.tool.ToolUtility;

/**
 * 验证码接口
 * 
 * @author howe
 * 
 */
public class RandCodeInterFace {

	private static final Log log = Logs.get();
	Ioc ioc = new NutIoc(new JsonLoader("ioc/dao.js"));
	private Dao dao = ioc.get(Dao.class, "dao");
	private static final String APP_ID = "nutz";
	private static final String APP_SECRET = "nutz";
	private static final String ACCESS_TOKEN = "nutz";
	//接收电信返回验证码接口, 域名就是该应用的域名
	private static final String URL = "http://api.nutz.cn/randcode/insert";
	//过期时间 单位 分钟  配合数据库时间使用
	private static final String EXP_TIME = "30";

	/**
	 * 清空验证码
	 * 
	 * @param mobi
	 * @return
	 */
	public boolean empty(String mobi) {

		if (ToolUtility.verifyMobile(mobi)) {
			try {
				Sql sql = Sqls
						.create("DELETE FROM tb_nutz_rand_code where MOBI = @MOBI");
				sql.params().set("MOBI", mobi);
				dao.execute(sql);
				return true;
			} catch (Exception e) {
				if (log.isDebugEnabled())
					log.debug("emptyRandCode!!", e);
				return false;
			}
		} else
			return false;
	}

	/**
	 * 验证手机验证码
	 * @param mobi
	 * @param randCode
	 * @return
	 */
	public boolean verify(String mobi, String randCode) {

		if (ToolUtility.verifyMobile(mobi) && randCode.length() == 6) {

			try {

				Sql sql = Sqls
						.create("SELECT * FROM tb_nutz_rand_code WHERE MOBI = @MOBI AND RANDCODE = @RANDCODE");
				sql.params().set("MOBI", mobi).set("RANDCODE", randCode);
				sql.setCallback(new SqlCallback() {
					public Object invoke(Connection conn, ResultSet rs, Sql sql)
							throws SQLException {
						List<String> list = new LinkedList<String>();
						while (rs.next())
							list.add(rs.getString("mobi"));
						return list;
					}
				});
				dao.execute(sql);
				if (sql.getList(String.class).size() == 0) // 不匹配
					return false;
				else
					return true;
			} catch (Exception e) {

				if (log.isErrorEnabled())
					log.error("insertRandCode!!", e);
				return false;
			}
		} else
			return false;
	}

	/**
	 * 发送验证码
	 * 
	 * @param mobi
	 * @return
	 */
	public boolean send(String mobi) {

		if (ToolUtility.verifyMobile(mobi)) {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String timestamp = dateFormat.format(date);
			String sign = encryptBASE64(HmacSHA1Encrypt("access_token="
					+ ACCESS_TOKEN + "&app_id=" + APP_ID + "&timestamp="
					+ timestamp, APP_SECRET));
			Map<String, Object> parms = new HashMap<String, Object>();
			parms.put("app_id", APP_ID);
			parms.put("access_token", ACCESS_TOKEN);
			parms.put("timestamp", timestamp);
			parms.put("sign", sign);
			String tmp = HttpClientTool.post(
					"http://api.189.cn/v2/dm/randcode/token", parms);

			System.out.println("获取token: " + tmp);
			if (tmp.indexOf("token") >= 0) {

				String token = tmp.substring(tmp.indexOf("token") + 8,
						tmp.indexOf("res_code") - 3);

				sign = encryptBASE64(HmacSHA1Encrypt("access_token="
						+ ACCESS_TOKEN + "&app_id=" + APP_ID + "&exp_time="
						+ EXP_TIME + "&phone=" + mobi + "&timestamp="
						+ timestamp + "&token=" + token + "&url=" + URL,
						APP_SECRET));

				parms = new HashMap<String, Object>();
				parms.put("app_id", APP_ID);
				parms.put("access_token", ACCESS_TOKEN);
				parms.put("token", token);
				parms.put("phone", mobi);
				parms.put("url", URL);
				parms.put("timestamp", timestamp);
				parms.put("exp_time", EXP_TIME);
				parms.put("sign", sign);
				tmp = HttpClientTool.post(
						"http://api.189.cn/v2/dm/randcode/send", parms);
				System.out.println("获取identifier: " + tmp);
				if (tmp.indexOf("identifier") >= 0) {

					try {
						Sql sql = Sqls
								.create("CALL pro_insert_ccode_1(@MOBI, @IDENTIFIER)");
						sql.params()
								.set("MOBI", mobi)
								.set("IDENTIFIER",
										tmp.substring(
												tmp.indexOf("identifier") + 13,
												tmp.indexOf("create_at") - 3));
						dao.execute(sql);
						return true;
					} catch (Exception e) {

						if (log.isErrorEnabled())
							log.error("send!!", e);
						return false;
					}
				} else
					return false;
			} else
				return false;
		} else
			return false;
	}

	/**
	 * 插入验证码
	 * 
	 * @param randCode
	 * @param identifier
	 * @return
	 */
	public boolean insert(String randCode, String identifier) {

		if (randCode.length() == 6 && !identifier.equals("")) {

			try {
				Sql sql = Sqls
						.create("CALL pro_insert_ccode_2(@RANDCODE, @IDENTIFIER)");
				sql.params().set("RANDCODE", randCode)
						.set("IDENTIFIER", identifier);
				dao.execute(sql);
				return true;
			} catch (Exception e) {

				if (log.isErrorEnabled())
					log.error("insertRandCode!!", e);
				return false;
			}
		} else
			return false;
	}

	private static String encryptBASE64(byte[] key) {
		try {
			return new String(Base64.encodeBase64(key));
		} catch (Exception e) {
			return null;
		}
	}

	private static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) {

		String MAC_NAME = "HmacSHA1";
		String ENCODING = "UTF-8";
		try {
			byte[] data = encryptKey.getBytes(ENCODING);
			SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
			Mac mac = Mac.getInstance(MAC_NAME);
			mac.init(secretKey);
			byte[] text = encryptText.getBytes(ENCODING);
			return mac.doFinal(text);
		} catch (Exception e) {
			return null;
		}
	}
}