#!/bin/sh
cd $(dirname "$0")/..
base=`pwd`

classpath="$base/lib/*"

java -cp "$classpath" atsb.eve.astrographer.Astrographer $@
