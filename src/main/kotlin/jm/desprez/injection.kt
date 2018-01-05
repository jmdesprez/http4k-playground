package jm.desprez

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton

val kodein = Kodein {
    bind<UserDAO>() with singleton { HashMapDAO() }

    bind<UserHandler>() with singleton { UserHandler(instance()) }

    bind<UserRoutes>() with singleton { UserRoutes(instance()) }

}