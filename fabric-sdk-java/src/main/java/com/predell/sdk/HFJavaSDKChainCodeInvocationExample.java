package com.predell.sdk;

import com.google.protobuf.ByteString;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.ChaincodeResponse;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class HFJavaSDKChainCodeInvocationExample {

    private static final Logger log = Logger.getLogger(HFJavaSDKChainCodeInvocationExample.class);
    
    public static Fichier queryFichier(HFClient client, String key)
            throws InvalidArgumentException, ProposalException {
        Channel channel = client.getChannel("mychannel");
        QueryByChaincodeRequest qpr = client.newQueryProposalRequest();
        ChaincodeID chainCodeId = ChaincodeID.newBuilder().setName("fabcar").build();
        qpr.setChaincodeID(chainCodeId);
        qpr.setFcn("queryFichier");
        qpr.setArgs(new String[]{key});

        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(qpr);
        for (ProposalResponse response : queryProposals) {
            if (response.isVerified() && response.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                ByteString payload = response.getProposalResponse().getResponse().getPayload();
                log.info("'" + payload.toStringUtf8() + "'");
                try (JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(payload.toByteArray()))) {
                    Fichier fichierRecord = Fichier.fromJsonObject(jsonReader.readObject());
                    fichierRecord.setKey(key);
                    return fichierRecord;
                }
            } else {
                throw new RuntimeException("response failed. status: " + response.getStatus());
            }
        }
        return null;
    }


    public static Map<String, Fichier> queryAllFichiersByOwner(HFClient client, String owner)
            throws ProposalException, InvalidArgumentException {
        // get channel instance from client
        Channel channel = client.getChannel("mychannel");
        // create chaincode request
        QueryByChaincodeRequest qpr = client.newQueryProposalRequest();
        // build cc id providing the chaincode name. Version is omitted here.
        ChaincodeID fabcarCCId = ChaincodeID.newBuilder().setName("fabcar").build();
        qpr.setChaincodeID(fabcarCCId);
        // CC function to be called
        qpr.setFcn("queryAllFichiersByOwner");
        qpr.setArgs(new String[]{owner});
        Collection<ProposalResponse> responses = channel.queryByChaincode(qpr);
        for (ProposalResponse response : responses) {
            if (response.isVerified() && response.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                ByteString payload = response.getProposalResponse().getResponse().getPayload();
                try (JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(payload.toByteArray()))) {
                    // parse response
                    JsonArray arr = jsonReader.readArray();
                    Map<String, Fichier> cars = new HashMap<>();
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject rec = arr.getJsonObject(i);
                        Fichier fichier = getFichier(rec);
                        cars.put(fichier.getHashIpfs(), fichier);
                    }
                    return cars;
                }
            } else {
                log.error("response failed. status: " + response.getStatus().getStatus());
            }
        }
        return Collections.emptyMap();
    }

    private static Fichier getFichier(JsonObject rec) {
        String key = rec.getString("Key");
        JsonObject fichierRec = rec.getJsonObject("Record");
        Fichier fichier = Fichier.fromJsonObject(fichierRec);
        fichier.setKey(key);
        return fichier;
    }


    public static void addFichier(HFClient client, Channel channel, String key, String hashIpfs, String nomFichier, String owner)
            throws ProposalException, InvalidArgumentException, InterruptedException, ExecutionException, TimeoutException {
        BlockEvent.TransactionEvent event = sendTransactionAddFichier(client, channel, key, hashIpfs, nomFichier, owner).get(60, TimeUnit.SECONDS);
        if (event.isValid()) {
            log.info("Transacion tx: " + event.getTransactionID() + " is completed.");
        } else {
            log.error("Transaction tx: " + event.getTransactionID() + " is invalid.");
        }
    }

    private static CompletableFuture<BlockEvent.TransactionEvent> sendTransactionAddFichier(HFClient client, Channel channel, String key, String hashIpfs, String nomFichier, String owner)
            throws InvalidArgumentException, ProposalException {
        TransactionProposalRequest tpr = client.newTransactionProposalRequest();
        ChaincodeID cid = ChaincodeID.newBuilder().setName("fabcar").build();
        tpr.setChaincodeID(cid);
        tpr.setFcn("createFichier");
        tpr.setArgs(new String[]{key, hashIpfs, nomFichier, owner});
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(tpr);
        List<ProposalResponse> invalid = responses.stream().filter(r -> r.isInvalid()).collect(Collectors.toList());
        if (!invalid.isEmpty()) {
            invalid.forEach(response -> {
                log.error(response.getMessage());
            });
            throw new RuntimeException("invalid response(s) found");
        }
        return channel.sendTransaction(responses);
    }
    
    public static void envoiFichier(HFClient client, Channel channel, String key, String newOwner) 
            throws ProposalException, InvalidArgumentException, InterruptedException, ExecutionException, TimeoutException {
        BlockEvent.TransactionEvent event = sendTransactionEnvoiFichier(client, channel, key, newOwner).get(60, TimeUnit.SECONDS);
        if (event.isValid()) {
            log.info("Transacion tx: " + event.getTransactionID() + " is completed.");
        } else {
            log.error("Transaction tx: " + event.getTransactionID() + " is invalid.");
        }
    }
    
    private static CompletableFuture<BlockEvent.TransactionEvent> sendTransactionEnvoiFichier(HFClient client, Channel channel, String key, String newOwner)
            throws InvalidArgumentException, ProposalException {
        TransactionProposalRequest tpr = client.newTransactionProposalRequest();
        ChaincodeID cid = ChaincodeID.newBuilder().setName("fabcar").build();
        tpr.setChaincodeID(cid);
        tpr.setFcn("envoiFichier");
        tpr.setArgs(new String[]{key, newOwner});
        Collection<ProposalResponse> responses = channel.sendTransactionProposal(tpr);
        List<ProposalResponse> invalid = responses.stream().filter(r -> r.isInvalid()).collect(Collectors.toList());
        if (!invalid.isEmpty()) {
            invalid.forEach(response -> {
                log.error(response.getMessage());
            });
            throw new RuntimeException("invalid response(s) found");
        }
        return channel.sendTransaction(responses);
    }
}
