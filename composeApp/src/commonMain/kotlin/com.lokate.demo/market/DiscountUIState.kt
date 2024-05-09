package com.lokate.demo.market

data class DiscountUIState(
    val campaign: String,
    val pool: List<String>,
)

val giris =
    DiscountUIState(
        campaign = "giris",
        pool =
            listOf(
                "Lokate Market'e Hoşgeldiniz, alışverişiniz boyunca size konum bazlı öneriler sunacağım.",
            ),
    )
val bebekBezi =
    DiscountUIState(
        campaign = "bebek bezi",
        pool =
            listOf(
                "50'li Prima Bebek Bezi %30 indirimde!",
                "30'lu Molfix Bebek Bezi %10 indirimde!",
                "Aptamil devam sütü 400 gr yalnızca 449 ₺!",
            ),
    )
val kuruyemis =
    DiscountUIState(
        campaign = "kuruyemis",
        pool =
            listOf(
                "Karışık kuruyemiş paketi 1 kg %20 indirimde!",
                "Tadım Antep fıstığı 500 gr yalnızca 29.99 ₺!",
                "Amigo Kavrulmuş badem 250 gr %15 indirimde!",
            ),
    )
val bira =
    DiscountUIState(
        campaign = "bira",
        pool =
            listOf(
                "Efes Pilsen 6'lı kutu %20 indirimde!",
                "Tuborg Gold 4'lü kutu %15 indirimde!",
                "Leffe Blonde 33 cl %10 indirimde!",
            ),
    )
val kahve =
    DiscountUIState(
        campaign = "kahve",
        pool =
            listOf(
                "Starbucks Espresso Kahve %25 indirimde!",
                "Nescafe Gold 200 gr yalnızca 24.99 ₺!",
                "Kahve Makinesi alana Lavazza kahve kapsülü hediye!",
            ),
    )
