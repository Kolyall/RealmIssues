package com.realmissues.db.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class RChild constructor(
    @PrimaryKey
    @Required
    var id: String = "",
    var name: String = "",
) : RealmObject() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RChild

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "\tRChild(\n\t\t\tid='$id'\n\t\t\tname='$name'\n\t\t)"
    }

}