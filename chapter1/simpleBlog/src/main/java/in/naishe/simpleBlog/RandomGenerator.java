package in.naishe.simpleBlog;

import java.util.UUID;

import com.datastax.driver.core.utils.UUIDs;

public class RandomGenerator {
	
	private static final String[] genre = {
		"Science fiction", "Satire", "Drama", 
		"Action and Adventure", "Romance", "Mystery", "Horror", 
		"Self help", "Guide", "Travel", "Children", "Religious", 
		"Science", "History", "Math", "Anthologies", "Poetry", 
		"Encyclopedias", "Dictionaries", "Comics", "Art", "Cookbooks", 
		"Diaries", "Journals", "Prayer books", "Series", "Trilogies", 
		"Biographies", "Autobiographies", "Fantasy"
		};
	
	private static final String[] commenters = {
		"aplha@gmail.com", "bravo@yahoo.com", "charlie@aol.com",
		"delta@gmail.com", "echo@yahoo.com", "foxtrot@aol.com",
		"golf@gmail.com", "hotel@yahoo.com", "india@aol.com", 
		"juliet@aol.com"
		};

	private static final String[] donQuixoteWords = (""
					+ "It had the effect, however, of bringing out a translation undertaken and " 
					+ "executed in a very different spirit, that of Charles Jervas, the portrait " 
					+ "painter, and friend of Pope, Swift, Arbuthnot, and Gay. Jervas has been " 
					+ "allowed little credit for his work, indeed it may be said none, for it is " 
					+ "known to the world in general as Jarvis's. It was not published until " 
					+ "after his death, and the printers gave the name according to the current " 
					+ "pronunciation of the day. It has been the most freely used and the most " 
					+ "freely abused of all the translations. It has seen far more editions than " 
					+ "any other, it is admitted on all hands to be by far the most faithful, " 
					+ "and yet nobody seems to have a good word to say for it or for its author. " 
					+ "Jervas no doubt prejudiced readers against himself in his preface, where " 
					+ "among many true words about Shelton, Stevens, and Motteux, he rashly and " 
					+ "unjustly charges Shelton with having translated not from the Spanish, but " 
					+ "from the Italian version of Franciosini, which did not appear until ten " 
					+ "years after Shelton's first volume. A suspicion of incompetence, too, " 
					+ "seems to have attached to him because he was by profession a painter and " 
					+ "a mediocre one (though he has given us the best portrait we have of " 
					+ "Swift), and this may have been strengthened by Pope's remark that he " 
					+ "translated 'Don Quixote' without understanding Spanish. He has been " 
					+ "also charged with borrowing from Shelton, whom he disparaged. It is true " 
					+ "that in a few difficult or obscure passages he has followed Shelton, and " 
					+ "gone astray with him; but for one case of this sort, there are fifty " 
					+ "where he is right and Shelton wrong. As for Pope's dictum, anyone who " 
					+ "examines Jervas's version carefully, side by side with the original, will " 
					+ "see that he was a sound Spanish scholar, incomparably a better one than " 
					+ "Shelton, except perhaps in mere colloquial Spanish. He was, in fact, an " 
					+ "honest, faithful, and painstaking translator, and he has left a version " 
					+ "which, whatever its shortcomings may be, is singularly free from errors " 
					+ "and mistranslations.").split("\\s+");
	
	private static final int wordsLen = donQuixoteWords.length;
	
	 /**
	  * using DataStax provided UUIDs util,
	  * you may replace it with java.util, Guava
	  * or any other implementation
	  */
	public static final UUID getUUID(){
		return UUIDs.random();
	}
	
	/**
	 * using DataStax provided UUIDs util, 
	 * you may replace it with Guava or any other implementation
	 */
	public static final UUID getTimeUUID(){
		return UUIDs.timeBased();
	}
	
	public static final String getString(int minlen, int maxlen){
		int len = getInt(minlen, maxlen);
		StringBuilder text = new StringBuilder();
		for ( int i=0; i<len; i++){
			text.append(donQuixoteWords[Constants.RANDOM.nextInt(wordsLen-1)]);
			text.append(" ");
		}
		return text.toString().trim();
	}
	
	public static final int getInt(int min, int max){
		return min + Constants.RANDOM.nextInt(max-min);
	}
	
	public static String getTag(){
		
		return genre[Constants.RANDOM.nextInt(genre.length-1)];
	}
	
	public static String getCommenter(){
		return commenters[Constants.RANDOM.nextInt(commenters.length-1)];
	}	
	
}
