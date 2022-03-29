# OpenSw 과제 전용 


## 명령어 (컴파일)
  jsoup 버젼 및 컴파일시 classpath 문제로 인하여 명령어를 --module-path를 사용하여 컴파일 하였습니다. 
  
  `javac --module-path jars/jsoup-1.14.3.jar:jars/kkma-2.1.jar src/scripts/*.java -d bin -encoding UTF8`
  
## 명령어 실행 (2주차)
  `java -cp ./jars/jsoup-1.14.3.jar:./jars/kkma-2.1.jar:bin scripts.kuir -c data`
## 명령어 실행  (3주차)
  `java -cp ./jars/jsoup-1.14.3.jar:./jars/kkma-2.1.jar:bin scripts.kuir -k ./collection.xml`
## 명령어 실행  (4주차)
  `java -cp ./jars/jsoup-1.14.3.jar:./jars/kkma-2.1.jar:bin scripts.kuir -i ./index.xml`
  
