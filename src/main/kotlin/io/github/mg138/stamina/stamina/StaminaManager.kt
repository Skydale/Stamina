package io.github.mg138.stamina.stamina

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

object StaminaManager {
    const val MAX_STAMINA = 1000
    const val RATIO = MAX_STAMINA / 20

    const val HEAL_RATE = 7
    const val MAX_COOLDOWN = 20

    private val map: MutableMap<UUID, Int> = mutableMapOf()
    private val cooldown: MutableMap<UUID, Int> = mutableMapOf()

    fun getStamina(player: ServerPlayerEntity): Int {
        return map.getOrPut(player.uuid) { MAX_STAMINA }
    }

    fun setStamina(player: ServerPlayerEntity, value: Int): Int {
        val v = value.coerceAtLeast(0).coerceAtMost(MAX_STAMINA)
        map[player.uuid] = v
        return v
    }

    fun increaseStamina(player: ServerPlayerEntity, amount: Int): Int {
        val uuid = player.uuid
        cooldown[uuid]?.let {
            if (it > 0) {
                return this.getStamina(player)
            }
        }

        return this.setStamina(player, map.getOrDefault(uuid, MAX_STAMINA) + amount)
    }

    fun reduceStamina(player: ServerPlayerEntity, amount: Int): Int {
        val uuid = player.uuid
        cooldown[player.uuid] = MAX_COOLDOWN

        return this.setStamina(player, map.getOrDefault(uuid, MAX_STAMINA) - amount)
    }

    fun register() {
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
            val uuid = handler.player.uuid
            map -= uuid
            cooldown -= uuid
        }
        ServerTickEvents.END_WORLD_TICK.register { server ->
            server.players.forEach { player ->
                this.increaseStamina(player, HEAL_RATE).let { stamina ->
                    player.hungerManager.foodLevel = stamina / RATIO
                }
                cooldown.compute(player.uuid) { _, cooldown ->
                    cooldown?.let {
                        player.addStatusEffect(StatusEffectInstance(StatusEffects.HUNGER, it, 0, true, false))
                        it - 1
                    } ?: MAX_COOLDOWN
                }
            }
        }
    }
}