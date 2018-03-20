package coursework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Implements a basic Evolutionary Algorithm to train a Neural Network
 * 
 * You Can Use This Class to implement your EA or implement your own class that extends {@link NeuralNetwork} 
 * 
 */
public class ExampleEvolutionaryAlgorithm extends NeuralNetwork {

	private int tournamentSize = 12;
	private Individual previousBest = new Individual();
	
	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {		
		//Initialise a population of Individuals with random weights
		population = initialise();

		//Record a copy of the best Individual in the population
		best = getBest(population);
		System.out.println("Best From Initialisation " + best);

		/**
		 * main EA processing loop
		 */		

		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * this is a skeleton EA - you need to add the methods.
			 * You can also change the EA if you want 
			 * You must set the best Individual at the end of a run
			 * 
			 */

			// Select 2 Individuals from the current population. Currently returns random Individual
			Individual parent1 = select(); 
			Individual parent2 = select();
			ArrayList<Individual> children = new ArrayList<Individual>();
			// Generate a child by crossover. Not Implemented
			if (evaluations % 10 == 100)
				children = reproduceUniform(parent1, parent2);
			else
				children = reproduce(parent1, parent2);	

		

			//mutate the offspring
			mutate(children);

			// Evaluate the children
			evaluateIndividuals(children);			

			// Replace children in population
			replace(children);
			
			// check to see if the best has improved
			best = getBest(population);
			
			if(previousBest.equals(best))
			{
				System.out.println("same");
			}
			previousBest = best;
			// Implemented in NN class. 
			outputStats();

			//Increment number of completed generations			
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}



	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 * 
	 */
	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}


	/**
	 * Returns a copy of the best individual in the population
	 * 
	 */
	private Individual getBest(ArrayList<Individual> pop) {
		best = null;;
		for (Individual individual : pop) {
			if (best == null) {
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}

	/**
	 * Generates a randomly initialised population
	 * 
	 */
	private ArrayList<Individual> initialise() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	/**
	 * Selection --
	 * 
	 * NEEDS REPLACED with proper selection this just returns a copy of a random
	 * member of the population
	 */
	private Individual select() {
		// Create a tournament population
		ArrayList<Individual> tournamentPopulation = new ArrayList<Individual>();

		// For each place in the tournament get a random individual
		for (int i = 0; i < tournamentSize; i++)
		{
			int randomId = (int) (Math.random() * population.size());
			tournamentPopulation.add(population.get(randomId).copy());
		}
		// Get the fittest
		Individual fittest = getBest(tournamentPopulation);

		//Individual parent = population.get(Parameters.random.nextInt(Parameters.popSize));
		return fittest.copy();
	}

	/**
	 * Crossover / Reproduction
	 * 
	 * NEEDS REPLACED with proper method this code just returns exact copies of the
	 * parents. 
	 */
	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();

		// Single point crossover
		double[] chromosome1 = Arrays.copyOfRange(parent1.chromosome, 0, parent1.chromosome.length / 2);
		double[] chromosome2 = Arrays.copyOfRange(parent2.chromosome, parent2.chromosome.length / 2, parent2.chromosome.length - 1);
		Individual child1 = new Individual();
		for (int i = 0; i < parent1.chromosome.length - 1; i++) 
		{
			if (i < parent1.chromosome.length / 2)
			{
				child1.chromosome[i] = chromosome1[i];
			}

			else
			{
				child1.chromosome[i] = chromosome2[i - chromosome2.length - 1];
			}

		}

		chromosome1 = Arrays.copyOfRange(parent1.chromosome, parent1.chromosome.length / 2, parent1.chromosome.length - 1);
		chromosome2 = Arrays.copyOfRange(parent2.chromosome, 0, parent2.chromosome.length / 2);

		Individual child2 = new Individual();
		for (int i = 0; i < parent1.chromosome.length - 1; i++) 
		{
			if (i < parent1.chromosome.length / 2)
				child2.chromosome[i] = chromosome2[i];
			else
				child2.chromosome[i] = chromosome1[i - chromosome1.length - 1];
		}

		children.add(child1.copy());
		children.add(child2.copy());		
		return children;
	} 

	private ArrayList<Individual> reproduceUniform(Individual parent1, Individual parent2)
	{
		ArrayList<Individual> children = new ArrayList<Individual>();
		Individual child1 = new Individual();
		// Loop through genes
		for (int i = 0; i < parent1.chromosome.length; i++) {
			// Crossover
			if (Math.random() <= 0.5) {
				child1.chromosome[i] = parent1.chromosome[i];
			} else {
				child1.chromosome[i] = parent2.chromosome[i];
			}
		}
		Individual child2 = new Individual();
		// Loop through genes
		for (int i = 0; i < parent1.chromosome.length; i++) {
			// Crossover
			if (Math.random() <= 0.5) {
				child2.chromosome[i] = parent2.chromosome[i];
			} else {
				child2.chromosome[i] = parent1.chromosome[i];
			}
		}
		children.add(child1.copy());
		children.add(child2.copy());
		return children;
	}

	/**
	 * Mutation
	 * 
	 * 
	 */
	private void mutate(ArrayList<Individual> individuals) {		
		for(Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < Parameters.mutateRate) {
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Math.random() * 1);
					} else {
						individual.chromosome[i] -= (Math.random() * 1);
					}
				}
			}
		}		
	}

	/**
	 * 
	 * Replaces the worst member of the population 
	 * (regardless of fitness)
	 * 
	 */
	private void replace(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			int idx = getWorstIndex();
			if(population.get(idx).fitness > individual.fitness)
				population.set(idx, individual);
		}		
	}



	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex() {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < population.size(); i++) {
			Individual individual = population.get(i);
			if (worst == null) {
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness) {
				worst = individual;
				idx = i; 
			}
		}
		return idx;
	}	

	@Override
	public double activationFunction(double x) {
		if (x < -20.0) {
			return -1.0;
		} else if (x > 20.0) {
			return 1.0;
		}
		return Math.tanh(x);
	}
}
