import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.div
import org.http4k.contract.meta
import org.http4k.core.Method.*
import org.http4k.lens.Path

interface RouteDefinition {
    val routes: Array<ContractRoute>
}

class UserRoutes(userHandler: UserHandler): RouteDefinition {
    private val userPath = "/users" / Path.of("login")

    private val getUser = userPath meta {
        summary = "get user"
        description = "get a user, return 404 if no user match the given login"
    } bindContract GET to userHandler::get


    private val putUser = userPath meta {
        summary = "put user"
        description = """add a user, or replace an existing user
        |the user login must match login provided with path
    """.trimMargin()
        body = userBody
    } bindContract PUT to userHandler::put

    private val delUser: ContractRoute = userPath meta {
        summary = "delete user"
        description = "delete a user"
    } bindContract DELETE to userHandler::delete

    override val routes = arrayOf(getUser, putUser, delUser)
}