@echo off
if "%JAVA_HOME%" == "" (
    echo Can't find JAVA_HOME, Contact to 'liushaofeng89@qq.com' for service!
	pause
) else (
    start javaw -Dfile.encoding=gbk -jar devkits-1.0.2.jar
)