package com.realmissues.db.base

import android.util.Log
import io.realm.Realm

fun Realm.safetyCloseAndCompact() {
    if (isInTransaction) {
        Log.e("Realm", "Transaction is not complete in Realm: ${configuration.realmFileName}")
    }
    //region явное закрытие Realm-инстанса
    if (!isClosed) {
        close()
    }
    //endregion

    compactRealm("safetyCloseAndCompact")
}

fun Realm.compactRealm(caller: String) {
    val isCompactRealm = Realm.compactRealm(configuration)
    if (isCompactRealm) {
        Log.d("Realm", "Success compact Realm: ${configuration.realmFileName} from $caller")
    }
}
