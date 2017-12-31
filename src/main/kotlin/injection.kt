import com.github.salomonbrys.kodein.*

val kodein = Kodein {
    bind<UserDAO>() with singleton { HashMapDAO() }

    bind<UserHandler>() with singleton { UserHandler(instance()) }

    bind<UserRoutes>() with singleton { UserRoutes(instance()) }

}