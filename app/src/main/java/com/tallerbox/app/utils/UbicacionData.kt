package com.tallerbox.app.utils

val estados = listOf(
    "Yucatán",
    "Campeche",
    "Quintana Roo",
    "Tabasco",
    "Chiapas",
    "Veracruz",
    "Oaxaca",
    "Puebla",
    "Tlaxcala",
    "Ciudad de México",
    "Estado de México",
    "Morelos",
    "Hidalgo",
    "Guerrero",
    "San Luis Potosí",
    "Querétaro",
    "Michoacán",
    "Guanajuato",
    "Colima",
    "Jalisco",
    "Tamaulipas",
    "Nuevo León",
    "Zacatecas",
    "Aguascalientes",
    "Nayarit",
    "Durango",
    "Coahuila",
    "Sinaloa",
    "Chihuahua",
    "Sonora",
    "Baja California Sur",
    "Baja California"
)


val municipiosPorEstado = mapOf(
    "Yucatán" to listOf(
        "Halachó",
        "Cepeda, Halachó",
        "Cuch-Holoch, Halachó",
        "Sihó, Halachó",
        "Santa María Acú, Halachó",
        "Kancabchén, Halachó",
        "San Mateo, Halachó",
        "Dzidzibachi, Halachó",
        "Maxcanú",
        "Kuchmil, Maxcanú",
        "San Fernando, Maxcanú",
        "Chunchucmil",
        "Kinchil",
        "Celestún",
        "Otro municipio"
    ),
    "Campeche" to listOf(
        "Calkiní",
        "Nunkiní, Calkiní",
        "Bécal, Calkiní",
        "Santa Cruz Pueblo, Calkiní",
        "Champotón",
        "Hopelchén",
        "Tenabo",
        "Otro municipio"
    ),
    "Quintana Roo" to listOf(
        "Otro municipio"
    )
) + estados
    .filterNot { it in listOf("Yucatán", "Campeche", "Quintana Roo") }
    .associateWith { listOf("Otro municipio") }
