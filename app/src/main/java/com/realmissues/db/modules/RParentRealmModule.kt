package com.realmissues.db.modules

import com.realmissues.db.models.RChild
import com.realmissues.db.models.RParent
import io.realm.annotations.RealmModule

@RealmModule(
    library = true,
    classes = [
        RParent::class,
        RChild::class
    ]
)
class RParentRealmModule
