package com.github.quillraven.mysticgarden.ui

import ktx.i18n.BundleLine

/** Generated from assets/i18n/strings.properties file. */
enum class I18N : BundleLine {
    CONTINUEGAME,
    CREDITS,
    CREDITSMENUITEM,
    ITEM_PICKUP,
    ITEM_PICKUP_DESCRIPTION_AXE,
    ITEM_PICKUP_DESCRIPTION_BOOTS,
    ITEM_PICKUP_DESCRIPTION_CLUB,
    ITEM_PICKUP_DESCRIPTION_CRYSTAL,
    ITEM_PICKUP_DESCRIPTION_ORB,
    ITEM_PICKUP_DESCRIPTION_WAND,
    ITEM_PICKUP_INFO_AXE,
    ITEM_PICKUP_INFO_BOOTS,
    ITEM_PICKUP_INFO_CLUB,
    ITEM_PICKUP_INFO_CRYSTAL,
    ITEM_PICKUP_INFO_ORB,
    ITEM_PICKUP_INFO_WAND,
    LOADING,
    NEEDEDGAMETIME,
    NEWGAME,
    PORTALINFO,
    PRESSANYKEY,
    QUITGAME,
    TIME,
    VOLUME,
    ;

    private val key = this.name.lowercase().replace("_", ".")

    override fun toString(): String = key
}

