@echo off

set CLASSPATH="../lib/*"

java -cp %CLASSPATH% atsb.eve.astrographer.Astrographer $@
