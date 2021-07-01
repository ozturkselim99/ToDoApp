package com.selim.todoapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.selim.todoapp.data.models.ToDoData

@Dao
interface ToDoDao {

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAllData():LiveData<List<ToDoData>>

    //bu, veritabanımızda zaten sahip olduğumuz öğeyle temelde aynı olan yeni öğe veritabanımıza geldiğinde, bu, kendi veritabanımızın ne olması gerektiği konusunda bir yıldız stratejisi belirleyebileceğimiz anlamına gelir.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
   suspend fun insertData(toDoData: ToDoData)
   //suspend functionlar, mevcut threadi bloke etmeden coroutinin yürütülmesini askıya alır. Böylelikle thread başka bir corutinenin işletilmesine başlar ve cpu daha verimli bir şekilde kullanılmış olur

   @Update
   suspend fun updateData(toDoData: ToDoData)

   @Delete
   suspend fun deleteItem(toDoData: ToDoData)

   @Query("DELETE FROM todo_table")
   suspend fun deleteAll()

   @Query("SELECT * FROM todo_table WHERE title LIKE:searchQuery")
   fun searchDatabase(searchQuery:String):LiveData<List<ToDoData>>

   @Query("SELECT * FROM todo_table ORDER BY CASE WHEN priority LIKE 'H%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'L%' THEN 3 END")
   fun sortByHighPriority():LiveData<List<ToDoData>>

   @Query("SELECT * FROM todo_table ORDER BY CASE WHEN priority LIKE 'L%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'H%' THEN 3 END")
   fun sortByLowPriority():LiveData<List<ToDoData>>

}