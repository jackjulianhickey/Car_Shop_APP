

public class Product {

	int id;
	String name, image;
	String desc;
	Double price;

	public Product(int id, String name, String desc, Double price, String image){
		id = this.id;
		name = this.name;
		desc = this.desc;
		price = this.price;
		image = this.image;
		
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setName(String name){
		name = this.name;
	}
	public void setDesc(String desc){
		desc = this.desc;
	}
	public void setId(int id){
		id = this.id;
	}
	
	public String getName(){
		return name;
	}
	public String getDesc(){
		return desc;
	}
	public int getId(){
		return id;
	}

}
