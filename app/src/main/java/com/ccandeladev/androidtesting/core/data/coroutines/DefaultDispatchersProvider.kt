package com.ccandeladev.androidtesting.core.data.coroutines

import com.ccandeladev.androidtesting.core.domain.coroutines.DispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

//To implement DispatchersProvider ()  ready to perform the testing
class DefaultDispatchersProvider @Inject constructor(): DispatchersProvider{

    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default

}