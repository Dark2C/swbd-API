# REST API per esame di SWBD

Il server REST fornisce gli entrypoint per la gestione e il monitoraggio di reti di sensori inerenti ad impianti elettrici (maggiori dettagli sulla specifica del progetto sono presenti nel file di specifica di requisiti).


## Guida per l'installazione
### Step 1: Esegui questi comandi nel terminale per scaricare e compilare i sorgenti e caricarli in un'istanza di Apache Tomcat
sudo apt install git default-jdk maven\
wget https://downloads.apache.org/tomcat/tomcat-9/v9.0.37/bin/apache-tomcat-9.0.37.tar.gz\
tar -zxvf apache-tomcat-9.0.37.tar.gz\
rm apache-tomcat-9.0.37.tar.gz\
cd apache-tomcat-9.0.37/\
rm -rf ./webapps/\
mkdir webapps\
cd ../\
mkdir API\
cd API/\
git init\
git pull https://github.com/Dark2C/swbd-API\
mvn package\
cd target/\
unzip API-0.1-SNAPSHOT.war -d ../../apache-tomcat-9.0.37/webapps/API/\
cd ../../


### Step 2: Importa lo schema del database (sul server dove è installato mySQL)
user@localhost:~/API/$ mysql swbd < db_schema.sql

### Step 3: Configura il servizio ed avvia tomcat
user@localhost:~/$ cd apache-tomcat-9.0.37/\
(Modificare il file web.xml secondo la configurazione del database server)\
user@localhost:~/apache-tomcat-9.0.37/$ nano webapps/WEB-INF/web.xml\
user@localhost:~/apache-tomcat-9.0.37/$ ./bin/catalina.sh start

### Il server REST è pronto ed è in ascolto alla porta 8080 al path /API/v1/
Enjoy!
