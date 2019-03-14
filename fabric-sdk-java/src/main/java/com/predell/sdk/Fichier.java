package com.predell.sdk;

import javax.json.JsonObject;

/**
 * <h1>CarRecord</h1>
 * <p>
 * Value object holding the card record
 */
public class Fichier {

    private String key;
    private String nomFichier;
    private String hashIpfs;
    private Boolean toRetrieve;
    private Boolean isRetrieved;
    private String owner ;

    public Fichier() {
        this(null, null, null, null, null, null);
    }

    public Fichier(String _hashIpfs, String _nomFichier, Boolean _toRetrieve, Boolean _isRetrieved, String _owner) {
        this(null, _hashIpfs, _nomFichier, _toRetrieve, _isRetrieved, _owner);
    }

    public Fichier(String _key, String _hashIpfs, String _nomFichier, Boolean _toRetrieve, Boolean _isRetrieved, String _owner) {
        this.key = _key;
        this.hashIpfs = _hashIpfs;
        this.nomFichier = _nomFichier;
        this.toRetrieve = _toRetrieve;
        this.isRetrieved = _isRetrieved;
        this.owner = _owner;
    }
    
    public String getKey() {
        return key ;
    }
    
    public void setKey(String _key) {
        this.key = _key;
    }
    
    public String getHashIpfs() {
        return hashIpfs;
    }

    public void setHashIpfs(String _hashIpfs) {
        this.hashIpfs = _hashIpfs;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String _nomFichier) {
        this.nomFichier = _nomFichier;
    }

    public Boolean getToRetrieve() {
        return toRetrieve;
    }

    public void setToRetrieve(Boolean _toRetrieve) {
        this.toRetrieve = _toRetrieve;
    }
    
    public Boolean getIsRetrieved() {
        return isRetrieved;
    }

    public void setIsRetrieved(Boolean _isRetrieved) {
        this.isRetrieved = _isRetrieved;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public static Fichier fromJsonObject(JsonObject json) {
        return new Fichier(
                json.getString("hashIpfs"), json.getString("nomFichier"),
                Boolean.valueOf(json.getString("toRetrieve")), Boolean.valueOf(json.getString("isRetrieved")),
                json.getString("owner")
        );
    }

    @Override
    public String toString() {
        return "Fichier {" +
                "\n\tkey='" + key + '\'' +
                "\n\t, hashIpfs='" + hashIpfs + '\'' +
                "\n\t, nomFichier='" + nomFichier + '\'' +
                "\n\t, toRetrieve='" + toRetrieve + '\'' +
                "\n\t, isRetrieved='" + isRetrieved + '\'' +
                "\n\t, owner='" + owner + '\'' +
                "\n}";
    }
}
