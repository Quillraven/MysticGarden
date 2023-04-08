package com.github.quillraven.mysticgarden.screen

import box2dLight.RayHandler
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.world
import com.github.quillraven.mysticgarden.Assets
import com.github.quillraven.mysticgarden.MusicAsset
import com.github.quillraven.mysticgarden.MysticGarden
import com.github.quillraven.mysticgarden.MysticGarden.Companion.prefKeyCrystals
import com.github.quillraven.mysticgarden.MysticGarden.Companion.prefKeyItems
import com.github.quillraven.mysticgarden.MysticGarden.Companion.prefKeyOrbs
import com.github.quillraven.mysticgarden.MysticGarden.Companion.prefKeyPlayerPos
import com.github.quillraven.mysticgarden.MysticGarden.Companion.prefKeySpeed
import com.github.quillraven.mysticgarden.MysticGarden.Companion.prefKeyTime
import com.github.quillraven.mysticgarden.TiledMapAsset
import com.github.quillraven.mysticgarden.audio.AudioService
import com.github.quillraven.mysticgarden.component.*
import com.github.quillraven.mysticgarden.component.Light.Companion.LightCone
import com.github.quillraven.mysticgarden.component.Light.Companion.LightPoint
import com.github.quillraven.mysticgarden.event.EventDispatcher
import com.github.quillraven.mysticgarden.event.MapChangeEvent
import com.github.quillraven.mysticgarden.input.KeyboardInput
import com.github.quillraven.mysticgarden.input.PlayerController
import com.github.quillraven.mysticgarden.system.*
import com.github.quillraven.mysticgarden.ui.model.GameModel
import com.github.quillraven.mysticgarden.ui.view.gameView
import ktx.app.KtxScreen
import ktx.app.gdxError
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.collections.GdxArray
import ktx.graphics.component1
import ktx.graphics.component2
import ktx.graphics.component3
import ktx.math.vec2
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import ktx.scene2d.actors
import kotlin.experimental.or

