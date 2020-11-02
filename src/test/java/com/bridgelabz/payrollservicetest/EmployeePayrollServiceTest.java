package com.bridgelabz.payrollservicetest;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bridgelabz.model.EmployeePayrollData;
import com.bridgelabz.payrollservice.EmployeePayrollService;
import com.bridgelabz.payrollservice.EmployeePayrollService.IOService;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class EmployeePayrollServiceTest {

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	private EmployeePayrollData[] getEmployeeList() {
		Response response = RestAssured.get("/employee-payroll");
		System.out.println("Employee Payroll Entries in JSON Server :\n" + response.asString());
		EmployeePayrollData[] arrayOfEmployees = new Gson().fromJson(response.asString(), EmployeePayrollData[].class);
		return arrayOfEmployees;
	}

	public Response addEmployeeToJsonServer(EmployeePayrollData newEmployee) {
		String employeeJson = new Gson().toJson(newEmployee);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(employeeJson);
		return request.post("/employee-payroll");
	}

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchNumberOfEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmployees = {
				new EmployeePayrollData(1, "Aditya Verma", 800000.0),
				new EmployeePayrollData(2, "Akhil Singh", 850000.0),
				new EmployeePayrollData(3, "Anamika Bhatt", 900000.0) 
				};

		EmployeePayrollService payrollServiceObject = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));
		payrollServiceObject.writeEmployeeData(IOService.FILE_IO);
		payrollServiceObject.printEmployeePayrollData(IOService.FILE_IO);
		Assert.assertEquals(3, payrollServiceObject.countEntries(IOService.FILE_IO));
	}

	@Test
	public void given3EmployeesWhenReadFromFileShouldMatchNumberOfEmployeeEntries() {

		EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
		payrollServiceObject.readEmployeeData(IOService.FILE_IO);
		long countOfEntriesRead = payrollServiceObject.countEntries(IOService.CONSOLE_IO);
		Assert.assertEquals(3, countOfEntriesRead);
	}

	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {

		EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
		payrollServiceObject.readEmployeeData(IOService.DB_IO);
		long countOfEntriesRetrieved = payrollServiceObject.countEntries(IOService.DB_IO);
		payrollServiceObject.printEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(6, countOfEntriesRetrieved);
	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() {
		try {
			EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
			payrollServiceObject.readEmployeeData(IOService.DB_IO);
			payrollServiceObject.updateEmployeeSalary("Teresa", 3000000.0);
			boolean result = payrollServiceObject.checkEmployeePayrollInSyncWithDB("Teresa");
			Assert.assertTrue(result);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
		payrollServiceObject.readEmployeeData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2019, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> employeePayrollData = payrollServiceObject.readEmployeeDataForDateRange(IOService.DB_IO, startDate, endDate);
		Assert.assertEquals(4, employeePayrollData.size());
	}

	@Test
	public void givenPayrollDataInDB_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue() {
		EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
		payrollServiceObject.readEmployeeData(IOService.DB_IO);
		Map<String, Double> averageSalaryByGender = payrollServiceObject.readAverageSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(averageSalaryByGender.get("M").equals(2000000.0) && 
						  averageSalaryByGender.get("F").equals(3000000.0));
	}

	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() {
		try {
			EmployeePayrollService payrollServiceObject = new EmployeePayrollService();
			payrollServiceObject.readEmployeeData(IOService.DB_IO);
			payrollServiceObject.addEmployeeToPayroll("Mark", 3000000.0, LocalDate.now(), "M");
			boolean result = payrollServiceObject.checkEmployeePayrollInSyncWithDB("Mark");
			Assert.assertTrue(result);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void givenEmployeeDataInJsonServer_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollData[] arrayOfEmployees = getEmployeeList();
		EmployeePayrollService payrollServiceObject = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));
		long entries = payrollServiceObject.countEntries(IOService.REST_IO);
		Assert.assertEquals(4, entries);
	}

	@Test
	public void givenNewEmployee_WhenAddedToJSONServer_ShouldMatch201ResponseAndEmployeeCount() {
		EmployeePayrollData[] arrayOfEmployees = getEmployeeList();
		EmployeePayrollService payrollServiceObject = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));
		
		EmployeePayrollData newEmployee = new EmployeePayrollData(0,"Manish",4000000.0, LocalDate.now(), "M");
		Response response = addEmployeeToJsonServer(newEmployee);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		
		newEmployee = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
		payrollServiceObject.addEmployeeToPayroll(newEmployee, IOService.REST_IO);
		long entries = payrollServiceObject.countEntries(IOService.REST_IO);
		Assert.assertEquals(5, entries);
	}

	@Test
	public void givenMultipleEmployees_WhenAdded_ShouldMatchTheCount() throws InterruptedException {
		
		EmployeePayrollData[] arrayOfEmployees = getEmployeeList();
		EmployeePayrollService payrollServiceObject = new EmployeePayrollService(Arrays.asList(arrayOfEmployees));
		
		EmployeePayrollData[] arrayOfNewEmployees = {
				new EmployeePayrollData(0, "Akshat", 400000.0, LocalDate.now(),  "M"),
				new EmployeePayrollData(0, "Rahul", 3000000.0, LocalDate.now(), "M"),
				new EmployeePayrollData(0, "Kashi", 4000000.0, LocalDate.now(), "F"),
				new EmployeePayrollData(0, "Mohit", 3000000.0, LocalDate.now(), "M"),
				new EmployeePayrollData(0, "Aasmi", 3000000.0, LocalDate.now(), "F")
			};
		
		Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
		Instant threadStart = Instant.now();
		Arrays.asList(arrayOfNewEmployees).stream().forEach(employee -> {
			Runnable employeeAddition = () -> {
				employeeAdditionStatus.put(employee.hashCode(), false);
				Response response = addEmployeeToJsonServer(employee);
				int statusCode = response.getStatusCode();
				Assert.assertEquals(201, statusCode);
				
				EmployeePayrollData newEmployee = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
				payrollServiceObject.addEmployeeToPayroll(newEmployee, IOService.REST_IO);
				employeeAdditionStatus.put(employee.hashCode(), true);
			};
			Thread thread = new Thread(employeeAddition, employee.getName());
			thread.start();
		});
		while(employeeAdditionStatus.containsValue(false)) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		Instant threadEnd = Instant.now();
		System.out.println("Duration with thread : "+ Duration.between(threadStart, threadEnd));
		long entries = payrollServiceObject.countEntries(IOService.REST_IO);
		Assert.assertEquals(10, entries);
	}
}
