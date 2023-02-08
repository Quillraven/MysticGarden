package com.github.quillraven.mysticgarden

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import ktx.app.gdxError
import ktx.assets.disposeSafely
import ktx.assets.getAsset
import ktx.assets.load
import ktx.assets.setLoader
import ktx.log.Logger

/**
 * Class to handle all assets of the game. Since it is a very small game,
 * we don't need asynchronous loading of assets.
 * Therefore, we use [AssetManager] and load all assets in one go.
 */
class Assets : Disposable {

    private val manager = AssetManager()

    // cache for single regions (=no animations)
    private val regionCache = mutableMapOf<RegionName, TextureRegion>()

    // cache for an array of regions (=animations)
    private val regionsCache = mutableMapOf<AtlasAsset, MutableMap<RegionName, Array<out TextureRegion>>>()

    fun load() {
        manager.setLoader(TmxMapLoader())

        AtlasAsset.values().forEach { manager.load<TextureAtlas>(it.path) }
        TiledMapAsset.values().forEach { manager.load<TiledMap>(it.path) }

        manager.finishLoading()
    }

    operator fun get(asset: AtlasAsset): TextureAtlas = manager.getAsset(asset.path)

    operator fun get(asset: TiledMapAsset): TiledMap = manager.getAsset(asset.path)

    operator fun get(region: RegionName): TextureRegion {
        if (regionCache.size >= 100) {
            log.info { "regionCache limit exceeded. Cache will be cleared!" }
            regionCache.clear()
        }

        return regionCache.getOrPut(region) {
            this[AtlasAsset.GAME].findRegion(region.key)
        }
    }

    fun getRegions(atlas: AtlasAsset, region: RegionName): Array<out TextureRegion> {
        if (regionsCache.size >= 100) {
            log.info { "regionsCache limit exceeded. Cache will be cleared!" }
            regionsCache.clear()
        }

        return regionsCache
            .getOrPut(atlas) { mutableMapOf() }
            .getOrPut(region) {
                val regions = this[atlas].findRegions(region.key)
                if (regions.isEmpty) {
                    gdxError("No regions found for region name $region in atlas $atlas")
                }
                regions
            }
    }

    override fun dispose() {
        log.debug { "Disposing assets: regionCacheSize=${regionCache.size}, regionsCacheSize=${regionsCache.values.sumOf { it.size }}" }
        manager.disposeSafely()
    }

    companion object {
        private val log = Logger(Assets::class.java.simpleName)
    }
}

enum class RegionName {
    FIREBALL,
    HERO_UP,
    HERO_DOWN,
    HERO_LEFT,
    HERO_RIGHT;

    val key: String = this.name.lowercase()
}

enum class AtlasAsset {
    GAME;

    val path: String = "graphics/${this.name.lowercase()}.atlas"
}

enum class TiledMapAsset {
    MAP;

    val path: String = "map/${this.name.lowercase()}.tmx"
}
