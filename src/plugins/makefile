all: qpcrypt.class qpjcrypt.class qpmd5.class

qpcrypt.class: qpcrypt.java
	javac -classpath .:../qp.jar:../lib qpcrypt.java

qpjcrypt.class: qpjcrypt.java
	javac -classpath .:../qp.jar:../lib qpjcrypt.java

qpmd5.class: qpmd5.java
	javac -classpath .:../qp.jar:../ganymedeServer.jar qpmd5.java

libqpnativecryptImp.so: qpnativecryptImp.c 
	gcc -O3 -o libqpnativecryptImp.so -shared \
		-Wl,-soname,libqpnativecryptImp.so \
		-I/opt/sun-jdk-1.5.0.11/include \
		-I/opt/sun-jdk-1.5.0.11/include/linux qpnativecryptImp.c  \
		-static -lcrypt

	
libqpnativeblowfishImp.so: qpnativeblowfishImp.c 
	gcc -O3 -o libqpnativeblowfishImp.so  -shared\
		-Wl,-soname,libqpnativeblowfishImp.so \
		-I/opt/sun-jdk-1.5.0.11/include \
		-I/opt/sun-jdk-1.5.0.11/include/linux qpnativeblowfishImp.c  \
		-I/home/candia/antonio/pg/qp/progc/bfsh-koc \
		-L/home/candia/antonio/pg/qp/progc/bfsh-koc 

libqpnativerijndaelImp.so: qpnativerijndaelImp.c 
	gcc -O3 -o libqpnativerijndaelImp.so  -shared\
		-Wl,-soname,libqpnativerijndaelImp.so \
		-I/opt/sun-jdk-1.5.0.11/include \
		-I/opt/sun-jdk-1.5.0.11/include/linux qpnativerijndaelImp.c  \
		-I/home/candia/antonio/pg/qp/progc/aes 

