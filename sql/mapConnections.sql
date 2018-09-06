CREATE TABLE `eve`.`mapConnections` (
  `connectionId` BIGINT AUTO_INCREMENT,
  `systemA` INT NOT NULL,
  `systemB` INT NULL,
  `distance` DOUBLE NULL,
  `gate` TINYINT NULL,
  `jdrive` TINYINT NULL,
  `jbridge` TINYINT NULL,
  PRIMARY KEY (`connectionId`),
  INDEX `sysA` (`systemA` ASC),
  INDEX `sysB` (`systemB` ASC),
  INDEX `distance` (`distance` ASC),
  INDEX `abd` (`systemA` ASC, `systemB` ASC, `distance` ASC));
