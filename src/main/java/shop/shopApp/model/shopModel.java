package shop.shopApp.model;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="`inventory`")
public class shopModel {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;
	
	@Column(name="price")
    private Integer price;
    
    @Column(name="item")
    private String item;

}
