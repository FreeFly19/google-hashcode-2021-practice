javac src/Main.java

mkdir output || true

for x in a b c d e; do
  time java -cp src Main < input/$x.in > output/$x.out && echo $x
done