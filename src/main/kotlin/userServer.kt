import com.github.salomonbrys.kodein.instance
import org.http4k.contract.ApiInfo
import org.http4k.contract.OpenApi
import org.http4k.contract.bind
import org.http4k.contract.contract
import org.http4k.core.HttpHandler
import org.http4k.core.UriTemplate.Companion.from
import org.http4k.format.Jackson
import org.http4k.routing.routes

fun main(args: Array<String>) {
    val userRoutes: UserRoutes = kodein.instance()

    val apiV1Info = ApiInfo("My great API", "v1.0")
    val handler: HttpHandler = routes(
            "/api/v1" bind contract(OpenApi(apiV1Info, Jackson), "", *userRoutes.routes)
    )

    val userAPI = from("http://test/api/v1/users/{login}")
    println(userAPI.generate(mapOf("login" to "alice")))

    //userPath.with({ it.query("login", "alice") })

    val alice = UserDTO("alice", Role.REGULAR)
    val client = UserClient("")

    fun get(login: String) {
        print("Get $login: ")
        val response = handler(client.get(login))
        //println(response)
        if (response.status.successful) {
            val dto = userBody(response)
            println("found $dto")
        } else {
            println("unable to find (${response.bodyString()})")
        }
    }

    fun put(userDTO: UserDTO, pathParam: String = userDTO.login) {
        print("Put $userDTO using path /v1/users/$pathParam: ")
        val response = handler(client.put(userDTO, pathParam))
        if (response.status.successful) {
            println("user added")
        } else {
            println("unable to put (${response.bodyString()})")
        }
    }

    fun delete(login: String) {
        print("Delete $login: ")
        val response = handler(client.delete(login))
        //println(response)
        if (response.status.successful) {
            println("deleted")
        } else {
            println("unable to delete (${response.bodyString()})")
        }
    }

    get("alice")
    put(alice)
    get("alice")
    delete("alice")
    get("alice")

    delete("bob")
    put(alice, "bob")

    put(UserDTO("root", Role.REGULAR))
    delete("root")
}
