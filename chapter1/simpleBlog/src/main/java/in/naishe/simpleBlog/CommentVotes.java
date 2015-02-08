package in.naishe.simpleBlog;

import in.naishe.simpleBlog.CassandraConnection.SessionWrapper;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = Constants.KEYSPACE, name = "comment_votes")
public class CommentVotes extends AbstractVO<CommentVotes>{
	@PartitionKey
	@Column(name = "comment_id")
	private UUID commentId;
	private long upvotes;
	private long downvotes;
	
	public UUID getCommentId() {
		return commentId;
	}
	public void setCommentId(UUID commentId) {
		this.commentId = commentId;
	}
	public long getUpvotes() {
		return upvotes;
	}
	public void setUpvotes(long upvotes) {
		this.upvotes = upvotes;
	}
	public long getDownvotes() {
		return downvotes;
	}
	public void setDownvotes(long downvotes) {
		this.downvotes = downvotes;
	}
	
	@Override
	protected CommentVotes getInstance() {
		return this;
	}
	
	@Override
	protected Class<CommentVotes> getType() {
		return CommentVotes.class;
	}
	
	@Override
	public void save(SessionWrapper sessionWrapper) {
		String update = 
				"UPDATE " + Constants.KEYSPACE + ".comment_votes "
				+ "SET "
				+ "upvotes = upvotes + " + this.getUpvotes() + ", "
				+ "downvotes = downvotes + " + this.getDownvotes() + " "
				+ "WHERE comment_id = " + this.getCommentId();
		sessionWrapper.getSession().execute(update);
	}
	
	public void upvote(UUID commentId, SessionWrapper sessionWrapper) {
		String update = 
				"UPDATE " + Constants.KEYSPACE + ".comment_votes "
				+ "SET "
				+ "upvotes = upvotes + 1 "
				+ "WHERE comment_id = " + commentId.toString();
		sessionWrapper.getSession().execute(update);
	}
	
	public void downvote(UUID commentId, SessionWrapper sessionWrapper) {
		String update = 
				"UPDATE " + Constants.KEYSPACE + ".comment_votes "
				+ "SET "
				+ "downvotes = downvotes + 1 "
				+ "WHERE comment_id = " + commentId.toString();
		sessionWrapper.getSession().execute(update);
	}
}
