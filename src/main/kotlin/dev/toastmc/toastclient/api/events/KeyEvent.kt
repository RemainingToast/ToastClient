package dev.toastmc.toastclient.api.events

import org.quantumclient.energy.Event

class KeyEvent(val window: Long, val key: Int, val scancode: Int) : Event()