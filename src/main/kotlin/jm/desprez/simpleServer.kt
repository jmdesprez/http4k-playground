package jm.desprez

import org.http4k.contract.*
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_PLAIN
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.lens.string
import org.http4k.routing.routes

//1. Define a route
//Firstly, create a route with the desired contract of path, headers, queries and body parameters.
val ageQuery = Query.int().required("age")
val fooQuery = Query.int().optional("foo", "You can provide a foo if you want")
val stringBody = Body.string(TEXT_PLAIN).toLens()

val route = ("/echo" / Path.of("name") meta {
    summary = "echo"
    queries += ageQuery
    queries += fooQuery
    body = stringBody
} bindContract Method.GET)

//2. Dynamic binding of calls to an HttpHandler
//Next, bind this route to a function which creates an `HttpHandler` for each invocation, which receives the dynamic path elements from the path:
fun echo(nameFromPath: String): HttpHandler = { request: Request ->
    val age: Int = ageQuery(request)
    val foo: Int? = fooQuery.extract(request)
    val sentMessage = stringBody(request)

    Response(OK).with(stringBody of "hello $nameFromPath you are $age. You sent $sentMessage")
}

//3. Combining Routes into a contract and bind to a context
//Finally, the `ContractRoutes` are added into a reusable `Contract` in the standard way, defining a renderer (in this example OpenApi/Swagger) and a security model (in this case an API-Key):

val routeWithBindings = route to ::echo

val security = ApiKey(Query.string().required("api")) { key ->
    key == "42"
}

val apiV1Info = ApiInfo("My great API", "v1.0")
val apiV2Info = ApiInfo("New version of the API, use it!!", "v2.0", "Contract xxx is broken")
val handler: HttpHandler = routes(
        "/api/v1" bind contract(OpenApi(apiV1Info, Jackson), "", security, routeWithBindings),
        "/api/v2" bind contract(OpenApi(apiV2Info, Jackson), "", security, routeWithBindings)
)


fun main(args: Array<String>) {
    val api = Request(Method.GET, "http://127.0.0.1:8080/api/v2")
    println(handler(api))

    val echo = Request(Method.GET,
                       "http://127.0.0.1:8080/api/v1/echo/john?api=42&age=12").with(stringBody of "Hello world")
    println(handler(echo))

    //    val server = handler.asServer(Jetty(8080))
    //    server.start()
    //
    //
    //    val client: HttpHandler = OkHttp()
    //
    //    println(client(api))
    //
    //    server.stop()
}