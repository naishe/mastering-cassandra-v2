package in.naishe.simpleBlog;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.base.Objects;

@Table(keyspace = Constants.KEYSPACE, name = "categories")
public class Category extends AbstractVO<Category>{
	@PartitionKey
	@Column(name = "cat_name")
	private String categoryName;
	@Column(name = "blog_id")
	private UUID blogId;
	@Column(name = "post_id")
	private UUID postId;
	@Column(name = "post_title")
	private String postTitle;
	
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public UUID getBlogId() {
		return blogId;
	}
	public void setBlogId(UUID blogId) {
		this.blogId = blogId;
	}
	public UUID getPostId() {
		return postId;
	}
	public void setPostId(UUID postId) {
		this.postId = postId;
	}
	public String getPostTitle() {
		return postTitle;
	}
	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}
	
	@Override
	public boolean equals(Object that) {
		return this.getCategoryName().equals(((Category)that).getCategoryName());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getCategoryName(), getBlogId(), getPostId());
	}
	
	@Override
	protected Category getInstance() {
		return this;
	}
	
	@Override
	protected Class<Category> getType() {
		return Category.class;
	}
}
