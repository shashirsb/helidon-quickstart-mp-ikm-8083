package io.helidon.examples.quickstart.mp;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.sql.DataSource;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import io.helidon.microprofile.cors.CrossOrigin;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.logging.Logger;


@Path("/user")
@ApplicationScoped
public class UserList {
	
	 private final DataSource dataSource;
	 private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());
	 private static final Logger LOGGER = Logger.getLogger(UserList.class.getName());	
	 @Inject
	  public UserList(@Default final DataSource dataSource) throws SQLException {
	    super();
	    this.dataSource = Objects.requireNonNull(dataSource);
	    this.dataSource.setLoginTimeout(2000000);
	  }
	 
	 
	
	 
	 @Path("/getUsers")
	 @GET
	 @Produces(MediaType.APPLICATION_JSON)
	 public JsonArray getAllUser() throws SQLException{
		 LOGGER.info("DBOperations:getAllUser");
		 Connection connection = null;
		 ResultSet resultSet = null;
		 JsonArray jsa = null;
		 try {
			 connection = this.dataSource.getConnection();
			Statement statement = connection.createStatement();
			resultSet = statement.executeQuery("select USER_ID, FIRSTNAME, LASTNAME, EMAIL, PHONE, CREATED_DATE from IKM_USER");
			jsa = getUserJson(resultSet);
		 } catch(Exception e) {
			 e.printStackTrace();
		 } finally{
			 connection.close();
		 }
		 
		 return jsa;
		}
	 
	  public static JsonArray getUserJson(ResultSet rs) throws SQLException {
		  LOGGER.info("DBOperations:getAllUser:getUserJson");
		  JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		  while(rs.next()) {
		 
		  final JsonObjectBuilder JSONDB = Json.createObjectBuilder();
		   JSONDB.add("id",rs.getString(String.valueOf("USER_ID")));
		   JSONDB.add("email", rs.getString(String.valueOf("EMAIL")));
		   JSONDB.add("firstName", rs.getString(String.valueOf("FIRSTNAME")));
		   JSONDB.add("lastName", rs.getString(String.valueOf("LASTNAME")));
		   JSONDB.add("phone", rs.getString(String.valueOf("PHONE")));
		   JSONDB.add("createdDate", rs.getString(String.valueOf("CREATED_DATE")));
		   jsonArray.add(JSONDB.build());
		   LOGGER.info("The JSON Value ==========>"+JSONDB.toString());
		  }
          
		  JsonArray json = jsonArray.build();
			
			LOGGER.info(json.toString());
			 LOGGER.info("DBOperations:getAllUser:getUserJson:end"+jsonArray);
          return json;
	  }
	  
	  	@Path("/openUserList")
	    @GET
	    @Produces(MediaType.TEXT_HTML)
	 	@CrossOrigin(value = {"http://152.70.192.169:8029/","http://152.70.192.169:8080/"},
       allowMethods = {HttpMethod.POST,HttpMethod.GET})
	    public  Response validateSession(@QueryParam("trackId") String trackId,@QueryParam("userId") String userId) throws URISyntaxException, SQLException {
	    	
	 		 LOGGER.info("DBOperations:validateSession"+"   Form Param : trackId===>"+trackId+"<=======userId===>"+userId);
	 		URI ui = null;
	 		 String checkSql = "select LOGIN_TIME, LOGIN_STATUS from USER_SSO where SESSION_ID = ? and USER_ID = ?";
	 		Connection connection = null;
	 		 try {
				 
				LOGGER.info(userId);
				 connection = this.dataSource.getConnection();
				PreparedStatement st = connection.prepareStatement(checkSql);
				int t = Integer.parseInt(userId);
				st.setString(1, trackId);
				st.setInt(2, t);
					 
				ResultSet rs = st.executeQuery();
				
				Timestamp ts = null;
				String loginStatus = null;
				while(rs.next()) {
					ts = rs.getTimestamp("LOGIN_TIME");
					loginStatus = rs.getString("LOGIN_STATUS");
				}
				LOGGER.info("Time Stamp       =======>  "+ts+"   and Login status ===========>"+loginStatus);
				if(calculateTimeDiff(ts) > 20) {
					 ui = new URI("http://152.70.192.169:8083/timeout.html");
				} else {
					 ui = new URI("http://152.70.192.169:8083/userList.html");
				}
			 }catch(Exception e) {
				 e.printStackTrace();
			 } finally {
				 connection.close();
			 }
	 		LOGGER.info("UI Get ----->"+ui);
	 		//NewCookie[] sesIdCok = SetCookie.create("regValidId",trackId);
			 return Response.seeOther(ui)
					 .header("Access-Control-Allow-Origin","*")
					 .build();
	    }
	 	
	 	public long calculateTimeDiff(Timestamp ts) {
	 		
	 		SimpleDateFormat fm = new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy");
	 	     long now = System.currentTimeMillis();
	 	     
	 	     Timestamp ps = new Timestamp(now);
	 	      
	 	     Date loginTime = new Date(ts.getTime());
	 	     Date currentTime = new Date(ps.getTime());
	 	     
	 	    try {
	 	    	loginTime = fm.parse(loginTime.toString());
	 	       currentTime = fm.parse(currentTime.toString());
	 	    } catch (Exception e) {
	 	        e.printStackTrace();
	 	    }    
	 	    
	 	    LOGGER.info("loginTime : "+loginTime);
	 	    LOGGER.info("currentTime : "+currentTime);
	 	      
	 	      long diff = currentTime.getTime() - loginTime.getTime();
	 	      long diffMinutes = diff / (60 * 1000);
	 	      LOGGER.info("Time in minutes: " + diffMinutes + " minutes.");   

	 		return diffMinutes;
	 	}
	  
	  

		
		
	
	

}
