package ca.yorku.eecs;

import java.util.ArrayList;

/**
 * This class is used to sort the movies by their IMDB rating and it is used by GetRank
 */
public class MergeSort {
	
	/**
	 * Perform the merge sort
	 * @param movies
	 * @param ratings
	 */
	public void sort(ArrayList<String> movies, ArrayList<Double> ratings) {
		if (movies.size() == 1)
			return;
		
		ArrayList<String> leftMovies = new ArrayList<String>();
		ArrayList<String> rightMovies = new ArrayList<String>();
		ArrayList<Double> leftRatings = new ArrayList<Double>();
		ArrayList<Double> rightRatings = new ArrayList<Double>();
		
		this.split(movies, ratings, leftMovies, rightMovies, leftRatings, rightRatings);
		
		this.sort(leftMovies, leftRatings);
		this.sort(rightMovies, rightRatings);
		
		movies = new ArrayList<String>();
		ratings = new ArrayList<Double>();
		
		this.merge(movies, ratings, leftMovies, rightMovies, leftRatings, rightRatings);
	}
	
	/**
	 * Split the list in halves
	 * @param movies
	 * @param ratings
	 * @param leftMovies
	 * @param rightMovies
	 * @param leftRatings
	 * @param rightRatings
	 */
	private void split(ArrayList<String> movies, ArrayList<Double> ratings, ArrayList<String> leftMovies, ArrayList<String> rightMovies, ArrayList<Double> leftRatings, ArrayList<Double> rightRatings) {
		int midPoint = movies.size() / 2;
		
		for (int i = 0; i < movies.size(); i++) {
			if (i < midPoint) {
				leftMovies.add(movies.get(i));
				leftRatings.add(ratings.get(i));
			}
			else {
				rightMovies.add(movies.get(i));
				rightRatings.add(ratings.get(i));
			}
		}
	}
	
	private void merge(ArrayList<String> movies, ArrayList<Double> ratings, ArrayList<String> leftMovies, ArrayList<String> rightMovies, ArrayList<Double> leftRatings, ArrayList<Double> rightRatings) {
		
	}
}