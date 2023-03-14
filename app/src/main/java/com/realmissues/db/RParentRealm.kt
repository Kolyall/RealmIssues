package com.realmissues.db

import com.realmissues.coroutine.RealmDispatchers
import com.realmissues.db.base.compactRealm
import com.realmissues.db.base.safetyCloseAndCompact
import com.realmissues.db.models.RParent
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.toFlow
import io.realm.kotlin.where
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class RParentRealm constructor(
    private val encryptionKey: ByteArray,
    private val name: String,
    private val dbVersionNumber: Int,
    private val baseModule: Any,
    private vararg val additionalModules: Any
) {

    private val buildRealmInstance: Realm
        get() {
            val realmConfiguration = coroutineRealmConfiguration()
            return Realm.getInstance(realmConfiguration)
        }

    private fun coroutineRealmConfiguration(): RealmConfiguration {
        val builder = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(dbVersionNumber.toLong())
            .encryptionKey(encryptionKey)
            .modules(baseModule, *additionalModules)
            .name("$name.realm")

        return builder.build()
    }

    suspend fun getListFlow(): Flow<List<RParent>> {
        return withContext(RealmDispatchers.DB) {
            with(buildRealmInstance) {
                //region compactRealm до того как откроем
                /*
                * compactRealm до того как откроем, т.к. нельзя быть уверенными что flow завершится успешно
                * */
                compactRealm("changeListenerFlow")
                //endregion
                where<RParent>()
                    .findAllAsync()
                    .toFlow()
                    .map {
                        copyFromRealm(it)
                    }
                    .onCompletion {
                        safetyCloseAndCompact()
                    }
                    .catch { throwable ->
                        safetyCloseAndCompact()
                        throw throwable
                    }
            }
        }
            .flowOn(RealmDispatchers.DB)
    }

    private suspend fun executeTransaction(transaction: Realm.Transaction): Boolean {
        return withContext(RealmDispatchers.DB) {
            val realm = buildRealmInstance
            try {
                realm.executeTransaction(transaction)
            } finally {
                realm.safetyCloseAndCompact()
            }
            true
        }
    }

    suspend fun replaceListSuspend(list: List<RParent>): Boolean {
        return executeTransaction { realm ->
            realm.deleteAll()
            realm.insertOrUpdate(list)
        }
    }

}
