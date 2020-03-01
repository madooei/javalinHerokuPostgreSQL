import io.javalin.Javalin;

public class Application {
  public static void main(String[] args) {
    final int PORT = getHerokuAssignedPort();
    Javalin app = Javalin.create().start(PORT);
    app.get("/", ctx -> ctx.result("Hello Heroku!"));
  }

  private static int getHerokuAssignedPort() {
    String herokuPort = System.getenv("PORT");
    if (herokuPort != null) {
      return Integer.parseInt(herokuPort);
    }
    return 7000;
  }
}
