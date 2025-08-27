## Добавление jar файла (на локальном устройстве, не на Virtual Private Server)

1. Собрать shadowJar:
   `./gradlew shadowJar` в консоли ИЛИ значок gradle на панели справа -> shadow -> shadowJar -> run в Intellij IDEA

2. Запуск jar (для проверки):
   java -jar build/libs/KotlinTelegramBot-1.0-SNAPSHOT-all.jar <ТОКЕН ТЕЛЕГРАМ>

## Подключение и установка java (для нового VPS)

1. Подключение к серверу:
   `ssh root@001.001.001.001`

2. Обновить установленные пакеты:
   `apt update`
   `apt upgrade`

3. Установка jdk:
   `apt install default-jdk`

4. Убедиться, что jdk установлен:
   `java --version`

## Запуск бота в фоне

1. Копировать jar на VPS переименовывая его одновременно в CringeLearningBot.jar:
   `scp build/libs/KotlinTelegramBot-1.0-SNAPSHOT-all.jar root@001.001.001.001:/root/CringeLearningBot.jar`

2. Копировать words.txt на VPS:
   `scp words.txt root@001.001.001.001:/root/words.txt`

3. Запустить бота в фоне командой:
   `nohup java -jar CringeLearningBot.jar <ТОКЕН ТЕЛЕГРАМ> &`

## Остановка бота

1. Найти данный фоновый процесс (искомый PID это второе поле):
   `ps aux | grep java`

2. Убить этот процесс по его PID:
   `kill -9 <PID>`