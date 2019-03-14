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
import io.ipfs.api.IPFS;
import io.ipfs.multihash.Multihash;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
/**
 *
 * @author person
 */
public class ClientReceiver {
    
     private static final Logger log = Logger.getLogger(ClientReceiver.class) ;
             
    public static final String CONNECTOR_NAME = "recepteur";
    public static final String IP_HYPERLEDGER_NETWORK = "10.0.15.100";
    public static final String IP_IPFS_SERVER = "192.168.9.182";
    
    public static void main(String[] args) throws Exception {
        
        IPFS ipfs = new IPFS("/ip4/"+IP_IPFS_SERVER+"/tcp/5001");
        ipfs.refs.local();
        
        HFJavaSDKBasicExample hyperledgerServer = new HFJavaSDKBasicExample(IP_HYPERLEDGER_NETWORK) ;
        HFCAClient caClient = hyperledgerServer.getHfCaClient("http://"+IP_HYPERLEDGER_NETWORK+":7054", null);
        AppUser user = hyperledgerServer.getAdmin(caClient);
        HFClient client = hyperledgerServer.getHfClient();
        client.setUserContext(user);
        Channel channel = hyperledgerServer.getChannel(client);
        
        Map<String,Fichier> mapFichierRecord = HFJavaSDKChainCodeInvocationExample.queryAllFichiersByOwner(client, CONNECTOR_NAME);
        
        log.debug(mapFichierRecord);
        
        for(Entry<String,Fichier> fichierEntry : mapFichierRecord.entrySet()) {
            Fichier fichier = fichierEntry.getValue();
            if(fichier.getToRetrieve()) {
                byte[] byteFile = ipfs.cat(Multihash.fromBase58(fichier.getHashIpfs()));
                File file = new File("recu-"+fichier.getNomFichier());
                OutputStream os = new FileOutputStream(file); 
                os.write(byteFile);
                os.close() ;
            } 
        }
    }
}
