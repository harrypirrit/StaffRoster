package service;

import io.jooby.AssetHandler;
import io.jooby.AssetSource;
import io.jooby.Cors;
import io.jooby.CorsHandler;
import io.jooby.Jooby;
import io.jooby.ServerOptions;
import io.jooby.json.GsonModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import resource.EventResource;
import resource.UserResource;
import roster.App;

public class Server extends Jooby {

    public Server() {
        // add support for JSON
        install(new GsonModule());
        // add CORS handler
        decorator(new CorsHandler(new Cors().setMethods("GET", "POST", "PUT", "DELETE")));
        mount(new UserResource(App.dao));
        mount(new EventResource(App.dao));
        // Serve all web related files
        try {
            AssetSource docs = AssetSource.create(Paths.get("static"));
            assets("/?*", new AssetHandler("index.html", docs));
        } catch(Exception ex) {
            ex.printStackTrace();
        }

    }
}
