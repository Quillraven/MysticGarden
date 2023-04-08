package com.github.quillraven.mysticgarden.ui

import ktx.i18n.BundleLine

/** Generated from assets/i18n/strings.properties file. */
enum class I18N : BundleLine {
    CLEARSAVE,
    CONTINUE,
    CONTROLSHERO,
    CONTROLSHERO_MOBILE,
    CONTROLSTOUCH,
    CONTROLSTOUCH_MOBILE,
    CREDITS,
    CREDITS_MENU,
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
    NEEDEDTIME,
    NEWGAME,
    NO,
    PORTALINFO,
    PRESSANYKEY,
    QUITGAME,
    TIME,
    VOLUME,
    YES,
    ;

    private val key = this.name.lowercase().replace("_", ".")

    override fun toString(): String = key
}

