package models

import java.util.Date

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

import scala.language.postfixOps

/**
  * Helper for pagination.
  */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

case class Employee(
                     id: Option[Int] = None,
                     firstName: String,
                     lastName: Option[String],
                     gender: Option[String],
                     birthDate: Option[Date],
                     hireDate: Option[Date]
                   )

object Employee {
  // -- Parsers

  /**
    * Parse an Employee from a ResultSet
    */
  val employee = {
    get[Option[Int]]("emp_no") ~
      get[String]("first_name") ~
      get[Option[String]]("last_name") ~
      get[Option[String]]("gender") ~
      get[Option[Date]]("birth_date") ~
      get[Option[Date]]("hire_date") map {
      case id ~ firstName ~ lastName ~ gender ~ birthDate ~ hireDate => Employee(id, firstName, lastName, gender, birthDate, hireDate)
    }
  }

  // -- Queries

  /**
    * Retrieve an employee from the id.
    */
  def findById(id: Int): Option[Employee] = {
    DB.withConnection { implicit connection =>
      SQL"select * from employees where emp_no = $id".as(employee.singleOpt)
    }
  }

  /**
    * Return a page of Employee.
    *
    * @param page     Page to display
    * @param pageSize Number of employees per page
    * @param orderBy  Employee property used for sorting
    * @param filter   Filter applied on the name column
    */
  def list(page: Int = 0, pageSize: Int = 1000, orderBy: Int = 1, filter: String = "%"): Page[(Employee)] = {

    val offset = pageSize * page

    DB.withConnection { implicit connection =>

      val employees = SQL"""
                      SELECT * FROM employees
                      WHERE employees.first_name LIKE $filter
                      ORDER BY -$orderBy DESC
                      LIMIT $pageSize OFFSET $offset"""
        .as(employee *)

      val totalRows = SQL"""
          SELECT COUNT(*) FROM employees
          WHERE employees.first_name LIKE $filter
        """
        .as(scalar[Long].single)

      Page(employees, page, offset, totalRows)

    }

  }

  /**
    * Update an employee.
    *
    * @param id       The employee id
    * @param employee The employee values.
    */
  def update(id: Int, employee: Employee) = {
    DB.withConnection { implicit connection =>
      SQL"""
          UPDATE employees
          SET first_name = ${employee.firstName}, last_name = ${employee.lastName},
          gender = ${employee.gender}, birth_date = ${employee.birthDate}, hire_date = ${employee.hireDate}
          where emp_no = $id
        """
        .executeUpdate()
    }
  }

  /**
    * Insert a new employee.
    *
    * @param employee The employee values.
    */
  def insert(employee: Employee) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          INSERT INTO employees (first_name, last_name, gender, birth_date, hire_date) VALUES (
            {first_name}, {last_name}, {gender}, {birth_date}, {hire_date}
            )
        """
      ).on(
        'first_name -> employee.firstName,
        'last_name -> employee.lastName,
        'gender -> employee.gender,
        'birth_date -> employee.birthDate,
        'hire_date -> employee.hireDate
      ).executeUpdate()
    }
  }

  /**
    * Delete an employee.
    *
    * @param id Id of the employee to delete.
    */
  def delete(id: Int) = {
    DB.withConnection { implicit connection =>
      SQL"DELETE FROM employees WHERE emp_no = $id".executeUpdate()
    }
  }

}

