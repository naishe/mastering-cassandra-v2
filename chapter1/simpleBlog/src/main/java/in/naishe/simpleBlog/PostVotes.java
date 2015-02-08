package in.naishe.simpleBlog;

import in.naishe.simpleBlog.CassandraConnection.SessionWrapper;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.base.Objects;

@Table(keyspace = Constants.KEYSPACE, name = "post_votes")
public class PostVotes extends AbstractVO<PostVotes>{
	@PartitionKey
	@Column(name = "post_id")
	private UUID postId;
	private long upvotes;
	private long downvotes;
	
	public UUID getPostId() {
		return postId;
	}
	public void setPostId(UUID postId) {
		this.postId = postId;
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
	public boolean equals(Object that) {
		return this.getPostId().equals(((PostVotes)that).getPostId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getPostId(), getUpvotes(), getDownvotes());
	}
	
	@Override
	protected PostVotes getInstance() {
		return this;
	}
	@Override
	protected Class<PostVotes> getType() {
		return PostVotes.class;
	}
	
	@Override
	public void save(SessionWrapper sessionWrapper) {
		String update = 
				"UPDATE " + Constants.KEYSPACE + ".post_votes "
				+ "SET "
				+ "upvotes = upvotes +" + this.getUpvotes() + ", "
				+ "downvotes = downvotes + " + this.getDownvotes() + " "
				+ "WHERE post_id = " + this.getPostId().toString();
		sessionWrapper.getSession().execute(update);
	}
	
	public void upvote(UUID postId, SessionWrapper sessionWrapper) {
		String update = 
				"UPDATE " + Constants.KEYSPACE + ".post_votes "
				+ "SET "
				+ "upvotes = upvotes + 1 "
				+ "WHERE post_id = " + postId.toString();
		sessionWrapper.getSession().execute(update);
	}
	
	public void downvote(UUID postId, SessionWrapper sessionWrapper) {
		String update = 
				"UPDATE " + Constants.KEYSPACE + ".post_votes "
				+ "SET "
				+ "downvotes = downvotes + 1 "
				+ "WHERE post_id = " + postId.toString();
		sessionWrapper.getSession().execute(update);
	}
}
