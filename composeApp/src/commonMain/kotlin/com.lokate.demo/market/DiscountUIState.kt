package com.lokate.demo.market

data class DiscountUIState(
    val category: String,
    val pool: List<String>,
)

val selfCare =
    DiscountUIState(
        category = "Self Care",
        pool =
            listOf(
                "There is a discount on \"Head & Shoulders Mentol 400ML\"",
                "Buy 2, get 1 free on Dove shampoo",
                "Save 30% on L'Oreal hair care products",
            ),
    )
val electronics =
    DiscountUIState(
        category = "Electronics",
        pool =
            listOf(
                "Special offer on electronics this weekend",
                "Get a free wireless charger with any smartphone purchase",
                "Limited stock: 50% off on selected laptops",
            ),
    )
val cloth =
    DiscountUIState(
        category = "Cloth",
        pool =
            listOf(
                "Get 20% off on all clothing items",
                "New arrivals: Explore our latest fashion collection",
                "Buy one dress, get the second one at half price",
            ),
    )
val homeAppliances =
    DiscountUIState(
        category = "Home Appliances",
        pool =
            listOf(
                "Limited time offer: Buy one get one free on selected items",
                "Flash sale: Up to 50% off on home appliances",
                "Exclusive deals for loyalty members only",
            ),
    )
