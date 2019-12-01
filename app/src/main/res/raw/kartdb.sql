CREATE TABLE `Building` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(45) NOT NULL,
  `address` varchar(100) NOT NULL,
  `Latitude` decimal(10,8) NOT NULL,
  `Longitude` decimal(10,8) NOT NULL,
  PRIMARY KEY (`ID`)
);

CREATE TABLE `Room` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(100) NOT NULL,
  `Description` varchar(100) NOT NULL,
  `Floor` varchar(45) NOT NULL,
  `Building_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `Building_ID` (`Building_ID`),
  CONSTRAINT `Room_ibfk_1` FOREIGN KEY (`Building_ID`) REFERENCES `Building` (`ID`) ON DELETE CASCADE
);

CREATE TABLE `Orders` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `OrderDate` date NOT NULL,
  `FromHour` int(11) NOT NULL,
  `ToHour` int(11) NOT NULL,
  `Name` varchar(45) NOT NULL,
  `Description` varchar(200) NOT NULL,
  `Mail` varchar(100) NOT NULL,
  `Room_ID` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `Room_ID` (`Room_ID`),
  CONSTRAINT `Orders_ibfk_1` FOREIGN KEY (`Room_ID`) REFERENCES `Room` (`ID`) ON DELETE CASCADE
);