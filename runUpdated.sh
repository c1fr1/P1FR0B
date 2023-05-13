echo "pulling from git"
git pull
rm P1FR0B-all.jar
echo "compiling jar"
gradle shadowjar
mv build/libs/P1FR0B-all.jar .
echo "running"
nohup java -jar P1FR0B-all.jar &

