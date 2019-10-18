@echo off

setLocal EnableDelayedExpansion
set CLASSPATH="
for /R ../lib %%a in (*.jar) do (
set CLASSPATH=!CLASSPATH!;%%a
)

set CLASSPATH=!CLASSPATH!";./;
start javaw -jar devkits-1.0.0.jar