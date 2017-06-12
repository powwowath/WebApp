package edu.upc.bdma;

import javafx.beans.binding.IntegerBinding;
import org.apache.commons.io.FileUtils;
import org.neo4j.driver.v1.*;

import javax.persistence.Query;
import javax.xml.soap.Node;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gerard on 28/05/2017.
 */
public class Neo4JService {
    private static final String USERNAME = "neo4j"; // Modify
    private static final String PASSWORD = "neo";
    private static final int MAX_RESULTS = 6;

    Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( USERNAME, PASSWORD ) );

    public List<Route> getRoutesN(
            String departure,
            String departureCountry,
            boolean isRound,
            int steps,
            boolean isBigCities,
            boolean isSmallCities,
            boolean checkCulture,
            boolean checkNight,
            boolean checkBeach,
            boolean checkMountain,
            boolean checkTurist,
            String distance
    ){
        List<Route> routes = new ArrayList<>();

        try ( Session session = driver.session() )
        {
            int numChecks = 0;
            if (checkBeach) numChecks++;
            if (checkCulture) numChecks++;
            if (checkMountain) numChecks++;
            if (checkNight) numChecks++;
            if (checkTurist) numChecks++;

            String query = "MATCH (c1:City)-[]-(a1:Airport)-[r1]->";

            // Intermediate steps
            for(int i = 1; i <= steps; i++) {
                query += "(a"+(i+1)+":Airport)-[r"+(i+1)+"]->";
                if (i == steps){
                    query += "(a"+(i+2)+":Airport)";
                }
            }
            String whereDistance = " AND (r1.distance";
            String wherePcts = "";
            for(int i = 1; i <= steps; i++) {
                query += " MATCH (a"+(i+1)+":Airport)-[]-(c"+(i+1)+":City)";
                if (checkBeach) wherePcts += " AND c"+(i+1)+".pctBeach > " + (50/numChecks);
                if (checkCulture) wherePcts += " AND c"+(i+1)+".pctCultural > " + (50/numChecks);
                if (checkMountain) wherePcts += " AND c"+(i+1)+".pctMountain > " + (40/numChecks);
                if (checkNight) wherePcts += " AND c"+(i+1)+".pctNightlife > " + (50/numChecks);
                if (checkTurist) wherePcts += " AND c"+(i+1)+".pctTourist > " + (50/numChecks);

                whereDistance += " + r"+(i+1)+".distance";

                if (i == steps){
                    query += " WHERE ";
                    if (isRound) {
                        query += "(a" + (i + 2) + ":Airport)-[]-(c1:City) ";
                    } else {
                        query += " c1 = c1 ";
                    }

                    query += whereDistance;
                    switch (distance){
                        case "S": query += ") < 2000";
                            break;
                        case "M": query += ") >= 2000 " + whereDistance + ") < 5000";
                            break;
                        case "L": query += ") >= 5000 " + whereDistance + ") < 15000";
                            break;
                        case "W": query += ") > 15000";
                            break;
                    }

                    query += wherePcts;
                }
            }
            query += " AND c1.name = \""+departure+"\" AND c1.country = \""+departureCountry+"\"";

            query += " RETURN DISTINCT c1, a1, r1";
            for(int i = 1; i <= steps; i++) {
                query += ", c"+(i+1)+", a"+(i+1)+", r"+(i+1)+" ";
            }

            query += " LIMIT " + MAX_RESULTS;

            System.out.println("");
            System.out.println("Query: "+query);
            System.out.println("");

            StatementResult res = session.run(query);
            List<Record> rl = res.list();


            for(int i = 0; i < rl.size(); i++){
                Route r = new Route();
                double rDistance = 0;
                int pctTourist = 0;
                int pctNightlife = 0;
                int pctCulture = 0;
                int pctBeach = 0;
                int pctMountain = 0;

                int extraPoints = 2;
                if(!isRound){
                    extraPoints--;
                }
                String lon[] = new String[steps+extraPoints];
                String lat[] = new String[steps+extraPoints];

                // Departrue
                String routeName = rl.get(i).get(0).get("name").asString() + " (" +rl.get(i).get(0).get("country").asString()+ ")";
                lon[0] = ""+rl.get(i).get(1).get("lon");
                lat[0] = ""+rl.get(i).get(1).get("lat");
                lon[0] = ""+rl.get(i).get(1).get("lon");
                lat[0] = ""+rl.get(i).get(1).get("lat");
                rDistance = rl.get(i).get(2).get("distance").asDouble();

                for (int j = 1; j <= steps; j++){
                    routeName += " - " + rl.get(i).get((j*3)).get("name").asString() + " (" +rl.get(i).get((j*3)).get("country").asString()+ ")";
                    lon[j] = "" + rl.get(i).get((j*3)+1).get("lon");
                    lat[j] = "" + rl.get(i).get((j*3)+1).get("lat");
                    rDistance += rl.get(i).get((j*3)+2).get("distance").asDouble();
                    pctBeach += rl.get(i).get((j*3)).get("pctBeach").asInt();
                    pctMountain += rl.get(i).get((j*3)).get("pctMountain").asInt();
                    pctTourist += rl.get(i).get((j*3)).get("pctTourist").asInt();
                    pctNightlife += rl.get(i).get((j*3)).get("pctNightlife").asInt();
                    pctCulture += rl.get(i).get((j*3)).get("pctCultural").asInt();
                }

                if (isRound) {
                    routeName += " - " + rl.get(i).get(0).get("name").asString() + " (" + rl.get(i).get(0).get("country").asString() + ")";
                    lon[steps + 1] = ""+rl.get(i).get(1).get("lon");
                    lat[steps + 1] = ""+rl.get(i).get(1).get("lat");
                }
                r.setName(routeName);
                r.setDeparture(rl.get(i).get(0).get("name").asString());
                r.setDepartureCountry(rl.get(i).get(0).get("country").asString());
                r.setRound(isRound);
                r.setLon(lon);
                r.setLat(lat);
                r.setnStopOver(steps);
                r.setDistance(rDistance);
                r.setIndBeach(pctBeach / steps);
                r.setIndMountain(pctMountain / steps);
                r.setIndCulture(pctCulture / steps);
                r.setIndTourist(pctTourist / steps);
                r.setIndNightlife(pctNightlife / steps);

                routes.add(r);
            }

        }
        driver.close();

        return routes;
    }















