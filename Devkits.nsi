;NSIS Modern User Interface
;Welcome/Finish Page Example Script
;Written by Joost Verburg

!define PRODUCT_NAME "devkits"
!define PRODUCT_VERSION "1.0.1"


;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------
;General

  ;Name and file
  Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"

  VIProductVersion "1.0.0.0"
  VIAddVersionKey /LANG=2052 "ProductName"        "蛋壳"
  VIAddVersionKey /LANG=2052 "CompanyName"        "七秒记忆网络科技服务部"
  VIAddVersionKey /LANG=2052 "ProductVersion"     "${PRODUCT_VERSION}"
  VIAddVersionKey /LANG=2052 "LegalCopyright"     "©2020 七秒记忆网络科技服务部"
  VIAddVersionKey /LANG=2052 "FileDescription"    "Devkits安装程序"
  VIAddVersionKey /LANG=2052 "FileVersion"        "${PRODUCT_VERSION}"
  
  BrandingText "七秒记忆网络科技服务部"

  OutFile "${PRODUCT_NAME}-${PRODUCT_VERSION}.exe"
  Unicode True

  ;Default installation folder
  InstallDir "$PROGRAMFILES\${PRODUCT_NAME}"

  ;从注册表获取安装文件夹（如果可用）
  InstallDirRegKey HKCU "Software\qmjy\${PRODUCT_NAME}" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel user

;--------------------------------
;Interface Settings

  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "LICENSE"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH

  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH

;--------------------------------
;Languages

  !insertmacro MUI_LANGUAGE "SimpChinese"
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "!Core" SecDummy

  ReadEnvStr $R0 JAVA_HOME
  
  SetOutPath "$INSTDIR\jre"
  File /r ".\jre\*.*"

  SetOutPath "$INSTDIR"
  
  ;ADD YOUR OWN FILES HERE...
  File /r ".\target\devkits-1.0.1\*.*"

  ;Store installation folder
  WriteRegStr HKCU "Software\qmjy\${PRODUCT_NAME}" "" $INSTDIR

  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecDummy ${LANG_SIMPCHINESE} "程序核心组件"

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...
  RMDir /r "$INSTDIR\bin\"
  RMDir /r "$INSTDIR\libs\"
  RMDir /r "$INSTDIR\logs\"
  RMDir /r "$INSTDIR\jre\"
  Delete "$INSTDIR\Uninstall.exe"
  RMDir /r "$INSTDIR"

  DeleteRegKey /ifempty HKCU "Software\qmjy\${PRODUCT_NAME}"

SectionEnd