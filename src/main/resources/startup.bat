@echo off
#start jre1.8/bin/java -jar devkits-1.0.0.jar
if "%JAVA_HOME%" == "" (
    echo Can't find JAVA_HOME
) else (
    start javaw -jar devkits-1.0.0.jar
)