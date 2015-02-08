package in.naishe.simpleBlog;

import static in.naishe.simpleBlog.RandomGenerator.getCommenter;
import static in.naishe.simpleBlog.RandomGenerator.getInt;
import static in.naishe.simpleBlog.RandomGenerator.getString;
import static in.naishe.simpleBlog.RandomGenerator.getTag;
import static in.naishe.simpleBlog.RandomGenerator.getTimeUUID;
import static in.naishe.simpleBlog.RandomGenerator.getUUID;
import in.naishe.simpleBlog.CassandraConnection.SessionWrapper;
import in.naishe.simpleBlog.exceptions.BlogNotFoundException;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.datastax.driver.core.Session;

public class Main {
	
	private static final Logger log = Logger.getLogger(Main.class);
	
	private static final String[][] blogs = {
		{"BC Blog", "‎Barbara Cartland", "bc@example.com", "bbra1234"},
		{"DS Blog", "‎Danielle Steel", "ds@example.com", "D5t33l"},
		{"HR Blog", "‎Harold Robbins", "hr@example.com", "password666"},
		{"S2 Blog", "‎Sidney Sheldon", "ss@example.com", "0penC3creT"},
		{"JR Blog", "JK Rowling", "jr@example.com", "SnapeKilledDumbledore"}
	};
	
	private static final String DROP_KEYSPACE = "DROP KEYSPACE IF EXISTS "  + Constants.KEYSPACE;
	private static final String CREATE_KEYSPACE = 
			"CREATE KEYSPACE IF NOT EXISTS "
	        + Constants.KEYSPACE
	        + "  WITH replication ="
	        + " {'class': 'SimpleStrategy', 'replication_factor' : 1}";
	private static final String BLOGS_TABLE_DEF =
			"CREATE TABLE IF NOT EXISTS " + Constants.KEYSPACE + ".blogs "
			+ "("
			+ "id uuid PRIMARY KEY, "
			+ "blog_name varchar, "
			+ "author varchar, "
			+ "email varchar, "
			+ "password varchar"
			+ ")";
	private static final String INDEX_ON_BLOGS = ""
			+ "CREATE INDEX idx_blog_name ON " + Constants.KEYSPACE + ".blogs(blog_name);";
	private static final String POSTS_TABLE_DEF =
			"CREATE TABLE IF NOT EXISTS " + Constants.KEYSPACE + ".posts "
			+ "("
			+ "id timeuuid, "
			+ "blog_id uuid, "
			+ "posted_on timestamp, "
			+ "title text, "
			+ "content text, "
			+ "tags set<varchar>, "
			+ "PRIMARY KEY(blog_id, id)"
			+ ")";
	private static final String CATEGORIES_TABLE_DEF = 
			"CREATE TABLE IF NOT EXISTS " + Constants.KEYSPACE + ".categories "
			+ "("
			+ "cat_name varchar, "
			+ "blog_id uuid, "
			+ "post_id timeuuid, "
			+ "post_title text, "
			+ "PRIMARY KEY(cat_name, blog_id, post_id)"
			+ ")";
	private static final String COMMENTS_TABLE_DEF = 
			"CREATE TABLE IF NOT EXISTS " + Constants.KEYSPACE + ".comments "
			+ "("
			+ "id timeuuid, "
			+ "post_id timeuuid, "
			+ "title text, "
			+ "content text, "
			+ "posted_on timestamp, "
			+ "commenter varchar, "
			+ "PRIMARY KEY(post_id, id)"
			+ ")";
	private static final String INDEX_ON_COMMENTS = 
			"CREATE INDEX idx_commenter ON " + Constants.KEYSPACE + ".comments (commenter)";
	private static final String POST_VOTES_TABLE_DEF = 
			"CREATE TABLE IF NOT EXISTS " + Constants.KEYSPACE + ".post_votes "
			+ "("
			+ "post_id timeuuid PRIMARY KEY, "
			+ "upvotes counter, "
			+ "downvotes counter"
			+ ")";
	private static final String COMMENT_VOTES_TABLE_DEF = 
			"CREATE TABLE IF NOT EXISTS " + Constants.KEYSPACE + ".comment_votes "
			+ "("
			+ "comment_id timeuuid PRIMARY KEY, "
			+ "upvotes counter, "
			+ "downvotes counter"
			+ ")";

	private static void setup() throws InterruptedException {
		log.info("Setting up environment...");
		Session conn = CassandraConnection.getSession();
		conn.execute(DROP_KEYSPACE);
		conn.execute(CREATE_KEYSPACE);
		//sleep for 1 sec to allow cluster to consolidate
		Thread.sleep(1000);
		conn.execute(BLOGS_TABLE_DEF);
		conn.execute(INDEX_ON_BLOGS);
		conn.execute(POSTS_TABLE_DEF);
		conn.execute(CATEGORIES_TABLE_DEF);
		conn.execute(COMMENTS_TABLE_DEF);
		conn.execute(INDEX_ON_COMMENTS);
		conn.execute(POST_VOTES_TABLE_DEF);
		conn.execute(COMMENT_VOTES_TABLE_DEF);
		log.info("All set!");
		conn.close();
	}
	
