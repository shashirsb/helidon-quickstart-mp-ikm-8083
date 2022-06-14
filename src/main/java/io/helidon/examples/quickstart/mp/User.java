package io.helidon.examples.quickstart.mp;

import java.time.LocalTime;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty; 


public class User {
	
	 String id;
	 String email;
	 String password;
	 String firstName;
	 String lastName;
	 String phone;
	 String created_date;
	
	 @SuppressWarnings("checkstyle:ParameterNumber")
	public User(String user_id, String email, String firstName, String lastName,String phone,String created_date) {
		
		this.id = user_id;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.created_date = created_date;
	}
	
	public String getId() {
		return id;
	}
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getCreated_date() {
		return created_date;
	}
	public String getPhone() {
		return phone;
	}
	
	@JsonbCreator
    @SuppressWarnings("checkstyle:ParameterNumber")
    public static User of(@JsonbProperty("id") String user_id, @JsonbProperty("firstName") String firstName,
            @JsonbProperty("lastName") String lastName, @JsonbProperty("emailId") String email, @JsonbProperty("phone") String phone, String string) {
        if (user_id == null || user_id.trim().equals("")) {
        	//user_id = UUID.randomUUID().toString();
        	user_id = null;
        }
        
        String created_date = LocalTime.now().toString();
        User e = new User(user_id, email,firstName, lastName,  phone, created_date);
        return e;
    }

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", phone=" + phone + ", created_date=" + created_date + "]";
	}
	
	

}