package org.netherald.quantium.util

class EmptyServerUtil : ServerUtil {
    override val isBlocked: Boolean = false
    override fun setBlockServer(value: Boolean) {}
}