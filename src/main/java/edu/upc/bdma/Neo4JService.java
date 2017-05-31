package edu.upc.bdma;

import org.apache.commons.io.FileUtils;
import org.neo4j.driver.v1.*;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gerard on 28/05/2017.
 */
public class Neo4JService {
    private static final String USERNAME = "neo4j"; // Modify
    private static final String PASSWORD = "neo";
    private static final int MAX_RESULTS = 10;

    Driver driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( USERNAME, PASSWORD ) );

    public List<Route> getRoutes(String departure, String destination, int steps){
        List<Route> routes = new ArrayList<>();

        try ( Session session = driver.session() )
        {
/*
                MATCH (c1:City)-[]-(a1:Airport)-[]-(a2:Airport)-[]-(a3:Airport)-[]-(a4:Airport)
                MATCH (a2:Airport)-[]-(c2:City)
                MATCH (a3:Airport)-[]-(c3:City)
                WHERE (a4:Airport)-[]-(c1:City)
                AND c1.name ="Barcelona" return c1.name, c1.country, c2,c3 limit 10;

*/

            String query = "MATCH (c1:City)-[]-(a1:Airport)-[]-";

            for(int i = 1; i <= steps; i++) {
                query += "(a"+(i+1)+":Airport)-[]-";
                if (i == steps){
                    query += "(a"+(i+2)+":Airport)";
                }
            }
            for(int i = 1; i <= steps; i++) {
                query += " MATCH (a"+(i+1)+":Airport)-[]-(c"+(i+1)+":City)";
                if (i == steps){
                    query += " WHERE (a"+(i+2)+":Airport)-[]-(c1:City)";
                }
            }
            query += " AND c1.name = \""+departure+"\"";

            query += " RETURN DISTINCT c1.name, c1.country, toString(a1.lon), toString(a1.lat)";
            for(int i = 1; i <= steps; i++) {
                query += ", c"+(i+1)+".name, c"+(i+1)+".country, toString(a"+(i+1)+".lon), toString(a"+(i+1)+".lat) ";
            }

            query += " LIMIT " + MAX_RESULTS;


System.out.println("Query: "+query);
// TODO: Add destination
/*
                query += "(dep:City)-[]-(:Airport)-[r1:flights*"+steps+"]->(a1:Airport)-[]->(s1:City)";
            query += " WHERE dep.name = \""+departure+"\" RETURN ";
*/
//            query += "(dep:City)-[]-(:Airport)-[r1:flights]->(:Airport)-[]->(s1:City)<-[]-(:Airport)-[r2:flights]->(:Airport)-[]->(arr:City) WHERE dep.name = {dep} RETURN dep.name AS departureCity, s1.name AS s1City, arr.name AS arrivalCity LIMIT 12")

            StatementResult res = session.run(query);
            List<Record> rl = res.list();
            for(int i = 0; i < rl.size(); i++){
                Route r = new Route();
                String lon[] = new String[steps+2];
                String lat[] = new String[steps+2];

                String routeName = rl.get(i).get(0).asString() + " (" +rl.get(i).get(1).asString()+ ")";
                lon[0] = rl.get(i).get(2).asString();
                lat[0] = rl.get(i).get(3).asString();

                int numReturnedValues = 4;
//                0   1    2   3   4   5    6   7
//                nom pais lon lat nom pais lon lat

                for (int j = 1; j <= steps; j++){
                    routeName += " - " + rl.get(i).get(j*numReturnedValues).asString() + " (" +rl.get(i).get(j*numReturnedValues+1).asString()+ ")";
                    lon[j] = rl.get(i).get(j*numReturnedValues+2).asString();
                    lat[j] = rl.get(i).get(j*numReturnedValues+3).asString();
                }
                routeName += " - " + rl.get(i).get(0).asString() + " (" +rl.get(i).get(1).asString()+ ")";
                lon[steps+1] = rl.get(i).get(2).asString();
                lat[steps+1] = rl.get(i).get(3).asString();

                r.setName(routeName);

                r.setLon(lon);
                r.setLat(lat);
                routes.add(r);
            }

        }
        driver.close();

        return routes;
    }
}
