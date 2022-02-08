package com.napier.sem;

import java.sql.*;

public class App
{
    /**
     * Connection to MySQL database.
     */
    private Connection con = null;

    /**
     * Connect to the MySQL database.
     */
    public void connect()
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Employee getEmployee(int ID)
    {
        Employee emp = new Employee();
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT emp_no, first_name, last_name "
                            + "FROM employees "
                            + "WHERE emp_no = " + ID
                            + ";";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);
            // Return new employee if valid.
            // Check one is returned
            if (rset.next())
            {
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("first_name");
                emp.last_name = rset.getString("last_name");
            }
            else
                return null;

            strSelect =
                    "SELECT titles.title as title, salaries.salary as salary\n" +
                    "FROM titles\n" +
                    "JOIN salaries ON (titles.emp_no = salaries.emp_no AND titles.to_date = salaries.to_date)\n" +
                    "WHERE titles.emp_no = " + ID + "\n" +
                    "AND titles.to_date = '9999-01-01'\n" +
                    ";";
            rset = stmt.executeQuery(strSelect);
            if (rset.next())
            {
                emp.title = rset.getString("title");
                emp.salary = rset.getInt("salary");
            }
            else
                return null;

            strSelect =
                    "SELECT departments.dept_name as department \n" +
                            "FROM dept_emp\n" +
                            "JOIN departments ON (dept_emp.dept_no = departments.dept_no)\n" +
                            "WHERE dept_emp.emp_no = " + ID + ";";
            rset = stmt.executeQuery(strSelect);
            if (rset.next())
            {
                emp.dept_name = rset.getString("department");
            }
            else
                return null;

            strSelect =
                    "SELECT employees.first_name as first_name, employees.last_name as last_name\n" +
                            "FROM dept_manager\n" +
                            "JOIN departments ON (dept_manager.dept_no = departments.dept_no)\n" +
                            "JOIN employees ON (dept_manager.emp_no = employees.emp_no)\n" +
                            "WHERE departments.dept_name = '" + emp.dept_name + "'" +
                            "AND dept_manager.to_date = " + "'9999-01-01'" +
                            ";";
            rset = stmt.executeQuery(strSelect);
            if (rset.next())
            {
                emp.manager = rset.getString("first_name") + " " + rset.getString("last_name");
            }
            else
                return null;

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
        return emp;

    }

    public void displayEmployee(Employee emp) {
        if (emp != null) {
            System.out.println(
                    emp.emp_no + " "
                    + emp.first_name + " "
                    + emp.last_name + "\n"
                    + emp.title + "\n"
                    + "Salary: " + emp.salary + "\n"
                    + emp.dept_name + "\n"
                    + "Manager: " + emp.manager + "\n");
        }
    }

    public static void main(String[] args)
    {
        // Create new App object
        App a = new App();

        // Connect to database
        a.connect();

        // Retrieve employee with employee id: 255530
        Employee emp = a.getEmployee(255530);

        // Display employee
        a.displayEmployee(emp);

        // Disconnect from database
        a.disconnect();
    }
}