package com.tp2025.mobile.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.tp2025.mobile.domain.model.AsientoVenta
import com.tp2025.mobile.data.network.ProxyRepository

class AsientosViewModel {
    private val proxyRepo = ProxyRepository()
    private val scope = CoroutineScope(Dispatchers.Main)

    // ESTADO
    val misAsientos = mutableStateListOf<AsientoVenta>()
    var ocupadosRemotos by mutableStateOf<List<String>>(emptyList())
        private set

    // ACCIONES
    fun cargarOcupados(eventoId: Long) {
        scope.launch {
            try {
                ocupadosRemotos = proxyRepo.obtenerAsientosOcupados(eventoId)
            } catch (e: Exception) {
                println("Error cargando ocupados: ${e.message}")
            }
        }
    }

    fun toggleAsiento(fila: Int, col: Int) {
        val yaSeleccionado = misAsientos.find { it.fila == fila && it.columna == col }
        if (yaSeleccionado != null) {
            misAsientos.remove(yaSeleccionado)
        } else {
            if (misAsientos.size < 4) { // Regla de negocio: Máximo 4
                misAsientos.add(AsientoVenta(fila, col))
            }
        }
    }

    fun esOcupadoRemoto(fila: Int, col: Int): Boolean {
        return ocupadosRemotos.any { it.trim() == "$fila-$col" }
    }
}