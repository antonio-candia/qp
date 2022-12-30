JAVA = java
JAVAC = javac
CLASSPATH = .:../qp.jar:../lib/ganymedeServer.jar:../crypt:../plugins


all: qpseq.class

qpseq.class: qpseq.java
	${JAVAC} -classpath ${CLASSPATH} qpseq.java

qpsgui.class: qpsgui.java ByscanDialog.class BydictDialog.class
	${JAVAC} -classpath ${CLASSPATH} qpsgui.java

BydictDialog.class: BydictDialog.java
	${JAVAC} -classpath ${CLASSPATH} BydictDialog.java

ByscanDialog.class: ByscanDialog.java
	${JAVAC} -classpath ${CLASSPATH} ByscanDialog.java

run: qpsgui.class
	${JAVA} -classpath ${CLASSPATH} qpsgui

clean:
	rm *.class
