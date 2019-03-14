/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.predell;

import com.predell.sdk.AppUser;
import com.predell.sdk.Fichier;
import com.predell.sdk.HFJavaSDKBasicExample;
import com.predell.sdk.HFJavaSDKChainCodeInvocationExample;
import fr.rhaz.ipfs.IPFSDaemon;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

/**
 *
 * @author person
 */
public class ClientSender { 
    
  private static final Logger log = Logger.getLogger(ClientSender.class);
  public static final String CONNECTOR_NAME = "emetteur";  
  public static final String IP_HYPERLEDGER_NETWORK = "10.0.15.100";
  public static final String IP_IPFS_SERVER = "127.0.0.1" ;
  
  public static class MyThread extends Thread {
        
        public MyThread () {
        }

        @Override
        public void run () {
            IPFSDaemon ipfsd = new IPFSDaemon();
            ipfsd.download();
            ipfsd.start();
        }
    }
    
  public static void main(String[] args) throws Exception {
        
        MyThread thread = new MyThread() ;
        thread.start();
        
        TimeUnit.SECONDS.sleep(10);
        
        IPFS ipfs = new IPFS("/ip4/"+IP_IPFS_SERVER+"/tcp/5001");
        ipfs.refs.local();
        NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File(args[0]));
        MerkleNode addResult = ipfs.add(file).get(0);
        
        log.debug("HASH : "+addResult.hash);
        
        HFJavaSDKBasicExample hyperledgerServer = new HFJavaSDKBasicExample(IP_HYPERLEDGER_NETWORK) ;
        HFCAClient caClient = hyperledgerServer.getHfCaClient("http://"+IP_HYPERLEDGER_NETWORK+":7054", null);
        AppUser user = hyperledgerServer.getAdmin(caClient);
        HFClient client = hyperledgerServer.getHfClient();
        client.setUserContext(user);
        Channel channel = hyperledgerServer.getChannel(client);
        
        log.debug("HYPERLEDGER CONNECTED");
        
        HFJavaSDKChainCodeInvocationExample.addFichier(client, channel, addResult.hash.toString()+args[0]+CONNECTOR_NAME, addResult.hash.toString(), args[0], CONNECTOR_NAME);
        
        HFJavaSDKChainCodeInvocationExample.envoiFichier(client, channel, addResult.hash.toString()+args[0]+CONNECTOR_NAME, "recepteur");
        
        Fichier fichierRecord = HFJavaSDKChainCodeInvocationExample.queryFichier(client, addResult.hash.toString()+args[0]+CONNECTOR_NAME);
        log.info(fichierRecord);
  }
}