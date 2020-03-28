import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

public class XueqiuTest {
    public static String code;
    public static RequestSpecification requestSpecification;
    public static ResponseSpecification responseSpecification;
    @BeforeClass
    public static void Login(){
        useRelaxedHTTPSValidation();

        requestSpecification = new RequestSpecBuilder().build();
        requestSpecification.port(80);
        requestSpecification.cookie("testerhome_id","hogwarts");
        requestSpecification.header("user-Agent","XueqiuTest Android 10.2");
    }




    @Test
    public void testPostJson(){
        HashMap<String,Object> map=new HashMap<String, Object>();
        map.put("a",1);
        map.put("b","testerhome");
        map.put("array",new String[] {"111","2222"});
        given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .body(map)
        .when().post("http://www.baidu.com")
        .then()
            .log().all().time(lessThan(1000L)).body("code",equalTo("ErrprDemo"));

    }
}
