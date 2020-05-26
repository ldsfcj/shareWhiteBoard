package remote;

import remote.IClient;

import java.io.Serializable;

public class user implements Serializable {
    public String name;
    public IClient client;

    //constructor
    public user(String name, IClient client){
        this.name = name;
        this.client = client;
    }
    //getters and setters
    public String getName(){
        return name;
    }
    public IClient getClient(){
        return client;
    }
}
