package edu.upc.bdma;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Gerard on 07/06/2017.
 */
public class UserRoute {
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("route")
    private Route route;

    public UserRoute() {
        // Empty constructor required as of Neo4j API 2.0.5
    };

    public UserRoute(String pUserName, Route pRoute){
        userName = pUserName;
        route = pRoute;
    }

/*    public UserRoute(String pUserName){
        System.out.println("OUT: "+pUserName);
    }
**/
    public String getUserName() {
        return userName;
    }

    public Route getRoute() {
        return route;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
