-- ----------------------------
-- Table structure for tb_nutz_rand_code
-- ----------------------------
DROP TABLE IF EXISTS `tb_nutz_rand_code`;
CREATE TABLE `tb_nutz_rand_code` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `mobi` varchar(11) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '手机号码',
  `randCode` int(11) DEFAULT NULL COMMENT '验证码',
  `identifier` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '序列',
  `sendDate` datetime DEFAULT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `identifier` (`identifier`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Procedure structure for pro_insert_ccode_1
-- ----------------------------
DROP PROCEDURE IF EXISTS `pro_insert_ccode_1`;
DELIMITER ;;
CREATE PROCEDURE `pro_insert_ccode_1`(IN mobile VARCHAR(11), IN identifier VARCHAR(10))
BEGIN
	DECLARE tmp int DEFAULT 0;
	SELECT COUNT(*) INTO tmp FROM tb_tianyi_rand_code t WHERE t.identifier = identifier;
	IF tmp=1 THEN
		DELETE FROM tb_nutz_rand_code WHERE mobi = mobile;
		UPDATE tb_nutz_rand_code a SET a.mobi = mobile WHERE a.identifier = identifier;
	ELSE
		DELETE FROM tb_nutz_rand_code WHERE mobi = mobile;
		INSERT INTO tb_nutz_rand_code(mobi,identifier,sendDate) VALUES(mobile,identifier,NOW());
	END IF;
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for pro_insert_ccode_2
-- ----------------------------
DROP PROCEDURE IF EXISTS `pro_insert_ccode_2`;
DELIMITER ;;
CREATE PROCEDURE `pro_insert_ccode_2`(IN code INT, IN identifier VARCHAR(10))
BEGIN
	DECLARE tmp int DEFAULT 0;
	SELECT COUNT(*) INTO tmp FROM tb_tianyi_rand_code t WHERE t.identifier = identifier;
	IF tmp=1 THEN
		UPDATE tb_nutz_rand_code a SET a.randCode = code WHERE a.identifier = identifier;
	ELSE
		INSERT INTO tb_nutz_rand_code(randCode,identifier,sendDate) VALUES(code,identifier,NOW());
	END IF;
END
;;
DELIMITER ;

-- ----------------------------
-- 定时清空过期的验证码
-- 30 数字为过期时间 单位为分钟 可自行设置过期时间
-- ----------------------------
SET GLOBAL event_scheduler = ON;
delimiter $$
DROP EVENT IF EXISTS eve_empty_rand_code;
CREATE EVENT eve_empty_rand_code
	ON SCHEDULE EVERY 1 HOUR
ON COMPLETION PRESERVE ENABLE
DO
BEGIN
	DELETE FROM tb_nutz_rand_code WHERE TIMESTAMPDIFF(MINUTE,sendDate,NOW())> 30;
END $$
delimiter ;
