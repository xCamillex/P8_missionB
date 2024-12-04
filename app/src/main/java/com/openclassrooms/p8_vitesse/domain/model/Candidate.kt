package com.openclassrooms.p8_vitesse.domain.model

import com.openclassrooms.p8_vitesse.data.entity.CandidateDto

data class Candidate (
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
    fun toDto(): CandidateDto {
    return CandidateDto(
        id = this.id?:0,
        firstName = this.firstName,
        lastName = this.lastName,
        photo = this.photo,
        phoneNumber = this.phoneNumber,
        emailAddress = this.emailAddress,
        dateOfBirth = this.dateOfBirth,
        expectedSalary = this.expectedSalary,
        informationNote = this.informationNote,
        isFavorite = this.isFavorite
    )
}

    companion object {
        fun fromDto(candiateDto: CandidateDto): Candidate {
            return candiateDto.toModel()
        }
    }
}