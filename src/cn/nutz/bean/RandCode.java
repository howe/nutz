package cn.nutz.bean;

import org.nutz.dao.entity.annotation.*;

/**
* 验证码类
*/
@Table("tb_nutz_rand_code")
public class RandCode {

	/**
	 * ID
	 */
	@Id
	@Column("id")
	private Integer id;
	/**
	 * 手机号码
	 */
	@Column("mobi")
	private String mobi;
	/**
	 * 验证码
	 */
	@Column("randCode")
	private Integer randCode;
	/**
	 * 序列
	 */
	@Column("identifier")
	private String identifier;
	/**
	 * 发送时间
	 */
	@Column("sendDate")
	private java.util.Date sendDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMobi() {
		return mobi;
	}

	public void setMobi(String mobi) {
		this.mobi = mobi;
	}

	public Integer getRandCode() {
		return randCode;
	}

	public void setRandCode(Integer randCode) {
		this.randCode = randCode;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public java.util.Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(java.util.Date sendDate) {
		this.sendDate = sendDate;
	}

}