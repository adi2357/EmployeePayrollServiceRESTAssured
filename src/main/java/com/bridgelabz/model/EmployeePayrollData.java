package com.bridgelabz.model;

import java.time.LocalDate;

public class EmployeePayrollData {

	private int id;
	private String name;
	private double salary;
	private LocalDate startDate;
	private String gender;

	public EmployeePayrollData() {
	}

	public EmployeePayrollData(int employeeId, String employeeName, double salary) {
		this.id = employeeId;
		this.name = employeeName;
		this.salary = salary;
	}

	public EmployeePayrollData(int employeeId, String employeeName, double salary, LocalDate startDate) {
		this(employeeId, employeeName, salary);
		this.startDate = startDate;
	}

	
	public EmployeePayrollData(int employeeId, String employeeName, double salary, LocalDate startDate, String gender) {
		this(employeeId, employeeName, salary, startDate);
		this.gender = gender;
	}

	public int getEmployeeId() {
		return id;
	}

	public void setEmployeeId(int employeeId) {
		this.id = employeeId;
	}

	public String getEmployeeName() {
		return name;
	}

	public void setEmployeeName(String employeeName) {
		this.name = employeeName;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "Id : " + id + "\t" + "Name : " + name + "\t" + "Gender : " + gender + "\t" + "Salary : "
				+ salary + "\t" + "Start Date : " + startDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayrollData other = (EmployeePayrollData) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	
}
