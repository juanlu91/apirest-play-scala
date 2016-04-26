package models

case class Employee(
                     id: Option[Int],
                     firstName: String,
                     lastName: String,
                     gender: String,
                     birthDate: String,
                     hireDate: String
                   )
