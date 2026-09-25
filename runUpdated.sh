#!/bin/sh -e
#cd $( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

echo "pulling from git"
git pull

echo "killing old process(es)"
while [ $(jps | grep P1FR0B | wc -l) != '0' ]; do
	PID=$(jps | grep P1FR0B | awk '{print $1;}' | head -n 1)
	kill $PID
	timeout 60 tail --pid=$PID -f /dev/null
	if [ $(ps | grep $PID | wc -l) != '0' ]; then
		echo process $PID declined when politely asked to kill themself, issuing SIGKILL
		kill -s SIGKILL $PID
	fi
done
mv log log.old
rm P1FR0B-all.jar

echo "compiling jar"
./gradlew shadowjar
mv build/libs/P1FR0B-all.jar .


echo "running"
nohup java -jar P1FR0B-all.jar > /dev/null
