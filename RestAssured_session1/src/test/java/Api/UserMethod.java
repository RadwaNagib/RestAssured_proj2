package Api;

import Pojo.Login;
import Pojo.UserRegister;
import Tests.User;
import Utilities.Utility;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
@Slf4j

public class UserMethod  extends User  {


    static UserRegister userRegister;

    // Register a new user
    public static Response registerNewUser() throws IOException {

        userRegister = Utility.readJsonFromFile("src/main/resources/testData.user/user.json", UserRegister.class);

        // user Randomize Email
        userRegister.setEmail("test" + Instant.now().toEpochMilli() + "@gmail.com");
        log.info(String.format("User Data {}:%s", userRegister));

        final String name_json = userRegister.getName();
        final String email_json = userRegister.getEmail();

        return given(requestSpec)
                .body(userRegister)
                .when()
                .post("/users/register")
                .then().log().body()
                .statusCode(201).body("data.name", equalTo(name_json))
                .body("data.email", equalTo(email_json)).body("message", equalTo("User account created successfully"))
                .extract().response();
    }

    // Login the user and capture token
    public static Response Login() throws IOException {

        Login login = new Login();
        login.setPassword(userRegister.getPassword());
        login.setEmail(userRegister.getEmail());

        return given(requestSpec)
                .body(login)
                .when().post("/users/login")
                .then().statusCode(200).
                log().body()
                .body("message", equalTo("Login successful")).extract().response();
    }

    // Retrieve user profile
    public static Response retrieveUserInfo(String My_token) {
        return given(requestSpec)
                .header("x-auth-token", My_token)
                .contentType(ContentType.JSON)
                .when().get("/users/profile").then().statusCode(200)
                .log().all().body("message", equalTo("Profile successful"))
                .extract().response();

    }

    //logout user
    public static Response logoutUser(String My_token) {
        return given(requestSpec).header("x-auth-token", My_token)
                .contentType(ContentType.JSON)
                .when().delete("/users/logout")
                .then().log().all().statusCode(200)
                .body("message", equalTo("User has been successfully logged out"))
                .extract().response();

    }

    //delete User from database
    public static Response deleteUser(String My_token) {
        return given(requestSpec).header("x-auth-token", My_token)
                .contentType(ContentType.JSON)
                .when().delete("/users/delete-account")
                .then().log().all().statusCode(200)
                .body("message", equalTo("Account successfully deleted"))
                .extract().response();
    }


}
