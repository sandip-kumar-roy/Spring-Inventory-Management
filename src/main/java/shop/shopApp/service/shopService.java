package shop.shopApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.shopApp.model.shopModel;
import shop.shopApp.repository.shopRepository;

@Service
public class shopService {
	@Autowired
	shopRepository rep;
	
	public shopModel saveItem(shopModel m) {
		return rep.save(m);
	}

	public void deleteItem(Long id) {
		rep.deleteById(id);
	}
	
	public List<shopModel> listAll() {
		return rep.findAll();
	}
	
	public shopModel listOneItem(Long id) {
		return rep.findById(id).get();
	}
	
	public void updateItem(String item, Integer price) {

		List<shopModel> ll = rep.getItem(item);
		for(shopModel s:ll) {
			s.setPrice(price);
			rep.save(s);
		}
		
	}
}
