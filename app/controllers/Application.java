package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.FetchConfig;
import play.*;
import play.mvc.*;
import play.data.Form;
import static play.libs.Json.*;
import views.html.*;
import models.*;

import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import com.google.gson.Gson;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result addUser() {
    	User user = Form.form(User.class).bindFromRequest().get();
    	user.save();
    	return redirect(routes.Application.index());
    }

    public static Result getUsers() {
    	List<User> users = User.find.all();
    	return ok(toJson(users));
    }
    
    public static Result getLoadData()  throws IOException {
        //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());

        City jogja = new City("Yogyakarta");
        jogja.save();

        String json = new String(Files.readAllBytes(Paths.get("2015-02.json")), StandardCharsets.UTF_8);
        JsonSembako[] jsonSembakos=  new Gson().fromJson(json, JsonSembako[].class);

        Arrays.stream(jsonSembakos).parallel().forEach(jsonSembako -> {
            Sembako sembako = new Sembako();
            sembako.name = jsonSembako.name;
            sembako.save();

            jsonSembako.prices.forEach((key, value) -> {
                SembakoPrice price = new SembakoPrice();
                price.city = jogja;
                price.date = new Date(2015,2,key);
                price.price = value;
                price.sembako=sembako;
                price.save();
            });
        });
        return ok("Data should be loaded.");
    }

    public static Result getSembakos(){
        List<Sembako> sembakos = Sembako.find.all();
    	return ok( toJson(sembakos) );
    }
}
