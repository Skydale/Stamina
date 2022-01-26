package io.github.mg138.stamina

import eu.pb4.polymer.api.resourcepack.PolymerRPUtils
import io.github.mg138.stamina.stamina.StaminaManager
import net.fabricmc.api.DedicatedServerModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Suppress("UNUSED")
object Main : DedicatedServerModInitializer {
    const val modId = "stamina"
    val logger: Logger = LogManager.getLogger(modId)

    override fun onInitializeServer() {
        PolymerRPUtils.addAssetSource(modId)

        StaminaManager.register()

        logger.info("Registered stamina.")
    }
}