package service

import models.{Employee}
import play.api.db.{Database}

/**
  * Created by Juanlu on 25/04/2016.
  */
object EmployeeService {

  var database: Database = null

  def listAllEmployees: Seq[Employee] = {
    database.withConnection { conn =>
      var employees: Seq[Employee] = Seq()
      try {
        val stmt = conn.createStatement
        val rs = stmt.executeQuery("SELECT * FROM employees.employees LIMIT 1000")

        while (rs.next()) {
          val employee = new Employee(Option(rs.getInt("emp_no")), rs.getString("first_name"),
            rs.getString("last_name"), rs.getString("gender"), rs.getString("birth_date"), rs.getString("hire_date"))
          employees = employees :+ employee
        }
      } finally {
        conn.close()
      }
      return employees
    }
  }

  def addEmployee(employee: Employee) = {
    database.withConnection { conn =>
      try {
        val prep = conn.prepareStatement("INSERT INTO employees (first_name, last_name, gender, birth_date, hire_date) VALUES (?, ?, ?, ?, ?) ")
        prep.setString(1, employee.firstName)
        prep.setString(2, employee.lastName)
        prep.setString(3, employee.gender)
        prep.setString(4, employee.birthDate)
        prep.setString(5, employee.hireDate)
        prep.executeUpdate
      } finally {
        conn.close()
      }
    }
  }

  def getEmployee(id: Int): Employee = {
    database.withConnection { conn =>
      var employee: Employee = null
      try {
        val stmt = conn.createStatement
        val rs = stmt.executeQuery(s"SELECT * FROM employees.employees WHERE emp_no=$id")

        while (rs.next()) {
          employee = new Employee(Option(rs.getInt("emp_no")), rs.getString("first_name"),
            rs.getString("last_name"), rs.getString("gender"), rs.getString("birth_date"), rs.getString("hire_date"))
        }
      } finally {
        conn.close()
      }
      return employee
    }
  }

}
