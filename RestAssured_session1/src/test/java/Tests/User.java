package Tests;
import Api.UserMethod;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import static Utilities.DataUtils.getEnvironmentPropertyValue;
import static io.restassured.RestAssured.given;

import java.io.IOException;

@Slf4j
public class User {
    protected static RequestSpecification requestSpec;

    @BeforeClass
    public static void setup()
    {
        RestAssured.baseURI=getEnvironmentPropertyValue("BASE_URL");
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void testUserLifeCycle() throws IOException {
        //create user
        UserMethod.registerNewUser();

        //login user
        Response response1 = UserMethod.Login();
        String token = response1.jsonPath().getString("data.token");
        System.out.println("token:" + token);

        //retrieve user info
        UserMethod.retrieveUserInfo(token);

        //logout user
        UserMethod.logoutUser(token);

        //delete User
        response1 = UserMethod.Login();
        token = response1.jsonPath().getString("data.token");
        System.out.println("token:" + token);
        UserMethod.deleteUser(token);

    }

}
