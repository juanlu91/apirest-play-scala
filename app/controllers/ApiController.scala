package controllers

import java.util.Date

import models.Employee
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller}
import play.api.libs.functional.syntax._

/**
  * Created by Juanlu on 27/04/2016.
  */
class ApiController extends Controller {

  def list() = Action { implicit request =>
    val result = Json.toJson(Employee.list().items)
    Ok(result)
  }

  def get(id: Int) = Action {implicit request =>
    val employee = Employee.findById(id)
    val result = Json.toJson(employee)
    Ok(result)
  }

  def create() = Action { implicit request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson
    jsonBody.get.validate[Employee] match {
      case s: JsSuccess[Employee] => {
        val employee: Employee = s.get
        Employee.insert(employee)
        Ok("Employee " + employee.firstName + " has been added successfully!")
      }
      case e: JsError => {
        InternalServerError("Error: " + e.toString())
      }
    }
  }

  def update(id: Int) = Action {implicit request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson
    jsonBody.get.validate[Employee] match {
      case s: JsSuccess[Employee] => {
        val employee: Employee = s.get
        Employee.update(id, employee)
        Ok("Employee " + employee.firstName + " has been updated successfully!")
      }
      case e: JsError => {
        InternalServerError("Error: " + e.toString())
      }
    }
  }

  def delete(id: Int) = Action {implicit request =>
    Employee.delete(id)
    Ok("Employee " + id + " has been deleted!")
  }


  /**
    * Reads and Writes
    */

  implicit val employeeWrites = new Writes[Employee] {
    def writes(employee: Employee) = Json.obj(
      "id" -> employee.id,
      "firstName" -> employee.firstName,
      "lastName" -> employee.lastName,
      "gender" -> employee.gender,
      "birthDate" -> employee.birthDate.get.toString(),
      "hireDate" -> employee.hireDate.get.toString()
    )
  }

  implicit val employeeReads: Reads[Employee] = (
    (JsPath \ "id").readNullable[Int] and
      (JsPath \ "firstName").read[String] and
      (JsPath \ "lastName").readNullable[String] and
      (JsPath \ "gender").readNullable[String] and
      (JsPath \ "birthDate").readNullable[Date](Reads.dateReads("yyyy-MM-dd")) and
      (JsPath \ "hireDate").readNullable[Date](Reads.dateReads("yyyy-MM-dd"))
    ) (Employee.apply _)
}
