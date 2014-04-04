package org.hello;

public class RespEmployee {
	private Employee employee;

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Override
	public String toString() {
		return "RespEmployee [employee=" + employee + "]";
	}
	
	
}
