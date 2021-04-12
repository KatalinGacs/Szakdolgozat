@echo off 
    setlocal enableextensions disabledelayedexpansion

    rem Where to find java information in registry
    set "javaKey=HKLM\SOFTWARE\JavaSoft\Java Runtime Environment"

    rem Get current java version
    set "javaVersion="
    for /f "tokens=3" %%v in ('reg query "%javaKey%" /v "CurrentVersion" 2^>nul') do set "javaVersion=%%v"

    rem Test if a java version has been found
    if not defined javaVersion (
        echo Java version not found
        goto endProcess
    )

    rem Get java home for current java version
    set "javaDir="
    for /f "tokens=2,*" %%d in ('reg query "%javaKey%\%javaVersion%" /v "JavaHome" 2^>nul') do set "javaDir=%%e"

    if not defined javaDir (
        echo Java directory not found
		start jre-8u271-windows-i586.exe
    ) else (
		echo JAVA_HOME : %javaDir%

		echo %javaDir%|findstr /r "jre1\.8"
		rem if errorlevel 1 (
			start jre-8u271-windows-i586.exe
		rem ) 
		start szakdolgozat_V1_0.exe
    )

:endProcess 
    endlocal