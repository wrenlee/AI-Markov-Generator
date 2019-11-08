//Wren Lee
//Generic class with train and generate function for the markov generator

import java.util.ArrayList;

public class MarkovGenerator<T> extends ProbabilityGenerator{

	/*
	 *inherited variables from probability generator class
		ArrayList<T> inputArr = new ArrayList(); // arraylist to hold variable amount of input
		ArrayList<T> alphabetArr = new ArrayList(); //arraylist for generates tokens, based on alphabet
		ArrayList<Integer> alphabetCounts = new ArrayList<Integer>(); //arraylist to hold the number of each unique token
		ArrayList<Float> probabilityArr = new ArrayList<Float>(); //arraylist to hold all the probabilities
		ArrayList<T> genArr = new ArrayList(); //arraylist to hold all of the generated values
	 */
	ArrayList<ArrayList<Float>> transTable = new ArrayList(); //transition table 2d arraylist
	//ArrayList<ArrayList<Float>> probTransTable = new ArrayList(); //transition table 2d arraylist for the probabilities
	ArrayList<Float> markovProbabilityArr = new ArrayList<Float>();
	ProbabilityGenerator<T> generator = new ProbabilityGenerator();
	int indexWithNoChance = -1;

	public void train(ArrayList inArr) {
		generator.train(inArr);
		//variables
		int inputIndex = 0; //index within the input array, data index
		int tokenIndex = 0; //index within the alphabet array, token index
		int lastIndex = -1; //the previous index

		for(int i = 0; i < inArr.size(); i++) { //stepping through input array, i = input index
			T compareVal = (T) inArr.get(i);
			tokenIndex = alphabetArr.indexOf(compareVal); //initialization of token index to the first value
			if(tokenIndex == -1) {
				//create new rows
				tokenIndex = alphabetArr.size(); //it'll always be at the end, so use size
				ArrayList<Float> row = new ArrayList(alphabetArr.size()); //makes the row
				transTable.add(row); //add the row to the transition table
				for (int k = 0; k < alphabetArr.size(); k++) {
					row.add(0.0f);
				}//add zeros to the existing array
				//create new columns
				for(int j = 0; j < transTable.size(); j++) {
					//System.out.println("Adding the 0s");
					transTable.get(j).add(0.0f); //adds 0s to the row which is the size of the token index
				}//adds to the column
				alphabetArr.add(compareVal); //adds input value to the alphabet array
			}//no other token exist
			if(lastIndex > -1) {
				float indexAdd;
				ArrayList<Float> row = transTable.get(lastIndex); //accesses the row that corresponds to the last index row in the transition table
				indexAdd = row.get(tokenIndex); //access column as determined by token index
				row.set(tokenIndex, indexAdd + 1); //changes the index by adding one
			}//if it already exists
			lastIndex = tokenIndex;
		}//stepping through input array
	}//train function

	public void printTransTable(ArrayList row) {
		System.out.println(row);
	}//print transition table

	public void probabilities(ArrayList row, int index) {
		//System.out.println(row);
		float totalCounts = 0;
		for(int i = 0; i < row.size(); i++) {
			totalCounts = (float) row.get(i) + totalCounts; //add all counts up
			//System.out.println("Total counts " + totalCounts);
		}//gets total
		if(totalCounts == 0) {
			indexWithNoChance = index;
			for(int i = 0; i < row.size(); i++) {
				markovProbabilityArr.set(i, (float) 0);	
			}//make all the probabilities 0
		}//total counts 0
		else {
			markovProbabilityArr.clear();
			for(int j = 0; j < row.size(); j++) {
				markovProbabilityArr.add(((float) row.get(j) / totalCounts)); //add the probabilities from each of the counts
				//System.out.println("Prob " + (float) row.get(j) / totalCounts);
			}//gets probabilities
		}//if total counts isn't 0
	}//probabilities function

	public void markovProbabilities() {
		for(int i = 0; i < transTable.size(); i++) {
			ArrayList<Float> tempArr = new ArrayList(); //makes temp array to hold values
			tempArr = transTable.get(i);
			probabilities(tempArr, i);
		}//loops through transition table and adds in the rows
	}//finds probabilities of markov generator

	public void printMarkovProbabilities() {
		for(int i = 0; i < transTable.size(); i++) {
			probabilities(transTable.get(i), i);
			System.out.print(alphabetArr.get(i));
			System.out.println(markovProbabilityArr);
		}//loops through transition table and adds in the rows
	}//finds probabilities of markov generator

	//markov generator generate() comes from the prob gen's generate()
	public T generate(T initToken){ //generate one note first from the prob gen generate
		assert(markovProbabilityArr.size()>1); //this object hasn't been trained
		int rowNum = alphabetArr.indexOf(initToken); //finds initial token in alphabet array
		ArrayList <Float> row =  transTable.get(rowNum); //gets token's slot in probabilities transition table
		probabilities(row, rowNum); //calls probability row
		if(rowNum == indexWithNoChance) {
			generator.generate();
		}//if the row number has -1 probability
		//System.out.println("Generated " + (T) generate(markovProbabilityArr)); //calls prob gen generate
		return (T) generate(markovProbabilityArr); //calls prob gen generate
	}//generate functions

	public ArrayList<T> generateMultiple(T initToken, int n){ //generate multiple notes
		ArrayList<T> genArr = new ArrayList();
		for(int i=0; i<n; i++) {
			T token = generate(initToken);
			initToken = token;
			genArr.add(token);
		}
		return genArr;
	}//generate multiple function

	public ArrayList<T> generateMultipleAndPrint(T initToken, int n){ //generate multiple notes
		ArrayList<T> genArr = new ArrayList();
		for(int i=0; i<n; i++) {
			T token = generate(initToken);
			//initToken = token;
			genArr.add(token);
		}
		System.out.println(genArr);
		return genArr;
	}//generate multiple function

	public void unitTest1() {//print out transition table
		System.out.println("-----Transition Table-----");
		System.out.println(alphabetArr);
		for(int i = 0; i < transTable.size(); i++) {
			printTransTable(transTable.get(i));
		}//loop through transition table
		System.out.println("");

		System.out.println("Transition Table Probabilities");
		System.out.println(alphabetArr);
		printMarkovProbabilities();
		System.out.println("------------");
	}//unit test one: print probabilities

	public void unitTest2() {
		System.out.println("Test 2");
		T genVal = generator.generate();
		generateMultipleAndPrint(genVal, 20);
	}//unit test two: one melody

}//markov generator class definition ends
