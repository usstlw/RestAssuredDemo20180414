import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.Matchers.*;

public class TesterHomeTest {
    @BeforeClass
    public static void setup(){
        useRelaxedHTTPSValidation();
        baseURI="https://testerhome.com";
        //RestAssured.proxy("127.0.0.1",8080);
    }

    @Test
    public void testHtml(){
        useRelaxedHTTPSValidation();
        given()
                .queryParam("q","appium")
        .when()
                .get("https://testerhome.com/search").prettyPeek()
        .then()
                .statusCode(200)
                .body("html.head.title",equalTo("appium · 搜索结果 · TesterHome"));
    }

    @Test
    public  void testTesterHomeJson(){
        given().when().get("https://testerhome.com/api/v3/topics.json").prettyPeek()
        .then()
                .statusCode(200)
                .body("topics.title",hasItems("Jenkins 启动 selenium 脚本定位不到元素，在本地启动是可以的大家有什么见解吗？"))
                .body("topics.title[1]",equalTo("[求助] 如何把 allure 的 index.html 所显示的内容，展示在 jenkins 发送的 Email 中？"))
                .body("topics.id[-1]",equalTo(22683))
                .body("topics.findAll{topic->topic.id == 22691}.title",hasItems("Jenkins 启动 selenium 脚本定位不到元素，在本地启动是可以的大家有什么见解吗？"))
                .body("topics.find{topic->topic.id == 22691}.title",equalTo("Jenkins 启动 selenium 脚本定位不到元素，在本地启动是可以的大家有什么见解吗？"))
                .body("topics.title.size()",equalTo(20))
        ;
    }



    @Test
    public void testTesterHomeJsonSingle(){
        given().when().get("https://testerhome.com/api/v3/topics/10254.json").prettyPeek()
                .then().statusCode(200).body("topic.title",equalTo("优质招聘汇总"));
    }

    @Test
    public void testTesterHomeSearch(){
        given().queryParam("霍格沃兹测试学院")
        .when().get("https://testerhome.com/search").prettyPeek()
        .then()
                .statusCode(200);
    }

    @Test
    public void testXML(){
        Response response=given().when().get("http://127.0.0.1:8000/hogwarts.xml").prettyPeek()
        .then()
                .statusCode(200)
                .body("shopping.category.item.name[2]",equalTo("Paper"))
                .body("shopping.category[1].item[1].name",equalTo("Pens"))
                .body("shopping.category.size()",equalTo(3))
                .body("shopping.category[1].item.size()",equalTo(2))
                .body("shopping.category.find{ it.@type=='present' }.item.name",equalTo("Kathryn's Birthday"))
                .body("**.find{it.price == 200}.name",equalTo("Kathryn's Birthday"))
        .extract().response();

        System.out.println(response.statusLine());
    }

    @Test
    public void testTesterHomeJsonSchema(){
        given().when().get("https://testerhome.com/api/v3/topics/6040.json")
                .then()
                .statusCode(200).body(matchesJsonSchema("/tmp/json2.schema"))
                ;
    }

    @Test
    public void testTesterHOmeJsonGlobal(){
                given().proxy("127.0.0.1",8080)
                .when().get("/api/v3/topics/10254.json").prettyPeek()
                .then()
                .statusCode(200)
                .body("topic.title",equalTo("优质招聘汇总"))
                ;
    }

    @Test
    public void testJsonPost(){
        HashMap<String,Object> data=new HashMap<String, Object>();
        data.put("id",6040);
        data.put("title","通过代理安装 appium");
        data.put("name","思寒");

        HashMap<String,Object> root = new HashMap<String, Object>();
        root.put("topic",data);

        given()
                .contentType(ContentType.JSON)
                .body(root)
        .when()
                .post("www.baidu.com").prettyPeek()
        .then()
                .time(lessThan(1000L))
        ;
    }

    @Test
    public void multiApi(){
        String name = given().get("https://testerhome.com/api/v3/topics/6040.json").prettyPeek()
        .then().statusCode(200).extract().path("topic.user.name")
        ;
        System.out.println(name);
        //https://testerhome.com/search?q=思寒
        given().queryParam("q",name)
                .cookie("uid",name)
                .when().get("/search")
                .then().statusCode(200).body(containsString(name));
    }
}
