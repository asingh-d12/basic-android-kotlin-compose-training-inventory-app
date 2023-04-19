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
        println("In Before!!")
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Throws(Exception::class)
    fun daoDeleteItems_deleteAllItemsFromDB() = runTest {
        addTwoItemsToDB()
        itemDao.delete(item1)
        itemDao.delete(item2)
        val allItems = itemDao.getAllItems().first()
        Assert.assertTrue(allItems.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Throws(Exception::class)
    fun daoUpdateItems_updateItemsInDB() = runTest {
        addTwoItemsToDB()
        val updateItem1 = item1.copy(price = 15.0, quantity = 25)
        val updateItem2 = item2.copy(price = 5.0, quantity = 50)
        itemDao.update(updateItem1)
        itemDao.update(updateItem2)
        val allItems = itemDao.getAllItems().first()
        Assert.assertNotEquals(listOf(item1, item2), allItems)
        Assert.assertEquals(listOf(updateItem1, updateItem2), allItems)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Throws(Exception::class)
    fun daoGetItem_returnsItemInDB() = runTest {
        addTwoItemsToDB()

        val item = itemDao.getItem(item1.id).first()
        Assert.assertEquals(item1, item)
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