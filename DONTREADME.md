1. Подключение к серверу:
ssh root@001.001.001.001

2. Обновить установленные пакеты:
apt update apt upgrade

3. Установка jdk:
apt install default-jdk

4. Убедиться, что jdk установлен:
java --version

5. Соберем shadowJar командой:
./gradlew shadowJar

6. Копировать jar на VPS переименовывая его одновременно в bot.jar:
scp build/libs/CringeTelegramBot-1.0-SNAPSHOT.jar root@001.001.001.001:/root/bot.jar

7. Копировать words.txt на VPS: 
scp words.txt root@001.001.001.001:/root/words.txt

8. Подключиться к серверу по SSH:
ssh root@001.001.001.001

9. Запустить бота в фоне командой:
nohup java -jar bot.jar <ТОКЕН ТЕЛЕГРАМ> &