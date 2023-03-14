package com.realmissues.ui

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.realmissues.R
import com.realmissues.db.RParentRealm
import com.realmissues.db.base.toRealm
import com.realmissues.db.models.RChild
import com.realmissues.db.models.RParent
import com.realmissues.db.modules.RParentRealmModule
import com.realmissues.utils.stringToHex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MainActivity : AppCompatActivity() {

    private val encryptionKey by lazy {
        "1234567890123456789012345678901234567890123456789012345678901234"
            .toByteArray()
            /*realm use 64 byte key*/
            .copyOfRange(0, 64)
            .also {
                val encryptionKeyString = String(it)
                Log.d(TAG, "Realm Open Key: $encryptionKeyString")
                Log.d(TAG, "Realm Open Key(128Hex): " + encryptionKeyString.stringToHex())
            }
    }

    private val rParentRealm = RParentRealm(
        encryptionKey,
        "db_parent",
        1,
        RParentRealmModule()
    )

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById<TextView>(R.id.textView)
        val submitButton = findViewById<TextView>(R.id.submitButton)

        subscribeToChangesInRealm()
        submitButton.setOnClickListener {
            startInsertingIntoDb()
        }
    }

    private fun subscribeToChangesInRealm() {
        lifecycleScope.launch(Dispatchers.IO) {
            rParentRealm.getListFlow()
                .map { parents ->
                    parents
                        .mapIndexed { index, rParent ->
                            "$index.\n$rParent"
                        }
                        .joinToString(separator = "\n\n")
                }
                .flowOn(Dispatchers.IO)
                .onEach { parentList ->
                    textView.text = parentList
                }
                .flowOn(Dispatchers.Main)
                .launchIn(this)
        }
    }

    private fun startInsertingIntoDb() {
        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                //region replaceList
                delay(1.toDuration(DurationUnit.SECONDS))
                rParentRealm.replaceListSuspend(
                    buildParents()
                )
                //endregion
            }
        }
    }

    private fun buildParents(): List<RParent> {
        return (0..1).map { index ->
            buildParent(index)
        }
    }

    private fun buildParent(index: Int): RParent {
        return RParent(
            id = UUID.randomUUID().toString(),
            name = "ParentName_$index",
            childs = (0..1).toList().map { childIndex ->
                RChild(
                    id = UUID.randomUUID().toString(),
                    name = "ChildName_$childIndex"
                )
            }.toRealm()
        )
    }

    companion object {
        const val TAG: String = "MainActivity"
    }
}