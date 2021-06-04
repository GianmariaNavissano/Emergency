package model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import model.Event.EventType;
import model.Patient.ColorCode;

public class Simulator {
	
	//Coda degli eventi
	private PriorityQueue<Event> queue;
	
	//Modello del mondo
	private List<Patient> patients;
	private PriorityQueue<Patient> waitingRoom; //per pazienti in sala d'attesa
	
	private int freeStudios;
	
	private Patient.ColorCode ultimoColore;
	
	//parametri di input
	private int totalStudios = 3;
	
	private int numPatients = 120;
	private Duration T_ARRIVAL = Duration.ofMinutes(5);
	
	private Duration DURATION_TRIAGE = Duration.ofMinutes(5);
	private Duration DURATION_WHITE = Duration.ofMinutes(10);
	private Duration DURATION_YELLOW = Duration.ofMinutes(15);
	private Duration DURATION_RED = Duration.ofMinutes(30);
	
	private Duration TIMEOUT_WHITE = Duration.ofMinutes(60);
	private Duration TIMEOUT_YELLOW = Duration.ofMinutes(30);
	private Duration TIMEOUT_RED = Duration.ofMinutes(30);
	
	private LocalTime startTime = LocalTime.of(8,  00);
	private LocalTime endTime = LocalTime.of(20,  00);
	
	//Parametri di output
	private int patientsTreated;
	private int patientsAbandoned;
	private int patientsDead;
	
	//Inizializzo il simulatore e crea gli eventi iniziali
	public void init(){
		//inizializzo coda eventi
		this.queue = new PriorityQueue<>();
		
		//inizializzo modello del mondo
		this.patients = new ArrayList<>();
		this.waitingRoom = new PriorityQueue<Patient>();
		this.freeStudios = this.totalStudios;
		this.ultimoColore = ColorCode.RED;
		
		//inietto gli eventi di input (ARRIVAL)
		
		LocalTime ora = this.startTime;
		int inseriti = 0;
		Patient.ColorCode colore = ColorCode.WHITE;
		
		this.queue.add(new Event(ora, EventType.TICK, null));
		
		while(ora.isBefore(endTime) && inseriti<this.numPatients) {
			Patient p = new Patient(ora, ColorCode.NEW, inseriti);
			
			Event e = new Event(ora, EventType.ARRIVAL, p);
			
			this.queue.add(e);
			this.patients.add(p);
			
			inseriti++;
			ora = ora.plus(T_ARRIVAL);
		}
		
		
		this.patientsAbandoned = 0;
		this.patientsDead = 0;
		this.patientsTreated = 0;
	}
	

	private Patient.ColorCode prossimoColore(){
		if(ultimoColore.equals(ColorCode.WHITE))
			ultimoColore = ColorCode.YELLOW;
		else if(ultimoColore.equals(ColorCode.YELLOW))
			ultimoColore = ColorCode.RED;
		else 
			ultimoColore = ColorCode.WHITE;
		return ultimoColore;
	}
	
	//Eseguo la simulazione
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			processEvent(e);
		}
	}
	
	private void processEvent(Event e) {
		
		Patient p = e.getPatient();
		LocalTime ora = e.getTime();
		

		switch(e.getType()) {
		case ARRIVAL: 
			this.queue.add(new Event(ora.plus(DURATION_TRIAGE), EventType.TRIAGE, p));
			break;
			
		case TRIAGE:
			p.setColor(this.prossimoColore());
			if(p.getColor().equals(Patient.ColorCode.WHITE)) {
				this.queue.add(new Event(ora.plus(TIMEOUT_WHITE), EventType.TIMEOUT, p));
				this.waitingRoom.add(p);
			} else if(p.getColor().equals(Patient.ColorCode.YELLOW)) {
				this.queue.add(new Event(ora.plus(TIMEOUT_YELLOW), EventType.TIMEOUT, p));
				this.waitingRoom.add(p);
			} else if(p.getColor().equals(Patient.ColorCode.RED)) {
				this.queue.add(new Event(ora.plus(TIMEOUT_RED), EventType.TIMEOUT, p));
				this.waitingRoom.add(p);
			}
			break;
			
		case FREE_STUDIO:
			if(this.freeStudios==0)
				return;
			//Quale paziente ha diritto ad entrare?
			Patient primo = this.waitingRoom.poll();
			if(primo!=null) {
				//ammetti paziente nello studio
				if(primo.getColor().equals(ColorCode.WHITE))
					this.queue.add(new Event(ora.plus(DURATION_WHITE), EventType.TREATED, primo));
				if(primo.getColor().equals(ColorCode.YELLOW))
					this.queue.add(new Event(ora.plus(DURATION_YELLOW), EventType.TREATED, primo));
				if(primo.getColor().equals(ColorCode.RED))
					this.queue.add(new Event(ora.plus(DURATION_RED), EventType.TREATED, primo));
				primo.setColor(Patient.ColorCode.TREATING);
				this.freeStudios --;
			}
		break;
		
		case TIMEOUT:
			Patient.ColorCode colore = p.getColor();
			switch(colore) {
			case WHITE:
				this.waitingRoom.remove(p);
				p.setColor(ColorCode.OUT);
				this.patientsAbandoned ++;
				break;
				
			case YELLOW:
				this.waitingRoom.remove(p);
				p.setColor(ColorCode.RED);
				this.waitingRoom.add(p);
				this.queue.add(new Event(ora.plus(TIMEOUT_RED), EventType.TIMEOUT, p));
				
				break;
				
			case RED:
				this.waitingRoom.remove(p);
				p.setColor(ColorCode.BLACK);
				this.patientsDead ++;
				break;
				
			}
			break;
			
		case TREATED:
			p.setColor(ColorCode.OUT);
			this.patientsTreated ++;
			this.freeStudios ++;
			this.queue.add(new Event(ora, EventType.FREE_STUDIO, null));
			break;
		
		case TICK:
			if(this.freeStudios>0 && !this.waitingRoom.isEmpty())
				this.queue.add(new Event(ora, EventType.FREE_STUDIO, null));
			if(ora.isBefore(endTime))
				this.queue.add(new Event(ora.plus(Duration.ofMinutes(5)), EventType.TICK, null));
			break;
		}
	}

	public int getPatientsTreated() {
		return patientsTreated;
	}

	public int getPatientsAbandoned() {
		return patientsAbandoned;
	}

	public int getPatientsDead() {
		return patientsDead;
	}

	public void setPatients(List<Patient> patients) {
		this.patients = patients;
	}

	public void setFreeStudios(int freeStudios) {
		this.freeStudios = freeStudios;
	}

	public void setTotalStudios(int totalStudios) {
		this.totalStudios = totalStudios;
	}

	public void setNumPatients(int numPatients) {
		this.numPatients = numPatients;
	}

	public void setT_ARRIVAL(Duration t_ARRIVAL) {
		T_ARRIVAL = t_ARRIVAL;
	}

	public void setDURATION_TRIAGE(Duration dURATION_TRIAGE) {
		DURATION_TRIAGE = dURATION_TRIAGE;
	}

	public void setDURATION_WHITE(Duration dURATION_WHITE) {
		DURATION_WHITE = dURATION_WHITE;
	}

	public void setDURATION_YELLOW(Duration dURATION_YELLOW) {
		DURATION_YELLOW = dURATION_YELLOW;
	}

	public void setDURATION_RED(Duration dURATION_RED) {
		DURATION_RED = dURATION_RED;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	
	
	
}
