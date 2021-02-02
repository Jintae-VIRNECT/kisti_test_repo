ALTER TABLE `device`
    ADD COLUMN `product_id` BIGINT(20) NOT NULL;
ALTER TABLE `device`
    ADD INDEX `FKjcfjqqqa4v1linjqxno5lblo0` (`product_id`),
	ADD CONSTRAINT `FKjcfjqqqa4v1linjqxno5lblo0` FOREIGN KEY (`product_id`) REFERENCES `product` (`product_id`);