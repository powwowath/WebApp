package edu.upc.bdma;

import javafx.beans.binding.IntegerBinding;
import org.apache.commons.io.FileUtils;
import org.neo4j.driver.v1.*;

import javax.persistence.Query;
import javax.xml.soap.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Gerard on 28/05/2017.
 */
public class Neo4JService {
    private static final String USERNAME = "neo4j"; // Modify
    private static final String PASSWORD = "neo";
    private static final int MAX_RESULTS = 6;
    private static final int RELATIONSHIP_BIG_CITY = 150;
    private static final int RELATIONSHIP_SMALL_CITY = 75;
    private static final int CATEGORY_THRESHOLD = 30;

    Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( USERNAME, PASSWORD ) );


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
    ) {

        List<Route> routes = getRoutesLike(departure, departureCountry, isRound, steps, isBigCities, isSmallCities, checkCulture, checkNight, checkBeach, checkMountain, checkTurist, distance);
        List<Route> routesNew = new ArrayList<>();

        int i = 0;
        while ( routes.size() < MAX_RESULTS && i < 20){
            routesNew = getRoutesN(departure, departureCountry, isRound, steps, isBigCities, isSmallCities, checkCulture, checkNight, checkBeach, checkMountain, checkTurist, distance, 1);

            if(routesNew.size() > 0) {
                if (!routes.contains(routesNew.get(0))) {
                    routes.add(routesNew.get(0));
                }
            }
            i++;
        }

/*
        List<Route> routes = getRoutesLike(departure, departureCountry, isRound, steps, isBigCities, isSmallCities, checkCulture, checkNight, checkBeach, checkMountain, checkTurist, distance);
        List<Route> routesNew = getRoutesN(departure, departureCountry, isRound, steps, isBigCities, isSmallCities, checkCulture, checkNight, checkBeach, checkMountain, checkTurist, distance, MAX_RESULTS - routes.size());

        for (int i = 0; i < routesNew.size(); i++){
            routes.add(routesNew.get(i));
        }
*/

        driver.close();

        return routes;
    }

    // New Routes
    private List<Route> getRoutesN(
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
            String distance,
            int limit
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
            String whereBigSmallCities = "";
            for(int i = 1; i <= steps; i++) {
                query += " MATCH (a"+(i+1)+":Airport)-[]-(c"+(i+1)+":City)";
                if (checkBeach) wherePcts += " AND c"+(i+1)+".pctBeach > " + (CATEGORY_THRESHOLD/numChecks);
                if (checkCulture) wherePcts += " AND c"+(i+1)+".pctCultural > " + (CATEGORY_THRESHOLD/numChecks);
                if (checkMountain) wherePcts += " AND c"+(i+1)+".pctMountain > " + (CATEGORY_THRESHOLD/numChecks);
                if (checkNight) wherePcts += " AND c"+(i+1)+".pctNightlife > " + (CATEGORY_THRESHOLD/numChecks);
                if (checkTurist) wherePcts += " AND c"+(i+1)+".pctTourist > " + (CATEGORY_THRESHOLD/numChecks);

                whereDistance += " + r"+(i+1)+".distance";
                if (isBigCities) {
                    whereBigSmallCities += " AND size((a"+(i+1)+":Airport)-[:flights]-(:Airport)) > " + RELATIONSHIP_BIG_CITY;
                }
                if (isSmallCities){
                    whereBigSmallCities += " AND size((a"+(i+1)+":Airport)-[:flights]-(:Airport)) < " + RELATIONSHIP_SMALL_CITY;
                    whereBigSmallCities += " AND size((c"+(i+1)+":City)-[:belongs]-(:Airport)) = 1 ";
                }

                if (i == steps){
//                    query += " WHERE  ";
                    int randomNum = ThreadLocalRandom.current().nextInt(0, 10);
                    query += " WHERE ID(c2)%10 >= " + (randomNum-1)+ " AND ID(c2)%10 <= " + (randomNum+1) + " AND ";
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

                    query += whereBigSmallCities;
                }
            }
            query += " AND c1.name = \""+departure+"\" AND c1.country = \""+departureCountry+"\"";

            query += " RETURN DISTINCT c1, a1, r1 ";
            for(int i = 1; i <= steps; i++) {
                query += ", c"+(i+1)+", a"+(i+1)+", r"+(i+1)+" ";
            }
            query += " LIMIT " + limit;

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
                r.setBigCities(isBigCities);
                r.setSmallCities(isSmallCities);

                routes.add(r);
            }

        }

        return routes;
    }



    // Stored Routes (LIKE's)
    private List<Route> getRoutesLike(
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
            String query = "MATCH (r:Route) WITH r, rand() AS ran ";
            query += "WHERE r.departure = \"" + departure + "\" ";
            query += "AND r.departureCountry = \"" + departureCountry + "\" ";
            query += "AND r.nStopOver = " + steps + " ";
            query += "AND r.isRound = " + isRound + " ";
            if (isBigCities)
                query += "AND r.isBigCities = " + isBigCities + " ";
            if (isSmallCities)
                query += "AND r.isSmallCities = " + isSmallCities + " ";

            switch (distance){
                case "S": query += "AND r.distance < 2000 ";
                    break;
                case "M": query += "AND r.distance >= 2000 AND r.distance < 5000 ";
                    break;
                case "L": query += "AND r.distance >= 5000 AND r.distance < 15000 ";
                    break;
                case "W": query += "AND r.distance > 15000 ";
                    break;
            }

            // TODO: Categories
            int numChecks = 0;
            if (checkBeach) numChecks++;
            if (checkCulture) numChecks++;
            if (checkMountain) numChecks++;
            if (checkNight) numChecks++;
            if (checkTurist) numChecks++;

            if (checkBeach)
                query += "AND r.indBeach >= " + (CATEGORY_THRESHOLD / numChecks) + " ";
            if (checkCulture)
                query += "AND r.indCulture >= " + (CATEGORY_THRESHOLD / numChecks) + " ";
            if (checkMountain)
                query += "AND r.indMountain >= " + (CATEGORY_THRESHOLD / numChecks) + " ";
            if (checkNight)
                query += "AND r.indNightlife >= " + (CATEGORY_THRESHOLD / numChecks) + " ";
            if (checkTurist)
                query += "AND r.indTourist >= " + (CATEGORY_THRESHOLD / numChecks) + " ";

            query += "RETURN r ORDER BY ran LIMIT 2";


            System.out.println("");
            System.out.println("Query LIKES: "+query);
            System.out.println("");

            StatementResult res = session.run(query);
            List<Record> rl = res.list();


            for(int i = 0; i < rl.size(); i++){
                Route r = new Route();

                r.setName(rl.get(i).get(0).get("name").asString());
                r.setDeparture(rl.get(i).get(0).get("departure").asString());
                r.setDepartureCountry(rl.get(i).get(0).get("departureCountry").asString());
                r.setRound(rl.get(i).get(0).get("isRound").asBoolean());
                r.setnStopOver(rl.get(i).get(0).get("nStopOver").asInt());
                r.setDistance(rl.get(i).get(0).get("distance").asDouble());
                r.setIndBeach(rl.get(i).get(0).get("indBeach").asInt());
                r.setIndMountain(rl.get(i).get(0).get("indMountain").asInt());
                r.setIndCulture(rl.get(i).get(0).get("indCulture").asInt());
                r.setIndTourist(rl.get(i).get(0).get("indTourist").asInt());
                r.setIndNightlife(rl.get(i).get(0).get("indNightlife").asInt());

                r.setLat(rl.get(i).get(0).get("lat").asList().toArray(new String[rl.get(i).get(0).get("lat").asList().size()]));
                r.setLon(rl.get(i).get(0).get("lon").asList().toArray(new String[rl.get(i).get(0).get("lon").asList().size()]));

                routes.add(r);
            }

        }

        return routes;
    }

    /**
     * Returns a psuedo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimim value
     * @param max Maximim value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    private static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