class GameScreen(
    private val game: MysticGarden,
    private val batch: Batch,
    private val assets: Assets,
    private val prefs: Preferences,
    private val audioService: AudioService,
    private val eventDispatcher: EventDispatcher,
    private val i18n: I18NBundle,
) : KtxScreen {

    private val uiStage = Stage(FitViewport(180f, 320f), batch)
    private val gameCamera = OrthographicCamera()
    private val gameViewport: Viewport = FitViewport(6.75f, 12f, gameCamera)
    private val physicWorld = createWorld()
    private val rayHandler = createRayHandler()
    private val world = createEntityWorld()
    private val keyboardInput = KeyboardInput(PlayerController(world), game)
    private val activeMap = assets[TiledMapAsset.MAP]
    private val gameModel = GameModel(eventDispatcher, keyboardInput, i18n, game)
    private var wasShown = false

    init {
        uiStage.actors { gameView(i18n, gameModel) }
        eventDispatcher.dispatch(MapChangeEvent(activeMap))
    }

    private fun createEntityWorld(): World = world {
        injectables {
            add(game)
            add(batch)
            add(gameCamera)
            add(gameViewport)
            add(uiStage)
            add(eventDispatcher)
            add(assets)
            add(physicWorld)
            add(prefs)
            add(rayHandler)
            add(audioService)
        }

        components {
            onRemove(Physic, Physic.onRemove)
            onRemove(LightPoint, Light.onRemove)
            onRemove(LightCone, Light.onRemove)
            onRemove(Particle, Particle.onRemove)
        }

        systems {
            add(GameTimeSystem())
            add(MapSystem())
            add(PhysicSystem())
            add(CollisionSystem())
            add(AnimationSystem())
            add(DisableSystem())
            add(ZoneSystem())
            add(CameraSystem())
            add(RenderSystem())
            add(ParticleSystem())
            add(LightSystem())
            add(RemoveSystem())
            if (MysticGarden.debugRender) {
                add(DebugRenderSystem())
            }
        }
    }

    private fun createRayHandler() = RayHandler(physicWorld).apply {
        // use diffuse light to make the light not super bright
        RayHandler.useDiffuseLight(true)
        // don't throw shadows for water bodies
        // -> only do that between player, objects and environment
        B2DLight.setGlobalContactFilter(
            MysticGarden.b2dPlayer,
            1,
            MysticGarden.b2dMapObject or MysticGarden.b2dEnvironment
        )

        setAmbientLight(Light.ambientColor)
    }

    fun newGame() {
        // reset map data BEFORE resetting active zone
        // because ZoneSystem will trigger the MapSystem via
        // a ZoneChangeEvent to load map entities.
        world.system<MapSystem>().resetMapData(prefs)
        val (_, x, y) = activeMap.startLocation
        world.system<ZoneSystem>().resetActiveZone(vec2(x, y))
        world.system<GameTimeSystem>().totalTime = 0
        val (r, g, b) = Light.ambientColor
        rayHandler.setAmbientLight(r, g, b, 1f)

        gameModel.reset()
    }

    fun loadGame() {
        val playerPos: Vector2 = prefs[prefKeyPlayerPos] ?: gdxError("Missing pref key $prefKeyPlayerPos")
        val time: Int = prefs[prefKeyTime] ?: gdxError("Missing pref key $prefKeyTime")
        val crystals: Int = prefs[prefKeyCrystals] ?: gdxError("Missing pref key $prefKeyCrystals")
        val orbs: Int = prefs[prefKeyOrbs] ?: gdxError("Missing pref key $prefKeyOrbs")
        val items: GdxArray<ItemType> = prefs[prefKeyItems] ?: gdxError("Missing pref key $prefKeyItems")
        val speed: Float = prefs[prefKeySpeed] ?: gdxError("Missing pref key $prefKeySpeed")

        with(world.system<MapSystem>()) {
            destroyObjects()
            destroyCollision()
        }
        world.system<ZoneSystem>().resetActiveZone(playerPos)
        world.system<GameTimeSystem>().totalTime = time
        val (r, g, b) = Light.ambientColor
        val gain = orbs * Light.ambientOrbGain
        rayHandler.setAmbientLight(r + gain, g + gain, b + gain, 1f)

        // load player crystals, orbs and items
        with(world) {
            val playerEntity = family { all(Player, Boundary) }.first()
            val playerCmp = playerEntity[Player]

            playerCmp.crystals = crystals
            playerCmp.chromas = orbs
            playerCmp.items.clear()
            playerCmp.items.addAll(items)

            playerEntity[Move].maxSpeed = speed

            val playerBoundary = playerEntity[Boundary]
            playerBoundary.pos(playerPos.x, playerPos.y)
            playerEntity.configure {
                it -= Physic
                it += Physic.of(physicWorld, playerBoundary, BodyType.DynamicBody, it, MysticGarden.b2dPlayer)
                it[LightPoint].light.attachToBody(it[Physic].body)
            }
        }

        // update UI
        gameModel.reset(crystals = crystals, orbs = orbs, time = time, items = items)
    }

    fun saveGame() {
        if (!wasShown) {
            // screen was never active -> don't save
            return
        }

        prefs.flush {
            with(world) {
                val playerEntity = family { all(Player, Boundary) }.first()
                val playerBoundary = playerEntity[Boundary]
                val (crystals, chromas, items) = playerEntity[Player]

                prefs[prefKeyPlayerPos] = vec2(playerBoundary.x, playerBoundary.y)
                prefs[prefKeyTime] = system<GameTimeSystem>().totalTime
                prefs[prefKeyCrystals] = crystals
                prefs[prefKeyOrbs] = chromas
                prefs[prefKeyItems] = items
                prefs[prefKeySpeed] = playerEntity[Move].maxSpeed
            }
        }

        val activeZone = world.system<ZoneSystem>().activeZone
        world.system<MapSystem>().saveObjects(activeZone)
    }

    override fun show() {
        Gdx.input.inputProcessor = InputMultiplexer(keyboardInput, uiStage)
        wasShown = true
        audioService.play(MusicAsset.GAME)
    }

    override fun hide() {
        saveGame()
    }

    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)

        val (screenX, screenY, screenW, screenH) = gameViewport
        rayHandler.useCustomViewport(screenX, screenY, screenW, screenH)
    }

    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.25f)
        world.update(dt)

        // render UI
        uiStage.viewport.apply()
        uiStage.act(dt)
        uiStage.draw()
        // reset alpha value for game rendering
        batch.color.a = 1f
    }

    override fun dispose() {
        world.dispose()
        physicWorld.disposeSafely()
        rayHandler.disposeSafely()
        uiStage.disposeSafely()
    }
}

private operator fun Viewport.component1(): Int = this.screenX

private operator fun Viewport.component2(): Int = this.screenY

private operator fun Viewport.component3(): Int = this.screenWidth

private operator fun Viewport.component4(): Int = this.screenHeight
