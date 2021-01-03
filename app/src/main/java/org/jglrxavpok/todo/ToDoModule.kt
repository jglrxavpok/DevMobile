package org.jglrxavpok.todo

import org.jglrxavpok.todo.network.*
import org.koin.dsl.module

// Koin module
val toDoModule = module {
    single<TasksRepository> { TasksRepositoryImpl() }

    single<UserInfoRepository> { UserInfoRepositoryImpl() }

    single<TasksWebService> { Api.Instance.retrofit.create(TasksWebService::class.java) }

    single<UserWebService> { Api.Instance.retrofit.create(UserWebService::class.java) }
}