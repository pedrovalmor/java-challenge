package com.avenuecode;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataAccessException;
import java.io.IOException;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import com.avenuecode.Graph;
import com.avenuecode.Route;
import com.avenuecode.GraphRepository;

@RestController
public class GraphController {

    @Autowired
    private GraphRepository graphRepository;

    /*
    Endpoint: http://<host>:<port>/graph
    HTTP Method: POST
    HTTP Response Code: CREATED
    Contract:
    Request payload
    */
    // @PostMapping("/graph")
    @RequestMapping(value = "/graph", method = {RequestMethod.POST}, consumes = {"application/json"})
    public ResponseEntity<Graph> save(@RequestBody String body) {
        // create new Graph Entity
        Graph g = new Graph();
        // set data from json to string
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);
            g.setData(objectMapper.writeValueAsString(jsonNode.get("data")));
        }catch(JsonProcessingException e){
            e.getMessage();
        }catch(IOException e){
            e.getMessage();
        }
        // save it
		Graph newGraph = graphRepository.save(g);
        // returning the response with new graph created
        return new ResponseEntity<Graph>(newGraph, HttpStatus.CREATED);
    }

    /*
    Endpoint: http://<host>:<port>/graph/<graph id>
    HTTP Method: GET
    HTTP Response Code: OK
    Contract:
    Request payload: none
    Response payload
    */
    @GetMapping("/graph/{id}")
    public ResponseEntity<Graph> retrieve(@PathVariable("id") Long id) {
		Graph g = graphRepository.findOne(id);
        return new ResponseEntity<Graph>(g, HttpStatus.OK);
    }

    /*
    For instance, in the graph (AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7), the possible routes from A to C with maximum of 3 stops would be: ["ABC", "ADC", "AEBC"]
    Endpoint: http://<host>:<port>/routes/<graph id>/from/<town 1>/to/<town 2>?maxStops=<maximum number of stops>
    HTTP Method: POST
    HTTP Response Code: OK
    Contract:
    Request payload: none
    Response payload
    */
    @PostMapping("/routes/{graph_id}/from/{town1}/to/{town2}")
    public ResponseEntity<String> findAvailableRoutes(
        @PathVariable Long graph_id,
        @PathVariable String town1,
        @PathVariable String town2,
        @RequestParam(defaultValue="100") Long maxStops
    ) {
        /*
        tried those exceptions but nothing works:
        NullPointerException
        org.springframework.dao.*
        */
        Graph g = new Graph();
        try{
            g = graphRepository.findOne(graph_id);
        }catch(DataAccessException e){ // what exception to use?
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        // get data from graph
        String data = g.getData();

        // initiate ArrayList of Route
        ArrayList<Route> routes = new ArrayList<Route>();

        // trying to map data from json
        try{
            ObjectMapper mapper = new ObjectMapper();
            // getting routes
            routes = mapper.readValue(data, new TypeReference<ArrayList<Route>>(){});
        }catch(JsonProcessingException e){
            e.getMessage();
        }catch(IOException e){
            e.getMessage();
        }

        // get all routes from each source and target getRoutesFromEachSourceAndTarget
        ArrayList<String> allRoutes = this.getTargetsFromSource(town1, town2, routes, new ArrayList<String>());

        if(allRoutes.isEmpty()){
            return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
        }

        //parse json in java is painfull, so I did it with strings
        String result = "{\"routes\":[";
        String route = "";
        int stops = 0;
        for (String r : allRoutes){
            // load route
            route += r;
            stops++;
            // town2 define when create a route for result
            if(r.equals(town2) && stops <= maxStops){ // if more then maxStops, do not show
                result += "{\"route\":\"" + town1 + route + "\",\"stops\":" + stops + "},";
                // initiate them for the new route
                route = "";
                stops = 0;
            }
        }
        //remove last damn comma
        result = result.substring(0, result.length() - 1);
        result += "]}";
        // returning the response
        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

    private ArrayList<String> getTargetsFromSource(
        String source,
        String target,
        ArrayList<Route> routes,
        ArrayList<String> sourceTargets
    ){
        for (Route route : routes) {
            // if has same source
            if(source.equals(route.getSource())){
                // get the target of that source
                sourceTargets.add(route.getTarget());
                // if same target, break
                if(target.equals(route.getTarget())){
                    break;
                }else{
                    // else, call with target as source
                    this.getTargetsFromSource(route.getTarget(), target, routes, sourceTargets);
                }
            }
		}
        return sourceTargets;
    }

    /*
    Endpoint: http://<host>:<port>/distance/<graph id>
    HTTP Method: POST
    HTTP Response Code: OK
    Contract:
    Request payload
    */
    @PostMapping("/distance/{graph_id}")
    public ResponseEntity<String> distance(
        @PathVariable Long graph_id,
        @RequestBody String body
    ) {
        Graph g = graphRepository.findOne(graph_id);
        String data = g.getData();

        // initiate ArrayList of Route
        ArrayList<Route> routes = new ArrayList<Route>();

        // trying to map data from json
        try{
            ObjectMapper mapper = new ObjectMapper();
            // getting routes
            routes = mapper.readValue(data, new TypeReference<ArrayList<Route>>(){});
        }catch(JsonProcessingException e){
            e.getMessage();
        }catch(IOException e){
            e.getMessage();
        }

        String path = "";
        // set data from json to string
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);
            path = objectMapper.writeValueAsString(jsonNode.get("path"));
        }catch(JsonProcessingException e){
            e.getMessage();
        }catch(IOException e){
            e.getMessage();
        }

        // remove what we dont want
        String[] pathArray = path.replaceAll("[\\[\\]\"]", "").split(",");
        String pathArrayComplete = "";

        // complete path to compare later
        for (String p : pathArray){
            pathArrayComplete += p;
        }

        // get all routes
        ArrayList<String> allRoutes = this.getTargetsFromSource(pathArray[0], pathArray[pathArray.length - 1], routes, new ArrayList<String>());

        // path exists ?
        String route = "";
        boolean pathIsTrue = false;
        for (String r : allRoutes){
            // load route
            route += r;
            // pathArray[pathArray.length -1] define when create a route
            if(r.equals(pathArray[pathArray.length -1])){
                if((pathArray[0] + route).equals(pathArrayComplete)){
                    pathIsTrue = true;
                }
                // initiate them for the new route
                route = "";
            }
        }

        // if do not exist path return not found
        if (!pathIsTrue){
            return new ResponseEntity<String>("-1", HttpStatus.NOT_FOUND);
        }

        // distance for result
        int distance = 0;

        // get distance
        int x = 0;
        for (Route r : routes ){
            String source = r.getSource();
            String target = r.getTarget();
            if((x + 1) < pathArray.length){
                if (
                    source.equals(pathArray[x]) &&
                    target.equals(pathArray[x + 1])
                ){
                    distance += r.getDistance();
                }
            }
            x++;
        }

        // create result in json format
        String result = "{\"distance\": " + distance + "}";

        // returning the response
        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

    /*
    Endpoint: http://<host>:<port>/distance/<graph id>/from/<town 1>/to/<town 2>
    HTTP Method: POST
    HTTP Response Code: OK
    Contract:
    Request payload: none
    Response payload
    */
    @PostMapping("/distance/{graph_id}/from/{town1}/to/{town2}")
    public ResponseEntity<String> distanceFromTowns(
        @PathVariable Long graph_id,
        @PathVariable String town1,
        @PathVariable String town2
    ) {

        // same town means distance 0 and the town
        if(town1.equals(town2)){
            // create result in json format
            String noResult = "{\"distance\": 0, \"path\":[\"" + town1 + "\"]}";
            // returning the response
            return new ResponseEntity<String>(noResult, HttpStatus.OK);
        }

        Graph g = graphRepository.findOne(graph_id);
        String data = g.getData();

        // initiate ArrayList of Route
        ArrayList<Route> routes = new ArrayList<Route>();

        // trying to map data from json
        try{
            ObjectMapper mapper = new ObjectMapper();
            // getting routes
            routes = mapper.readValue(data, new TypeReference<ArrayList<Route>>(){});
        }catch(JsonProcessingException e){
            e.getMessage();
        }catch(IOException e){
            e.getMessage();
        }

        // get all routes
        ArrayList<String> allRoutes = this.getTargetsFromSource(town1, town2, routes, new ArrayList<String>());

        // path exists ?
        String route = "";
        ArrayList<String> paths = new ArrayList<String>();
        for (String r : allRoutes){
            // load route
            route += r;
            // town2 define when create a route
            if(r.equals(town2)){
                String completeRoute = town1 + route;
                paths.add(completeRoute);
                // initiate them for the new route
                route = "";
            }
        }

        // if do not exist path return not found and -1 as result
        if (paths.isEmpty()){
            return new ResponseEntity<String>("-1", HttpStatus.NOT_FOUND);
        }

        // distance for result
        int distance = 0;
        String path = "";
        int newDistance = 0;
        // get distance
        for (int x = 0; x < paths.size(); x++){
            // get chars of path
            char[] chars = paths.get(x).toCharArray();
            for(int y = 0; (y + 1) < chars.length; y++){
                for (Route r : routes ){
                    // lets compare the characters of source and next (target)
                    if (
                        r.getSource().equals(Character.toString(chars[y])) &&
                        r.getTarget().equals(Character.toString(chars[y + 1]))
                    ){
                        // sum up the distance
                        newDistance += r.getDistance();
                    }
                }
            }
            // if the new distance is the shortest, take it, as the path too
            if((newDistance != 0 && newDistance < distance) || distance == 0){
                path = paths.get(x);
                distance = newDistance;
            }else{
                newDistance = 0;
            }
        }

        // change path to json format
        char[] chars = path.toCharArray();
        String newPath = "";
        for(char c : chars){
            newPath += "\"" + c + "\",";
        }

        // remove last comma
        newPath = newPath.substring(0, newPath.length() - 1);

        // create result in json format
        String result = "{\"distance\": " + distance + ", \"path\":[" + newPath + "]}";

        // returning the response
        return new ResponseEntity<String>(result, HttpStatus.OK);
    }

}
