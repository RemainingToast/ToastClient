package dev.toastmc.toastclient.api.util

import kotlin.math.cos
import kotlin.math.sin

class Vector2f(var x: Float, var y: Float) {

    fun length(): Float {
        return kotlin.math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    fun max(): Float {
        return x.coerceAtLeast(y)
    }

    fun dot(r: Vector2f): Float {
        return x * r.x + y * r.y
    }

    fun normalized(): Vector2f {
        val length = length()
        return Vector2f(x / length, y / length)
    }

    fun cross(r: Vector2f): Float {
        return x * r.y - y * r.x
    }

    fun lerp(dest: Vector2f, lerpFactor: Float): Vector2f {
        return dest.sub(this).mul(lerpFactor).add(this)
    }

    fun rotate(angle: Float): Vector2f {
        val rad = Math.toRadians(angle.toDouble())
        val cos = cos(rad)
        val sin = sin(rad)
        return Vector2f((x * cos - y * sin).toFloat(), (x * sin + y * cos).toFloat())
    }

    fun add(r: Vector2f): Vector2f {
        return Vector2f(x + r.x, y + r.y)
    }

    fun add(r: Float): Vector2f {
        return Vector2f(x + r, y + r)
    }

    fun sub(r: Vector2f): Vector2f {
        return Vector2f(x - r.x, y - r.y)
    }

    fun sub(r: Float): Vector2f {
        return Vector2f(x - r, y - r)
    }

    fun mul(r: Vector2f): Vector2f {
        return Vector2f(x * r.x, y * r.y)
    }

    fun mul(r: Float): Vector2f {
        return Vector2f(x * r, y * r)
    }

    operator fun div(r: Vector2f): Vector2f {
        return Vector2f(x / r.x, y / r.y)
    }

    operator fun div(r: Float): Vector2f {
        return Vector2f(x / r, y / r)
    }

    fun abs(): Vector2f {
        return Vector2f(kotlin.math.abs(x), kotlin.math.abs(y))
    }

    override fun toString(): String {
        return "($x $y)"
    }

    fun equals(r: Vector2f): Boolean {
        return x == r.x && y == r.y
    }

    operator fun set(x: Float, y: Float): Vector2f {
        this.x = x
        this.y = y
        return this
    }

    fun set(r: Vector2f): Vector2f {
        x = r.x
        y = r.y
        return this
    }
}

