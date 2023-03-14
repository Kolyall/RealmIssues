package com.realmissues.db.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class RParent constructor(
    @PrimaryKey
    @Required
    var id: String = "",
    var name: String = "",
    var childs: RealmList<RChild>? = null,
) : RealmObject() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RParent

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "RParent(\n\tid='$id'\n\tname='$name'\n\tchilds=\n\t${childs?.joinToString(separator = "\n") { child -> child.toString() }}\n)"
    }

}

