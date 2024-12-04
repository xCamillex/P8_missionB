package com.openclassrooms.p8_vitesse.domain.model

class Candidate (
    var id : Long,
    var photo : String,
    var firstName : String,
    var lastName : String,
    var phoneNumber : String,
    var emailAddress : String,
    var dateOfBirth : Long,
    var expectedSalary : Int,
    var informationNote : String,
    var isFavorite : Boolean
) {
}