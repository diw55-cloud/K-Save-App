package com.denoh.k_save.ui.screens.DashBoard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denoh.k_save.models.RideOption
import com.denoh.k_save.network.RideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: RideRepository = RideRepository()) : ViewModel() {

    private val _rides = MutableStateFlow<List<RideOption>>(emptyList())
    val rides: StateFlow<List<RideOption>> = _rides.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()
    
    private var currentDestination: String = ""

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repository.getAllCategories()
        }
    }

    fun setDestination(destination: String) {
        currentDestination = destination
    }

    fun loadRidesByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _rides.value = repository.getRidesByCategory(category, currentDestination)
            _isLoading.value = false
        }
    }
}
