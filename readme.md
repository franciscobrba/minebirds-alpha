### How to run this project

`docker compose up` or `docker-compose up`, depending or your docker version.

### How to build the plugin

You only need `java 21`, then you can build normally with `mvn`.
After build you need replace the existent plugin in `server/plugins` and reload the server to see your modifications...

### Shortcut to build and reload

```
mvn package -f "{REPLACE_WITH_YOUR_PATH}/mb-env/plugin/minebirds/pom.xml" && cp {REPLACE_WITH_YOUR_PATH}/plugin/minebirds/target/minebirds-1.0-SNAPSHOT.jar {REPLACE_WITH_YOUR_PATH}/plugins/minebirds-1.0-SNAPSHOT.jar && curl -X POST http://127.0.0.1:4567/v1/server/exec -H "Content-Type: application/x-www-form-urlencoded" -H "key: server_tap_password" -d "command=reload confirm"
```
