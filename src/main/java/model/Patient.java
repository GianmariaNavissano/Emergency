package model;

import java.time.LocalTime;

public class Patient implements Comparable<Patient>{

	public enum ColorCode{
		NEW, //triage
		WHITE, YELLOW, RED, BLACK, //sala d'attesa
		TREATING, //dentro lo studio medico
		OUT //a casa
	};
	
	private LocalTime arrivalTime;
	private ColorCode color; //Indica lo stato del paziente
	private int num;
	
	public Patient(LocalTime arrivalTime, ColorCode color, int num) {
		super();
		this.arrivalTime = arrivalTime;
		this.color = color;
		this.num = num;
	}

	@Override
	public String toString() {
		return "Patient [arrivalTime=" + arrivalTime + ", color=" + color + ", num=" + num + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + num;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		if (num != other.num)
			return false;
		return true;
	}

	public LocalTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public ColorCode getColor() {
		return color;
	}

	public void setColor(ColorCode color) {
		this.color = color;
	}

	@Override
	public int compareTo(Patient other) {
		if(this.color.equals(other.color))
			return this.arrivalTime.compareTo(other.arrivalTime);
		else if(this.color.equals(Patient.ColorCode.RED))
			return -1;
		else if(other.color.equals(Patient.ColorCode.RED))
			return +1;
		else if(this.color.equals(Patient.ColorCode.YELLOW))
			return -1;
		else 
			return +1;
	}
	
	
	
	
	
}
