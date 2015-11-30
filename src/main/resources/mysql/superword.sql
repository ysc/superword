CREATE DATABASE superword
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;

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

  `user_name` VARCHAR(200)  NOT NULL,

  `text` TEXT  NOT NULL,

  `md5` VARCHAR(32)  NOT NULL UNIQUE,

  `date_time` TIMESTAMP  NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `user_name_INDEX` (`user_name` ASC) ,

  INDEX `date_time_INDEX` (`date_time` ASC) );

CREATE  TABLE `superword`.`user_url` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `user_name` VARCHAR(200)  NOT NULL,

  `url` VARCHAR(200)  NOT NULL,

  `md5` VARCHAR(32)  NOT NULL UNIQUE,

  `date_time` TIMESTAMP  NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `user_name_INDEX` (`user_name` ASC) ,

  INDEX `date_time_INDEX` (`date_time` ASC) );

CREATE  TABLE `superword`.`user_book` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `user_name` VARCHAR(200)  NOT NULL,

  `book` VARCHAR(200)  NOT NULL,

  `md5` VARCHAR(32)  NOT NULL UNIQUE,

  `date_time` TIMESTAMP  NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `user_name_INDEX` (`user_name` ASC) ,

  INDEX `date_time_INDEX` (`date_time` ASC) );

CREATE  TABLE `superword`.`user` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `user_name` VARCHAR(35)  NOT NULL UNIQUE,

  `password` VARCHAR(32)  NOT NULL,

  `date_time` TIMESTAMP  NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `user_name_INDEX` (`user_name` ASC) ,

  INDEX `date_time_INDEX` (`date_time` ASC) );

CREATE  TABLE `superword`.`user_dynamic_suffix` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `user_name` VARCHAR(200)  NOT NULL,

  `dynamic_suffix` VARCHAR(50)  NOT NULL,

  `md5` VARCHAR(32)  NOT NULL UNIQUE,

  `date_time` TIMESTAMP  NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `user_name_INDEX` (`user_name` ASC) ,

  INDEX `date_time_INDEX` (`date_time` ASC) );

CREATE  TABLE `superword`.`user_dynamic_prefix` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `user_name` VARCHAR(200)  NOT NULL,

  `dynamic_prefix` VARCHAR(50)  NOT NULL,

  `md5` VARCHAR(32)  NOT NULL UNIQUE,

  `date_time` TIMESTAMP  NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `user_name_INDEX` (`user_name` ASC) ,

  INDEX `date_time_INDEX` (`date_time` ASC) );

CREATE  TABLE `superword`.`user_similar_word` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `user_name` VARCHAR(200)  NOT NULL,

  `similar_word` VARCHAR(32)  NOT NULL,

  `md5` VARCHAR(32)  NOT NULL UNIQUE,

  `date_time` TIMESTAMP  NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `user_name_INDEX` (`user_name` ASC) ,

  INDEX `date_time_INDEX` (`date_time` ASC) );

CREATE  TABLE `superword`.`user_qq` (

  `id` INT NOT NULL AUTO_INCREMENT ,

  `user_name` VARCHAR(35)  NOT NULL UNIQUE,

  `password` VARCHAR(32)  NOT NULL,

  `nickname` VARCHAR(32)  NOT NULL,

  `gender` VARCHAR(5)  NOT NULL,

  `birthday` VARCHAR(10)  NOT NULL,

  `location` VARCHAR(50)  NOT NULL,

  `avatarURL30` VARCHAR(150)  NOT NULL,

  `avatarURL50` VARCHAR(150)  NOT NULL,

  `avatarURL100` VARCHAR(150)  NOT NULL,

  `date_time` TIMESTAMP  NOT NULL ,

  PRIMARY KEY (`id`) ,

  INDEX `user_name_INDEX` (`user_name` ASC) ,

  INDEX `date_time_INDEX` (`date_time` ASC) );