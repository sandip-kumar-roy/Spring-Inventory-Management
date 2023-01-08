package shop.shopApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import shop.shopApp.model.shopModel;
import shop.shopApp.service.shopService;

@Controller
public class shopController {
	
	@Autowired
	shopService service;
	
	@PostMapping("/add")
    public String postAdd(@ModelAttribute("shopModel") shopModel model) {
		service.saveItem(model);
		return "redirect:/index";
    }
	
	@GetMapping(value="/")
    public String GetDefault() {
        return "redirect:/index";
    }
    
    @GetMapping(value="/index")
    public String GetIndex(Model model, @ModelAttribute("shopModel") shopModel smodel) {
    	model.addAttribute("allItems", service.listAll());
        return "index";
    }

    @GetMapping(value="/showItem/{pid}")
    public String getShowItem(@PathVariable(value = "pid") Long id, Model model, @ModelAttribute("shopModel") shopModel smodel) {
    	shopModel cur = service.listOneItem(id);
    	model.addAttribute("shopModel", cur);
        return "update";
    }
    
    @PostMapping(value="/updateItem")
    public String postUpdateItem(@ModelAttribute("shopModel") shopModel smodel) {
    	service.updateItem(smodel.getItem(), smodel.getPrice());
        return "redirect:/index";
    }

    @GetMapping(value="/delete/{pid}")
    public String getDelete(@PathVariable(value = "pid") Long id) {
        service.deleteItem(id);
        return "redirect:/index";
    }
}
