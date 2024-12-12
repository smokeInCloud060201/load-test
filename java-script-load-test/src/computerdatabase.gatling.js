import {
  simulation,
  scenario,
  exec,
  csv,
  pause,
  css,
  feed,
  repeat,
  tryMax,
  rampUsers
} from "@gatling.io/core";
import { http, status } from "@gatling.io/http";

export default simulation((setUp) => {
  const feeder = csv("search.csv").random();

  const search = exec(
    http("Home").get("/"),
    pause(1),
    feed(feeder),
    http("Search")
      .get("/computers?f=#{searchCriterion}")
      .check(css("a:contains('#{searchComputerName}')", "href").saveAs("computerUrl")),
    pause(1),
    http("Select").get("#{computerUrl}").check(status().is(200)),
    pause(1)
  );

  const browse =
    repeat(4, "i").on(http("Page #{i}").get("/computers?p=#{i}"), pause(1));
  const edit =
    tryMax(2)
      .on(
        http("Form").get("/computers/new"),
        pause(1),
        http("Post")
          .post("/computers")
          .formParam("name", "Beautiful Computer")
          .formParam("introduced", "2012-05-30")
          .formParam("discontinued", "")
          .formParam("company", "37")
          .check(
            status().is(
              (session) => 200 + Math.floor(Math.random() * 2) // +0 or +1 at random
            )
          )
      )
      .exitHereIfFailed();

  const httpProtocol = http
    .baseUrl("https://computer-database.gatling.io")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader(
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0"
    );

  const users = scenario("Users").exec(search, browse);
  const admins = scenario("Admins").exec(search, browse, edit);

  setUp(
    users.injectOpen(rampUsers(10).during(10)),
    admins.injectOpen(rampUsers(2).during(10))
  ).protocols(httpProtocol);
});