/*




    public List<Route> getRoutes(
            String departure,
            String departureCountry,
            boolean isRound,
            int steps,
            boolean isBigCities,
            boolean isSmallCities,
            boolean checkCulture,
            boolean checkNight,
            boolean checkBeach,
            boolean checkMountain,
            boolean checkTurist,
            String distance
    ){
        List<Route> routes = new ArrayList<>();

        try ( Session session = driver.session() )
        {

            int numChecks = 0;
            if (checkBeach) numChecks++;
            if (checkCulture) numChecks++;
            if (checkMountain) numChecks++;
            if (checkNight) numChecks++;
            if (checkTurist) numChecks++;

            String query = "MATCH (c1:City)-[]-(a1:Airport)-[r1]-";

            // Intermediate steps
            for(int i = 1; i <= steps; i++) {
                query += "(a"+(i+1)+":Airport)-[r"+(i+1)+"]-";
                if (i == steps){
                    query += "(a"+(i+2)+":Airport)";
                }
            }
            String whereDistance = " AND (r1.distance";
            String wherePcts = "";
            for(int i = 1; i <= steps; i++) {
                query += " MATCH (a"+(i+1)+":Airport)-[]-(c"+(i+1)+":City)";
                if (checkBeach) wherePcts += " AND c"+(i+1)+".pctBeach > " + (50/numChecks);
                if (checkCulture) wherePcts += " AND c"+(i+1)+".pctCultural > " + (50/numChecks);
                if (checkMountain) wherePcts += " AND c"+(i+1)+".pctMountain > " + (40/numChecks);
                if (checkNight) wherePcts += " AND c"+(i+1)+".pctNightlife > " + (50/numChecks);
                if (checkTurist) wherePcts += " AND c"+(i+1)+".pctTourist > " + (50/numChecks);

                whereDistance += " + r"+(i+1)+".distance";
                if (i == steps){
                    query += " WHERE ";
                    if (isRound) {
                        query += "(a" + (i + 2) + ":Airport)-[]-(c1:City) ";
                    } else {
                        query += " c1 = c1 ";
                    }

                    query += whereDistance;
                    switch (distance){
                        case "S": query += ") < 2000";
                            break;
                        case "M": query += ") >= 2000 " + whereDistance + ") < 5000";
                            break;
                        case "L": query += ") >= 5000 " + whereDistance + ") < 15000";
                            break;
                        case "W": query += ") > 15000";
                            break;
                    }

                    query += wherePcts;
                }
            }
            query += " AND c1.name = \""+departure+"\" AND c1.country = \""+departureCountry+"\"";

            query += " RETURN DISTINCT c1.name, c1.country, toString(a1.lon), toString(a1.lat)";
            for(int i = 1; i <= steps; i++) {
                query += ", c"+(i+1)+".name, c"+(i+1)+".country, toString(a"+(i+1)+".lon), toString(a"+(i+1)+".lat) ";
            }

            query += " LIMIT " + MAX_RESULTS;

            System.out.println("");
            System.out.println("Query: "+query);
            System.out.println("");

            StatementResult res = session.run(query);
            List<Record> rl = res.list();

            for(int i = 0; i < rl.size(); i++){
                Route r = new Route();
                ArrayList<City> rSteps = new ArrayList<City>();
                City step = new City();

                int extraPoints = 2;
                if(!isRound){
                    extraPoints--;
                }
                String lon[] = new String[steps+extraPoints];
                String lat[] = new String[steps+extraPoints];

                // Departrue
                String routeName = rl.get(i).get(0).asString() + " (" +rl.get(i).get(1).asString()+ ")";
                lon[0] = rl.get(i).get(2).asString();
                lat[0] = rl.get(i).get(3).asString();
                step.setName(rl.get(i).get(0).asString());
                step.setCountry(rl.get(i).get(1).asString());
                rSteps.add(step);

                int numReturnedValues = 4;
                for (int j = 1; j <= steps; j++){
                    routeName += " - " + rl.get(i).get(j*numReturnedValues).asString() + " (" +rl.get(i).get(j*numReturnedValues+1).asString()+ ")";
                    lon[j] = rl.get(i).get(j*numReturnedValues+2).asString();
                    lat[j] = rl.get(i).get(j*numReturnedValues+3).asString();

                    step = new City();
                    step.setName(rl.get(i).get(j*numReturnedValues).asString());
                    step.setCountry(rl.get(i).get(j*numReturnedValues+1).asString());
                    rSteps.add(step);
                }

                if (isRound) {
                    routeName += " - " + rl.get(i).get(0).asString() + " (" + rl.get(i).get(1).asString() + ")";
                    lon[steps + 1] = rl.get(i).get(2).asString();
                    lat[steps + 1] = rl.get(i).get(3).asString();

                    step.setName(rl.get(i).get(0).asString());
                    step.setCountry(rl.get(i).get(1).asString());
                    rSteps.add(step);
                }
                r.setName(routeName);
                r.setSteps(rSteps);

                r.setLon(lon);
                r.setLat(lat);
                routes.add(r);
            }

        }
        driver.close();

        return routes;
    }

*/

}
