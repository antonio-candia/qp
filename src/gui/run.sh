export LD_LIBRARY_PATH=`pwd`/../plugins
java -server -classpath .:../qp.jar:../plugins:../lib qpsgui
