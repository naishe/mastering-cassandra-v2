package in.naishe.simpleBlog;

import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface AllQueries {

	@Query("SELECT * FROM " + Constants.KEYSPACE + ".blogs")
	public Result<Blog> getAll();
	
	@Query("SELECT * FROM " + Constants.KEYSPACE + ".blogs WHERE blog_name = :blogName")
	public Result<Blog> getBlogByName(@Param("blogName") String blogName);
	
	@Query("SELECT * FROM " + Constants.KEYSPACE + ".posts WHERE blog_id = :blogId AND "
			+ "id < :postId ORDER BY id DESC LIMIT :pageSize")
	public Result<Post> getPostsReverseChronologically(UUID blogId, UUID startFromPostId, int pageSize);
	
	@Query("SELECT * FROM " + Constants.KEYSPACE + ".posts WHERE blog_id = :blogId "
			+ "ORDER BY id DESC LIMIT :pageSize")
	public Result<Post> getPostsReverseChronologically(UUID blogId, int pageSize);
	
	@Query("SELECT * FROM " + Constants.KEYSPACE + ".comments WHERE post_id = :postId ORDER BY id DESC")
	public Result<Comment> getComments(UUID postId);
}
