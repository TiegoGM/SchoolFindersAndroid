package com.example.schoolfinders.models

data class user (
    var name: String = "",
    var surname: String = "",
    var id: String = "",
    var phoneNumbers: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",


    ) {
        override fun toString(): String {
            return " $name $surname $id  $phoneNumbers $email  $password $confirmPassword "
        }
}