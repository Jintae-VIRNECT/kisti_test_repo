ALTER TABLE `device`
    ADD COLUMN `product_id` BIGINT(20) NOT NULL;
ALTER TABLE `device`
    ADD INDEX `FKjcfjqqqa4v1linjqxno5lblo0` (`product_id`),
	ADD CONSTRAINT `FKjcfjqqqa4v1linjqxno5lblo0` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);


UPDATE `downloads`.`device` SET `product_id`='2' WHERE  `device_id`=1;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=1;
UPDATE `downloads`.`device` SET `product_id`='1' WHERE  `device_id`=2;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=2;
UPDATE `downloads`.`device` SET `product_id`='1' WHERE  `device_id`=3;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=3;
UPDATE `downloads`.`device` SET `product_id`='5' WHERE  `device_id`=4;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=4;
UPDATE `downloads`.`device` SET `product_id`='5' WHERE  `device_id`=5;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=5;
UPDATE `downloads`.`device` SET `product_id`='5' WHERE  `device_id`=6;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=6;
UPDATE `downloads`.`device` SET `product_id`='5' WHERE  `device_id`=7;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=7;
UPDATE `downloads`.`device` SET `product_id`='1' WHERE  `device_id`=8;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=8;
UPDATE `downloads`.`device` SET `product_id`='3' WHERE  `device_id`=9;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=9;
UPDATE `downloads`.`device` SET `product_id`='3' WHERE  `device_id`=10;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=10;
UPDATE `downloads`.`device` SET `product_id`='5' WHERE  `device_id`=8;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=8;
UPDATE `downloads`.`device` SET `product_id`='5' WHERE  `device_id`=9;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=9;
UPDATE `downloads`.`device` SET `product_id`='1' WHERE  `device_id`=10;
SELECT `device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id` FROM `downloads`.`device` WHERE  `device_id`=10;

INSERT INTO `device` (`device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id`) VALUES (11, '2020-07-23 11:59:52', '2020-07-23 11:59:53', 'REALWEAR', 'Realwear', 'ANDROID', '<span style="color: #1468e2">HMT-1</span>용 APK', 3);
INSERT INTO `device` (`device_id`, `created_at`, `updated_at`, `type`, `type_description`, `model`, `model_description`, `product_id`) VALUES (12, '2020-07-23 11:59:52', '2020-07-23 11:59:53', 'MOBILE', 'Google Play', '스마트폰/타블릿', '<span style="color: #1468e2">스마트폰/타블릿</span>', 3);



update app set app.device_id=11 where app.product_id=3 and app.device_id=2;
update app set app.device_id=12 where app.product_id=3 and app.device_id=3;
UPDATE app SET app.device_id=8 WHERE app.app_url="https://file.virnect.com/app/v1.0.0_vtracksample_windows_pc_201223.zip";
