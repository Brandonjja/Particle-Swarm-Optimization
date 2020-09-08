
public class Particle {
	double pos[]; // Position in space at time t
	double velocity[]; // Velocity of particle at time t
	double pBest[]; // Particle's best known position
	
	static double globalBest[] = new double[2]; // Global/Swarm best known position
	static double globalFitness = Double.MAX_VALUE; // Global/Swarm best known fitness
	
	Particle(int variables) {
		pos = new double[variables];
		velocity = new double[variables];
		pBest = new double[variables];
	}
	
	/** Evaluate the fitness of a particle's current position in space
	 *  The return value is the function being optimized
	 *  @param particle
	 *  @return
	 */
	double evalFitness(Particle particle) {
		double x = particle.pos[0];
		double y = particle.pos[1];

		return MainPSO.calculateFunction(x, y);
	}

	/** Evaluate the fitness of the particle's best known position
	 *  The return value is the function being optimized
	 *  @param particle
	 *  @return
	 */
	double evalBestFitness(Particle particle) {
		double x = particle.pBest[0];
		double y = particle.pBest[1];

		return MainPSO.calculateFunction(x, y);
	}

	/** If the particle's current position in space is better than it's own
	 *  known best position, update the particle's personal best with it's
	 *  current position in space
	 *  @param particle
	 *  @param fitness
	 *  @param variables
	 */
	void updatePersonalBest(Particle particle, double fitness, int variables) {
		if (evalBestFitness(particle) > fitness) {
			for (int i = 0; i < variables; i++) {
				particle.pBest[i] = particle.pos[i];
			}
		}
	}
}
