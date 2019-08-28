* Generating a Development Key Hash for facebook
  * C:\Program Files\Java\jdk1.8.0_211\bin>    keytool -exportcert -alias androiddebugkey -keystore %HOMEPATH%\.android\debug.keystore | C:\openssl\bin\openssl.exe sha1 -binary | C:\openssl\bin\openssl.exe enc -a -e
