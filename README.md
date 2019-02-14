# TestModelJDBC

Compile: scalac -cp openmodbat-3.2.jar ModelTemplate.scala dbSim.scala

Run: scala -cp /Users/Username/Desktop/modbat/postgresql-42.2.22.jar:.: openmodbat-3.2.jar --classpath=. modbat.ModelTemplate -n=100

# Mutation Testing Configuration

tar xvfz postgresql-11.2.tar.gz
cd postgresql-11.2
./configure --prefix=/Users/Username/Desktop/postgresql-11.2
make
make install
mkdir /Users/Username/Desktop/postgresql-11.2/data
Default Port is 5432

Create the clusters
initdb -D /Users/Username/Desktop/postgresql-11.2/data

Start The server
pg_ctl -D /Users/Username/Desktop/postgresql-11.2/data -l logfile start

Create Test DB
/Users/Username/Desktop/postgresql-11.2/bin/createdb test
/Users/Username/Desktop/postgresql-11.2/bin/psql test

