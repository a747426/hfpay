-- MySQL dump 10.13  Distrib 5.6.48, for Linux (x86_64)
--
-- Host: localhost    Database: unicom
-- ------------------------------------------------------
-- Server version	5.6.48-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `card`
--

DROP TABLE IF EXISTS `card`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `card` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `card_no` varchar(50) NOT NULL,
  `com` varchar(50) NOT NULL,
  `_login_cookie` text,
  `_uop_id_cookie` varchar(2000) DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `create_time` int(11) NOT NULL,
  `update_time` int(11) DEFAULT NULL,
  `card_status` tinyint(4) NOT NULL DEFAULT '0',
  `group_id` int(11) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `ver_code` varchar(11) DEFAULT NULL,
  `pro_name` varchar(200) DEFAULT NULL,
  `city_name` varchar(200) DEFAULT NULL,
  `_login_token` varchar(1000) DEFAULT NULL,
  `proxy_ip` varchar(255) DEFAULT NULL,
  `proxy_port` int(11) DEFAULT '0',
  `proxy_expire_time` int(11) DEFAULT '0',
  `proxy_fail_times` int(11) DEFAULT '0',
  `web_login_status` tinyint(4) DEFAULT '0',
  `web_ver_code` varchar(11) DEFAULT NULL,
  `_web_login_cookie` text,
  `web_update_time` int(11) DEFAULT NULL,
  `app_place_order_flag` tinyint(4) DEFAULT '1',
  `web_place_order_flag` tinyint(4) DEFAULT '1',
  `en_channel_str` varchar(5000) DEFAULT NULL,
  `en_channel_str_update_time` int(11) NOT NULL DEFAULT '0',
  `buy_card_count` int(11) NOT NULL DEFAULT '0',
  `buy_card_last_time` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `index_card_card_no` (`card_no`),
  KEY `index_card_status` (`status`),
  KEY `index_card_create_time` (`create_time`),
  KEY `index_card_card_status` (`card_status`),
  KEY `index_card_card_group_id` (`group_id`),
  KEY `index_card_card_web_login_status` (`web_login_status`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card`
--

LOCK TABLES `card` WRITE;
/*!40000 ALTER TABLE `card` DISABLE KEYS */;
INSERT INTO `card` VALUES (2,'17612760005','123456','c_sfbm=234g_00;path=/,jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2JpbGUiOiIxNzYxMjc2MDAwNSIsInBybyI6IjA3MSIsImNpdHkiOiI3MTAiLCJpZCI6ImRlZjlmNTQ0ZTYxMGNiNDdkY2U1MDc5Yjg5NDIwY2M5In0.PrKLHnVVnwd9HljNM3_BAdoAbFUFPoRhe_EGVuSkEI4; Domain=10010.com; Path=/,ecs_token=eyJkYXRhIjoiNWVjMzc1MzNjZDhiYmJhZTEwYWQ1NDMzYjIyNDJkODc2M2Q4ZWU2M2U4ZjAxYTk5OGEzNTQ2NDcwMDFmNzI3NGY1Y2FhYjc5YmU4MjUwNmFkZTA2YmI4ZmVkMTc1ZDE3YTZmYjQxNThmZmE4NWE5YTM5Njc0OTE3ZWE5Mjc2ODMwZmJkYTMxOGFlMDk4OGY5OGE3NDc3ODZiMDQwMjdmZjBhZDhlZmE5YTZiNmQ5OThjZTYwODQ5ZmU3MTM5YzI4IiwidmVyc2lvbiI6IjAwIn0=; Domain=10010.com; Path=/,third_token=eyJkYXRhIjoiMTMyYzJlNGFmOTFiOWU0ZTRmMmMyMDQwOWVkNWU5NDJjY2E2N2IxMTIwZTkxNjM4ZWUyODk1ZmJiMDhmNjZhMGMzMzY2YmVhYTA2ZTg1NGNjNjhjNWM5MDRmZjgxYjQ4MjQ4YTc0ODlhNDhjNmIyOGI5ODJjNDFmNmFhYjM0YTA0NDU3MTAzODNlNTQ0NmUyODZiYjYxYThhZjBiYjU4NSIsInZlcnNpb24iOiIwMCJ9,u_areaCode=710,wo_family=0,c_mobile=17612760005,invalid_at=e3dd93055672606f182215d5fd302afe020ecab4da88b9463fdb113d4cd4619e,t3_token=42ad67c8e50f89c766079e1ecf25d9d1,cw_mutual=6ff66a046d4cb9a67af6f2af5f74c321df188333d0f6c2ab89d7cab10989125f36fd46487833e045f1a7ffa4fabfdc91067f008a171c84443f99339f10c94977; Domain=.10010.com; Path=/,random_login=0,ecs_acc=DlonmuBgGWJSuxFZJnsv2ixUKhGSObSIjumQ1HJbK7zGKCV+bqPRv7FP5sGtONrQQ/+xg9QZC/r3ao4ylUH9O5dXyEnDgs62fnU43tcJrlTQMsal9HcFRqZHFhJocCzBFFN8AFIjEsPbThrK9dr65YZj7cmgu4203yI/aBmhBMc=; Domain=.10010.com; Path=/,enc_acc=DlonmuBgGWJSuxFZJnsv2ixUKhGSObSIjumQ1HJbK7zGKCV+bqPRv7FP5sGtONrQQ/+xg9QZC/r3ao4ylUH9O5dXyEnDgs62fnU43tcJrlTQMsal9HcFRqZHFhJocCzBFFN8AFIjEsPbThrK9dr65YZj7cmgu4203yI/aBmhBMc=,d_deviceCode=867600052612176,c_version=android@6.0100,city=071|710|90657326|-99; Domain=.10010.com; Path=/,u_account=17612760005; Domain=.10010.com; Path=/,login_type=06; Domain=.10010.com; Path=/,login_type=06,c_id=a8db7d7b55249b6ac06d5cba1040217c731650489fcc66586a2b7d03e16bd17a,a_token=eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1OTUyMTQ4MTUsInRva2VuIjp7ImxvZ2luVXNlciI6IjE3NjEyNzYwMDA1IiwicmFuZG9tU3RyIjoieWg1YjlkMzMxNTk0NjEwMDE1In0sImlhdCI6MTU5NDYxMDAxNX0.EpNkDbwBfPM5FR03DgmF6CnhI9R1rMQGCKliICUSzq-dg806PcnM9pNptSUdaADeEW80ZXDP5ZigZXB-7RSMpA','_uop_id=npfauth_iepa8f0c51805afd2e7e76dbd5319b5107505547ynh84nga',1,1594379459,1594610012,1,1,'','3025','湖北','武汉','927a72c0a917a806b209014134d78ba1d8ca32bb92f4bd6bd7e21e08a44fea741a6cc5caed4a765bfc5d48583d5061baee98ff08b760dae8ffef645f8630a9980e4b4ecda63de85367efb595e076dc0d40722e965761c01cb8179c851a781240df98ec524ff130664863fcb93eaf489a3b07b16827affd5d3656ce4dc35d76a156ee440fb8da3c726efc4b4bdf140588cdbaf8138d597f75ad3440cbd369d24f9e343a40abc291cd3a29c058517a7d04880bb6c73b7d7d4b3547e8662d8758ec17da870f5be338d56b5bad2f851264161b47c3e8cd0e630894734585fde6ecfdab1601e4221ad41344f01dbeba4ee2115c5556e72c05634dcdff5e1cda4d59643a1b3d016b07faf88ab13abb27dcf863','182.105.227.97',3617,1594635552,329,2,'','JUT=ItozGF0QfkYrA6bdhVYC5UzlwdDMlz7u4thaCM1GSHhNZ9vRqkIByH1lheo69L+LdON94qTXseOiHPabpatfm+q/OeKIGD5p5h7cFr3fjFk4AsuFJsIFFAy9uSHU387wow0ryp0574j6n8soGZCclY6tDVlfsrrTTcBKPT9OmqpO5jUXlyYq4GvDW2kifEcRiyaIBBjwhEudCb2JZZAarZEXSqKVeVxcIaQdN1pyA4VnyFVo5nXHXVMUD/gb+NK7yjFKhzJoQXZ3xLoPMPlE7XwgTYpZ6JIXaBsp55eWMFGJiw7GYjyYdf+NYdvtnLmQwc2Bd08xia/847GOCoq6gjkehv6pAVTf8dI2Z1E8UsGUfzhTFUlhaDaKhZ3ES5OrX94syP9gn6hAGdsuSnf26j/EigwHPFcsJES8q2JeYeCFFgwnQ5EN2UstjMFQsklXSqGh7pF1Re7LNsCDpJ9WozXgQ7W4MJwAYN35OS+0nwC/IQRyvEZJ11bSjMZWqGZ2AMGWazRnbRTEt9ndBfgU8TFfQzgx+d5j6/8ml3ipX08=s533yCDiFcvgzDJ3q5BiQg==; domain=.10010.com; path=/;',1594609400,1,1,NULL,1594456940,0,1594604442),(3,'13080907846','comm','c_sfbm=234g_00;path=/,jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2JpbGUiOiIxMzA4MDkwNzg0NiIsInBybyI6IjA4NCIsImNpdHkiOiI4NDEiLCJpZCI6IjczYTI3MDRkOTI3NThmNTkxMmE4ZmI4MTM5NmQ3ZDVlIn0.67wmPBSG0-PCkfeUi5JRSTfV2VP_zhiDiCTzUur5bVc; Domain=10010.com; Path=/,ecs_token=eyJkYXRhIjoiNWVjMzc1MzNjZDhiYmJhZTEwYWQ1NDMzYjIyNDJkODc2M2Q4ZWU2M2U4ZjAxYTk5OGEzNTQ2NDcwMDFmNzI3NGY1Y2FhYjc5YmU4MjUwNmFkZTA2YmI4ZmVkMTc1ZDE3YTZmYjQxNThmZmE4NWE5YTM5Njc0OTE3ZWE5Mjc2ODMwZmJkYTMxOGFlMDk4OGY5OGE3NDc3ODZiMDQwMjdmZjlmMjZlYmRjOTA4MGMxMTY5MDJiZWMxODA5MDYyMDQ4IiwidmVyc2lvbiI6IjAwIn0=; Domain=10010.com; Path=/,third_token=eyJkYXRhIjoiMTMyYzJlNGFmOTFiOWU0ZTRmMmMyMDQwOWVkNWU5NDJjY2E2N2IxMTIwZTkxNjM4ZWUyODk1ZmJiMDhmNjZhMGMzMzY2YmVhYTA2ZTg1NGNjNjhjNWM5MDRmZjgxYjQ4MjQ4YTc0ODlhNDhjNmIyOGI5ODJjNDFmNmFhYjM0YTA2YzMxMWVlMGQxNmViMzhjMTI4Njg5YTg4OGYzZjdhMCIsInZlcnNpb24iOiIwMCJ9,u_areaCode=841,wo_family=0,c_mobile=13080907846,invalid_at=e3dd93055672606f182215d5fd302afe020ecab4da88b9463fdb113d4cd4619e,t3_token=97ade1cf9b023d398469e306fbb61956,cw_mutual=6ff66a046d4cb9a67af6f2af5f74c321df188333d0f6c2ab89d7cab10989125fcd7a409968476b12fd1f188c081a8c82b140899e1b98bb33241033178c18c5d6; Domain=.10010.com; Path=/,random_login=0,ecs_acc=Gtt4QwK6pPkhUymv7ZopDQcZNjPIU2lDIxJon9stCOhY3IU+CGmcgaKn9X4xRgA0/O3577mr8Zf+x3iI9qxq+Bj/GLWGRna0HjrZhFPAygFmbObHeHPUPcykmm15EwqnBof/LWYLVLowuky61h/gyZ6UVxyJVGVn8M7SPIl5uB0=; Domain=.10010.com; Path=/,enc_acc=Gtt4QwK6pPkhUymv7ZopDQcZNjPIU2lDIxJon9stCOhY3IU+CGmcgaKn9X4xRgA0/O3577mr8Zf+x3iI9qxq+Bj/GLWGRna0HjrZhFPAygFmbObHeHPUPcykmm15EwqnBof/LWYLVLowuky61h/gyZ6UVxyJVGVn8M7SPIl5uB0=,d_deviceCode=869078462680130,c_version=android@6.0100,city=084|841|90337592|-99; Domain=.10010.com; Path=/,u_account=13080907846; Domain=.10010.com; Path=/,login_type=06; Domain=.10010.com; Path=/,login_type=06,c_id=71e2c66ff651c4c35071f5b76f77db7cf65f189f6a874d71952e1894bb22d1fb,a_token=eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1OTUyMTQ4MTUsInRva2VuIjp7ImxvZ2luVXNlciI6IjEzMDgwOTA3ODQ2IiwicmFuZG9tU3RyIjoieWg2ZGMwM2MxNTk0NjEwMDE1In0sImlhdCI6MTU5NDYxMDAxNX0.DZahCEIRBVCNGH_4iX7dJLhintfCly5SVubc6rOYBQZrhRHLUo0kgNXAKsHcztsh5PNH_8nz4VDWZptJkeTz_A','_uop_id=npfauth_jhexxwrue87b8db7ee78b671a64ec3e26421c736umpzjlsh',1,1594608205,1594610012,1,6,'','','陕西','西安','927a72c0a917a806b209014134d78ba1d8ca32bb92f4bd6bd7e21e08a44fea74eb2142d1b7d7e9370598b47e3e867d5c217d9747911d5974ceb5ecae27074cb6a2c40c3ba95d7a7f7b368d1cfee531a3db711f660e6d565817a034a2950776020ff8bbdb94d0a8060cd94855e16bb7ec5d0a05af15e55b4ec114f3c0d114794289b10dfd13198012bec78373bf51b421772a9bf400de21e378a8b7cf56669825601d230626595b41ba3f0b39f94df3b171c45c2422cd4b978ad7883a160c70218bb262633e65167f0a23eb1f0028738d8018512323100412c03e37db6ef82b90620e30c8c26d3e3e7a6ea7a58924c3f34bf3a54a2830f19091e45739f62b50f1bac31599f1f3cc1c416a61cf87f65c02','114.101.246.74',5412,1594635454,329,2,'370433',NULL,1594608615,1,1,NULL,0,0,0),(4,'15529693878','cc','c_sfbm=234g_00;path=/,jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2JpbGUiOiIxNTUyOTY5Mzg3OCIsInBybyI6IjA4NCIsImNpdHkiOiI4NDEiLCJpZCI6IjYyYjRlNDQ2ZTkyODQ2NzcxNmU0ODk5MmM1ODY2MDcwIn0.Y7Xg865K39G5PiU2tUl1xcFBXVS84C3Aq5rc0jrEphg; Domain=10010.com; Path=/,ecs_token=eyJkYXRhIjoiNWVjMzc1MzNjZDhiYmJhZTEwYWQ1NDMzYjIyNDJkODc2M2Q4ZWU2M2U4ZjAxYTk5OGEzNTQ2NDcwMDFmNzI3NGY1Y2FhYjc5YmU4MjUwNmFkZTA2YmI4ZmVkMTc1ZDE3YTZmYjQxNThmZmE4NWE5YTM5Njc0OTE3ZWE5Mjc2ODMwZmJkYTMxOGFlMDk4OGY5OGE3NDc3ODZiMDQwMjdmZjY3MjUyMjdlYjAxNGU5MDcxZTZiYWE5NWU3YzNlZjRjIiwidmVyc2lvbiI6IjAwIn0=; Domain=10010.com; Path=/,third_token=eyJkYXRhIjoiMTMyYzJlNGFmOTFiOWU0ZTRmMmMyMDQwOWVkNWU5NDJjY2E2N2IxMTIwZTkxNjM4ZWUyODk1ZmJiMDhmNjZhMGMzMzY2YmVhYTA2ZTg1NGNjNjhjNWM5MDRmZjgxYjQ4MjQ4YTc0ODlhNDhjNmIyOGI5ODJjNDFmNmFhYjM0YTAyODUxMTcyMDE1Njk0N2YwY2ViY2QxNDAwYTAxNzk1OCIsInZlcnNpb24iOiIwMCJ9,u_areaCode=841,wo_family=0,c_mobile=15529693878,invalid_at=e3dd93055672606f182215d5fd302afe020ecab4da88b9463fdb113d4cd4619e,t3_token=df1cbcc9e782b79653eaea168a7e41be,cw_mutual=6ff66a046d4cb9a67af6f2af5f74c321df188333d0f6c2ab89d7cab10989125f3d94078a6125e00a2494b0a0c315d9217847a565d6b6ace11cbc6135dca76fea; Domain=.10010.com; Path=/,random_login=0,ecs_acc=oZC/8wlNk5ymqlwknHmqSyeVDg58rxzRZ7c3sDS3XO+8RLU+hQnaFNpTvHosKUdoWXK6M4EMNuYB/pRUpNtEmVnQ3P1Sxz7OkCp4XHmC++lS52y9jqgHQsrH1y3f0POuCysdgPi1Gh9vD7ZYLfSs0XIxTpi22fN7NWPSBufRNPg=; Domain=.10010.com; Path=/,enc_acc=oZC/8wlNk5ymqlwknHmqSyeVDg58rxzRZ7c3sDS3XO+8RLU+hQnaFNpTvHosKUdoWXK6M4EMNuYB/pRUpNtEmVnQ3P1Sxz7OkCp4XHmC++lS52y9jqgHQsrH1y3f0POuCysdgPi1Gh9vD7ZYLfSs0XIxTpi22fN7NWPSBufRNPg=,d_deviceCode=866938782629155,c_version=android@6.0100,city=084|841|90337592|-99; Domain=.10010.com; Path=/,u_account=15529693878; Domain=.10010.com; Path=/,login_type=06; Domain=.10010.com; Path=/,login_type=06,c_id=c62711c57068e091fe551006e9d99efbd55ba8a7c2cd9b02310435c955b0d325,a_token=eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1OTUyMTQ4MTUsInRva2VuIjp7ImxvZ2luVXNlciI6IjE1NTI5NjkzODc4IiwicmFuZG9tU3RyIjoieWhiYjVmMjQxNTk0NjEwMDE1In0sImlhdCI6MTU5NDYxMDAxNX0.H6Fn_cehqKf5tKGfWEedzdEy0FtWubCAAq7_NFGxDunHHXWx8Qh5rqOUMH0RnqH0yB0g4bNSb2jW_MSPNwwbNA','_uop_id=npfauth_26sv6nq23802f9499c262655915477293eeb5986pdjj4st5',1,1594608815,1594610012,1,6,'','','陕西','西安','927a72c0a917a806b209014134d78ba1d8ca32bb92f4bd6bd7e21e08a44fea744fe10a3088349b07a6bcd8e86021fcd3655169d347718d1bdfd7de8c9e2343433a7e5d919f96c9e6286f144d9fcdbe13d7050e399414c3adc386da4ca6cae0516a99c8afc8f1cde6ba785a9ee55a6c3df7b4f9c6d47dcfed8c94048d630ff3b63b17d12897d55ac35b60bb8436ba00f4c42d1fd4fbbcd5f4be5f3a737625a8ece68a3197dfaf932efabc79a461acd2b98e58e4c51ba7a741df4faf453349b15e297165d509777e0b6b8ce5a19eb70ad41817731094ad1f8eb5d1b07861f1cdc8275c0c0d45824438fb52f71da851ff9b8812cec421fc9ba966a2b30c614d406d7a078e1883b22980fc0ddc378cb84400','114.101.246.74',5412,1594635454,328,2,'656186',NULL,1594608819,1,1,NULL,0,0,0),(5,'15529523228','c',NULL,NULL,1,1594609173,1594609176,2,6,'','',NULL,NULL,NULL,'117.57.85.113',3617,1594635382,172,2,NULL,NULL,1594609177,1,1,NULL,0,0,0),(6,'13040992620','com',NULL,NULL,1,1594635020,1594635024,2,7,'','2346',NULL,NULL,NULL,'117.42.200.110',5412,1594635651,1202,0,'',NULL,NULL,1,1,NULL,0,0,0);
/*!40000 ALTER TABLE `card` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cdkey`
--

DROP TABLE IF EXISTS `cdkey`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cdkey` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `card_id` int(11) NOT NULL,
  `no` varchar(48) DEFAULT NULL,
  `pwd` varchar(64) DEFAULT NULL,
  `face_value` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '1',
  `create_time` int(11) DEFAULT NULL,
  `is_use` tinyint(4) DEFAULT '0',
  `unicom_order_id` varchar(255) NOT NULL,
  `order_id` int(11) NOT NULL,
  `export_time` int(11) DEFAULT NULL,
  `card_no` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_cdkey_card_id` (`card_id`),
  KEY `index_cdkey_status` (`status`),
  KEY `index_cdkey_is_use` (`is_use`),
  KEY `index_cdkey_unicom_order_id` (`unicom_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cdkey`
--

LOCK TABLES `cdkey` WRITE;
/*!40000 ALTER TABLE `cdkey` DISABLE KEYS */;
/*!40000 ALTER TABLE `cdkey` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `key` varchar(255) DEFAULT NULL,
  `value` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` VALUES (1,'站点名称','sitename','话费卡密系统'),(2,'卡商接口密码(&password=****)','suppassword','XXSSSUSSUER'),(3,'开启支付宝','alipay_flag','0'),(4,'优先下单(1:微信/2:支付宝)','first_dispatch_type','1'),(5,'代理IP地址','proxy_url',''),(6,'是否sdk模式','pay_model','0'),(8,'预下单开关','ready_order_flag','1'),(9,'预下单20缓存数','ready_20order_num','0'),(10,'预下单30缓存数','ready_30order_num','3'),(11,'预下单50缓存数','ready_50order_num','3'),(12,'预下单100缓存数','ready_100order_num','2'),(13,'预下单200金额缓存数量','ready_200order_num','1'),(14,'预下单300金额缓存数量','ready_300order_num','0'),(15,'100订单卡密面额(只能填20,50,100)','order_100_cdk_value','100'),(16,'200订单卡密面额(只能填20,50,100)','order_200_cdk_value','100'),(17,'300订单卡密面额(只能填20,30,50,100)','order_300_cdk_value','100');
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group`
--

DROP TABLE IF EXISTS `group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `remarks` varchar(255) DEFAULT NULL,
  `create_time` int(11) NOT NULL,
  `dispatch_order_status` tinyint(4) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group`
--

LOCK TABLES `group` WRITE;
/*!40000 ALTER TABLE `group` DISABLE KEYS */;
INSERT INTO `group` VALUES (1,'1',1,'',0,1,0),(6,'6',1,'',1591455270,1,0),(7,'7',1,'',1591460377,0,0),(8,'8',1,'',1591460408,0,0),(9,'9',1,'',1591501291,1,0);
/*!40000 ALTER TABLE `group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ip_pool`
--

DROP TABLE IF EXISTS `ip_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ip_pool` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(100) NOT NULL,
  `port` int(10) NOT NULL,
  `expire_time` int(11) NOT NULL,
  `city` varchar(200) DEFAULT NULL,
  `use_count` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42164 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ip_pool`
--

LOCK TABLES `ip_pool` WRITE;
/*!40000 ALTER TABLE `ip_pool` DISABLE KEYS */;
/*!40000 ALTER TABLE `ip_pool` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log`
--

DROP TABLE IF EXISTS `log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `title` varchar(200) NOT NULL,
  `ip` varchar(100) NOT NULL,
  `created` bigint(20) NOT NULL,
  `path` varchar(800) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='操作日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log`
--

LOCK TABLES `log` WRITE;
/*!40000 ALTER TABLE `log` DISABLE KEYS */;
/*!40000 ALTER TABLE `log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant`
--

DROP TABLE IF EXISTS `merchant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchant` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nick_name` varchar(20) DEFAULT NULL,
  `pay_key` varchar(50) NOT NULL,
  `create_time` int(11) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `remarks` varchar(100) DEFAULT NULL,
  `user_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10003 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant`
--

LOCK TABLES `merchant` WRITE;
/*!40000 ALTER TABLE `merchant` DISABLE KEYS */;
INSERT INTO `merchant` VALUES (10000,'测试商户1','1b7f1d545af6b368e65b0c2c016b992633',1545029921,1,'是234',4),(10002,'江湖','f322f04af9f700be1d72975a0d720023',1595173709,1,'',3);
/*!40000 ALTER TABLE `merchant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) NOT NULL,
  `content` varchar(1000) DEFAULT NULL,
  `create_time` int(11) DEFAULT NULL,
  `type` tinyint(4) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `amount` int(11) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '0',
  `pay_type` varchar(50) NOT NULL,
  `pay_time` int(11) DEFAULT NULL,
  `merchant_id` int(11) NOT NULL,
  `merchant_order_no` varchar(100) NOT NULL,
  `notify_status` tinyint(4) NOT NULL DEFAULT '0',
  `notify_url` varchar(150) DEFAULT NULL,
  `card_id` int(11) DEFAULT NULL,
  `create_time` int(11) NOT NULL,
  `notify_time` int(11) DEFAULT NULL,
  `pay_url` varchar(8000) DEFAULT NULL,
  `unicom_order_id` varchar(255) DEFAULT NULL,
  `notify_times` tinyint(4) NOT NULL DEFAULT '0',
  `get_cdk_status` tinyint(4) DEFAULT '0',
  `send_get_cdk_sms_time` int(11) DEFAULT NULL,
  `get_cdk_sms` varchar(30) DEFAULT NULL,
  `wx_h5_return` varchar(1000) DEFAULT NULL,
  `card_no` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_order_status` (`status`),
  KEY `index_order_pay_type` (`pay_type`),
  KEY `index_order_notify_status` (`notify_status`),
  KEY `index_order_card_id` (`card_id`),
  KEY `index_order_create_time` (`create_time`),
  KEY `index_order_unicom_order_id` (`unicom_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_pool`
--

DROP TABLE IF EXISTS `order_pool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_pool` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `unicom_order_id` varchar(255) NOT NULL,
  `card_id` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `pay_url` varchar(2000) DEFAULT NULL,
  `create_time` int(11) DEFAULT NULL,
  `is_use` tinyint(4) DEFAULT '0',
  `status` tinyint(4) DEFAULT '1',
  `order_no` varchar(255) DEFAULT NULL,
  `update_time` int(11) DEFAULT NULL,
  `order_type` varchar(20) DEFAULT NULL,
  `wx_h5_return` varchar(1000) DEFAULT NULL,
  `pay_type` varchar(20) NOT NULL,
  `card_no` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_order_pool_unicom_order_id` (`unicom_order_id`),
  KEY `index_order_pool_card_id` (`card_id`),
  KEY `index_order_pool_amount` (`amount`),
  KEY `index_order_pool_create_time` (`create_time`),
  KEY `index_order_pool_is_use` (`is_use`),
  KEY `index_order_pool_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_pool`
--

LOCK TABLES `order_pool` WRITE;
/*!40000 ALTER TABLE `order_pool` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_pool` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `account` varchar(64) NOT NULL,
  `nick_name` varchar(50) NOT NULL,
  `password` char(32) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `create_time` int(11) unsigned NOT NULL,
  `update_time` int(11) unsigned DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `bind_ip` varchar(200) DEFAULT NULL,
  `vcode` varchar(50) NOT NULL DEFAULT '',
  `group` tinyint(4) NOT NULL DEFAULT '0' COMMENT '分组信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account` (`account`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','超级管理员','e10adc3949ba59abbe56e057f20f883e','1',1543500410,1543500410,1,'','',0),(4,'user','商户','c33367701511b4f6020ec61ded352059','',1595176745,1595176745,1,NULL,'',2);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-07-26 20:44:55
