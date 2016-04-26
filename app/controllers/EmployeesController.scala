package controllers


import javax.inject.Inject

import models.Employee
import service.EmployeeService
import play.api.db._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by Juanlu on 07/04/2016.
  */

class EmployeesController @Inject()(db: Database) extends Controller {

  EmployeeService.database = db

  def list = Action {
    val employees = EmployeeService.listAllEmployees
    val result = Json.toJson(employees)
    Ok(result)
  }

  def create = Action { request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson
    jsonBody.get.validate[Employee] match {
      case s: JsSuccess[Employee] => {
        val employee: Employee = s.get
        EmployeeService.addEmployee(employee)
        Ok("Employee " + employee.firstName + " has been added successfully!")
      }
      case e: JsError => {
        InternalServerError("Error: " + e.toString())
      }
    }
  }

  def read(id: String) = Action {
    val employee = EmployeeService.getEmployee(id.toInt)
    val result = Json.toJson(employee)
    Ok(result)
  }

  def update(id: String) = TODO

  def delete(id: String) = TODO


  implicit val employeeWrites = new Writes[Employee] {
    def writes(employee: Employee) = Json.obj(
      "id" -> employee.id,
      "firstName" -> employee.firstName,
      "lastName" -> employee.lastName,
      "gender" -> employee.gender,
      "birthDate" -> employee.birthDate,
      "hireDate" -> employee.hireDate
    )
  }

  implicit val employeeReads: Reads[Employee] = (
    (JsPath \ "emp_no").readNullable[Int] and
      (JsPath \ "first_name").read[String] and
      (JsPath \ "last_name").read[String] and
      (JsPath \ "gender").read[String] and
      (JsPath \ "birth_date").read[String] and
      (JsPath \ "hire_date").read[String]
    ) (Employee.apply _)

}
