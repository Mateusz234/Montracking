package mg.montracking.repository;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import mg.montracking.core.utils.RegulationError;
import mg.montracking.entity.Regulation;

public class RegulationRepository {

	private ScheduledExecutorService overseerScheduler;
	private Regulation regulation;
	private boolean startWritingTask = true;

	private Runnable saveToFile = () -> {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println(timestamp);
		try (FileWriter fw = new FileWriter("regulationData.txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(regulation.getBottomMotorPwm()+ " " + regulation.getxError() + " " + regulation.getUpperMotorPwm()+ " " + regulation.getyError());
//			out.println("Timestamp: " + timestamp);
		} catch (IOException e) {
			System.out.println("Exception when saving to file: " + e);
		}
	};

	public void saveToDb(Regulation regulation) {
		try {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("mDB");
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			em.persist(regulation);
			em.getTransaction().commit();
			em.close();
			emf.close();
		} catch (PersistenceException e) {
			System.out.println("Exception when persisting data!: " + e);
		}

	}

	public void saveToFile(Regulation regulation) {
		this.regulation = regulation;
		if (startWritingTask) {
			this.overseerScheduler = Executors.newSingleThreadScheduledExecutor();
			this.overseerScheduler.scheduleAtFixedRate(saveToFile, 0, 50, TimeUnit.MILLISECONDS);
		}
		startWritingTask = false;
	}
}
