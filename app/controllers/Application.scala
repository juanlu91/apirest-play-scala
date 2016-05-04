package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import views._
import models._

/**
  * Manage a database of employees
  */
class Application extends Controller {

  /**
    * This result directly redirect to the application home.
    */
  val Home = Redirect(routes.Application.list(0, 2, ""))

  /**
    * Describe the employee form (used in both edit and create screens).
    */
  val employeeForm = Form(
    mapping(
      "id" -> ignored(None: Option[Int]),
      "firstName" -> nonEmptyText,
      "lastName" -> optional(text),
      "gender" -> optional(text),
      "birthDate" -> optional(date("yyyy-MM-dd")),
      "hireDate" -> optional(date("yyyy-MM-dd"))
    )(Employee.apply)(Employee.unapply)
  )

  // -- Actions

  /**
    * Handle default path requests, redirect to employees list
    */
  def index = Action {
    Home
  }

  /**
    * Display the paginated list of employees.
    *
    * @param page    Current page number (starts from 0)
    * @param orderBy Column to be sorted
    * @param filter  Filter applied on employee names
    */
  def list(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    Ok(html.list(
      Employee.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")), orderBy, filter
    ))
  }

  /**
    * Display the 'new employee form'.
    */
  def create = Action {
    Ok(html.createForm(employeeForm))
  }

  /**
    * Display the 'edit form' of an existing Employee.
    *
    * @param id Id of the employee to edit
    */
  def edit(id: Int) = Action {
    Employee.findById(id).map { employee =>
      Ok(html.editForm(id, employeeForm.fill(employee)))
    }.getOrElse(NotFound)
  }

  /**
    * Handle the 'edit form' submission
    *
    * @param id Id of the employee to edit
    */
  def update(id: Int) = Action { implicit request =>
    employeeForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id, formWithErrors)),
      employee => {
        Employee.update(id, employee)
        Home.flashing("success" -> s"Employee ${employee.firstName} has been updated")
      }
    )
  }

  /**
    * Handle the 'new employee form' submission.
    */
  def save = Action { implicit request =>
    employeeForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createForm(formWithErrors)),
      employee => {
        Employee.insert(employee)
        Home.flashing("success" -> s"Employee ${employee.firstName} has been created")
      }
    )
  }

  /**
    * Handle employee deletion.
    */
  def delete(id: Int) = Action {
    Employee.delete(id)
    Home.flashing("success" -> "Employee has been deleted")
  }
}

