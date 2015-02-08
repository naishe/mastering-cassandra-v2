package in.naishe.simpleBlog;

import in.naishe.simpleBlog.CassandraConnection.SessionWrapper;
import in.naishe.simpleBlog.exceptions.BlogNotFoundException;

import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.hash.Hashing;

@Table(keyspace = Constants.KEYSPACE, name = "blogs")
public class Blog extends AbstractVO<Blog> {
	@PartitionKey
	private UUID id;
	@Column(name = "blog_name")
	private String blogName;
	private String author;
	private String email;
	private String password;
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getBlogName() {
		return blogName;
	}
	public void setBlogName(String blogName) {
		this.blogName = blogName;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		/* Ideally, you'd use a unique salt with this hashing */
		this.password = Hashing
						.sha256()
						.hashString(password, Charsets.UTF_8)
						.toString();
	}
	
	@Override
	public boolean equals(Object that) {
		return this.getId().equals(((Blog)that).getId());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getId(), getEmail(), getAuthor(), getBlogName());
	}
	
	@Override
	protected Blog getInstance() {
		return this;
	}
	
	@Override
	protected Class<Blog> getType() {
		return Blog.class;
	}
	
	// ----- ACCESS VIA QUUERIES -----
	
	public static Blog getBlogByName(String blogName, SessionWrapper sessionWrapper) throws BlogNotFoundException {
		AllQueries queries = sessionWrapper.getAllQueries();
		Result<Blog> rs = queries.getBlogByName(blogName);
		if (rs.isExhausted()){
			throw new BlogNotFoundException();
		}
		return rs.one();
	}
	
}