	private static void insertData(){
		
		
		try (SessionWrapper sessionWrapper = new SessionWrapper()){
			for (String[] blag: blogs){
				Blog blog = new Blog();
				blog.setBlogName(blag[0]);
				blog.setAuthor(blag[1]);
				blog.setEmail(blag[2]);
				blog.setPassword(blag[3]);
				blog.setId(getUUID());
				blog.save(sessionWrapper);
				
				// insert 10-20 posts
				insertPosts(sessionWrapper, blog.getId(), getInt(10, 20));
			}
		}
	}
	
	private static void insertPosts(SessionWrapper sessionWrapper, UUID blogId, int numPosts){
		for (int i=0; i<numPosts; i++){
			Post post = new Post();
			
			post.setId(getTimeUUID());
			post.setPostedOn(System.currentTimeMillis());
			post.setBlogId(blogId);
			post.setContent(getString(10,50));
			post.setTitle(getString(2,5));
			HashSet<String> tags = new HashSet<>();
			for (int j=0; j<getInt(3, 10); j++){
				tags.add(getTag());
			}
			post.setTags(tags);
			post.save(sessionWrapper);
			
			PostVotes votes = new PostVotes();
			votes.setPostId(post.getId());
			votes.setUpvotes(getInt(0, 100));
			votes.setDownvotes(getInt(0,100));
			votes.save(sessionWrapper);
			
			insertComments(sessionWrapper, post, getInt(1, 10));
		}
	}
	
	private static void insertComments(SessionWrapper sessionWrapper, Post post, int numComments) {
		for ( int i=0; i<numComments; i++){
			Comment com = new Comment();
			com.setCommenter(getCommenter());
			com.setContent(getString(1, 10));
			com.setId(getTimeUUID());
			com.setPostedOn(System.currentTimeMillis());
			com.setPostId(post.getId());
			com.setTitle(getString(1, 3));
			com.save(sessionWrapper);
			
			CommentVotes votes = new CommentVotes();
			votes.setCommentId(com.getId());
			votes.setDownvotes(getInt(0, 10));
			votes.setUpvotes(getInt(0, 10));
			votes.save(sessionWrapper);
		}
	}

	private static final Blog getRandomBlog() throws BlogNotFoundException{
		try (SessionWrapper sessionWrapper = new SessionWrapper()){
			String blogName = blogs[Constants.RANDOM.nextInt(blogs.length-1)][0];
			Blog blog = Blog.getBlogByName(blogName, sessionWrapper);
			return blog;
		}
	}
	
	private static final void printPostsAndComments(Blog blog){
		UUID blogId = blog.getId();
		int maxPageToPrint = 3;
		int pageSize = 2; //2 posts per page
		System.out.println("###################################################");
		System.out.println(blog.getBlogName().toUpperCase() + ":: by " + blog.getAuthor());
		System.out.println("###################################################");
		System.out.println("");
		
		try(SessionWrapper sessionWrapper = new SessionWrapper()){
			UUID lastPostId = null;
			for (int i=1; i<= maxPageToPrint; i++){
				List<Post> posts = Post.getPosts(blogId, lastPostId, pageSize, sessionWrapper);
				for (Post post: posts){
					System.out.println(post.getTitle().toUpperCase() + "\t"
							+ "" + new Date(post.getPostedOn()).toString());
					PostVotes pvotes = new PostVotes().get(sessionWrapper, post.getId());
					System.out.println("Votes: +"+pvotes.getUpvotes() +"/-"+pvotes.getDownvotes());
					System.out.println("--------------------------------------------");
					System.out.println(post.getContent());
					System.out.println("Tags: " + post.getTags().toString());
					
					System.out.println("### COMMENTS:\n");
					
					List<Comment> comments = Comment.getComments(post.getId(), sessionWrapper);
					for (Comment comment : comments){
						System.out.println("  >> " + comment.getTitle().toUpperCase());
						System.out.println("  "+ comment.getContent());
						System.out.println("   -- " +comment.getCommenter() + " on "
								+ new Date(comment.getPostedOn()).toString());
						CommentVotes cvotes = new CommentVotes().get(sessionWrapper, comment.getId());
						System.out.println("  Votes: +"+cvotes.getUpvotes()+"/-"+cvotes.getDownvotes());
						System.out.println("  -.-.-.-.-.-.-");
					}
					System.out.println();
					System.out.println("============================================");
					System.out.println();
				}
				System.out.println("END OF PAGE: " + i);
				System.out.println();
				System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
				System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
				System.out.println();
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			setup();
			log.info("Setup completed");
			insertData();
			log.info("Data has been loaded.");
			Blog blog = getRandomBlog();
			log.info("Retrieved Blog: [" + blog.getBlogName() + "].");
			printPostsAndComments(blog);
		} catch (Exception e) {
			log.fatal("Something went wrong, please look into the stacktrace here:", e);
			System.exit(1);
		} finally {
			cleanup();
		}

	}
	
	
	/**
	 * ---- Helpers ----
	 */
	private static final void cleanup(){
		CassandraConnection.closeClusterConnection();
	}
	

}
