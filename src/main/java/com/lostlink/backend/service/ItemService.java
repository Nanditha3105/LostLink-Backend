package com.lostlink.backend.service;

import com.lostlink.backend.entity.Item;
import com.lostlink.backend.entity.User;
import com.lostlink.backend.repository.ItemRepository;
import com.lostlink.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findByReturnedFalseOrderByDateReportedDesc();
    }

    public List<Item> getMyReports(String email) {
        User reporter = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return itemRepository.findByReporter_IdOrderByDateReportedDesc(reporter.getId());
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Item createItem(Item item, String reporterEmail) {
        User reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        item.setReporter(reporter);
        item.setReturned(false);
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, Item updatedItem) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        existingItem.setTitle(updatedItem.getTitle());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setCategory(updatedItem.getCategory());
        existingItem.setStatus(updatedItem.getStatus());
        existingItem.setLocation(updatedItem.getLocation());
        existingItem.setDateReported(updatedItem.getDateReported());
        existingItem.setContactName(updatedItem.getContactName());
        existingItem.setContactEmail(updatedItem.getContactEmail());

        return itemRepository.save(existingItem);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}