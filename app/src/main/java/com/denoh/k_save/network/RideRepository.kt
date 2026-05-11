package com.denoh.k_save.network

import com.denoh.k_save.models.RideOption
import kotlinx.coroutines.delay

class RideRepository {
    private val allRides = mutableListOf<RideOption>()

    init {
        val places = listOf(
            "CBD", "Westlands", "Kilimani", "Kileleshwa", "Lavington", "Hurlingham", "Riverside", "Upper Hill", "Nairobi West", "South C", "South B", "Madaraka", "Langata", "Karen", "Hardy", "Ngong Town", "Ongata Rongai", "Kajiado", "Kitengela", "Athi River", "Syokimau", "Mlolongo", "Embakasi", "Imara Daima", "Pipeline", "Donholm", "Buruburu", "Umoja", "Kayole", "Eastleigh", "Pangani", "Ngara", "Parklands", "Highridge", "Gigiri", "Runda", "Muthaiga", "Nyari", "Rosslyn", "Lower Kabete", "Spring Valley", "Kasarani", "Roysambu", "Zimmerman", "Kahawa West", "Kahawa Sukari", "Kahawa Wendani", "Githurai 44", "Githurai 45", "Ruiru", "Juja", "Thika Town", "Limuru", "Kikuyu", "Kabete Town", "Wangige Town", "Banana Hill", "Ruaka Town", "Muchatha", "Karuri Town", "Tigoni", "Ndenderu", "Gachie Town", "Two Rivers Mall", "Village Market", "Garden City", "TRM Mall", "Sarit Centre", "Westgate Mall", "Junction Mall", "The Hub Karen", "Nairobi Hospital", "Aga Khan Hospital", "Kenyatta Hospital", "Mater Hospital", "MP Shah Hospital"
        )
        
        // --- 1. Ride (Bolt/Uber) ---
        places.forEachIndexed { index, p ->
            if (p != "CBD") {
                allRides.add(RideOption(allRides.size, "Bolt Lite 🚗", "CBD → $p", "Moderate", 30, 250 + (index % 100), "Ride", "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?auto=format&fit=crop&w=800&q=80"))
                allRides.add(RideOption(allRides.size, "Boda Boda 🏍️", "Nearby → $p", "Heavy", 60, 150 + (index % 50), "Ride", "https://images.unsplash.com/photo-1558981403-c5f91cbba527?auto=format&fit=crop&w=800&q=80"))
                allRides.add(RideOption(allRides.size, "UberX 🚕", "CBD → $p", "Light", 10, 400 + (index % 150), "Ride", "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?auto=format&fit=crop&w=800&q=80"))
            }
        }

        // --- 2. Rentals ---
        val rentalModels = listOf(
            "Toyota Prado TXL", "Land Rover Defender", "Mercedes G63 AMG", "Range Rover Vogue", "Tesla Model X", "Audi Q7", "BMW X5 M", "Toyota Harrier", "Mazda CX-5", "Nissan X-Trail"
        )
        for (i in 1..25) {
            val model = rentalModels.random()
            allRides.add(RideOption(1000 + i, model, "Daily Rental", "Verified Partner", 0, (3500..20000).random(), "Rentals", "https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=800&q=80"))
        }

        // --- 3. Airport ---
        allRides.add(RideOption(3001, "Flight KQ 102 ✈️", "JKIA → Mombasa", "On Time", 0, 7500, "Airport", "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?auto=format&fit=crop&w=800&q=80"))
        allRides.add(RideOption(3003, "SGR Express 🚆", "Terminus → Mombasa", "On Time", 0, 1500, "Airport", "https://images.unsplash.com/photo-1474487056235-9d697521c3bb?auto=format&fit=crop&w=800&q=80"))

        // --- 4. Package ---
        allRides.add(RideOption(4001, "Quick Box", "Local Delivery", "Fast", 0, 400, "Package", "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=800&q=80"))
        allRides.add(RideOption(4002, "Relocation Van", "House Move", "Pro", 0, 8500, "Package", "https://images.unsplash.com/photo-1519003722824-192d992a6059?auto=format&fit=crop&w=800&q=80"))

        // --- 5. Tow (Emergency) ---
        allRides.add(RideOption(5001, "Flatbed Tow Truck", "Within Nairobi", "Emergency", 0, 4500, "Tow", "https://cdn-icons-png.flaticon.com/512/2830/2830305.png"))
        allRides.add(RideOption(5002, "Wheel Lift Truck", "Within Nairobi", "Fast", 0, 3000, "Tow", "https://cdn-icons-png.flaticon.com/512/2830/2830305.png"))
        allRides.add(RideOption(5003, "Heavy Duty Tow", "Inter-City", "Heavy", 0, 12000, "Tow", "https://cdn-icons-png.flaticon.com/512/2830/2830305.png"))
    }

    suspend fun getRidesByCategory(category: String, destination: String = ""): List<RideOption> {
        delay(800)
        return allRides.filter { ride ->
            val isTargetCategory = ride.category.equals(category, ignoreCase = true)
            
            val isTargetRoute = when {
                destination.isEmpty() -> true
                ride.category == "Rentals" || ride.category == "Package" || ride.category == "Tow" -> true
                else -> ride.route.split("→").last().trim().contains(destination, ignoreCase = true)
            }
            
            isTargetCategory && isTargetRoute
        }
    }

    suspend fun getAllCategories(): List<String> = listOf("Ride", "Package", "Rentals", "Airport", "Tow")
}
