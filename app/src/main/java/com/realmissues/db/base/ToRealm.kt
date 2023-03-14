package com.realmissues.db.base

import io.realm.RealmList
import io.realm.RealmResults

internal fun <E> List<E>.toRealm(): RealmList<E> {
    val rlist = RealmList<E>()
    forEach { rlist.add(it) }
    return rlist
}
