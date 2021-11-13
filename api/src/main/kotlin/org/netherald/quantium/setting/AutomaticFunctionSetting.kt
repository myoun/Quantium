package org.netherald.quantium.setting

data class AutomaticFunctionSetting(
    var autoStart : Boolean = true,
    var autoDelete : Boolean = true,
    var autoSendToLobby : Boolean = true,
    var autoCreateInstance : Boolean = true
)