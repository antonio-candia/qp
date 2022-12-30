JAVAC = javac -d classes -Xlint:unchecked
CLASSPATH = classes:ganymedeServer.jar:crypt


all: qpAlgorithm.class Task.class Rules.class Search.class Benchmark.class

qpAlgorithm.class: src/qp/qpAlgorithm.java
	${JAVAC} -classpath ${CLASSPATH} src/qp/qpAlgorithm.java

Task.class: src/qp/Task.java
	${JAVAC} -classpath ${CLASSPATH} src/qp/Task.java

Rules.class: src/qp/Rules.java
	${JAVAC} -classpath ${CLASSPATH} src/qp/Rules.java

Search.class: src/qp/Search.java classes/qp/Rules.class
	${JAVAC} -classpath ${CLASSPATH} src/qp/Search.java

Benchmark.class: src/qp/Benchmark.java classes/qp/Search.class classes/qp/Rules.class
	${JAVAC} -classpath ${CLASSPATH} src/qp/Benchmark.java

qp.jar:
	(cd classes;jar cvf ../qp.jar qp/*.class)

clean:
	rm src/qp/*.class
