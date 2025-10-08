package com.dyaco.spirit_commercial.support.room.fitness_test

import android.content.Context
import androidx.room.Room
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableAirForce
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableArmy
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableCoastGuard
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableMarines
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TableNavy
import com.dyaco.spirit_commercial.support.room.fitness_test.entity_fitness_test.TablePeb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object FitnessTestDbManager {

    @Volatile
    private var db: FitnessTestDatabase? = null
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * 必須在使用前從 Application 或其他地方呼叫一次。
     */
    @JvmStatic
    fun getInstance(context: Context): FitnessTestDbManager {
        db ?: synchronized(this) {
            db ?: buildDatabase(context).also { db = it }
        }
        return this
    }

    private fun buildDatabase(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            FitnessTestDatabase::class.java,
            "DyacoMedical.db"
        )
        .createFromAsset("databases/DyacoMedical.db")
            // 錯誤回呼，顯示錯誤視窗
        //    .openHelperFactory(ErrorCallbackSQLiteOpenHelperFactory())
        .build()


//    @JvmStatic
//    fun getAirForceList(callback: DatabaseCallback<TableAirForce>) {
//        execute(callback::onDataLoadedList) { it.fitnessTestDao().getAirForceAll() }
//    }

    @JvmStatic
    fun getArmy(gender: Int, age: Int, time: Int, callback: DatabaseCallback<TableArmy>) {
        execute(callback::onDataLoadedBean) { it.fitnessTestDao().getArmy(gender, age, time) }
    }
    
    @JvmStatic
    fun getAirForce(gender: Int, age: Int, time: Int, callback: DatabaseCallback<TableAirForce>) {
        execute(callback::onDataLoadedBean) { it.fitnessTestDao().getAirForce(gender, age, time) }
    }

    @JvmStatic
    fun getNavy(gender: Int, age: Int, time: Int, callback: DatabaseCallback<TableNavy>) {
        execute(callback::onDataLoadedBean) { it.fitnessTestDao().getNavy(gender, age, time) }
    }
    
    @JvmStatic
    fun getCoastGuard(gender: Int, age: Int, time: Int, callback: DatabaseCallback<TableCoastGuard>) {
        execute(callback::onDataLoadedBean) { it.fitnessTestDao().getCoastGuard(gender, age, time) }
    }

    @JvmStatic
    fun getPeb(gender: Int, age: Int, time: Int, callback: DatabaseCallback<TablePeb>) {
        execute(callback::onDataLoadedBean) { it.fitnessTestDao().getPeb(gender, age, time) }
    }

    @JvmStatic
    fun getMarine(gender: Int, time: Int, callback: DatabaseCallback<TableMarines>) {
        execute(callback::onDataLoadedBean) { it.fitnessTestDao().getMarines(gender, time) }
    }

    /**
     * @param onResult 成功時要執行的回呼 lambda。
     * @param block 要在 IO 線程上執行的資料庫操作。
     */
    private fun <T> execute(onResult: (T) -> Unit, block: suspend (FitnessTestDatabase) -> T) {
        val database = db ?: run {
            return
        }
        
        scope.launch {
            try {
                val result = block(database)
                // 切換回主線程以安全地呼叫回呼
                withContext(Dispatchers.Main) {
                    onResult(result)
                }
            } catch (e: Exception) {
            }
        }
    }
    
    /**
     * 關閉資料庫並取消所有正在進行的協程。
     * 應在應用程式結束時呼叫。
     */
    @JvmStatic
    fun close() {
        scope.cancel() // 取消所有由這個 scope 啟動的協程
        db?.close()
    }


    suspend fun getArmyAsync(gender: Int, age: Int, time: Int) = 
        db?.fitnessTestDao()?.getArmy(gender, age, time)

    suspend fun getAirForceAllAsync() = db?.fitnessTestDao()?.getAirForceAll()
    
}

/**
 * 為了向後相容而保留的 Callback 介面。
 * 舊的 Java 程式碼會繼續使用它。
 */
interface DatabaseCallback<T> {
//    fun onDataLoadedList(t: List<T>?)
    fun onDataLoadedBean(t: T?)
}