package in.naishe.simpleBlog;

import in.naishe.simpleBlog.CassandraConnection.SessionWrapper;

import java.util.List;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.base.Objects;

@Table(keyspace = Constants.KEYSPACE, name = "comments")
public class Comment extends AbstractVO<Comment>{
	@PartitionKey
	private UUID id;
	@Column(name = "post_id")
	private UUID postId;
	private String title;
	private String content;
	@Column(name = "posted_on")
	private long postedOn;
	private String commenter;
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public UUID getPostId() {
		return postId;
	}
	public void setPostId(UUID postId) {
		this.postId = postId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getPostedOn() {
		return postedOn;
	}
	public void setPostedOn(long postedOn) {
		this.postedOn = postedOn;
	}
	public String getCommenter() {
		return commenter;
	}
	public void setCommenter(String commenter) {
		this.commenter = commenter;
	}
	
	@Override
	public boolean equals(Object that) {
		return this.getId().equals(((Comment)that).getId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getId(), getTitle(), getContent(), getCommenter());
	}
	
	@Override
	protected Comment getInstance() {
		return this;
	}
	@Override
	protected Class<Comment> getType() {
		return Comment.class;
	}
	
	public static List<Comment> getComments(UUID postId, SessionWrapper sessionWrapper){
		AllQueries queries =  sessionWrapper.getAllQueries();
		return queries.getComments(postId).all();
	}
}
