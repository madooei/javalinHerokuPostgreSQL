import io.javalin.Javalin;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {
  public static void main(String[] args) {
    final int PORT = getHerokuAssignedPort();
    Javalin app = Javalin.create().start(PORT);
    app.get("/", ctx -> ctx.result("Hello Heroku!"));
    workWithDatabase();
  }

  private static int getHerokuAssignedPort() {
    String herokuPort = System.getenv("PORT");
    if (herokuPort != null) {
      return Integer.parseInt(herokuPort);
    }
    return 7000;
  }

  private static void workWithDatabase(){
    try (Connection conn = getConnection()) {

      String sql = "CREATE TABLE IF NOT EXISTS Courses(" +
          "name VARCHAR(30) NOT NULL," +
          "url VARCHAR(100)" +
          ");";
      Statement st = conn.createStatement();
      st.execute(sql);

      sql = "INSERT INTO Courses(name, url) VALUES ('oose', 'jhu-oose.com');";
      st.execute(sql);

    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static Connection getConnection() throws URISyntaxException, SQLException {
    String databaseUrl = System.getenv("DATABASE_URL");
    if (databaseUrl == null) {
      // Not on Heroku, so use SQLite
      return DriverManager.getConnection("jdbc:sqlite:./Store.db");
      // This is a very bad practice: to run one DB locally and another on server
    }

    URI dbUri = new URI(databaseUrl);

    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

    return DriverManager.getConnection(dbUrl, username, password);
  }
}
