# nfs-client
NFS client written in Java for a distributed systems course.

## Authors
- Samantha Rosso
- Jacques Dafflon

## Running the NFS client

1. Compile the code as follow:
```bash
mkdir bin
javac -d bin -cp ./lib/remotetea/classes/jrpcgen.jar:./lib/remotetea/classes/oncrpc.jar:./lib/sss/sss-0.1.jar **/*.java
```

2. Run the main class:
```bash
java -cp ./lib/remotetea/classes/jrpcgen.jar:./lib/remotetea/classes/oncrpc.jar:./lib/sss/sss-0.1.jar:./bin ch.usi.inf.ds.nfsclient.Main <config.properties>
```
where the `config.properties` is a properties files. Sample ones are given in the `configs` folder.


## Limitations
The Samir Shared Secret is highly unstable and crashes almo st ever time. It is too unstable to be usable.