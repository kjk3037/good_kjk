package gdpi.kjk.po;

import java.util.List;

public class Category {
	private String cid;
	private String cname;
	private List<Category> children;//所有子分类
	private Category parent;//父分类
	private String desc;
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public List<Category> getChildren() {
		return children;
	}
	public void setChildren(List<Category> children) {
		this.children = children;
	}
	public Category getParent() {
		return parent;
	}
	public void setParent(Category parent) {
		this.parent = parent;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	@Override
	public String toString() {
		return "Category [cid=" + cid + ", cname=" + cname + ", children=" + children + ", parent=" + parent + ", desc="
				+ desc + "]";
	}

}
