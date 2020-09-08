import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainPSO {
	static final double w = 0.729844, c1 = 1.496180, c2 = 1.496180; // Values from Van den Bergh
	
	static final int maxIterations = 60, popSize = 40;
	int iteration = 0;
	
	/** Search domain restrictions listed below.
	 *  Must update the min and max variables to match these
	 *  restrictions before running the PSO
	 */
	/*
	 Booth: -10 <= x, y, <= 10
	 Cross-in-tray: -10 <= x, y, <= 10
	 Holder: -10 <= x, y, <= 10
	 McCormick: -1.5 <= x <= 4 | -3 <= y <= 4
	 Eggholder: -512 <= x, y, <= 512
	 */
	final static double minX = -512, maxX = 512;
	final static double minY = -512, maxY = 512;
	
	public static double calculateFunction(double x, double y) {
		//return Math.pow((x + 2 * y - 7), 2) + Math.pow((2 * x + y -5), 2); // Booth Function
		//return -0.0001 * Math.pow((Math.abs(Math.sin(x) * Math.sin(y) * Math.exp(Math.abs(100 - (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))) / Math.PI ))) + 1), 0.1); // Cross-in-tray function
		//return -Math.abs(Math.sin(x) * Math.cos(y) * Math.exp(Math.abs(1 - (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) / Math.PI)))); // Holder table function
		//return Math.sin(x + y) + Math.pow(x - y,  2) - 1.5 * x + 2.5 * y + 1; // McCormick function
		return -(y + 47) * Math.sin(Math.sqrt(Math.abs((x / 2) + (y + 47)))) - x * Math.sin(Math.sqrt(Math.abs(x - (y + 47)))); // Eggholder function (global min = -959.6407)
	}
	
	static final int variables = 2; // Leave this as 2 unless you add another function above

	private List<Particle> particles;
	
	MainPSO() {
		iteration = 0;
		particles = new ArrayList<>();
		Particle.globalBest = new double[2];
		Particle.globalFitness = Double.MAX_VALUE;
	}
	
		// Initialize all values for PSO
		// Adds particles to the "particles" array, which is the population
		void init() {
			for (int i = 0; i < popSize; i++) {
				Particle particle = new Particle(variables);

				Random r = new Random();
				double d = r.nextDouble() * maxX;
				if (r.nextInt(2) % 2 == 0) {
					d = r.nextDouble() * minX;
				}
				particle.pos[0] = d;
				d = r.nextDouble() * maxY;
				if (r.nextInt(2) % 2 == 0) {
					d = r.nextDouble() * minY;
				}
				particle.pos[1] = d;

				particles.add(particle);
			}
		}
		
		// If a particle finds a position that is more optimal than the current
		// global best position, update the global best position with the new
		// optimal position
		void updateGlobalBest() {
			double pBestFitness;
			for (Particle p : particles) {
				pBestFitness = p.evalBestFitness(p);
				if (pBestFitness < Particle.globalFitness) {
					for (int i = 0; i < variables; i++) {
						Particle.globalBest[i] = p.pBest[i];
					}
					Particle.globalFitness = pBestFitness;
				}
			}
		}
		
		// Check if the coordinates are within the bounds of the problem. If not,
		// set the value that is out of bounds, to that particular bound.
		// If the velocity will set a particle out of bounds, reset the velocity
		// back to zero
		static void checkBounds(Particle p) {
			if (p.pos[0] > maxX) {
				p.pos[0] = maxX;
			} else if (p.pos[0] < minX) {
				p.pos[0] = minX;
			}
			
			if (p.pos[1] > maxY) {
				p.pos[1] = maxY;
			} else if (p.pos[1] < minY) {
				p.pos[1] = minY;
			}
			
			if (p.velocity[0] > maxX || p.velocity[0] < minX) {
				p.velocity[0] = 0;
			}
			if (p.velocity[1] > maxY || p.velocity[1] < minY) {
				p.velocity[1] = 0;
			}
		}
		
		// Calculates and updates the new velocity of the current particle
		void calcNewVelocity(Particle p) {
			Random r = new Random();
			double inertia[] = new double[variables];
			double cognitive[] = new double[variables];
			double social[] = new double[variables];
			
			for (int i = 0; i < variables; i++) {
				Arrays.setAll(inertia, j -> w * p.velocity[j]);
				Arrays.setAll(cognitive, j -> c1 * r.nextDouble() * (p.pBest[j] - p.pos[j]));
				Arrays.setAll(social, j -> c2 * r.nextDouble() * (Particle.globalBest[j] - p.pos[j]));
			}
			
			for (int i = 0; i < variables; i++) {
				p.velocity[i] = inertia[i] + cognitive[i] + social[i];
			}
		}
		
		// Update the position of a particle using the velocity
		void updateParticlePos(Particle p) {
			for (int i = 0; i < variables; i++) {
				p.pos[i] = p.pos[i] + p.velocity[i];
			}
		}
		
		// Base function to run the PSO
		void runPSO() {
			
			init();
			
			while (iteration < maxIterations) {
				double fitness;
				for (Particle p : particles) {
					checkBounds(p);
					fitness = p.evalFitness(p);
					p.updatePersonalBest(p, fitness, variables);
				}
				updateGlobalBest();
				for (Particle p : particles) {
					calcNewVelocity(p);
					updateParticlePos(p);
				}
				iteration++;
			}
			String point = String.format("(%.4f, %.4f)", Particle.globalBest[0], Particle.globalBest[1]);
			System.out.println("Best Point: " + point + " with fitness (global minimum): " + String.format("%.10f", Particle.globalFitness));
		}
		
	public static void main(String args[]) {
		MainPSO INSTANCE;
		
		int runs = 30; // How many times to run the PSO
		for (int i = 0; i < runs; i++) {
			INSTANCE = new MainPSO();
			INSTANCE.runPSO();
		}
		System.out.println("\nThe PSO was executed " + runs + " times");
		
	}
	
	
}
