package remote;

import java.io.Serializable;

public class user implements Serializable {
    public String name;
    public IClient client;

    //constructor
    public user(String name, IClient client){
        this.name = name;
        this.client = client;
    }

    //getters
    public String getName(){
        return name;
    }
    public IClient getClient(){
        return client;
    }
}
