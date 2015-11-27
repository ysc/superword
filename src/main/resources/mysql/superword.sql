CREATE DATABASE superword;

USE superword;

CREATE  TABLE `superword`.`user_word` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `user_name` VARCHAR(200)  NOT NULL ,

  `word` VARCHAR(200)  NOT NULL ,

  `dictionary` VARCHAR(200)  NOT NULL ,

  `date_time` TIMESTAMP  NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `user_name_INDEX` (`user_name` ASC) ,

  INDEX `date_time_INDEX` (`date_time` ASC) );

CREATE  TABLE `superword`.`user_text` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `user_name` VARCHAR(200)  NOT NULL UNIQUE,

  `text` LONGTEXT  NOT NULL ,

  `date_time` TIMESTAMP  NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `user_name_INDEX` (`user_name` ASC) ,

  INDEX `date_time_INDEX` (`date_time` ASC) );