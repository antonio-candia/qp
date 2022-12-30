JAVAC = javac
CLASSPATH = .:ganymedeServer.jar:crypt:plugins


all: Task.class Rules.class Search.class Benchmark.class TextRecognition.class

Task.class: Task.java
	${JAVAC} -classpath ${CLASSPATH} Task.java

Rules.class: Rules.java
	${JAVAC} -classpath ${CLASSPATH} Rules.java

Search.class: Search.java Rules.class
	${JAVAC} -classpath ${CLASSPATH} Search.java

Benchmark.class: Benchmark.java Search.class Rules.class
	${JAVAC} -classpath ${CLASSPATH} Benchmark.java

TextRecognition.class: TextRecognition.class
	${JAVAC} -classpath ${CLASSPATH} TextRecognition.java

clean:
	rm *.class
