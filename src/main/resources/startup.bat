@echo off
if "%JAVA_HOME%" == "" (
    echo Can't find JAVA_HOME, Contact to 'admin@devkits.cn' for service!
	pause
) else (
    start javaw -jar devkits-1.0.1.jar
)