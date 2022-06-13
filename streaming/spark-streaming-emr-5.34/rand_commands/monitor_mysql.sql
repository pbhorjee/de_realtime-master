
DROP PROCEDURE IF EXISTS `MyDataStatus`;
 
CREATE PROCEDURE `MyDataStatus` ()
BEGIN
    SET @SecondsToPause = 4;

    WHILE 1 DO
        SELECT COUNT(*)  FROM bus_status;

        DO SLEEP(@SecondsToPause);
    END  WHILE;
END //





SELECT directionid, 
  event_time, 
  heading, 
  id, 
  kph, 
  lat as Latitude, 
  leadingvehicleid, 
  lon as Longitude, 
  predictable, 
  record_id from data_engineer.bus_status 