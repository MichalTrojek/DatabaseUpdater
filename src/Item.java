
public class Item {

	private String ean;
	private String name;
	private String price;
	private String normalizedName;
	private String lowerCaseName;

	public Item(String ean, String name, String price, String normalizedName, String lowerCaseName) {
		this.setEan(ean);
		this.setName(name);
		this.setPrice(price);
		this.setNormalizedName(normalizedName);
		this.lowerCaseName = lowerCaseName;
	}
	
	public Item(String ean, String name, String price, String normalizedName) {
		this.setEan(ean);
		this.setName(name);
		this.setPrice(price);
		this.setNormalizedName(normalizedName);
		
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNormalizedName() {
		return normalizedName;
	}

	public void setNormalizedName(String normalizedName) {
		this.normalizedName = normalizedName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getLowerCaseName() {
		return lowerCaseName;
	}

	public void setLowerCaseName(String lowerCaseName) {
		this.lowerCaseName = lowerCaseName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ean == null) ? 0 : ean.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (ean == null) {
			if (other.ean != null)
				return false;
		} else if (!ean.equals(other.ean))
			return false;
		return true;
	}
	
	
	

}
