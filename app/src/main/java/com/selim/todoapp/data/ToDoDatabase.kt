package com.selim.todoapp.data

import android.content.Context
import androidx.room.*
import com.selim.todoapp.data.models.ToDoData

@Database(entities = [ToDoData::class],version = 1,exportSchema = false)
@TypeConverters(Converter::class)
abstract class ToDoDatabase:RoomDatabase() {

    abstract fun toDoDao():ToDoDao

    companion object{

        //Bu annotation sayesinde bir thread’de bu instance değiştiğinde diğer thread’ler hemen görebilecek.
        @Volatile
        private var INSTANCE:ToDoDatabase?=null

        fun getDatabase(context:Context):ToDoDatabase {
            val tempInstance= INSTANCE
            if (tempInstance!=null)
            {
                return tempInstance
            }
            //bir iş parçacığı senkronize çağırdığında, o senkronize edilmiş bloğun kilidini alır. Kilidi alan önceki iş parçacığı kilidi serbest bırakmadığı sürece diğer iş parçacıklarının aynı senkronize bloğu çağırma izni yoktur.
            synchronized(this){
                val instance=Room.databaseBuilder(
                        context.applicationContext,
                        ToDoDatabase::class.java,
                    "todo_database"
                ).build()
                INSTANCE=instance
                return instance
            }
        }
    }

}