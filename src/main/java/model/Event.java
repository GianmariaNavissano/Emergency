package model;

import java.time.LocalTime;

public class Event implements Comparable<Event>{
	
	
	
	@Override
	public String toString() {
		return "Event [time=" + time + ", type=" + type + ", patient=" + patient + "]";
	}

	enum EventType{
		ARRIVAL, //Arrivo
		TRIAGE, //dopo 5 minuti di triage si entra in sala d'attesa
		TIMEOUT, //Attesa finita (per qualcosa)
		FREE_STUDIO, //si libera uno studio e quindi posso chiamare un altro paziente
		TREATED, //paziente curato
		TICK //timer per controllare se ci sono studi vuoti
	};
	
	private LocalTime time;
	private EventType type;
	private Patient patient;
	
	public Event(LocalTime time, EventType type, Patient patient) {
		super();
		this.time = time;
		this.type = type;
		this.patient = patient;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	@Override
	public int compareTo(Event other) {
		return this.time.compareTo(other.time);
	}
	
	

}
