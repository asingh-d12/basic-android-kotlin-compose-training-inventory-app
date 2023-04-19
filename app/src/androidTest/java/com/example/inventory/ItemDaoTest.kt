package com.example.inventory

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventory.data.InventoryDatabase
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.jvm.Throws


@RunWith(AndroidJUnit4::class)
class ItemDaoTest {

    private lateinit var itemDao: ItemDao
    private lateinit var inventoryDatabase: InventoryDatabase
    private var item1 = Item(1, "Apples", 10.0, 20)
    private var item2 = Item(2, "Bananas", 15.0, 97)


    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed
        inventoryDatabase =
            Room.inMemoryDatabaseBuilder(
                context = context,
                klass = InventoryDatabase::class.java
            ).allowMainThreadQueries() //  // Allowing main thread queries, just for testing.
                .build()

        itemDao = inventoryDatabase.itemDao()

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemToDB() = runTest {
        addOneItemToDB()
        val allItems = itemDao.getAllItems().first()
        Assert.assertEquals(item1, allItems[0])
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Throws(Exception::class)
    fun daoInsert_returnsAllItemsFromDB() = runTest {
        addTwoItemsToDB()
        val allItems = itemDao.getAllItems().first()
        Assert.assertEquals(listOf(item1, item2), allItems)
    }

    @After
    @Throws(IOException::class)
    fun closeDb(){
        inventoryDatabase.close()
    }

    private suspend fun addOneItemToDB(){
        itemDao.insert(item1)
    }

    private suspend fun addTwoItemsToDB(){
        itemDao.insert(item1)
        itemDao.insert(item2)
    }

}