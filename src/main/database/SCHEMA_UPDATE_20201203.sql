ALTER TABLE `app`
    DROP `product`,
    DROP `version`,
    DROP `app_origin_url`;
ALTER TABLE `app`
    CHANGE COLUMN `app_download_count` `app_download_count` BIGINT(20) NOT NULL DEFAULT '0',
    CHANGE COLUMN `guide_download_count` `guide_download_count` BIGINT(20) NOT NULL DEFAULT '0';

ALTER TABLE `device`
    DROP FOREIGN KEY `FKo9oabhnk3f79y77ifapu6yp7t`;
ALTER TABLE `device`
    DROP INDEX `device_type_id`;
ALTER TABLE `device`
    DROP `dispaly_title`,
    DROP `name`,
    DROP `type`,
    DROP `device_type_id`;

ALTER TABLE `device`
    ADD COLUMN `type` VARCHAR(255) NULL DEFAULT NULL,
    ADD COLUMN `type_description` VARCHAR(255) NOT NULL ,
    ADD COLUMN `model` VARCHAR(255) NOT NULL,
    ADD COLUMN `model_description` VARCHAR(255) NOT NULL ;

ALTER TABLE `os`
    ADD COLUMN `description` VARCHAR(255) NOT NULL;
ALTER TABLE `product`
    ADD COLUMN `description` VARCHAR(255) NOT NULL;


UPDATE `downloads`.`device` SET `type`='PC', `type_description`='PC', `model`='Windows', `model_description`='<span style="color: #1468e2">Window</span>용 설치 파일' WHERE  `device_id`=1;
UPDATE `downloads`.`device` SET `type`='REALWEAR', `type_description`='Realwear', `model`='HMT-1', `model_description`='<span style="color: #1468e2">HMT-1</span>용 APK' WHERE  `device_id`=2;
UPDATE `downloads`.`device` SET `type`='MOBILE', `type_description`='Google Play', `model`='스마트폰/타블릿', `model_description`='<span style="color: #1468e2">스마트폰/타블릿</span>' WHERE  `device_id`=3;

UPDATE `downloads`.`os` SET `description`='IOS' WHERE  `os_id`=1;
UPDATE `downloads`.`os` SET `description`='ANDROID' WHERE  `os_id`=2;
UPDATE `downloads`.`os` SET `description`='WINDOWS' WHERE  `os_id`=3;

UPDATE `downloads`.`product` SET `description`='REMOTE' WHERE  `product_id`=1;
UPDATE `downloads`.`product` SET `description`='MAKE' WHERE  `product_id`=2;
UPDATE `downloads`.`product` SET `description`='VIEW' WHERE  `product_id`=3;
UPDATE `downloads`.`product` SET `description`='PLATFORM' WHERE  `product_id`=4;
INSERT INTO `downloads`.`product` (`product_id`, `created_at`, `updated_at`, `name`, `description`) VALUES ('5', '2020-12-03 11:30:23', '2020-12-03 11:30:23', 'VRSDK', 'VRSDK');
