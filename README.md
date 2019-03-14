# IPFSxFabric

Test send files with IPFS on Hyperledger Fabric Blockchain

To use :
Download hyperledger fabric sample with fabcar exemple.
Remplace fabcar.go with given chaincode.
Change IP_HYPERLEDGER_NETWORK in both ClientSender.java and ClientReceiver.java
Change IP_IPFS_SERVER in ClientReceiver.java to match sender IP
Launch hyperledger fabric fabcar example.
Launch ClientSender jar with path to file as arguments.
Launch ClientReceiver, should receive all files on Sender IPFS server.
