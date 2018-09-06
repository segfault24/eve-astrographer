@echo off

set CLASSPATH="../lib/*"
set PROPS="../cfg/db.ini"

java -cp %CLASSPATH% -Dconfig=%PROPS% atsb.eve.astrographer.Astrographer $@
